package com.feelyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feelyou.R;

/**
 * œ¬‘ÿ∆µµ¿
 * @author Administrator
 *
 */
public class DownActivity extends Activity implements OnItemClickListener {
	private TextView tvTitle;
	private String[] arrayOfDownName;
	private String[] arrayOfDownValue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.down);
		
		tvTitle = (TextView)findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());
		ListView listView = (ListView)findViewById(R.id.down_list);
		arrayOfDownName = getResources().getStringArray(R.array.down_name_list);
		arrayOfDownValue = getResources().getStringArray(R.array.down_value_list);
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayOfDownName);
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemId, long arg3) {
		Uri uri = Uri.parse(this.arrayOfDownValue[itemId]);
		Intent intent = new Intent("android.intent.action.VIEW", uri);
		startActivity(intent);
	}

}
