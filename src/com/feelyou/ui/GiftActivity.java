package com.feelyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.feelyou.R;

/**
 * ÓÅ»Ý×¨Çø
 * @author Administrator
 *
 */
public class GiftActivity extends Activity {
	
	private WebView wvGift ;
	private String URL = "http://wap.feelyou.me/VIP/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gift);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		wvGift = (WebView)findViewById(R.id.wvGift);
		wvGift.getSettings().setJavaScriptEnabled(true);
		wvGift.getSettings().setSupportZoom(true);
		wvGift.loadUrl(URL);
		wvGift.requestFocus();
	}
}
