package com.feelyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.feelyou.R;

/**
 * ≥‰÷µ∞Ô÷˙ΩÁ√Ê
 * @author Administrator
 *
 */
public class ChargeHelpActivity extends Activity {
	private TextView tvTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charge_help);
		
		tvTitle = (TextView)findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());
	}

}
