package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.manager.RecordManager;
import com.feelyou.net.MyNetwork2;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

public class CallOneActivity extends Activity {
	private TextView tvTitle;
	private MenuInflater mi;
	private Button cal_one_btnBegin, btn_select_phone;
	private EditText call_one_phone_et;
	private String strPhoneNo;
	Dialog dialog;

	// ���ش�ֵʱ�ж���
	private static final int CONTACT_REQUEST_CODE = 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.callone_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_main:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_back:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_one);
		tvTitle = (TextView) findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());

		mi = new MenuInflater(this);
		call_one_phone_et = (EditText) findViewById(R.id.call_one_phone_et);

		// ѡ��ϵͳ�绰��
		btn_select_phone = (Button) findViewById(R.id.btn_select_phone);
		btn_select_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (canSelectPhone) {
				// Intent intent = new Intent(Intent.ACTION_PICK,
				// Contacts.People.CONTENT_URI);
				// CallOneActivity.this.startActivityForResult(intent, 1);
				// }

				// ҳ�洫ֵ����ȡ�ش�ֵ
				Intent intent = new Intent();
				intent.setClass(CallOneActivity.this, ContactListView.class);
				Bundle bundle = new Bundle();
				String wNumberStr = call_one_phone_et.getText().toString();
				bundle.putString("wNumberStr", wNumberStr);
				intent.putExtras(bundle);
				startActivityForResult(intent, CONTACT_REQUEST_CODE);
			}
		});
		// ��绰
		cal_one_btnBegin = (Button) findViewById(R.id.cal_one_btnBegin);
		cal_one_btnBegin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strPhoneNo = call_one_phone_et.getText().toString().trim();
				if (strPhoneNo.equals("")) {
					call_one_phone_et.setError("�ֻ����벻��Ϊ��");
					DialogUtil.showOperateFailureDialog(CallOneActivity.this,
							R.string.call_phone_empty_error1);
					return;
				}

				// http://wap.feelyou.me/api/javacall.php?ggid=342323&telno=1355445772&checkcode=1

				StringBuilder sb = new StringBuilder();
				sb.append("http://wap.feelyou.me/api/javacall.php?telno=");
				sb.append(strPhoneNo);
				sb.append("&ggid=").append(
						SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
				sb.append("&checkcode=");
				sb.append(SystemUtil.toMd5(
						strPhoneNo
								+ SkyMeetingUtil
										.getPreference(SkyMeetingUtil.USER_NO)
								+ "fy602").toUpperCase());
				new MyNetwork2(handler, sb.toString()).start();
				System.out.println(sb.toString());
				dialog = DialogUtil.showProgressDialog(CallOneActivity.this,
						"���ڷ�������....");

				// MSG msg = new MSG();
				// msg.setType(2);
				// msg.setVersion(0);
				// msg.appendByte(SystemUtil.encoding(SkyMeetingUtil
				// .getPreference(SkyMeetingUtil.USER_NO)));
				// msg.appendByte(SystemUtil.encoding(SkyMeetingUtil
				// .getPreference(SkyMeetingUtil.USER_PASS)));
				// msg.appendByte(SystemUtil.encoding("")); // dst="0"
				// msg.appendChar(SystemUtil.encoding(strPhoneNo)); //
				// �˺��������ֶ�Ϊword
				// new Network(handler, msg.getMessage()).start();
				// dialog = DialogUtil.showProgressDialog(CallOneActivity.this,
				// "���ڷ�������....");

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONTACT_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				String numberStr = null;
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					numberStr = bundle.getString("numberStr");
				}
				call_one_phone_et.setText(numberStr);
			}
			break;
		}
		// ѡ��һ����ϵ�˵绰
		// switch (requestCode) {
		// case 1:
		// if (data == null) {
		// return;
		// }
		// Uri uri = data.getData();
		// Cursor cursor = getContentResolver().query(uri, null, null, null,
		// null);
		// cursor.moveToFirst();
		// String number =
		// cursor.getString(cursor.getColumnIndexOrThrow(Phones.NUMBER));
		// call_one_phone_et.setText(number);
		// call_one_phone_et.setSelection(number.length());
		// break;
		//
		// default:
		// break;
		// }
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			dialog.dismiss();
			String info = "";
			System.out.println("msg.what = " + msg.what);  // 200
			System.out.println("msg.obj = " + msg.obj );  // 0
			if (msg.what == MyNetwork2.NET_OK) {
				int retCode = Integer.valueOf((String) msg.obj);
				switch (retCode) {
				case 0:
					info = "���гɹ�";
					break;
				case 1:
					info = "������2�ɱ�";
					break;
				case 2:
					info = "δ��ֵ�û�ֻ�ܴ�60����";
					break;
				case 3:
					info = "1������ֻ�ܷ���1�κ���"; // "����ʧ��";
					break;
				case 4:
					info = "��֤ʧ��";
					break;
				default:
					info = "��������";
					break;
				}
			} else if (msg.what == MyNetwork2.NET_ERROR) {
				info = (String) msg.obj;
			}
			
			DialogUtil.showAlertDialog(CallOneActivity.this, "��ʾ", info);
//			new RecordManager(CallOneActivity.this).insertRecord(strPhoneNo);
//			super.handleMessage(msg);

			// ԭʼͨ��Э��ʹ��
			// dialog.dismiss();
			// Bundle bundle = msg.getData();
			// DialogUtil.showAlertDialog(CallOneActivity.this, "��ʾ",
			// bundle.getString("msg"));
			String[] phoneStrings = strPhoneNo.split(",");
			RecordManager recordManager = new RecordManager(CallOneActivity.this);
			for (int i = 0; i < phoneStrings.length; i++) {
				recordManager.insertRecord(phoneStrings[i]);
			}
			// new RecordManager(CallOneActivity.this).insertRecord(strPhoneNo);
			super.handleMessage(msg);

		}
	};
}
