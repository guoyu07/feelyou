package com.feelyou.service;

import java.io.File;

import com.feelyou.R;
import com.feelyou.ui.MainActivity;
import com.feelyou.util.Global;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class UpdateService extends Service {
	// 标题
	private int titleId = 0;
	// 文件存储
	private File updateDir = null;
	private File updateFile = null;
	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		// 创建文件
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(), Global.downloadDir);
			updateFile = new File(updateDir.getPath(), getResources().getString(titleId) + ".apk");
		}
		
		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();
		
		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		// 设置通知栏显示内容
		updateNotification.icon = R.drawable.application;
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this, "飞友", "0%", updatePendingIntent);
		// 发出通知
		updateNotificationManager.notify(0, updateNotification);
		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
//		new Thread(new UpdateRunnable()).start(); // 这个是下载的重点，是下载的过程
		
		super.onStart(intent, startId);
	}

}
