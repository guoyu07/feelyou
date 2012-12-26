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
	
	BMapManager mBMapMan = null; // 百度mapApi的管理类
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0"; 
	
	// 创建定位事件内部类
	LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {
				String strLoc = String.format("您当前的位置：经度：%f 纬度：%f", 
						location.getLongitude(), location.getLatitude());
				Toast.makeText(NearListActivity.this, strLoc, Toast.LENGTH_LONG).show();
				double lat = location.getLatitude();
				double lon = location.getLongitude();
				if (lat != 0 && lon != 0) {
					mBMapMan.stop(); // 停止定位
					
					String ggid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO);
//					http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
					StringBuilder sb = new StringBuilder();
					sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
					sb.append(ggid);
					sb.append("&longitude=").append(location.getLongitude()).append("&latitude=").append(location.getLatitude());
					sb.append("&checkcode=").append(SystemUtil.toMd5(ggid + location.getLongitude() + location.getLatitude() + "fy602"));
					Log.i("LBS", sb.toString());  // 打印发送url
					new MyNetwork2(handler, sb.toString()).start();
					dialog = DialogUtil.showProgressDialog(NearListActivity.this, "正在发送数据....");
					
				}
			}  // end location != null
			
		}
	};
	// 定义内部类
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			Toast.makeText(NearListActivity.this, "您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(NearListActivity.this, "百度地图key没有正确授权！", Toast.LENGTH_LONG).show();
			}
		}
	}  // end MKGeneralListener
	
	private ListView listView;
	Dialog dialog;  // 进度条
	UserInfo userInfo = null;  // 选中的行
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_list);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
//		String user_uid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_UID);
//		if (user_uid == null || "".equals(user_uid)) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示").setMessage("请重新登录飞友！")
//			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
		menu.setHeaderTitle("操作");
		menu.setHeaderIcon(R.drawable.application);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.lbs_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.lbs_dial:
			// 拨打电话
			MSG msg = new MSG();
			msg.setType(2);
			msg.setVersion(0);
			msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
			msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
			msg.appendByte(SystemUtil.encoding("")); //dst="0"
			msg.appendChar(SystemUtil.encoding(userInfo.getUtel()));  // 此函数长度字段为word
			new Network(handler2, msg.getMessage()).start();
			dialog = DialogUtil.showProgressDialog(NearListActivity.this, "正在发送数据....");
			
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
			map.put("name", "任刚在" + i);
			map.put("distance", "100米以内" + i);
			map.put("signature", "个性签名" + i);
			list.add(map);
		}*/
		
//		List<UserInfo> list = new ArrayList<UserInfo>();
/*		UserInfo userInfo;
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) { 
				userInfo = new UserInfo("ggid" + i, "tel" + i, "任刚在" + i, "0", "100米以内" + i, "个性签名" + i);
			} else {
				userInfo = new UserInfo("ggid" + i, "tel" + i, "任刚在" + i, "1", "100米以内" + i, "个性签名" + i);
			}
			list.add(userInfo);
		}*/
		Gson gson = new Gson();
		String strJson = "[{'udistance':'100米以内0','uid':'ggid0','uname':'任刚在0','usex':'0','usignature':'个性签名0','utel':'tel0'},{'udistance':'100米以内1','uid':'ggid1','uname':'任刚在1','usex':'1','usignature':'个性签名1','utel':'tel1'},{'udistance':'100米以内2','uid':'ggid2','uname':'任刚在2','usex':'0','usignature':'个性签名2','utel':'tel2'},{'udistance':'100米以内3','uid':'ggid3','uname':'任刚在3','usex':'1','usignature':'个性签名3','utel':'tel3'},{'udistance':'100米以内4','uid':'ggid4','uname':'任刚在4','usex':'0','usignature':'个性签名4','utel':'tel4'},{'udistance':'100米以内5','uid':'ggid5','uname':'任刚在5','usex':'1','usignature':'个性签名5','utel':'tel5'},{'udistance':'100米以内6','uid':'ggid6','uname':'任刚在6','usex':'0','usignature':'个性签名6','utel':'tel6'},{'udistance':'100米以内7','uid':'ggid7','uname':'任刚在7','usex':'1','usignature':'个性签名7','utel':'tel7'},{'udistance':'100米以内8','uid':'ggid8','uname':'任刚在8','usex':'0','usignature':'个性签名8','utel':'tel8'},{'udistance':'100米以内9','uid':'ggid9','uname':'任刚在9','usex':'1','usignature':'个性签名9','utel':'tel9'}]";
		List<UserInfo> list = gson.fromJson(strJson, 
				new TypeToken<List<UserInfo>>(){}.getType());
		Log.i("Hello", strJson);
		NearListAdapter adapter = new NearListAdapter(this, list);
		listView.setAdapter(adapter);
	}
	// 创建内部类对象
	Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			String info = "";
			String ret = (String)msg.obj;
//			Log.i("LBS", ret);  // 打印返回的json数据
			if (ret.length() > 1) {
				info = "有数据";
				Gson gson = new Gson();
				List<UserInfo> list = gson.fromJson(ret, new TypeToken<List<UserInfo>>(){}.getType());
				NearListAdapter adapter = new NearListAdapter(NearListActivity.this, list);
				listView.setAdapter(adapter);
			} else {  // 无数据
				info = "暂时没有找到附近也使用该功能的人，请稍后再尝试查看。" + ret;
//				switch (Integer.valueOf(ret)) {
//				case 0:
//					info = "其它错误0";
//					break;
//				case 1:
//					info = "其它错误1";
//					break;
//				case 2:
//					info = "其它错误2";
//					break;
//				default:
//					info = "暂时没有找到附近也使用该功能的人，请稍后再尝试查看。";
//					break;
//				}
				DialogUtil.showAlertDialog(NearListActivity.this, "提示", info);
			}
			super.handleMessage(msg);
		}  // end handleMessage
	};
	
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(NearListActivity.this, "提示", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

}
