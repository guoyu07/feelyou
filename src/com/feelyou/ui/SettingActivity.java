package com.feelyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;

/**
 * Œ“µƒ’ ªß
 * @author Administrator
 *
 */
public class SettingActivity extends Activity {
	private TextView tvSetting_phone = null;
	private TextView tvSetting_userno = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		tvSetting_phone = (TextView)findViewById(R.id.setting_phone);
		tvSetting_userno = (TextView)findViewById(R.id.setting_userno);
		
		GridView gridView = (GridView)findViewById(R.id.gridview);
		ArrayAdapter arrayAdapterOfItem = ArrayAdapter.createFromResource(this, R.array.setting_item, android.R.layout.simple_list_item_1);
		gridView.setAdapter(arrayAdapterOfItem);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int itemId,
					long arg3) {
				Intent intent = null;
				switch (itemId) {
				case 0:
					intent = new Intent(SettingActivity.this, SettingAccountActivity.class);
					break;
				case 1:
					intent = new Intent(SettingActivity.this, SettingPasswordActivity.class);
					break;
				case 2:
					intent = new Intent(SettingActivity.this, SettingFindPasswordActivity.class);
					break;
				case 3:
					intent = new Intent(SettingActivity.this, SettingSearchBalanceActivity.class);
					break;
				case 4:
					intent = new Intent(SettingActivity.this, SettingLinkActivity.class);
					break;
				}
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
	}
	@Override
	protected void onStart() {
		tvSetting_phone.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PHONE));
		tvSetting_userno.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
		super.onStart();
	}
}
