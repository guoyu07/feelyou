package com.feelyou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.manager.RecordManager;
import com.feelyou.net.MSG;
import com.feelyou.net.MyNetwork;
import com.feelyou.net.Network;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

/**
 * 呼叫记录
 * @author Administrator
 *
 */
public class RecordActivity extends Activity {
	
//	private static final int 
	int currentId = -1;
	String currentRemark = "";
	List<Integer> ids;
	ListView lvRecords = null;
	RecordManager manager;
	ArrayList<String> phoneList;
	TextView tvRemark;
	Dialog dialog = null;
	String phone = "";
	
	public RecordActivity() {
		ids = new ArrayList<Integer>();
		phoneList = new ArrayList<String>();
	}
	
	public void fillListView() {
		this.currentId = -1;
		this.currentRemark = "";
		this.ids.clear();
		this.phoneList.clear();
		HashMap<Integer, String> hashMap = manager.getRecords();
		for (Iterator<Integer> iterator = hashMap.keySet().iterator(); iterator.hasNext();) {
			int i = iterator.next().intValue();
			ids.add(i);
			phoneList.add(hashMap.get(i));
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, 
				android.R.id.text1, phoneList);
		lvRecords.setAdapter(arrayAdapter);
		lvRecords.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		manager = new RecordManager(this);
		lvRecords = (ListView)findViewById(R.id.record_listview);
		registerForContextMenu(lvRecords);
		TextView tvEmpty = new TextView(this);
		tvEmpty.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT));
		tvEmpty.setText("没有通话记录信息！");
		
		//
		tvEmpty.setVisibility(View.GONE);
		((ViewGroup)lvRecords.getParent()).addView(tvEmpty);
		//
		lvRecords.setEmptyView(tvEmpty);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		fillListView();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_call:
			if (this.lvRecords.getCheckedItemPositions().size() < 1) {
				DialogUtil.showOperateFailureDialog(this, "请选择电话号码");
				return super.onOptionsItemSelected(item);
			}
			phone = getSelectedPhones();
			if (phone.contains("#")) {
				DialogUtil.showOperateFailureDialog(this, "只能选择一个号码");
				return super.onOptionsItemSelected(item);
			} else {
//				http://wap.feelyou.me/api/javacall.php?ggid=342323&telno=1355445772&checkcode=1
				/*StringBuilder sb = new StringBuilder();
				sb.append("http://wap.feelyou.me/api/javacall.php?telno=");
				sb.append(phone);
				sb.append("&ggid=").append(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
				sb.append("&checkcode=");
				sb.append(SystemUtil.toMd5(phone + SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO) + "fy602").toUpperCase());
				
				new MyNetwork(handler, sb.toString()).start();*/
				
				MSG msg = new MSG();
				msg.setType(2);
				msg.setVersion(0);
				msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
				msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
				msg.appendByte(SystemUtil.encoding("")); //dst="0"
				msg.appendChar(SystemUtil.encoding(phone));  // 此函数长度字段为word
				new Network(handler, msg.getMessage()).start();
				dialog = DialogUtil.showProgressDialog(this, "正在发送数据....");
			}
			break;
		case R.id.menu_del:
			if (this.lvRecords.getCheckedItemPositions().size() < 1) {
				DialogUtil.showOperateFailureDialog(this, "请选择电话号码");
				return super.onOptionsItemSelected(item);
			}
			manager.deleteByIds(getSelectedIdString());
			fillListView();
			break;
		case R.id.menu_clear:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("真的要清空所有通话记录吗？").setTitle("提示");
			builder.setPositiveButton(R.string.confrim, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int arg1) {
					manager.clear();
					fillListView();
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = new MenuInflater(this);
		menuInflater.inflate(R.menu.calllog_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public String getSelectedIdString() {
		StringBuilder sb = new StringBuilder();
		SparseBooleanArray sparseBooleanArray = this.lvRecords.getCheckedItemPositions();
		for (int i = 0; i < sparseBooleanArray.size(); i++) {
			String id = String.valueOf(this.ids.get(sparseBooleanArray.keyAt(i)));
			sb.append(id).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public String getSelectedPhones() {
		String ids = getSelectedIdString();
		return manager.getPhonesByIds(ids);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("详细信息");
		menu.add(0, 1, 0, "详情");
		menu.add(0, 2, 1, "备注");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			/*dialog.dismiss();
			String info = "";
			switch (msg.what) {
			case 0:
				info = "呼叫成功";
				break;
			case 1:
				info = "余额低于2飞币";
				break;
			case 2:
				info = "未充值用户只能打60分钟";
				break;
			case 3:
				info = "1分钟内只能发起1次呼叫";  // "呼叫失败";
				break;
			case 4:
				info = "验证失败";
				break;
			default:
				info = "其它错误";
				break;
		}
		DialogUtil.showAlertDialog(RecordActivity.this, "提示", info);
		new RecordManager(RecordActivity.this).insertRecord(phone);
		super.handleMessage(msg);*/
			
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(RecordActivity.this, "提示", bundle.getString("msg"));
			new RecordManager(RecordActivity.this).insertRecord(phone);
			super.handleMessage(msg);
			
		}
	};

}
