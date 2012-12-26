package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.net.MSG;
import com.feelyou.net.Network;
import com.feelyou.util.Config;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;
import com.feelyou.util.DialogUtil.DialogCallBack;
/**
 * 修改密码
 * @author Administrator
 *
 */
public class SettingPasswordActivity extends Activity {
	private String newPwd1 = "";
	private String newPwd2 = "";
	private String oldPwd = "";
	private Button btnSave = null;
	Dialog progressdialog;
	private boolean isSucc = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingpassword);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		TextView tvUserNo = (TextView)findViewById(R.id.setting_password_account);
		StringBuilder sb = new StringBuilder();
		sb.append(tvUserNo.getText());
		sb.append(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
		tvUserNo.setText(sb.toString());
		
		btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				oldPwd = String.valueOf(((EditText)findViewById(R.id.oldPwd)).getText()).trim();
				newPwd1 = String.valueOf(((EditText)findViewById(R.id.newPwd1)).getText()).trim();
				newPwd2 = String.valueOf(((EditText)findViewById(R.id.newPwd2)).getText()).trim();
				if ((oldPwd.equals("")) || (newPwd1.equals("")) || (newPwd2.equals(""))){
					DialogUtil.showOperateFailureDialog(SettingPasswordActivity.this, "不能为空");
					return;
				}
				if (!newPwd1.equals(newPwd2)) {
					DialogUtil.showOperateFailureDialog(SettingPasswordActivity.this, "两次输入新密码不一致");
					return;
				}
				MSG msg = new MSG();
				msg.setType(6);
				msg.setVersion(1);
				msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
				msg.appendByte(SystemUtil.encoding(oldPwd));
				msg.appendByte(SystemUtil.encoding(newPwd1)); 
				new Network(handler, msg.getMessage()).start();
				progressdialog = DialogUtil.showProgressDialog(SettingPasswordActivity.this, "正在发送数据....");
			}
		});
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressdialog.dismiss();
			Bundle bundle = msg.getData();
			isSucc = bundle.getBoolean("issucc", false);
			DialogUtil.showAlertDialog(SettingPasswordActivity.this, "提示", bundle.getString("msg"), okDlgCallback, null);
			if (bundle.getBoolean("issucc", false)) {
				SkyMeetingUtil.setPreference(SkyMeetingUtil.USER_PASS, newPwd1);
			}
			super.handleMessage(msg);
		}
	};
	
	public DialogCallBack okDlgCallback = new DialogCallBack() {

		@Override
		public void callBack(DialogInterface dialogInterface) {
			dialogInterface.cancel();
			dialogInterface.dismiss();
			if (isSucc) {
				Intent intent = new Intent(SettingPasswordActivity.this, SettingActivity.class);
				SettingPasswordActivity.this.startActivity(intent);
				SettingPasswordActivity.this.finish();
			}
		}
	};

}
