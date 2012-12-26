package com.feelyou.ui;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.feelyou.R;
import com.feelyou.net.MyNetwork2;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.DialogUtil.DialogCallBack;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;
/**
 *  �����ʻ�
 * @author Administrator
 *
 */
public class SettingAccountActivity extends Activity implements OnClickListener  {
	private EditText etPhone, etUserNo, etPass;
	private String fromPhone = "";
	private String password = "";
	private String userno = "";
	Dialog progressdialog;
	private boolean isSucc = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingaccount);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
//		etPhone = (EditText)findViewById(R.id.phone);
		etUserNo = (EditText)findViewById(R.id.userno);
		etPass = (EditText)findViewById(R.id.password);
		((Button)findViewById(R.id.btnSave)).setOnClickListener(this);
		
		init();
	}
	
	private void init() {
		TelephonyManager phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//		etPhone.setText(phoneMgr.getLine1Number());  // �������룬��Ҫread_phone_stateȨ��
//		etPhone.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PHONE));
//		etUserNo.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
		etUserNo.setText(phoneMgr.getLine1Number());
		etPass.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS));
		
	}

	@Override
	public void onClick(View v) {
//		fromPhone = etPhone.getText().toString().trim();
		userno = etUserNo.getText().toString().trim();
		password = etPass.getText().toString().trim();
		if ( (userno.equals("")) || (password.equals(""))) {
			etUserNo.setError("�ֻ�������˺Ų���Ϊ��");
			DialogUtil.showOperateFailureDialog(this, R.string.setting_error_4);
			return;
		}
/*		MSG msg = new MSG();
		msg.setType(7);
		msg.setVersion(1);
		msg.appendByte(SystemUtil.encoding(userno));
		msg.appendByte(SystemUtil.encoding(password));
		msg.appendByte(SystemUtil.encoding(fromPhone));
		new Network(handler, msg.getMessage()).start();
		progressdialog = DialogUtil.showProgressDialog(SettingAccountActivity.this, "���ڷ�������....");*/
		
		
		
//		http://wap.feelyou.me/api/check_user.php?fid={$MobileNo}&fpwd={$mima}&checkcode=".md5($MobileNo.$mima.'fy602'));
		StringBuilder sb = new StringBuilder();
//		sb.append("http://wap.feelyou.me/api/check_user.php?fid=");
		sb.append("http://wap.feelyou.me/api/check_user_json.php?fid=");  
		sb.append(userno);
		sb.append("&fpwd=").append(password);
		sb.append("&checkcode=");
		sb.append(SystemUtil.toMd5(userno + password + "fy602"));
		new MyNetwork2(handler, sb.toString()).start();
		progressdialog = DialogUtil.showProgressDialog(SettingAccountActivity.this, "���ڷ�������....");
		
	}
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String info = "";
			progressdialog.dismiss();
			String ret = (String)msg.obj;
			if ((ret == null) || ("".equals(ret)))
				ret = "4";
			if (ret.length() > 1) {
				info = "��¼�ɹ�";
//				String [] accounts = ret.split("_"); // ���Ѻ�_�ֻ���
				// �����˻���Ϣ
				JSONTokener jsonParser = new JSONTokener(ret);
				try {
					JSONObject user = (JSONObject) jsonParser.nextValue();
					SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, user.getString("ggid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, user.getString("tel"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_UID, user.getString("uid"));
					SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, password);
					DialogUtil.showAlertDialog(SettingAccountActivity.this, "��ʾ", info, okDlgCallback, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					DialogUtil.showAlertDialog(SettingAccountActivity.this, "��ʾ", "���ص�JSON�����д���", okDlgCallback, null);
					e.printStackTrace();
				}
			} else {
				switch (Integer.valueOf(ret)) {
				case 1:
					info = "��Ч���˺Ż�����";
					break;
				default:
					info = "��������";
					break;
				}
				DialogUtil.showAlertDialog(SettingAccountActivity.this, "��ʾ", info);
			}
			
/*			progressdialog.dismiss();
			Bundle bundle = msg.getData();		
			isSucc = bundle.getBoolean("issucc", false);
			DialogUtil.showAlertDialog(SettingAccountActivity.this, "��ʾ", bundle.getString("msg"), okDlgCallback, null);
			if (bundle.getBoolean("issucc", false)) {
				SkyMeetingUtil.setPreference(SkyMeetingUtil.ACCOUNT_OK, "1");
				SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_NO, userno);
				SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, password);
				SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PHONE, fromPhone);
			}*/
			super.handleMessage(msg);
		}
	};
	
	public DialogCallBack okDlgCallback = new DialogCallBack() {

		@Override
		public void callBack(DialogInterface dialogInterface) {
			
			dialogInterface.cancel();
			dialogInterface.dismiss();
			Intent intent = new Intent(SettingAccountActivity.this, SettingActivity.class);
			SettingAccountActivity.this.startActivity(intent);
			SettingAccountActivity.this.finish();
			
/*			dialogInterface.cancel();
			dialogInterface.dismiss();
			if (isSucc) {
				Intent intent = new Intent(SettingAccountActivity.this, SettingActivity.class);
				SettingAccountActivity.this.startActivity(intent);
				SettingAccountActivity.this.finish();
			}*/
		}
	};

}
