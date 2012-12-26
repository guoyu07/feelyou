package com.feelyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.net.MyNetwork;

public class SplashActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		
//		TextView tvProgressText = (TextView) findViewById(R.id.progresstext);
		
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
			TextView tvVersionName = (TextView)findViewById(R.id.tvVersionName);
			tvVersionName.setText("Version：" + pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
//		tvProgressText.setText("检查网络状态....");
//		tvProgressText.setText("检查程序更新....");
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
		}, 2000);
	}
	
	
//	   public void simpleDownload() {
//	        String new_version_url = "http://wap.feelyou.me/album/soft/FeelYou.apk";
//	        Uri uri = Uri.parse(new_version_url);
//	        Intent web = new Intent(Intent.ACTION_VIEW, uri);
//	        startActivity(web);
//	    }
//	    
//	    public void simpleAutoUpdate() {
//			StringBuilder sb = new StringBuilder();
//			sb.append("http://wap.feelyou.me/api/version.php?versioncode=");
//			sb.append(String.valueOf(getAppVersionCode(this)));
//		
//			new MyNetwork(handler, sb.toString()).start();
//	    }
}
