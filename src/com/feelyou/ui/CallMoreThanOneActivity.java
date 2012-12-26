package com.feelyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.feelyou.R;

public class CallMoreThanOneActivity extends Activity {
	private MenuInflater menuInflater;
	private TextView tvTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_more);
		tvTitle = (TextView)findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());
		menuInflater = new MenuInflater(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuInflater.inflate(R.menu.callmore_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_call:
			break;
		case R.id.menu_main:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_contacts:
			break;
		case R.id.menu_back:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
