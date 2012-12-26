package com.feelyou.receiver;

import com.feelyou.util.SkyMeetingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			SkyMeetingUtil.init(context);
			SkyMeetingUtil.setPreference(SkyMeetingUtil.ISDIRECT_OPEN, "1");
		}
	}
}
