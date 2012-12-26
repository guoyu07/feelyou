package com.feelyou.ui;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.feelyou.R;
import com.feelyou.adapter.NearListAdapter2;
import com.feelyou.net.MSG;
import com.feelyou.net.MyNetwork2;
import com.feelyou.net.Network;
import com.feelyou.quickaction.ActionItem;
import com.feelyou.quickaction.QuickActionBar;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;
import com.feelyou.vo.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author �θ���
 */
public class NearListActivity2 extends Activity implements OnScrollListener, OnItemClickListener,
		android.view.View.OnClickListener {
	protected static final String TAG = NearListActivity2.class.getSimpleName();
	private Handler handler;
	private DisapearThread disapearThread;
	/** ��ʶList�Ĺ���״̬�� */
	private int scrollState;
	private NearListAdapter2 listAdapter;
	private ListView listMain;
	private TextView txtOverlay;
	private WindowManager windowManager;
	
	//===========
	List<UserInfo> list = null;
	Dialog dialog;  // ������
	//======�ٶ�========
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
					Toast.makeText(NearListActivity2.this, strLoc, Toast.LENGTH_LONG).show();
					double lat = location.getLatitude();
					double lon = location.getLongitude();
					if (lat != 0 && lon != 0) {
						mBMapMan.stop(); // ֹͣ��λ
						
						String ggid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO);
//						http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
						StringBuilder sb = new StringBuilder();
						sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
						sb.append(ggid);
						sb.append("&longitude=").append(location.getLongitude()).append("&latitude=").append(location.getLatitude());
						sb.append("&checkcode=").append(SystemUtil.toMd5(ggid + location.getLongitude() + location.getLatitude() + "fy602"));
						Log.i("LBS", sb.toString());  // ��ӡ����url
						new MyNetwork2(handler1, sb.toString()).start();
						dialog = DialogUtil.showProgressDialog(NearListActivity2.this, "���ڷ�������....");
						
					}
				}  // end location != null
				
			}
		};
		// �����ڲ���
		class MyGeneralListener implements MKGeneralListener {

			@Override
			public void onGetNetworkState(int iError) {
				// TODO Auto-generated method stub
				Toast.makeText(NearListActivity2.this, "���������������", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onGetPermissionState(int iError) {
				// TODO Auto-generated method stub
				if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
					Toast.makeText(NearListActivity2.this, "�ٶȵ�ͼkeyû����ȷ��Ȩ��", Toast.LENGTH_LONG).show();
				}
			}
		}  // end MKGeneralListener
		

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near_list2);
		((TextView)findViewById(R.id.app_version)).setText(this.getTitle());
		
		String user_uid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_UID);
		if (user_uid == null || "".equals(user_uid)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��ʾ").setMessage("�����µ�¼���ѣ�")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent it = new Intent(NearListActivity2.this, SettingAccountActivity.class);
					startActivity(it);
					NearListActivity2.this.finish();
				}
			});
			builder.create().show();
		}
		
		handler = new Handler();
		// ��ʼ������ĸ������ʾ��
		txtOverlay = (TextView) LayoutInflater.from(this).inflate(R.layout.popup_char_hint, null);
		txtOverlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(txtOverlay, lp);
		//====baidu====
		mBMapMan = new BMapManager(this);
		mBMapMan.init(mStrKey, new MyGeneralListener());
		mBMapMan.getLocationManager().enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
		
		// ��ʼ��ListAdapter
//		loadData();
//		listAdapter = new NearListAdapter2(this, list, this);
		listMain = (ListView) findViewById(R.id.listInfo);
		listMain.setOnItemClickListener(this);
		listMain.setOnScrollListener(this);
//		listMain.setAdapter(listAdapter);
		changeFastScrollerDrawable(listMain);
		disapearThread = new DisapearThread();
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

	/** ����ָ��ListView�Ŀ��ٹ�����ͼ�ꡣ */
	private void changeFastScrollerDrawable(ListView list) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object obj = f.get(list);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(obj);
			drawable = getResources().getDrawable(R.drawable.fast_scroller_img);
			f.set(obj, drawable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** ListView.OnScrollListener */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		// ���м��ListItemΪ��׼�
//		txtOverlay.setText(list.get(firstVisibleItem + (visibleItemCount >> 1)).getUname().charAt(0));
	}

	/** ListView.OnScrollListener */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
