package com.feelyou.ui;

import android.app.Application;

public class FeelyouApp extends Application {
	
	static FeelyouApp mFeelyouApp;
	// �ٶ�MapApi�Ĺ�����
//	BMapManager mBMapMan = null;
	// ��ȨKey
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0";
	boolean m_bKeyRight = true;
	
//	static class MyGeneralListener implements MKGeneralListener {
//
//		@Override
//		public void onGetNetworkState(int iError) {
//			// TODO Auto-generated method stub
//			Toast.makeText(FeelyouApp.mFeelyouApp.getApplicationContext(), 
//					"���������������", Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onGetPermissionState(int iError) {
//			// TODO Auto-generated method stub
//			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
//				// ��ȨKey����
//				Toast.makeText(FeelyouApp.mFeelyouApp.getApplicationContext(), 
//						"�ٶȵ�ͼKeyû����ȷ��Ȩ��", Toast.LENGTH_LONG).show();
//				FeelyouApp.mFeelyouApp.m_bKeyRight = false;
//			}
//		}
//	} // end MKGeneralListener
	
	@Override
	public void onCreate() {
		mFeelyouApp = this;
//		mBMapMan = new BMapManager(this);
//		mBMapMan.init(this.mStrKey, new MyGeneralListener());
		super.onCreate();
	}
	
	// ����������app�˳�֮ǰ����mapapi��destroy()�����������ظ���ʼ��������ʱ������
	@Override
	public void onTerminate() {
//		if (mBMapMan != null) {
//			mBMapMan.destroy();
//			mBMapMan = null;
//		}
		super.onTerminate();
	}
}
