package com.feelyou.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.feelyou.R;
import com.feelyou.net.MyNetwork;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

public class NearActivity extends Activity implements MKSearchListener {
	
//	LocationListener mLocationListener = null;  //create时注册此listener，destroy时需要remove 
	MKSearch mkSearch;  // 
	BMapManager mBMapMan = null;  // 百度MapAPI的管理类
	// 授权Key
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0";
	
	
	// 定位事件
	LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				String strLoc = String.format("您当前的位置：经度：%f 纬度：%f", 
						location.getLongitude(), location.getLatitude());

				double lat = location.getLatitude();
				double lon = location.getLongitude();
				
				if (lat != 0 && lon != 0) {
					mBMapMan.stop();  // 停止定位
					
					
//					http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
					StringBuilder sb = new StringBuilder();
					sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
					sb.append(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
					sb.append("&longitude=").append(lon).append("&latitude=").append(lat);
//					new MyNetwork(handler, sb.toString()).start();
//					dialog = DialogUtil.showProgressDialog(CallOneActivity.this, "正在发送数据....");
					
					
					
					
				}	
			}  // end location != null
		}
	};
	
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			Toast.makeText(NearActivity.this, "您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权key错误：
				Toast.makeText(NearActivity.this, "百度地图Key没有正确授权！", Toast.LENGTH_LONG).show();
			}
		}
	}  // end MKGeneralListener
	

	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		// baidu
/*		mBMapMan = new BMapManager(this);
		mBMapMan.init(mStrKey, new MyGeneralListener());
		mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
		mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_GPS_PROVIDER);
		mBMapMan.start();  // 定位开始
*/		
		
//		mkSearch = new MKSearch(); // 初始化一个MKSearch，根据location解析详细地址
//		mkSearch.init(mBMapMan, this);
		
		
		
		Button btnNear = (Button) findViewById(R.id.btn_near);
		btnNear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 显示提示(选择)对话框
				LayoutInflater inflater = (LayoutInflater) NearActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.lbs_open_dialog_view, null);
				new AlertDialog.Builder(NearActivity.this).setView(view).setTitle("提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(NearActivity.this, NearListActivity.class);
//						startActivity(intent);
						Intent intent = new Intent(NearActivity.this, NearListActivity2.class);
						startActivity(intent);
					}
				})
				.setNegativeButton("取消", null)
				.create().show();
			}
		});
		
		// 清除痕迹按钮
		Button btnClear = (Button) findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(NearActivity.this, NearListActivity2.class);
//				startActivity(intent);
				
				new AlertDialog.Builder(NearActivity.this).setTitle("提示").setMessage("清除位置信息后，别人将不能查看到你！清除位置信息并退出？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 发送消息去服务器上清除位置信息
						//  .........
						// 关闭本窗体
						NearActivity.this.finish();
					}
				})
				.setNegativeButton("取消", null)
				.create().show();
			}
		});
	}
	
	private void getLBS() {
		
	}
	
	@Override
	protected void onPause() {
		// 移除listener
//		mBMapMan.getLocationManager().removeUpdates(mLocationListener);
//		mBMapMan.stop();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// 注册Listener
//		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
//		mBMapMan.stop(); // 默认不定位
		super.onResume();
	}

	// MKSearchListener
	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
