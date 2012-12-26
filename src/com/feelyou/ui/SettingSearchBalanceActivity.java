package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.net.MSG;
import com.feelyou.net.Network;
import com.feelyou.util.Config;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;


public class SettingSearchBalanceActivity extends Activity {
	private TextView  balance_info;
	Dialog dialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsearchbalance);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		balance_info = (TextView)findViewById(R.id.setting_searchBalance_info);
		
//		MSG msg = new MSG();   //Ŀǰ��ѯ������Ϣ�������Ƿ������˽��������⣬��ʱ�����ã���������ʱ�õ�½��Ϣ���棬��Ϊ��½�ɹ��Ժ󣬷��������ص��������Ϣ�����ÿ������ϡ�
//		msg.setType(5);
//		msg.setVersion(1);                             
//		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
//		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
//		new Network(handler, msg.getMessage()).start();
//		dialog = DialogUtil.showProgressDialog(SettingSearchBalanceActivity.this, "���ڷ�������....");
		
		MSG msg = new MSG();
		msg.setType(7);
		msg.setVersion(1);
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO )));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PHONE)));
		new Network(handler, msg.getMessage()).start();
		dialog = DialogUtil.showProgressDialog(SettingSearchBalanceActivity.this, "���ڷ�������....");		
	}
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			if (bundle.getBoolean("issucc", false)) {
				StringBuilder sb = new StringBuilder();
				sb.append(balance_info.getText());
				sb.append(bundle.getString("msg"));
				balance_info.setText(sb.toString());				
			} else {
				balance_info.setTextColor(Color.RED);
				balance_info.setText(bundle.getString("msg"));
			}
			super.handleMessage(msg);
		}
		
	};

}
