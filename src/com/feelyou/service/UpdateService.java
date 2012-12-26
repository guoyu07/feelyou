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
	// ����
	private int titleId = 0;
	// �ļ��洢
	private File updateDir = null;
	private File updateFile = null;
	// ֪ͨ��
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// ֪ͨ����תIntent
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
		// ��ȡ��ֵ
		titleId = intent.getIntExtra("titleId", 0);
		// �����ļ�
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(), Global.downloadDir);
			updateFile = new File(updateDir.getPath(), getResources().getString(titleId) + ".apk");
		}
		
		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();
		
		// �������ع����У����֪ͨ�����ص�������
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		// ����֪ͨ����ʾ����
		updateNotification.icon = R.drawable.application;
		updateNotification.tickerText = "��ʼ����";
		updateNotification.setLatestEventInfo(this, "����", "0%", updatePendingIntent);
		// ����֪ͨ
		updateNotificationManager.notify(0, updateNotification);
		// ����һ���µ��߳����أ����ʹ��Serviceͬ�����أ��ᵼ��ANR���⣬Service����Ҳ������
//		new Thread(new UpdateRunnable()).start(); // ��������ص��ص㣬�����صĹ���
		
		super.onStart(intent, startId);
	}

}
