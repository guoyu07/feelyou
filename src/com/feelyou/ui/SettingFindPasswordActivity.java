package com.feelyou.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.feelyou.R;
import com.feelyou.net.MSG;
import com.feelyou.net.Network;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

public class SettingFindPasswordActivity extends Activity {
	private static final String REGEX_PHONE = "^(13|14|15|18|16|17)\\d{9}$";
	String phoneNum = "";
	EditText etPhone; 
	Dialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingfindpassword);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		TelephonyManager phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		etPhone = (EditText) findViewById(R.id.phoneNum);
		etPhone.setText(phoneMgr.getLine1Number());  // 本机号码，需要read_phone_state权限
		
		Button btnConfirm = (Button)findViewById(R.id.btnSave);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			HttpURLConnection conn = null;
			InputStream in = null;
			
			@Override
			public void onClick(View v) {
				phoneNum = String.valueOf(etPhone.getText()).trim();
				if (phoneNum.equals("")) {
					DialogUtil.showOperateFailureDialog(SettingFindPasswordActivity.this, "号码不能为空");
					return;
				}
				Pattern pattern = Pattern.compile(REGEX_PHONE);
				if (!pattern.matcher(phoneNum).matches()) {
					DialogUtil.showOperateFailureDialog(SettingFindPasswordActivity.this, "号码格式不正确");
					return;
				}
				
//				http://wap.feelyou.me/api/find_pwd.php?mobileno=123772&checkcode=1
				StringBuilder sb = new StringBuilder();
				sb.append("http://wap.feelyou.me/api/find_pwd.php?mobileno=");
				sb.append(phoneNum);
				sb.append("&checkcode=");
				sb.append(SystemUtil.toMd5(phoneNum + "fy602"));
				dialog = DialogUtil.showProgressDialog(SettingFindPasswordActivity.this, "正在发送数据....");
				try {
					URL url = new URL(sb.toString());
					// HttpURLConnection 
					conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5 * 1000);
					// InputStream 
					in = conn.getInputStream();
					dialog.dismiss();
					int code = in.read();  // ascii码
					code = code - 48;
					String msg = "";
					switch (code) {
						case 0:
							msg = "成功，请注意查看短信。";
							break;
						case 1:
							msg = "错误，您今天找回密码的次数已经到极限了";
							break;
						case 3:
							msg = "错误，您的飞币不够了，请与管理员联系";
							break;
						case 4:
							msg = "错误，号码不存在";
							break;
						case 5:
							msg = "其它错误";
							break;
					}
					DialogUtil.showAlertDialog(SettingFindPasswordActivity.this, "提示", msg);
					
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
				}
				
//				String URL = "http://wap.feelyou.me/api/find_pwd.php?mobileno=123772&checkcode=1";
				
				
				MSG msg = new MSG();
				msg.setType(25);
				msg.setVersion(1);                           
				msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PHONE)));
				msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
//				new Network(handler, msg.getMessage()).start();
//				dialog = DialogUtil.showProgressDialog(SettingFindPasswordActivity.this, "正在发送数据....");
			}
		});
	}
	
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(SettingFindPasswordActivity.this, "提示", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

}
