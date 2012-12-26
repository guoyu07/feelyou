package com.feelyou.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class CallReceiver extends BroadcastReceiver {
	
	private Dialog currentDialog;
	private Context context;
	
	private CallReceiver(Context context, Dialog dialog) {
		this.currentDialog = dialog;
		this.context = context;
	}
	public static BroadcastReceiver registerCallReceiver(Context context, Dialog dialog) {
		CallReceiver callReceiver = new CallReceiver(context, dialog);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.feelyou.action.CALL_COMPLETED");
		context.registerReceiver(callReceiver, intentFilter);
		return callReceiver;
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.feelyou.action.CALL_COMPLETED")) {
			boolean bool = intent.getBooleanExtra("isDirectCall", true);
			int i = intent.getIntExtra("type", 0);
//			if
		}
		
	}

}
