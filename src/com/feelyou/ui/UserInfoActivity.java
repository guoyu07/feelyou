package com.feelyou.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.feelyou.vo.UserInfo;

public class UserInfoActivity extends Activity {
	private static final int MENU_BACK_CLIENT = Menu.FIRST;
	private WebView browser;
	private StringBuilder url = new StringBuilder();
	private String user_uid = "";
	Dialog progress = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS); // 设置Activity显示进度条
		setContentView(R.layout.wap);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		user_uid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_UID);
		if (user_uid == null || "".equals(user_uid)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示").setMessage("请重新登录飞友！")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent it = new Intent(UserInfoActivity.this, SettingAccountActivity.class);
					startActivity(it);
					UserInfoActivity.this.finish();
				}
			});
			builder.create().show();
		}
		String ggid = ((UserInfo)getIntent().getExtras().get("userInfo")).getUid();
		//http://wap.feelyou.me/user_show.php?uid=270f1b33579c840e9ae8598e5441aafe&ggid=290446
		url.append("http://wap.feelyou.me/user_show.php?uid=")
		.append(user_uid).append("&ggid=").append(ggid);
		
		browser = (WebView)findViewById(R.id.webkit);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.getSettings().setSupportZoom(true);
		browser.getSettings().setBuiltInZoomControls(true);
		browser.loadUrl(url.toString());
		progress = DialogUtil.showProgressDialog(this, "正在加载中....");
		browser.requestFocus();
		
		browser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// 重写方法，设置Activity的进度条
				UserInfoActivity.this.setProgress(newProgress * 100);
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
