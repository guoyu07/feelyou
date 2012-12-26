package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

public class WapActivity extends Activity {
	private static final int MENU_BACK_CLIENT = Menu.FIRST;
	private WebView browser;
	private String URL2 = "http://wap.feelyou.me/check_user.php?";
	private StringBuilder md5 = new StringBuilder();
	private String temp = "";
	private String user_no = "";
	private String user_pass = "";
	Dialog progress = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS); // 设置Activity显示进度条
		setContentView(R.layout.wap);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		user_no = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO);
		user_pass = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS);
		
		md5.append(user_no);
		md5.append(user_pass);
		md5.append("fy602");
		temp = "fid=" +  user_no + "&fpwd=" + user_pass + "&checkcode=" + SystemUtil.toMd5(md5.toString());
		
		URL2 = URL2 + temp;
		browser = (WebView)findViewById(R.id.webkit);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.getSettings().setSupportZoom(true);
		browser.getSettings().setBuiltInZoomControls(true);
		browser.loadUrl(URL2);
		progress = DialogUtil.showProgressDialog(this, "正在加载中");
		browser.requestFocus();
		
		browser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// 重写方法，设置Activity的进度条
				WapActivity.this.setProgress(newProgress * 100);
			}
		});
		
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true; 
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				progress.dismiss();
				super.onPageFinished(view, url);
			}
		});	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (browser.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			browser.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_BACK_CLIENT, 0, "返回客户端").setIcon(R.drawable.ic_menu_home);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_BACK_CLIENT:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
