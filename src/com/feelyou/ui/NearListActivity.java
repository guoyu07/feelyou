package com.feelyou.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.feelyou.R;
import com.feelyou.manager.RecordManager;
import com.feelyou.net.MSG;
import com.feelyou.net.MyNetwork2;
import com.feelyou.net.Network;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;
import com.feelyou.vo.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NearListActivity extends Activity {
	
	BMapManager mBMapMan = null; // �ٶ�mapApi�Ĺ�����
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0"; 
	
	// ������λ�¼��ڲ���
	LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {
				String strLoc = String.format("����ǰ��λ�ã����ȣ�%f γ�ȣ�%f", 
						location.getLongitude(), location.getLatitude());
				Toast.makeText(NearListActivity.this, strLoc, Toast.LENGTH_LONG).show();
				double lat = location.getLatitude();
				double lon = location.getLongitude();
				if (lat != 0 && lon != 0) {
					mBMapMan.stop(); // ֹͣ��λ
					
					String ggid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO);
//					http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
					StringBuilder sb = new StringBuilder();
					sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
					sb.append(ggid);
					sb.append("&longitude=").append(location.getLongitude()).append("&latitude=").append(location.getLatitude());
					sb.append("&checkcode=").append(SystemUtil.toMd5(ggid + location.getLongitude() + location.getLatitude() + "fy602"));
					Log.i("LBS", sb.toString());  // ��ӡ����url
					new MyNetwork2(handler, sb.toString()).start();
					dialog = DialogUtil.showProgressDialog(NearListActivity.this, "���ڷ�������....");
					
				}
			}  // end location != null
			
		}
	};
	// �����ڲ���
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			Toast.makeText(NearListActivity.this, "���������������", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(NearListActivity.this, "�ٶȵ�ͼkeyû����ȷ��Ȩ��", Toast.LENGTH_LONG).show();
			}
		}
	}  // end MKGeneralListener
	
	private ListView listView;
	Dialog dialog;  // ������
	UserInfo userInfo = null;  // ѡ�е���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_list);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
//		String user_uid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_UID);
//		if (user_uid == null || "".equals(user_uid)) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("��ʾ").setMessage("�����µ�¼���ѣ�")
//			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent it = new Intent(NearListActivity.this, SettingAccountActivity.class);
//					startActivity(it);
//					NearListActivity.this.finish();
//				}
//			});
//			builder.create().show();
//		}
		
		listView = (ListView) findViewById(R.id.listview);
		registerForContextMenu(listView);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				userInfo = ((NearListAdapter)parent.getAdapter()).mData.get(position);
				return false;
			}
			
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(NearListActivity.this, UserInfoActivity.class);
				userInfo = ((NearListAdapter)parent.getAdapter()).mData.get(position);
				intent.putExtra("userInfo", userInfo);
				NearListActivity.this.startActivity(intent);
			}
		});
		
		// baidu
		mBMapMan = new BMapManager(this);
		mBMapMan.init(mStrKey, new MyGeneralListener());
		mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);		
	}
	
	@Override
	protected void onResume() {
		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		mBMapMan.start();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mBMapMan.stop();
		super.onPause();
	}
	
	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("����");
		menu.setHeaderIcon(R.drawable.application);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.lbs_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.lbs_dial:
			// ����绰
			MSG msg = new MSG();
			msg.setType(2);
			msg.setVersion(0);
			msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
			msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
			msg.appendByte(SystemUtil.encoding("")); //dst="0"
			msg.appendChar(SystemUtil.encoding(userInfo.getUtel()));  // �˺��������ֶ�Ϊword
			new Network(handler2, msg.getMessage()).start();
			dialog = DialogUtil.showProgressDialog(NearListActivity.this, "���ڷ�������....");
			
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void loadData() {
/*		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < 50; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ggid", "123" + i);
			
			if (i % 2 == 0) {
				map.put("icon", R.drawable.female);
			} else {
				map.put("icon", R.drawable.male);
			}
			map.put("name", "�θ���" + i);
			map.put("distance", "100������" + i);
			map.put("signature", "����ǩ��" + i);
			list.add(map);
		}*/
		
//		List<UserInfo> list = new ArrayList<UserInfo>();
/*		UserInfo userInfo;
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) { 
				userInfo = new UserInfo("ggid" + i, "tel" + i, "�θ���" + i, "0", "100������" + i, "����ǩ��" + i);
			} else {
				userInfo = new UserInfo("ggid" + i, "tel" + i, "�θ���" + i, "1", "100������" + i, "����ǩ��" + i);
			}
			list.add(userInfo);
		}*/
		Gson gson = new Gson();
		String strJson = "[{'udistance':'100������0','uid':'ggid0','uname':'�θ���0','usex':'0','usignature':'����ǩ��0','utel':'tel0'},{'udistance':'100������1','uid':'ggid1','uname':'�θ���1','usex':'1','usignature':'����ǩ��1','utel':'tel1'},{'udistance':'100������2','uid':'ggid2','uname':'�θ���2','usex':'0','usignature':'����ǩ��2','utel':'tel2'},{'udistance':'100������3','uid':'ggid3','uname':'�θ���3','usex':'1','usignature':'����ǩ��3','utel':'tel3'},{'udistance':'100������4','uid':'ggid4','uname':'�θ���4','usex':'0','usignature':'����ǩ��4','utel':'tel4'},{'udistance':'100������5','uid':'ggid5','uname':'�θ���5','usex':'1','usignature':'����ǩ��5','utel':'tel5'},{'udistance':'100������6','uid':'ggid6','uname':'�θ���6','usex':'0','usignature':'����ǩ��6','utel':'tel6'},{'udistance':'100������7','uid':'ggid7','uname':'�θ���7','usex':'1','usignature':'����ǩ��7','utel':'tel7'},{'udistance':'100������8','uid':'ggid8','uname':'�θ���8','usex':'0','usignature':'����ǩ��8','utel':'tel8'},{'udistance':'100������9','uid':'ggid9','uname':'�θ���9','usex':'1','usignature':'����ǩ��9','utel':'tel9'}]";
		List<UserInfo> list = gson.fromJson(strJson, 
				new TypeToken<List<UserInfo>>(){}.getType());
		Log.i("Hello", strJson);
		NearListAdapter adapter = new NearListAdapter(this, list);
		listView.setAdapter(adapter);
	}
	// �����ڲ������
	Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			String info = "";
			String ret = (String)msg.obj;
//			Log.i("LBS", ret);  // ��ӡ���ص�json����
			if (ret.length() > 1) {
				info = "������";
				Gson gson = new Gson();
				List<UserInfo> list = gson.fromJson(ret, new TypeToken<List<UserInfo>>(){}.getType());
				NearListAdapter adapter = new NearListAdapter(NearListActivity.this, list);
				listView.setAdapter(adapter);
			} else {  // ������
				info = "��ʱû���ҵ�����Ҳʹ�øù��ܵ��ˣ����Ժ��ٳ��Բ鿴��" + ret;
//				switch (Integer.valueOf(ret)) {
//				case 0:
//					info = "��������0";
//					break;
//				case 1:
//					info = "��������1";
//					break;
//				case 2:
//					info = "��������2";
//					break;
//				default:
//					info = "��ʱû���ҵ�����Ҳʹ�øù��ܵ��ˣ����Ժ��ٳ��Բ鿴��";
//					break;
//				}
				DialogUtil.showAlertDialog(NearListActivity.this, "��ʾ", info);
			}
			super.handleMessage(msg);
		}  // end handleMessage
	};
	
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(NearListActivity.this, "��ʾ", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

}
