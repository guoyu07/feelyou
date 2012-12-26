package com.feelyou.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.feelyou.ui.SelectCallTypeActivity;
import com.feelyou.util.SkyMeetingUtil;

public class PhoneStateReceiver extends BroadcastReceiver {

	private static final String ACTION = "android.intent.action.NEW_OUTGOING_CALL";

	@Override
	public void onReceive(Context context, Intent intent) {

		// //////////////////////////////
		System.out.println("----监控到啦----");
		System.out.println("去电号码"
				+ intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
		System.out.println("当前action" + intent.getAction());

		System.out.println("通话状态"
				+ intent.getStringExtra(TelephonyManager.EXTRA_STATE));
		System.out
				.println("来电号码"
						+ intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

		// //////////////////////////////

//		if (SelectCallTypeActivity.isSel)
//			return;
//		SelectCallTypeActivity.isSel = false;
		
		SkyMeetingUtil.init(context);
		if (intent.getAction().equals(ACTION)) {
			if ((SkyMeetingUtil.getPreference(SkyMeetingUtil.ACCOUNT_OK)
					.equals("1"))
					&& (SkyMeetingUtil
							.getPreference(SkyMeetingUtil.ISDIRECT_OPEN)
							.equals("1"))) {
				String phone = intent
						.getStringExtra("android.intent.extra.PHONE_NUMBER");
				Intent intentCallType = new Intent(context,
						SelectCallTypeActivity.class);
				intentCallType.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentCallType.putExtra("phone", phone);
				context.startActivity(intentCallType);
				setResultData(null);
			}
		}
	}

}
