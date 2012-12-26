package com.feelyou.util;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.PerformanceTestCase;

public class SkyMeetingUtil {
	
	public static final int USER_NO = 0;
	public static final int USER_PASS = 1;
	public static final int USER_PHONE = 2;
	public static final int SERVER_ADDRESS = 3;
	public static final int AGENT_NAME = 4;
	public static final int AGENT_KEY = 5;
	public static final int FIRSTIN = 6;
	public static final int ACCOUNT_OK = 7;
	public static final int ISDIRECT_OPEN = 8;
	public static final int LINK_TYPE = 9;
	public static final int IS_INITIAL = 10;
	public static final int IS_TESTACCOUNT = 11;
	public static final int VERSION = 12;
	public static final int REQUEST_WAITTIME = 13;
	public static final int VIDEO_TIME = 14;
	public static final int WAPLINK = 15;
	public static final int AUTOUPDATE = 16;
	public static final int USER_UID = 17;
	private static SharedPreferences PREFERENCES = null;

	public static String getPreference(int key) {
		String strKey = String.valueOf(key);
		return PREFERENCES.getString(strKey, "");
	}
	
	public static void init(Context context) {
		if (PREFERENCES == null) {
			PREFERENCES = context.getSharedPreferences("feelyou", Context.MODE_PRIVATE);
			readParam();
		}
		if (getPreference(SkyMeetingUtil.IS_INITIAL).equals("")) {
			setPreference(SkyMeetingUtil.FIRSTIN, "1");
			setPreference(SkyMeetingUtil.ACCOUNT_OK, "0");
			setPreference(SkyMeetingUtil.ISDIRECT_OPEN, "1");
			setPreference(SkyMeetingUtil.LINK_TYPE, "0");
			setPreference(SkyMeetingUtil.IS_INITIAL, "1");
			setPreference(SkyMeetingUtil.IS_TESTACCOUNT, "0");
			setPreference(SkyMeetingUtil.WAPLINK, "0");
			setPreference(SkyMeetingUtil.AUTOUPDATE, "1");
			setPreference(SkyMeetingUtil.USER_UID, "");
		}
	}
	

	private static void readParam() {
//		Config.account = PREFERENCES.getString("0", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.phone = PREFERENCES.getString("", "");
//		Config.host = PREFERENCES.getString("", "");
////		Config.first_boot = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
//		Config.password = PREFERENCES.getString("", "");
	}

	public static void setPreference(int key, String value) {
		Editor editor = PREFERENCES.edit();
		String str = String.valueOf(key);
		editor.putString(str, value);
		editor.commit();
	}
}
