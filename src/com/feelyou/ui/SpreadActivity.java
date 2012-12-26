package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feelyou.R;

/**
 * 推荐好友
 * @author Administrator
 *
 */
public class SpreadActivity extends Activity implements OnClickListener {
	
	private static final int MENU_CONTACTS = Menu.FIRST;
	
//	private static final int DIALOG_CONTACTS = 0;
	
//	private static final String REGEX_PHONE = "^(13|14|15|18)\\d{9}$";
	Dialog  currentDialog;
	ListView lvContacts;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spread);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		findViewById(R.id.spread_btn_confirm).setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, MENU_CONTACTS, 0, R.string.m_contacts).setIcon(R.drawable.ic_menu_contact);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CONTACTS:
			this.showDialog(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			;
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(View v) {
		String strContent = ((EditText)findViewById(R.id.spread_content)).getText().toString().trim();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, strContent);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getTitle()));
		
//		Pattern pattern = Pattern.compile(REGEX_PHONE);
//		String strPhones = ((EditText)findViewById(R.id.spread_et_phone)).getText().toString().trim();
//		if (strPhones.equals("")) {
//			DialogUtil.showOperateFailureDialog(this, "号码不能为空");
//			return;
//		}
//		String[] arrayOfPhone = strPhones.split("#");
//		SmsManager smsManager = SmsManager.getDefault();
//		for (String strPhone: arrayOfPhone) {
//			smsManager.sendTextMessage(strPhone, null, getResources().getString(R.string.spread_content), null, null);
//		}
//		DialogUtil.showAlertDialog(this, "提示", "短信发送成功");
	}
}