//		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
//			handler.removeCallbacks(disapearThread);
//			// ��ʾ�ӳ�1.5s����ʧ
//			boolean bool = handler.postDelayed(disapearThread, 1500);
//			Log.d("ANDROID_INFO", "postDelayed=" + bool);
//		} else {
//			txtOverlay.setVisibility(View.VISIBLE);
//		}
	}

	/** OnItemClickListener */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String personalName = list.get(position).getUname();
		String signature = list.get(position).getUsignature();
		showInfo(personalName, signature);
	}

	/**
	 * View.OnClickListener <br/>
	 * �����������ͼƬ��<br/>
	 */
	@Override
	public void onClick(View view) {
		if (view instanceof ImageView) {
			// "����"ͼ��
			int position = ((Integer) view.getTag()).intValue();
			ActionItem actionAdd = new ActionItem(getResources().getDrawable(R.drawable.icon_info),
					"��Ϣ", this);
			ActionItem actionWeb = new ActionItem(getResources().getDrawable(R.drawable.icon_web),
					"�鿴", this);
			ActionItem actionEMail = new ActionItem(getResources().getDrawable(
					R.drawable.icon_call), "����", this);
			QuickActionBar qaBar = new QuickActionBar(view, position);
			qaBar.setEnableActionsLayoutAnim(true);
			qaBar.addActionItem(actionAdd);
			qaBar.addActionItem(actionWeb);
			qaBar.addActionItem(actionEMail);
			qaBar.show();
		} else if (view instanceof LinearLayout) {
			// ActionItem���
			LinearLayout actionsLayout = (LinearLayout) view;
			QuickActionBar bar = (QuickActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();
			int listItemIdx = bar.getListItemIndex();
			TextView txtView = (TextView) actionsLayout.findViewById(R.id.qa_actionItem_name);
			String actionName = txtView.getText().toString();
			String personalName = list.get(listItemIdx).getUname();
			String url = list.get(listItemIdx).getUsignature();
			UserInfo userInfo = list.get(listItemIdx);
			if (actionName.equals("��Ϣ")) {
				showInfo(personalName, url);
			} else if (actionName.equals("�鿴")) {
				go2Web(userInfo);
			} else if (actionName.equals("����")) {
				go2Call(userInfo);
//				sendEMail(personalName, url);
			}
		}
	}

	private void go2Call(UserInfo userInfo) {
		// ����绰
		MSG msg = new MSG();
		msg.setType(2);
		msg.setVersion(0);
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
		msg.appendByte(SystemUtil.encoding("")); //dst="0"
		msg.appendChar(SystemUtil.encoding(userInfo.getUtel()));  // �˺��������ֶ�Ϊword
		new Network(handler2, msg.getMessage()).start();
		dialog = DialogUtil.showProgressDialog(NearListActivity2.this, "���ڷ�������....");
		
	}
	
	// �����ڲ������
	Handler handler1 = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			String info = "";
			String ret = (String)msg.obj;
			Log.i("LBS", ret);  // ��ӡ���ص�json����
			if (ret.length() > 1) {
				info = "������";
				Gson gson = new Gson();
				Log.i(TAG, "���ص�ԭʼ����:" + ret);
				try {
					list = gson.fromJson(ret, new TypeToken<List<UserInfo>>(){}.getType());
				} catch (Exception e) {
					Log.i(TAG, "JSON��������:" + e.getMessage());
//					DialogUtil.showAlertDialog(NearListActivity2.this, "��ʾ", "JSON��������:" + e.getMessage());
				}
				
				Log.i(TAG, "GSONת����list.toString:" + list.toString());
				listAdapter = new NearListAdapter2(NearListActivity2.this, list, NearListActivity2.this);
				listMain.setAdapter(listAdapter);
//				NearListAdapter adapter = new NearListAdapter(NearListActivity2.this, list);
//				listView.setAdapter(adapter);
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
				DialogUtil.showAlertDialog(NearListActivity2.this, "��ʾ", info);
			}
			super.handleMessage(msg);
		}  // end handleMessage
	};
	
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(NearListActivity2.this, "��ʾ", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

	public void showInfo(String name, String url) {
		String content = "�ǳ�:" + name + "\n"//
				+ "����ǩ��:" + url;
//		Dialog dialog = new Dialog(this);
//		dialog.setContentView(R.layout.info_dialog);
//		dialog.setTitle("������Ϣ");
//		TextView text = (TextView) dialog.findViewById(R.id.text);
//		text.setText(content);
//		ImageView image = (ImageView) dialog.findViewById(R.id.image);
//		image.setImageResource(R.drawable.icon_info);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.show();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		Dialog dialog = builder.setTitle("������Ϣ").setMessage(content).setIcon(R.drawable.icon_info).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void go2Web(UserInfo userInfo) {
		Intent intent = new Intent(NearListActivity2.this, UserInfoActivity.class);
		intent.putExtra("userInfo", userInfo);
		startActivity(intent);
	}

	private void sendEMail(String name, String url) {
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		// �����ʼ�����Ϊ���ı�����Ϊ���пɼ��ַ���һЩ�򵥵Ŀ��Ʒ�����ϡ�
		mailIntent.setType("plain/text");
		// �ռ��˵�ַ
		String[] arrReceiver = { "sodinoopen@hotmail.com" };
		// ���͵�ַ
		String[] arrCc = { "sodinoopen@hotmail.com" };
		// ���͵�ַ
		String[] arrBcc = { "sodinoopen@hotmail.com", "sodinoopen@hotmail.com" };
		String mailSubject = "This is a mail send by list demo";
		String mailBody = "Hello everyone, I think the my demo is very good, haha. thanks.";
		// ָ��Ҫ��ӵĸ���������·��
		String attachPath = "file:///sdcard/Test.apk";
		mailIntent.putExtra(Intent.EXTRA_EMAIL, arrReceiver);
		mailIntent.putExtra(Intent.EXTRA_CC, arrCc);
		mailIntent.putExtra(Intent.EXTRA_BCC, arrBcc);
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		mailIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
		mailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachPath));
		// ����Intent������GMail���棬֮����Send���ɽ��ʼ����ͳ�ȥ��
		Intent finalIntent = Intent.createChooser(mailIntent, "Mail Sending...");
		startActivity(finalIntent);
	}

	private class DisapearThread implements Runnable {
		public void run() {
			// ������1.5s�ڣ��û��ٴ��϶�ʱ��ʾ����ִ���������
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				txtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// ��txtOverlayɾ����
		txtOverlay.setVisibility(View.INVISIBLE);
		windowManager.removeView(txtOverlay);
	}
	
	private void loadData() {
		Gson gson = new Gson();
		String strJson = "[{'udistance':'100������0','uid':'ggid0','uname':'�θ���0','usex':'0','usignature':'����ǩ��0','utel':'tel0'},{'udistance':'100������1','uid':'ggid1','uname':'�θ���1','usex':'1','usignature':'����ǩ��1','utel':'tel1'},{'udistance':'100������2','uid':'ggid2','uname':'�θ���2','usex':'0','usignature':'����ǩ��2','utel':'tel2'},{'udistance':'100������3','uid':'ggid3','uname':'�θ���3','usex':'1','usignature':'����ǩ��3','utel':'tel3'},{'udistance':'100������4','uid':'ggid4','uname':'�θ���4','usex':'0','usignature':'����ǩ��4','utel':'tel4'},{'udistance':'100������5','uid':'ggid5','uname':'�θ���5','usex':'1','usignature':'����ǩ��5','utel':'tel5'},{'udistance':'100������6','uid':'ggid6','uname':'�θ���6','usex':'0','usignature':'����ǩ��6','utel':'tel6'},{'udistance':'100������7','uid':'ggid7','uname':'�θ���7','usex':'1','usignature':'����ǩ��7','utel':'tel7'},{'udistance':'100������8','uid':'ggid8','uname':'�θ���8','usex':'0','usignature':'����ǩ��8','utel':'tel8'},{'udistance':'100������9','uid':'ggid9','uname':'�θ���9','usex':'1','usignature':'����ǩ��9','utel':'tel9'}, {'udistance':'100������0','uid':'ggid0','uname':'�θ���0','usex':'0','usignature':'����ǩ��0','utel':'tel0'},{'udistance':'100������1','uid':'ggid1','uname':'�θ���1','usex':'1','usignature':'����ǩ��1','utel':'tel1'},{'udistance':'100������2','uid':'ggid2','uname':'�θ���2','usex':'0','usignature':'����ǩ��2','utel':'tel2'},{'udistance':'100������3','uid':'ggid3','uname':'�θ���3','usex':'1','usignature':'����ǩ��3','utel':'tel3'},{'udistance':'100������4','uid':'ggid4','uname':'�θ���4','usex':'0','usignature':'����ǩ��4','utel':'tel4'},{'udistance':'100������5','uid':'ggid5','uname':'�θ���5','usex':'1','usignature':'����ǩ��5','utel':'tel5'},{'udistance':'100������6','uid':'ggid6','uname':'�θ���6','usex':'0','usignature':'����ǩ��6','utel':'tel6'},{'udistance':'100������7','uid':'ggid7','uname':'�θ���7','usex':'1','usignature':'����ǩ��7','utel':'tel7'},{'udistance':'100������8','uid':'ggid8','uname':'�θ���8','usex':'0','usignature':'����ǩ��8','utel':'tel8'},{'udistance':'100������9','uid':'ggid9','uname':'�θ���9','usex':'1','usignature':'����ǩ��9','utel':'tel9'}, {'udistance':'100������0','uid':'ggid0','uname':'�θ���0','usex':'0','usignature':'����ǩ��0','utel':'tel0'},{'udistance':'100������1','uid':'ggid1','uname':'�θ���1','usex':'1','usignature':'����ǩ��1','utel':'tel1'},{'udistance':'100������2','uid':'ggid2','uname':'�θ���2','usex':'0','usignature':'����ǩ��2','utel':'tel2'},{'udistance':'100������3','uid':'ggid3','uname':'�θ���3','usex':'1','usignature':'����ǩ��3','utel':'tel3'},{'udistance':'100������4','uid':'ggid4','uname':'�θ���4','usex':'0','usignature':'����ǩ��4','utel':'tel4'},{'udistance':'100������5','uid':'ggid5','uname':'�θ���5','usex':'1','usignature':'����ǩ��5','utel':'tel5'},{'udistance':'100������6','uid':'ggid6','uname':'�θ���6','usex':'0','usignature':'����ǩ��6','utel':'tel6'},{'udistance':'100������7','uid':'ggid7','uname':'�θ���7','usex':'1','usignature':'����ǩ��7','utel':'tel7'},{'udistance':'100������8','uid':'ggid8','uname':'�ڸ���8','usex':'0','usignature':'����ǩ��8','utel':'tel8'},{'udistance':'100������9','uid':'ggid9','uname':'�θ���9','usex':'1','usignature':'����ǩ��9','utel':'tel9'}, {'udistance':'100������0','uid':'ggid0','uname':'�θ���0','usex':'0','usignature':'����ǩ��0','utel':'tel0'},{'udistance':'100������1','uid':'ggid1','uname':'�θ���1','usex':'1','usignature':'����ǩ��1','utel':'tel1'},{'udistance':'100������2','uid':'ggid2','uname':'�θ���2','usex':'0','usignature':'����ǩ��2','utel':'tel2'},{'udistance':'100������3','uid':'ggid3','uname':'�θ���3','usex':'1','usignature':'����ǩ��3','utel':'tel3'},{'udistance':'100������4','uid':'ggid4','uname':'�θ���4','usex':'0','usignature':'����ǩ��4','utel':'tel4'},{'udistance':'100������5','uid':'ggid5','uname':'�θ���5','usex':'1','usignature':'����ǩ��5','utel':'tel5'},{'udistance':'100������6','uid':'ggid6','uname':'�θ���6','usex':'0','usignature':'����ǩ��6','utel':'tel6'},{'udistance':'100������7','uid':'ggid7','uname':'�θ���7','usex':'1','usignature':'����ǩ��7','utel':'tel7'},{'udistance':'100������8','uid':'ggid8','uname':'����8','usex':'0','usignature':'����ǩ��8','utel':'tel8'},{'udistance':'100������9','uid':'ggid9','uname':'����9','usex':'1','usignature':'����ǩ��9','utel':'tel9'}]";
		list = gson.fromJson(strJson, 
				new TypeToken<List<UserInfo>>(){}.getType());
	}

}
