package com.feelyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feelyou.R;


/**
 * ¿ªÊ¼ºô½Ð
 * @author Administrator
 *
 */
public class CallActivity extends Activity {
	private TextView tvTitle;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call);
		tvTitle = (TextView)findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());
		
		ListView callTypeListView = (ListView)findViewById(R.id.call_listview_selecttype);
		ArrayAdapter callTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.call_main_type, android.R.layout.simple_list_item_1);
		callTypeListView.setAdapter(callTypeArrayAdapter);
		callTypeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int itemId,
					long arg3) {
				Intent intent = null;
				switch (itemId) {
				case 0:
					intent = new Intent(CallActivity.this, CallOneActivity.class);
					break;
				case 1:
					intent = new Intent(CallActivity.this, CallMoreThanOneActivity.class);
					break;
				}
				if (intent != null) {
					startActivity(intent);
				}
				
			}
			
		});
		
	}

}
