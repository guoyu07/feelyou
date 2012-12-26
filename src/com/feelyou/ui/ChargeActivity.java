package com.feelyou.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.net.MSG;
import com.feelyou.net.Network;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

/**
 * 马上充值
 * 
 * @author Administrator
 * 
 */
public class ChargeActivity extends Activity implements OnClickListener {
	private TextView tvTitle;
	private TextView tvCount;
	private String[] arrayOfChargeTypeId;
	private String chargeTypeId = "";
	private int[] chargeIcon;
	private String[] chargeArray ;
	private Dialog dialog;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charge);

		tvTitle = (TextView) findViewById(R.id.app_version);
		tvTitle.setText(this.getTitle());

		tvCount = (TextView) findViewById(R.id.charge_tv_account);
		tvCount.setText(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));

		chargeIcon = new int[]{R.drawable.fill_motion, R.drawable.fill_unicom, R.drawable.ic_menu_back};
		chargeArray = getResources().getStringArray(R.array.charge_type_list);
		arrayOfChargeTypeId = getResources().getStringArray(
				R.array.charge_type_ID);
		chargeTypeId = arrayOfChargeTypeId[0];
		ArrayAdapter arrayAdapterOfChargeTypeList = ArrayAdapter
				.createFromResource(this, R.array.charge_type_list,
						android.R.layout.simple_spinner_item);
		arrayAdapterOfChargeTypeList
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		ArrayAdapter arrayAdapterOfChargeTypeList = new ArrayAdapter(this, android.R.layout.simple_spinner_item, R.array.charge_type_list) {
//			@Override
//			public View getDropDownView(int position, View convertView,
//					ViewGroup parent) {
//				LinearLayout line = new LinearLayout(context);
//				line.setOrientation(0);
//				ImageView image = new ImageView(context);
//				image.setImageResource(chargeIcon[position]);
//				TextView text = new TextView(context);
//				text.setText(chargeArray[position]);
//				text.setTextSize(20);
//				text.setTextColor(Color.RED);
//				
//				line.addView(image);
//				line.addView(text);
//				parent.addView(line);
//				return line;
//			}
//			
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				LinearLayout line = new LinearLayout(context);
//				line.setOrientation(0);
//				ImageView image = new ImageView(context);
//				image.setImageResource(chargeIcon[position]);
//				TextView text = new TextView(context);
//				text.setText(chargeArray[position]);
//				text.setTextSize(20);
//				text.setTextColor(Color.RED);
//				
//				line.addView(image);
//				line.addView(text);
//				return line;
//			}
//		};
		
		Spinner spinner = (Spinner) findViewById(R.id.charge_spinner_chargeType);
		spinner.setAdapter(arrayAdapterOfChargeTypeList);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int itemId,
					long arg3) {
				chargeTypeId = arrayOfChargeTypeId[itemId];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		findViewById(R.id.charge_btn_help).setOnClickListener(this);
		findViewById(R.id.charge_btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.charge_btn_help:
			// 显示充值帮助界面
			Intent intent = new Intent(this, ChargeHelpActivity.class);
			startActivity(intent);
			break;
		case R.id.charge_btn_ok:
			// 充值
			String etCardId = ((EditText) findViewById(R.id.charge_et_cardId))
					.getText().toString().trim();
			String etCardPwd = ((EditText) findViewById(R.id.charge_et_cardPwd))
					.getText().toString().trim();
			String etMoney = ((EditText) findViewById(R.id.charge_et_money))
					.getText().toString().trim();
			if ((etCardId.equals("")) || (etCardPwd.equals(""))
					|| (etMoney.equals(""))) {
				DialogUtil.showOperateFailureDialog(this, "不能为空");
				return;
			}
			MSG msg = new MSG();
			msg.setType(26);
			msg.setVersion(1);
			msg.appendByte(SystemUtil.encoding(etCardId));
			msg.appendByte(SystemUtil.encoding(etCardPwd));
			msg.appendByte(SystemUtil.encoding(etMoney));
			msg.appendByte(SystemUtil.encoding(chargeTypeId));
			msg.appendByte(SystemUtil.encoding(SkyMeetingUtil
					.getPreference(SkyMeetingUtil.USER_NO)));
			new Network(handler, msg.getMessage()).start();
			dialog = DialogUtil.showProgressDialog(ChargeActivity.this,
					"正在发送数据....");
			break;
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(ChargeActivity.this, "提示",
					bundle.getString("msg"));
			super.handleMessage(msg);
		}

	};

}
