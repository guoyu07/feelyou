package com.feelyou.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.feelyou.R;
import com.feelyou.manager.RecordManager;
import com.feelyou.net.MSG;
import com.feelyou.net.Network;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.PhoneUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

public class SelectCallTypeActivity extends Activity {
	
	int index = 0;
	ListView lvCallTypes;
	Button btnOK;
	Dialog dialog = null;
	String phone = "";
	
	@Override
	protected void onStart() {
		phone = getIntent().getStringExtra("phone");
		super.onStart();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_call_type);
		phone = getIntent().getStringExtra("phone");
		lvCallTypes = (ListView)findViewById(R.id.list_calltype);
		ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.call_types, android.R.layout.simple_list_item_single_choice);
		lvCallTypes.setAdapter(arrayAdapter);
		lvCallTypes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvCallTypes.setItemChecked(0, true);
		lvCallTypes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				index = arg2;
			}
			
		});
		btnOK = (Button)findViewById(R.id.btnOk);
		btnOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				phone = phone.replace("-", "");
				System.out.println(phone);
				if (phone.startsWith("+86")) {
					phone = phone.substring(3);
				}
				switch (index) {
				case 0:
					MSG msg = new MSG();
					msg.setType(2);
					msg.setVersion(0);
					msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
					msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
					msg.appendByte(SystemUtil.encoding("")); //dst="0"
					msg.appendChar(SystemUtil.encoding(phone));  // �˺��������ֶ�Ϊword
					new Network(handler, msg.getMessage()).start();
					dialog = DialogUtil.showProgressDialog(SelectCallTypeActivity.this, "���ڷ�������....");		
					break;
				case 1:
					TelephonyManager telephonyManager;
					telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					try {
						PhoneUtil.getITelephony(telephonyManager).call(phone);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SelectCallTypeActivity.this.finish();
					break;
				}
			}
		});
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			AlertDialog.Builder builder = new AlertDialog.Builder(SelectCallTypeActivity.this);
			builder.setMessage(bundle.getString("msg")).setTitle("��ʾ");
			builder.setPositiveButton(R.string.confrim, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int arg1) {
					new RecordManager(SelectCallTypeActivity.this).insertRecord(phone);
					SelectCallTypeActivity.this.finish();
				}
			});
			builder.create().show();
//			DialogUtil.showAlertDialog(SelectCallTypeActivity.this, "��ʾ", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

}
