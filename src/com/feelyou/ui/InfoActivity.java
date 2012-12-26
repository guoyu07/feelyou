package com.feelyou.ui;

import com.feelyou.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * ¿Í»§°ïÖú
 * @author Administrator
 *
 */
public class InfoActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
	}
	

}
