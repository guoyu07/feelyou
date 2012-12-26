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
 * ����������
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
		ownPhoneNo = phoneMgr.getLine1Number();   // �������룬��Ҫread_phone_stateȨ��
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
				//------------�������Ԫ�ص�ֵΪ�����ڿؼ���������ʾ�û�--------------
				if (phone1.equals("")) {
					ft_phone1.setError("�ֻ����벻��Ϊ�գ�");
					return;
				}
				if (phone2.equals("")) {
					ft_phone2.setError("ȷ���ֻ����벻��Ϊ�գ�");
					return;
				}
//				//------------�������Ԫ�ص�ֵΪ���򵯳��Ի�����ʾ�û�--------------
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
				Log.i(TAG, "����URL��" + tempURL);
				
				new MyNetwork2(handler, tempURL).start();
				progressdialog = DialogUtil.showProgressDialog(FreeTrialActivity.this, "���ڷ�������....");
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
					Log.i(TAG, "���յ����ݣ�" + str);
					if (str.length() > 1) {
						JSONTokener jsonParser = new JSONTokener(str);
						try {
							JSONObject user = (JSONObject) jsonParser.nextValue();
							SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, user.getString("ggid"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, user.getString("tel"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_UID, user.getString("uid"));
							SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone1.substring(phone1.length() - 5, phone1.length()));
							msg = "�ɹ������ķ����˺�Ϊ��" + user.getString("ggid") + "��\n����Ϊ���ֻ��ź�5λ����";
						} catch (JSONException e) {
							DialogUtil.showAlertDialog(FreeTrialActivity.this, "��ʾ", "���ص�JSON�����д���", null, null);
							e.printStackTrace();
						}
//						msg = "ע���������ύ��ϵͳ�Ժ󽫻��ע��ķ��Ѻź����뷢��ע���ֻ��ϣ���ע����գ�����24Сʱ�ڵ�¼http://wap.feelyou.me,�����ʺ���ɱҴ�绰��";
//						msg = "�ɹ������ķ����˺�Ϊ��" + str + "��\n����Ϊ���ֻ��ź�5λ����";
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, str);
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone1.substring(phone1.length() - 5, phone1.length()));
//						SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, phone1);
						
						AlertDialog.Builder builder = new AlertDialog.Builder(FreeTrialActivity.this);
			    		builder.setMessage(msg).setTitle("��ʾ");
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
							msg = "������Ч���ֻ�����";
							break;
						case 2:
							msg = "��ʾ�����ֻ���ע��";
							break;
						case 3:
							msg = "�����޷��Ѻſɷ���";
							break;
						case 5:
							msg = "��������";
							break;
						}
						DialogUtil.showAlertDialog(FreeTrialActivity.this, "��ʾ", msg);
					}
					
//					DialogUtil.showAlertDialog(SettingFindPasswordActivity.this, "��ʾ", msg);
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
			Log.i(TAG, "���յ����ݣ�" + str);
			if (str.length() > 1) {
				JSONTokener jsonParser = new JSONTokener(str);
				try {
					JSONObject user = (JSONObject) jsonParser.nextValue();
					SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, user.getString("ggid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, user.getString("tel"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_UID, user.getString("uid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, phone.substring(phone.length() - 5, phone.length()));
					info = "�ɹ������ķ����˺�Ϊ��" + user.getString("ggid") + "��\n����Ϊ���ֻ��ź�5λ����";
				} catch (JSONException e) {
					DialogUtil.showAlertDialog(FreeTrialActivity.this, "��ʾ", "���ص�JSON�����д���", null, null);
					e.printStackTrace();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(FreeTrialActivity.this);
	    		builder.setMessage(info).setTitle("��ʾ");
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
					info = "������Ч���ֻ�����";
					break;
				case 2:
					info = "��ʾ�����ֻ���ע��";
					break;
				case 3:
					info = "�����޷��Ѻſɷ���";
					break;
				case 5:
					info = "��������";
					break;
				}
				DialogUtil.showAlertDialog(FreeTrialActivity.this, "��ʾ", info);
			}
			super.handleMessage(msg);
		}
	};	

}
