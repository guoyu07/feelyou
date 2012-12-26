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
		
//		MSG msg = new MSG();   //目前查询余额的消息，可能是服务器端解析有问题，暂时不能用，我现在暂时用登陆消息代替，因为登陆成功以后，服务器返回的是余额信息，正好可以用上。
//		msg.setType(5);
//		msg.setVersion(1);                             
//		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
//		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
//		new Network(handler, msg.getMessage()).start();
//		dialog = DialogUtil.showProgressDialog(SettingSearchBalanceActivity.this, "正在发送数据....");
		
		MSG msg = new MSG();
		msg.setType(7);
		msg.setVersion(1);
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO )));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PHONE)));
		new Network(handler, msg.getMessage()).start();
		dialog = DialogUtil.showProgressDialog(SettingSearchBalanceActivity.this, "正在发送数据....");		
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
