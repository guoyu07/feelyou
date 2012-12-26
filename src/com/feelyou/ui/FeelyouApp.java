package com.feelyou.ui;

import android.app.Application;

public class FeelyouApp extends Application {
	
	static FeelyouApp mFeelyouApp;
	// 百度MapApi的管理类
//	BMapManager mBMapMan = null;
	// 授权Key
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0";
	boolean m_bKeyRight = true;
	
//	static class MyGeneralListener implements MKGeneralListener {
//
//		@Override
//		public void onGetNetworkState(int iError) {
//			// TODO Auto-generated method stub
//			Toast.makeText(FeelyouApp.mFeelyouApp.getApplicationContext(), 
//					"您的网络出错啦！", Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onGetPermissionState(int iError) {
//			// TODO Auto-generated method stub
//			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
//				// 授权Key错误
//				Toast.makeText(FeelyouApp.mFeelyouApp.getApplicationContext(), 
//						"百度地图Key没有正确授权！", Toast.LENGTH_LONG).show();
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
	
	// 建议在您的app退出之前调用mapapi的destroy()函数，避免重复初始化带来的时间消耗
	@Override
	public void onTerminate() {
//		if (mBMapMan != null) {
//			mBMapMan.destroy();
//			mBMapMan = null;
//		}
		super.onTerminate();
	}
}
