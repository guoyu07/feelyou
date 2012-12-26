package com.feelyou.manager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.feelyou.R;
import com.feelyou.receiver.CallReceiver;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SystemUtil;

public class CallManager {
	public static void call(Context context, String phoneNo, boolean isDirectCall) {
		Dialog dialog = null;
		if (!isDirectCall) {
			String msg = SystemUtil.getString(context, R.string.call_service_notify_waitcall);
			dialog = DialogUtil.showProgressDialog(context, msg);
			dialog.show();
		} else {
			new NotifyManager(context).notifyWaitCall();
		}
		BroadcastReceiver broadcastReceiver = CallReceiver.registerCallReceiver(context, dialog);
		Intent intent = new Intent("skymeetingcall");
		intent.putExtra("phone", phoneNo);
		intent.putExtra("isDirectCall", isDirectCall);
		context.startService(intent);
	}

}
