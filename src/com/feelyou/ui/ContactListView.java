package com.feelyou.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feelyou.R;

public class ContactListView extends Activity {

	ListView listView;
	AutoCompleteTextView textView;
	TextView emptytextView;
	protected CursorAdapter mCursorAdapter;
	protected Cursor mCursor = null;
	protected ContactAdapter ca;
	ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
	// ѡ�е��ֻ���
	protected String numberStr = "";
	protected String[] autoContact = null;
	protected String[] wNumStr = null;
	private static final int DIALOG_KEY = 0;
	
	Button btn_add = null;
	Button btn_back =null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view);

		listView = (ListView) findViewById(R.id.list);
		textView = (AutoCompleteTextView) findViewById(R.id.edit);
		emptytextView = (TextView) findViewById(R.id.empty);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_back = (Button) findViewById(R.id.btn_back);

		emptytextView.setVisibility(View.GONE);

		// ��ȡǰҳ��ֵ,������ֶ���д���ֻ�����ͨѶ¼��,��Ĭ�Ϲ���
		// ����ֶ���д���ֻ��Ų���ͨѶ¼��,���ڻش�ֵ��ʱ�����ȥ(�������ֻ���ʽ��ȥ��)
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String wNumberStr = bundle.getString("wNumberStr").replace("��", ",");
		wNumStr = wNumberStr.split(",");

		// ��������
		new GetContactTask().execute("");

		// �б����¼�����
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {
				LinearLayout ll = (LinearLayout) view;
				CheckBox cb = (CheckBox) ll.getChildAt(0).findViewById(
						R.id.check);
				// ѡ�������ѡ���ַ�����,ȡ������ַ�����ɾ��
				if (cb.isChecked()) {
					cb.setChecked(false);
					numberStr = numberStr
							.replace(
									","
											+ contactList.get(position)
													.getUserNumber(), "");
					contactList.get(position).isChecked = false;
				} else {
					cb.setChecked(true);
					numberStr += ","
							+ contactList.get(position).getUserNumber();
					contactList.get(position).isChecked = true;
				}
				ContactListView.this.onBtnTextChange();  // ѡ�����ϵ�˱仯ʱ��֪ͨUI�仯
			}
		});
		
		btn_add.setOnClickListener(btnClick);
		btn_back.setOnClickListener(btnClick);
	}

	public void onBtnTextChange() {
		System.out.println("numberStr = " + numberStr);
		btn_add.setText("ȷ��(" + (numberStr.split(",").length - 1) + ")");
	}

	// ��ť����
	private OnClickListener btnClick = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add:
				// ���ȷ�Ͻ�ѡ�е��ֻ��Żش�
				Log.i("eoe", numberStr);
				Intent intent = getIntent();
				Bundle bundle = new Bundle();
				String bundleStr = numberStr;
				if (bundleStr != "") {
					bundleStr = bundleStr.substring(1);
				}
				bundle.putString("numberStr", bundleStr);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
				break;

			case R.id.btn_back:
				finish();
				break;
			}
		}
	};

	// ����AUTOTEXT���ݱ仯,�����ַ���ѡ����ϵ��[��ϵ��(�ֻ���)]�������,���ù���,������ѡ���ֻ�����
	private TextWatcher mTextWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int before,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int after) {

			String autoText = s.toString();
			if (autoText.length() >= 13) {
				Pattern pt = Pattern.compile("\\(([1][3,5,8]+\\d{9})\\)");
				Matcher mc = pt.matcher(autoText);
				if (mc.find()) {
					String sNumber = mc.group(1);
					DealWithAutoComplete(contactList, sNumber);
					// ˢ���б�
					Toast.makeText(ContactListView.this, "��ѡ���������Ľ��!", 1000)
							.show();
					ca.setItemList(contactList);
					ContactListView.this.onBtnTextChange();  // =====
					ca.notifyDataSetChanged();
				}
			}
		}

		public void afterTextChanged(Editable s) {
		}

	};

	// ��ȡͨѶ¼����
	private class GetContactTask extends AsyncTask<String, String, String> {
		public String doInBackground(String... params) {
			/*
			 * try{ Thread.sleep(4000); } catch(Exception e){}
			 */
			// �ӱ����ֻ��л�ȡ
			GetLocalContact();
			// ��SIM���л�ȡ
			GetSimContact("content://icc/adn");
			// �����е��ֻ���SIM����ϵ�������·��...���Զ�ȡ��(ÿ����֤�Ƿ��Ѵ���)
			GetSimContact("content://sim/adn");
			return "";
		}

		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_KEY);
		}

		@Override
		public void onPostExecute(String Re) {
			// ��LISTVIEW
			if (contactList.size() == 0) {
				emptytextView.setVisibility(View.VISIBLE);
			} else {
				// ������ƴ��˳������
				Comparator comp = new Mycomparator();
				Collections.sort(contactList, comp);

				numberStr = GetNotInContactNumber(wNumStr, contactList)
						+ numberStr;
				ca = new ContactAdapter(ContactListView.this, contactList);
				listView.setAdapter(ca);
				listView.setTextFilterEnabled(true);
				// �༭AUTOCOMPLETE����
				autoContact = new String[contactList.size()];
				for (int c = 0; c < contactList.size(); c++) {
					autoContact[c] = contactList.get(c).contactName + "("
							+ contactList.get(c).userNumber + ")";
				}
				// ��AUTOCOMPLETE
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						ContactListView.this,
						android.R.layout.simple_dropdown_item_1line,
						autoContact);
				textView.setAdapter(adapter);
				textView.addTextChangedListener(mTextWatcher);
			}
			ContactListView.this.onBtnTextChange();  // ����UI��ʾ
			removeDialog(DIALOG_KEY);
		}
	}

	// ����"�鿴"�Ի���
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_KEY: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("��ȡͨѶ¼��...���Ժ�");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	// �ӱ�����ȡ��
	private void GetLocalContact() {
		// ��ȡ�ֻ����ֻ���
		Cursor cursor = getContentResolver().query(People.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			ContactInfo cci = new ContactInfo();
			// ȡ����ϵ������
			int nameFieldColumnIndex = cursor.getColumnIndex(People.NAME);
			cci.contactName = cursor.getString(nameFieldColumnIndex);
			// ȡ�õ绰����
			int numberFieldColumnIndex = cursor.getColumnIndex(People.NUMBER);
			cci.userNumber = cursor.getString(numberFieldColumnIndex);
			cci.userNumber = GetNumber(cci.userNumber);
			cci.isChecked = false;
			if (IsUserNumber(cci.userNumber)) {
				if (!IsContain(contactList, cci.userNumber)) {
					if (IsAlreadyCheck(wNumStr, cci.userNumber)) {
						cci.isChecked = true;
						numberStr += "," + cci.userNumber;
					}
					contactList.add(cci);
					// Log.i("eoe", "*********"+cci.userNumber);
				}
			}
		}
		cursor.close();
	}

	// ��SIM����ȡ��
	private void GetSimContact(String add) {
		// ��ȡSIM���ֻ���,�����ֿ���:content://icc/adn��content://sim/adn
		try {
			Intent intent = new Intent();
			intent.setData(Uri.parse(add));
			Uri uri = intent.getData();
			mCursor = getContentResolver().query(uri, null, null, null, null);
			if (mCursor != null) {
				while (mCursor.moveToNext()) {
					ContactInfo sci = new ContactInfo();
					// ȡ����ϵ������
					int nameFieldColumnIndex = mCursor.getColumnIndex("name");
					sci.contactName = mCursor.getString(nameFieldColumnIndex);
					// ȡ�õ绰����
					int numberFieldColumnIndex = mCursor
							.getColumnIndex("number");
					sci.userNumber = mCursor.getString(numberFieldColumnIndex);

					sci.userNumber = GetNumber(sci.userNumber);
					sci.isChecked = false;

					if (IsUserNumber(sci.userNumber)) {
						if (!IsContain(contactList, sci.userNumber)) {
							if (IsAlreadyCheck(wNumStr, sci.userNumber)) {
								sci.isChecked = true;
								numberStr += "," + sci.userNumber;
							}
							contactList.add(sci);
							// Log.i("eoe", "*********"+sci.userNumber);
						}
					}
				}
				mCursor.close();
			}
		} catch (Exception e) {
			Log.i("eoe", e.toString());
		}
	}

	// �Ƿ���LIST��ֵ
	private boolean IsContain(ArrayList<ContactInfo> list, String un) {
		for (int i = 0; i < list.size(); i++) {
			if (un.equals(list.get(i).userNumber)) {
				return true;
			}
		}
		return false;
	}

	// �����ֻ��ŵ��Ƿ���ͨѶ¼��
	private boolean IsAlreadyCheck(String[] Str, String un) {
		for (int i = 0; i < Str.length; i++) {
			if (un.equals(Str[i])) {
				return true;
			}
		}
		return false;
	}

	// ��ȡ�����ֻ��Ų���ͨѶ¼�еĺ���
	private String GetNotInContactNumber(String[] Str,
			ArrayList<ContactInfo> list) {
		String re = "";
		for (int i = 0; i < Str.length; i++) {
			if (IsUserNumber(Str[i])) {
				for (int l = 0; l < list.size(); l++) {
					if (Str[i].equals(list.get(l).userNumber)) {
						Str[i] = "";
						break;
					}
				}
				if (!Str[i].equals("")) {
					re += "," + Str[i];
				}
			}
		}
		return re;
	}

	// ����������ѡ�е��ֻ���
	private void DealWithAutoComplete(ArrayList<ContactInfo> list, String un) {
		for (int i = 0; i < list.size(); i++) {
			if (un.equals(list.get(i).userNumber)) {
				if (!list.get(i).isChecked) {
					list.get(i).isChecked = true;
					numberStr += "," + un;
					textView.setText("");
				}
			}
		}
	}

	// ͨѶ�簴����ƴ������
	public class Mycomparator implements Comparator {
		public int compare(Object o1, Object o2) {
			ContactInfo c1 = (ContactInfo) o1;
			ContactInfo c2 = (ContactInfo) o2;
			Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
			return cmp.compare(c1.contactName, c2.contactName);
		}

	}

	// �Ƿ�Ϊ�ֻ�����
	public static boolean IsUserNumber(String num) {
		boolean re = false;
		if (num.length() == 11) {
			if (num.startsWith("13")) {
				re = true;
			} else if (num.startsWith("15")) {
				re = true;
			} else if (num.startsWith("18")) {
				re = true;
			}
		}
		return re;
	}

	// ��ԭ11λ�ֻ���
	public static String GetNumber(String num) {
		if (num != null) {
			if (num.startsWith("+86")) {
				num = num.substring(3);
			} else if (num.startsWith("86")) {
				num = num.substring(2);
			}
		} else {
			num = "";
		}
		return num;
	}
}
