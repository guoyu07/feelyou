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
	
//	LocationListener mLocationListener = null;  //createʱע���listener��destroyʱ��Ҫremove 
	MKSearch mkSearch;  // 
	BMapManager mBMapMan = null;  // �ٶ�MapAPI�Ĺ�����
	// ��ȨKey
	String mStrKey = "ADAFD20FAC1046E4DCE38E2C60BF1FB65F4691B0";
	
	
	// ��λ�¼�
	LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				String strLoc = String.format("����ǰ��λ�ã����ȣ�%f γ�ȣ�%f", 
						location.getLongitude(), location.getLatitude());

				double lat = location.getLatitude();
				double lon = location.getLongitude();
				
				if (lat != 0 && lon != 0) {
					mBMapMan.stop();  // ֹͣ��λ
					
					
//					http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
					StringBuilder sb = new StringBuilder();
					sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
					sb.append(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO));
					sb.append("&longitude=").append(lon).append("&latitude=").append(lat);
//					new MyNetwork(handler, sb.toString()).start();
//					dialog = DialogUtil.showProgressDialog(CallOneActivity.this, "���ڷ�������....");
					
					
					
					
				}	
			}  // end location != null
		}
	};
	
	class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			Toast.makeText(NearActivity.this, "���������������", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// ��Ȩkey����
				Toast.makeText(NearActivity.this, "�ٶȵ�ͼKeyû����ȷ��Ȩ��", Toast.LENGTH_LONG).show();
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
		mBMapMan.start();  // ��λ��ʼ
*/		
		
//		mkSearch = new MKSearch(); // ��ʼ��һ��MKSearch������location������ϸ��ַ
//		mkSearch.init(mBMapMan, this);
		
		
		
		Button btnNear = (Button) findViewById(R.id.btn_near);
		btnNear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ��ʾ��ʾ(ѡ��)�Ի���
				LayoutInflater inflater = (LayoutInflater) NearActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.lbs_open_dialog_view, null);
				new AlertDialog.Builder(NearActivity.this).setView(view).setTitle("��ʾ")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(NearActivity.this, NearListActivity.class);
//						startActivity(intent);
						Intent intent = new Intent(NearActivity.this, NearListActivity2.class);
						startActivity(intent);
					}
				})
				.setNegativeButton("ȡ��", null)
				.create().show();
			}
		});
		
		// ����ۼ���ť
		Button btnClear = (Button) findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(NearActivity.this, NearListActivity2.class);
//				startActivity(intent);
				
				new AlertDialog.Builder(NearActivity.this).setTitle("��ʾ").setMessage("���λ����Ϣ�󣬱��˽����ܲ鿴���㣡���λ����Ϣ���˳���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// ������Ϣȥ�����������λ����Ϣ
						//  .........
						// �رձ�����
						NearActivity.this.finish();
					}
				})
				.setNegativeButton("ȡ��", null)
				.create().show();
			}
		});
	}
	
	private void getLBS() {
		
	}
	
	@Override
	protected void onPause() {
		// �Ƴ�listener
//		mBMapMan.getLocationManager().removeUpdates(mLocationListener);
//		mBMapMan.stop();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// ע��Listener
//		mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
//		mBMapMan.stop(); // Ĭ�ϲ���λ
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
