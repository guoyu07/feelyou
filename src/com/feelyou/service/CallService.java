package com.feelyou.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class CallService extends Service implements Handler.Callback{
	
	public static final String SKYMEETING_CALL = "com.skymeeting.action.CALL_COMPLETED";
	private Handler.Callback callback;
	boolean isDirectCall = false;
	
	public CallService() {
		this.callback = this;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		String phone = intent.getStringExtra("phone");
		this.isDirectCall = intent.getBooleanExtra("isDirectCall", false);
//		new RecordManager(this).insertRecord(phone);
		//Éú³ÉmessageNetWorkUtil.start()
		Handler handler = new Handler(this);
		
	}
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
