package com.feelyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.util.SkyMeetingUtil;
/**
 * …Ë÷√¡¨Ω”
 * @author Administrator
 *
 */
public class SettingLinkActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settinglink);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		CheckBox directCheckBox = (CheckBox)findViewById(R.id.setting_directTel);
		
		boolean bValue = SkyMeetingUtil.getPreference(SkyMeetingUtil.ISDIRECT_OPEN).equals("1");
		directCheckBox.setChecked(bValue);
		directCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					SkyMeetingUtil.setPreference(SkyMeetingUtil.ISDIRECT_OPEN, "1");
				} else {
					SkyMeetingUtil.setPreference(SkyMeetingUtil.ISDIRECT_OPEN, "0");
				}
			}
			
		});
		
		bValue = SkyMeetingUtil.getPreference(SkyMeetingUtil.AUTOUPDATE).equals("1");
		CheckBox chkAutoupdate = (CheckBox)findViewById(R.id.setting_autoupdate);
		chkAutoupdate.setChecked(bValue);
		chkAutoupdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					SkyMeetingUtil.setPreference(SkyMeetingUtil.AUTOUPDATE, "1");
				} else {
					SkyMeetingUtil.setPreference(SkyMeetingUtil.AUTOUPDATE, "0");
				}
			}
		});
	}

}
