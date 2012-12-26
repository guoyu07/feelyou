package com.feelyou.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.feelyou.R;
import com.feelyou.ui.MainActivity;
import com.feelyou.util.SystemUtil;

public class NotifyManager {
	private static final int NOTIFY_CALL_FAILURE = 1;
	private static final int NOTIFY_CALL_SUCCESS = 2;
	private static final int NOTIFY_START_CALL = 3;
	Context context;
	
	public NotifyManager(Context context) {
		this.context = context;
	}
	
	private void beginNotify(String strContent, int id) {
		NotificationManager notificationManager = 
			(NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		long l = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.application, strContent, l);
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		String strTitle = SystemUtil.getString(context, R.string.application_title);
		notification.setLatestEventInfo(context, strTitle, strContent, pendingIntent);
		notificationManager.notify(id, notification);
	}
	
	public void clear() {
		NotificationManager notificationManager = 
			(NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}
	
	public void notifyCallFailure() {
		clear();
		String str = SystemUtil.getString(context, R.string.call_service_notify_callfailure); // 呼叫请求提交失败，请重试
		beginNotify(str, NOTIFY_CALL_FAILURE);
	}
	
	public void notifyCallSuccess() {
		clear();
		String str = SystemUtil.getString(context, R.string.call_service_notify_requestok); // 呼叫请求提交成功
		beginNotify(str, NOTIFY_CALL_SUCCESS);
	}
	
	public void notifyWaitCall() {
		clear();
		String str = SystemUtil.getString(context, R.string.call_service_notify_waitcall); // 呼叫请求正在提交，请稍后...
		beginNotify(str, NOTIFY_START_CALL);
	}
}
