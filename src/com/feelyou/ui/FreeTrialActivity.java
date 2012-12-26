package com.feelyou.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.net.MyNetwork2;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

/**
 * 免费体验界面
 * @author Administrator
 *
 */
public class FreeTrialActivity extends Activity {
	private EditText ft_phone1 ;
	private EditText ft_phone2 ;
	private Button ft_begin ;
	private String regex = "1[3458]\\d{9}$";
	private String ownPhoneNo = "";
	private static final int MENU_ACCOUNT = Menu.FIRST;
	protected static final String TAG = "FreeTrialActivity";
	private String strURL = "http://wap.feelyou.me/api/reg.php?mobileno=";
	private String tempURL = "";
	Dialog progressdialog;
	private String phone;
//	http://wap.feelyou.me/api/reg.php?mobileno=15803839754&src=android&checkcode=md5(mobileno + src + "fy602")

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.freetrial);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		TelephonyManager phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		ownPhoneNo = phoneMgr.getLine1Number();   // 本机号码，需要read_phone_state权限
		ft_phone1 = (EditText)findViewById(R.id.ft_phone1);
		ft_phone2 = (EditText)findViewById(R.id.ft_phone2);
		ft_phone1.setText(ownPhoneNo);
		ft_phone2.setText(ownPhoneNo);
		
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FreeTrialActivity.this, SettingAccountActivity.class);
				startActivity(intent);
				FreeTrialActivity.this.finish();
			}
		});
		
		ft_begin = (Button)findViewById(R.id.ft_begin);
		ft_begin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Pattern pattern = Pattern.compile(regex);
				String phone1 = ft_phone1.getText().toString().trim();
				String phone2 = ft_phone2.getText().toString().trim();
				phone = phone1;
				//------------如果界面元素的值为空则在控件自身上提示用户--------------
				if (phone1.equals("")) {
					ft_phone1.setError("手机号码不能为空！");
					return;
				}
				if (phone2.equals("")) {
					ft_phone2.setError("确认手机号码不能为空！");
					return;
				}
//				//------------如果界面元素的值为空则弹出对话框提示用户--------------
//				if (phone1.equals("") || phone2.equals("")) {
//					DialogUtil.showOperateFailureDialog(FreeTrialActivity.this, R.string.freetrial_dialog_error1);
//					return;
//				}
				if (! pattern.matcher(phone1).matches()) {
					DialogUtil.showOperateFailureDialog(FreeTrialActivity.this, R.string.freetrial_dialog_error2);
					return;
				}
				if (! phone1.equals(phone2)) {
					DialogUtil.showOperateFailureDialog(FreeTrialActivity.this, R.string.freetrial_dialog_error3);
					return;
				}
//				MSG msg = new MSG();
//				msg.setType(22);
//				msg.setVersion(1);
//				msg.appendByte(SystemUtil.encoding("")); // Agent
//				msg.appendByte(SystemUtil.encoding(phone1)); // phone
//				msg.appendByte(SystemUtil.encoding("")); // check info
//				new Network(handler, msg.getMessage()).start();
				StringBuilder sb = new StringBuilder();
				sb.append(phone1);
				sb.append("androidfy602");
				tempURL = "";
				tempURL = strURL + phone1 + "&src=android&checkcode=" + SystemUtil.toMd5(sb.toString());
				Log.i(TAG, "请求URL：" + tempURL);
				
				new MyNetwork2(handler, tempURL).start();
				progressdialog = DialogUtil.showProgressDialog(FreeTrialActivity.this, "正在发送数据....");
				/*
				HttpURLConnection conn = null;
				InputStream in = null;
				try {
					
					URL url = new URL(tempURL);
					conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5 * 1000);
					in = conn.getInputStream();
					byte[] b = new byte[512];
					in.read(b);
					String str = new String(b);
					str = str.trim();
					String msg = "";
					Log.i(TAG, "接收的数据：" + str);
					if (str.length() > 1) {
						JSONTokener jsonParser = new JSONTokener(str);
						try {
							JSONObject user = (JSONObject) jsonParser.nextValue();
							SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, user.getString("ggid"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, user.getString("tel"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_UID, user.getString("uid"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone1.substring(phone1.length() - 5, phone1.length()));
							msg = "成功，您的飞友账号为：" + user.getString("ggid") + "，\n密码为【手机号后5位】。";
						} catch (JSONException e) {
							DialogUtil.showAlertDialog(FreeTrialActivity.this, "提示", "返回的JSON数据有错误", null, null);
							e.printStackTrace();
						}
//						msg = "注册申请已提交，系统稍后将会把注册的飞友号和密码发到注册手机上，请注意查收，并在24小时内登录http://wap.feelyou.me,激活帐号领飞币打电话。";
//						msg = "成功，您的飞友账号为：" + str + "，\n密码为【手机号后5位】。";
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, str);
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone1.substring(phone1.length() - 5, phone1.length()));
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, phone1);
						
						AlertDialog.Builder builder = new AlertDialog.Builder(FreeTrialActivity.this);
			    		builder.setMessage(msg).setTitle("提示");
			    		builder.setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(FreeTrialActivity.this, MainActivity.class);
								startActivity(intent);
								FreeTrialActivity.this.finish();
							}
						});
			    		builder.create().show();
						
					} else {
						switch (Integer.valueOf(str)) {
						case 1:
							msg = "错误，无效的手机号码";
							break;
						case 2:
							msg = "提示，此手机已注册";
							break;
						case 3:
							msg = "错误，无飞友号可分配";
							break;
						case 5:
							msg = "其它错误";
							break;
						}
						DialogUtil.showAlertDialog(FreeTrialActivity.this, "提示", msg);
					}
					
//					DialogUtil.showAlertDialog(SettingFindPasswordActivity.this, "提示", msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					conn.disconnect();
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // end finally
				*/
			} // end onClick
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ACCOUNT, 1, R.string.freetrial_account);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCOUNT:
			Intent intent = new Intent(this, SettingAccountActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			progressdialog.dismiss();
			String info = "";
			String str = (String)msg.obj;
			Log.i(TAG, "接收的数据：" + str);
			if (str.length() > 1) {
				JSONTokener jsonParser = new JSONTokener(str);
				try {
					JSONObject user = (JSONObject) jsonParser.nextValue();
					SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, user.getString("ggid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, user.getString("tel"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_UID, user.getString("uid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone.substring(phone.length() - 5, phone.length()));
					info = "成功，您的飞友账号为：" + user.getString("ggid") + "，\n密码为【手机号后5位】。";
				} catch (JSONException e) {
					DialogUtil.showAlertDialog(FreeTrialActivity.this, "提示", "返回的JSON数据有错误", null, null);
					e.printStackTrace();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(FreeTrialActivity.this);
	    		builder.setMessage(info).setTitle("提示");
	    		builder.setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(FreeTrialActivity.this, MainActivity.class);
						startActivity(intent);
						FreeTrialActivity.this.finish();
					}
				});
	    		builder.create().show();
				
			} else {
				switch (Integer.valueOf(str)) {
				case 1:
					info = "错误，无效的手机号码";
					break;
				case 2:
					info = "提示，此手机已注册";
					break;
				case 3:
					info = "错误，无飞友号可分配";
					break;
				case 5:
					info = "其它错误";
					break;
				}
				DialogUtil.showAlertDialog(FreeTrialActivity.this, "提示", info);
			}
			super.handleMessage(msg);
		}
	};	

}
