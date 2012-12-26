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
 * @author 任刚在
 */
public class NearListActivity2 extends Activity implements OnScrollListener, OnItemClickListener,
		android.view.View.OnClickListener {
	protected static final String TAG = NearListActivity2.class.getSimpleName();
	private Handler handler;
	private DisapearThread disapearThread;
	/** 标识List的滚动状态。 */
	private int scrollState;
	private NearListAdapter2 listAdapter;
	private ListView listMain;
	private TextView txtOverlay;
	private WindowManager windowManager;
	
	//===========
	List<UserInfo> list = null;
	Dialog dialog;  // 进度条
	//======百度========
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
					Toast.makeText(NearListActivity2.this, strLoc, Toast.LENGTH_LONG).show();
					double lat = location.getLatitude();
					double lon = location.getLongitude();
					if (lat != 0 && lon != 0) {
						mBMapMan.stop(); // 停止定位
						
						String ggid = SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO);
//						http://wap.feelyou.me/api/lbs.php?ggid=342323&longitude=1355445772&latitude=xx&checkcode=1
						StringBuilder sb = new StringBuilder();
						sb.append("http://wap.feelyou.me/api/lbs.php?ggid=");
						sb.append(ggid);
						sb.append("&longitude=").append(location.getLongitude()).append("&latitude=").append(location.getLatitude());
						sb.append("&checkcode=").append(SystemUtil.toMd5(ggid + location.getLongitude() + location.getLatitude() + "fy602"));
						Log.i("LBS", sb.toString());  // 打印发送url
						new MyNetwork2(handler1, sb.toString()).start();
						dialog = DialogUtil.showProgressDialog(NearListActivity2.this, "正在发送数据....");
						
					}
				}  // end location != null
				
			}
		};
		// 定义内部类
		class MyGeneralListener implements MKGeneralListener {

			@Override
			public void onGetNetworkState(int iError) {
				// TODO Auto-generated method stub
				Toast.makeText(NearListActivity2.this, "您的网络出错啦！", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onGetPermissionState(int iError) {
				// TODO Auto-generated method stub
				if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
					Toast.makeText(NearListActivity2.this, "百度地图key没有正确授权！", Toast.LENGTH_LONG).show();
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
			builder.setTitle("提示").setMessage("请重新登录飞友！")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
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
		// 初始化首字母悬浮提示框
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
		
		// 初始化ListAdapter
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

	/** 更改指定ListView的快速滚动条图标。 */
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
		// 以中间的ListItem为标准项。
//		txtOverlay.setText(list.get(firstVisibleItem + (visibleItemCount >> 1)).getUname().charAt(0));
	}

	/** ListView.OnScrollListener */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
//		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
//			handler.removeCallbacks(disapearThread);
//			// 提示延迟1.5s再消失
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
	 * 点击“咧牙”图片。<br/>
	 */
	@Override
	public void onClick(View view) {
		if (view instanceof ImageView) {
			// "咧牙"图标
			int position = ((Integer) view.getTag()).intValue();
			ActionItem actionAdd = new ActionItem(getResources().getDrawable(R.drawable.icon_info),
					"信息", this);
			ActionItem actionWeb = new ActionItem(getResources().getDrawable(R.drawable.icon_web),
					"查看", this);
			ActionItem actionEMail = new ActionItem(getResources().getDrawable(
					R.drawable.icon_call), "拨打", this);
			QuickActionBar qaBar = new QuickActionBar(view, position);
			qaBar.setEnableActionsLayoutAnim(true);
			qaBar.addActionItem(actionAdd);
			qaBar.addActionItem(actionWeb);
			qaBar.addActionItem(actionEMail);
			qaBar.show();
		} else if (view instanceof LinearLayout) {
			// ActionItem组件
			LinearLayout actionsLayout = (LinearLayout) view;
			QuickActionBar bar = (QuickActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();
			int listItemIdx = bar.getListItemIndex();
			TextView txtView = (TextView) actionsLayout.findViewById(R.id.qa_actionItem_name);
			String actionName = txtView.getText().toString();
			String personalName = list.get(listItemIdx).getUname();
			String url = list.get(listItemIdx).getUsignature();
			UserInfo userInfo = list.get(listItemIdx);
			if (actionName.equals("信息")) {
				showInfo(personalName, url);
			} else if (actionName.equals("查看")) {
				go2Web(userInfo);
			} else if (actionName.equals("拨打")) {
				go2Call(userInfo);
//				sendEMail(personalName, url);
			}
		}
	}

	private void go2Call(UserInfo userInfo) {
		// 拨打电话
		MSG msg = new MSG();
		msg.setType(2);
		msg.setVersion(0);
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_NO)));
		msg.appendByte(SystemUtil.encoding(SkyMeetingUtil.getPreference(SkyMeetingUtil.USER_PASS)));
		msg.appendByte(SystemUtil.encoding("")); //dst="0"
		msg.appendChar(SystemUtil.encoding(userInfo.getUtel()));  // 此函数长度字段为word
		new Network(handler2, msg.getMessage()).start();
		dialog = DialogUtil.showProgressDialog(NearListActivity2.this, "正在发送数据....");
		
	}
	
	// 创建内部类对象
	Handler handler1 = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			String info = "";
			String ret = (String)msg.obj;
			Log.i("LBS", ret);  // 打印返回的json数据
			if (ret.length() > 1) {
				info = "有数据";
				Gson gson = new Gson();
				Log.i(TAG, "返回的原始数据:" + ret);
				try {
					list = gson.fromJson(ret, new TypeToken<List<UserInfo>>(){}.getType());
				} catch (Exception e) {
					Log.i(TAG, "JSON解析出错:" + e.getMessage());
//					DialogUtil.showAlertDialog(NearListActivity2.this, "提示", "JSON解析出错:" + e.getMessage());
				}
				
				Log.i(TAG, "GSON转换后list.toString:" + list.toString());
				listAdapter = new NearListAdapter2(NearListActivity2.this, list, NearListActivity2.this);
				listMain.setAdapter(listAdapter);
//				NearListAdapter adapter = new NearListAdapter(NearListActivity2.this, list);
//				listView.setAdapter(adapter);
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
				DialogUtil.showAlertDialog(NearListActivity2.this, "提示", info);
			}
			super.handleMessage(msg);
		}  // end handleMessage
	};
	
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Bundle bundle = msg.getData();
			DialogUtil.showAlertDialog(NearListActivity2.this, "提示", bundle.getString("msg"));
			super.handleMessage(msg);
		}
	};

	public void showInfo(String name, String url) {
		String content = "昵称:" + name + "\n"//
				+ "个性签名:" + url;
//		Dialog dialog = new Dialog(this);
//		dialog.setContentView(R.layout.info_dialog);
//		dialog.setTitle("个人信息");
//		TextView text = (TextView) dialog.findViewById(R.id.text);
//		text.setText(content);
//		ImageView image = (ImageView) dialog.findViewById(R.id.image);
//		image.setImageResource(R.drawable.icon_info);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.show();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		Dialog dialog = builder.setTitle("个人信息").setMessage(content).setIcon(R.drawable.icon_info).create();
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
		// 设置邮件类型为纯文本，即为所有可见字符与一些简单的控制符的组合。
		mailIntent.setType("plain/text");
		// 收件人地址
		String[] arrReceiver = { "sodinoopen@hotmail.com" };
		// 抄送地址
		String[] arrCc = { "sodinoopen@hotmail.com" };
		// 密送地址
		String[] arrBcc = { "sodinoopen@hotmail.com", "sodinoopen@hotmail.com" };
		String mailSubject = "This is a mail send by list demo";
		String mailBody = "Hello everyone, I think the my demo is very good, haha. thanks.";
		// 指定要添加的附件的完整路径
		String attachPath = "file:///sdcard/Test.apk";
		mailIntent.putExtra(Intent.EXTRA_EMAIL, arrReceiver);
		mailIntent.putExtra(Intent.EXTRA_CC, arrCc);
		mailIntent.putExtra(Intent.EXTRA_BCC, arrBcc);
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		mailIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
		mailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachPath));
		// 过滤Intent并启动GMail界面，之后点击Send即可将邮件发送出去。
		Intent finalIntent = Intent.createChooser(mailIntent, "Mail Sending...");
		startActivity(finalIntent);
	}

	private class DisapearThread implements Runnable {
		public void run() {
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				txtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// 将txtOverlay删除。
		txtOverlay.setVisibility(View.INVISIBLE);
		windowManager.removeView(txtOverlay);
	}
	
	private void loadData() {
		Gson gson = new Gson();
		String strJson = "[{'udistance':'100米以内0','uid':'ggid0','uname':'任刚在0','usex':'0','usignature':'个性签名0','utel':'tel0'},{'udistance':'100米以内1','uid':'ggid1','uname':'任刚在1','usex':'1','usignature':'个性签名1','utel':'tel1'},{'udistance':'100米以内2','uid':'ggid2','uname':'任刚在2','usex':'0','usignature':'个性签名2','utel':'tel2'},{'udistance':'100米以内3','uid':'ggid3','uname':'任刚在3','usex':'1','usignature':'个性签名3','utel':'tel3'},{'udistance':'100米以内4','uid':'ggid4','uname':'任刚在4','usex':'0','usignature':'个性签名4','utel':'tel4'},{'udistance':'100米以内5','uid':'ggid5','uname':'任刚在5','usex':'1','usignature':'个性签名5','utel':'tel5'},{'udistance':'100米以内6','uid':'ggid6','uname':'任刚在6','usex':'0','usignature':'个性签名6','utel':'tel6'},{'udistance':'100米以内7','uid':'ggid7','uname':'任刚在7','usex':'1','usignature':'个性签名7','utel':'tel7'},{'udistance':'100米以内8','uid':'ggid8','uname':'任刚在8','usex':'0','usignature':'个性签名8','utel':'tel8'},{'udistance':'100米以内9','uid':'ggid9','uname':'任刚在9','usex':'1','usignature':'个性签名9','utel':'tel9'}, {'udistance':'100米以内0','uid':'ggid0','uname':'任刚在0','usex':'0','usignature':'个性签名0','utel':'tel0'},{'udistance':'100米以内1','uid':'ggid1','uname':'任刚在1','usex':'1','usignature':'个性签名1','utel':'tel1'},{'udistance':'100米以内2','uid':'ggid2','uname':'任刚在2','usex':'0','usignature':'个性签名2','utel':'tel2'},{'udistance':'100米以内3','uid':'ggid3','uname':'任刚在3','usex':'1','usignature':'个性签名3','utel':'tel3'},{'udistance':'100米以内4','uid':'ggid4','uname':'任刚在4','usex':'0','usignature':'个性签名4','utel':'tel4'},{'udistance':'100米以内5','uid':'ggid5','uname':'任刚在5','usex':'1','usignature':'个性签名5','utel':'tel5'},{'udistance':'100米以内6','uid':'ggid6','uname':'任刚在6','usex':'0','usignature':'个性签名6','utel':'tel6'},{'udistance':'100米以内7','uid':'ggid7','uname':'任刚在7','usex':'1','usignature':'个性签名7','utel':'tel7'},{'udistance':'100米以内8','uid':'ggid8','uname':'任刚在8','usex':'0','usignature':'个性签名8','utel':'tel8'},{'udistance':'100米以内9','uid':'ggid9','uname':'任刚在9','usex':'1','usignature':'个性签名9','utel':'tel9'}, {'udistance':'100米以内0','uid':'ggid0','uname':'任刚在0','usex':'0','usignature':'个性签名0','utel':'tel0'},{'udistance':'100米以内1','uid':'ggid1','uname':'任刚在1','usex':'1','usignature':'个性签名1','utel':'tel1'},{'udistance':'100米以内2','uid':'ggid2','uname':'任刚在2','usex':'0','usignature':'个性签名2','utel':'tel2'},{'udistance':'100米以内3','uid':'ggid3','uname':'任刚在3','usex':'1','usignature':'个性签名3','utel':'tel3'},{'udistance':'100米以内4','uid':'ggid4','uname':'任刚在4','usex':'0','usignature':'个性签名4','utel':'tel4'},{'udistance':'100米以内5','uid':'ggid5','uname':'任刚在5','usex':'1','usignature':'个性签名5','utel':'tel5'},{'udistance':'100米以内6','uid':'ggid6','uname':'任刚在6','usex':'0','usignature':'个性签名6','utel':'tel6'},{'udistance':'100米以内7','uid':'ggid7','uname':'任刚在7','usex':'1','usignature':'个性签名7','utel':'tel7'},{'udistance':'100米以内8','uid':'ggid8','uname':'在刚任8','usex':'0','usignature':'个性签名8','utel':'tel8'},{'udistance':'100米以内9','uid':'ggid9','uname':'任刚在9','usex':'1','usignature':'个性签名9','utel':'tel9'}, {'udistance':'100米以内0','uid':'ggid0','uname':'任刚在0','usex':'0','usignature':'个性签名0','utel':'tel0'},{'udistance':'100米以内1','uid':'ggid1','uname':'任刚在1','usex':'1','usignature':'个性签名1','utel':'tel1'},{'udistance':'100米以内2','uid':'ggid2','uname':'任刚在2','usex':'0','usignature':'个性签名2','utel':'tel2'},{'udistance':'100米以内3','uid':'ggid3','uname':'任刚在3','usex':'1','usignature':'个性签名3','utel':'tel3'},{'udistance':'100米以内4','uid':'ggid4','uname':'任刚在4','usex':'0','usignature':'个性签名4','utel':'tel4'},{'udistance':'100米以内5','uid':'ggid5','uname':'任刚在5','usex':'1','usignature':'个性签名5','utel':'tel5'},{'udistance':'100米以内6','uid':'ggid6','uname':'任刚在6','usex':'0','usignature':'个性签名6','utel':'tel6'},{'udistance':'100米以内7','uid':'ggid7','uname':'任刚在7','usex':'1','usignature':'个性签名7','utel':'tel7'},{'udistance':'100米以内8','uid':'ggid8','uname':'刚在8','usex':'0','usignature':'个性签名8','utel':'tel8'},{'udistance':'100米以内9','uid':'ggid9','uname':'刚在9','usex':'1','usignature':'个性签名9','utel':'tel9'}]";
		list = gson.fromJson(strJson, 
				new TypeToken<List<UserInfo>>(){}.getType());
	}

}
