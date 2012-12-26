package com.feelyou.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.feelyou.R;
import com.feelyou.net.MyNetwork;
import com.feelyou.service.UpdateService;
import com.feelyou.util.DialogUtil;
import com.feelyou.util.Global;
import com.feelyou.util.SkyMeetingUtil;
/**
 * 主界面
 * @author Administrator
 *
 */
public class MainActivity extends Activity implements OnItemClickListener {
	Dialog dialog;
	//再按一次后退键退出应用程序
	private static Boolean isExit = false;
	private static Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = false;
			hasTask = true;
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT).show();
				if (! hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}
	/////////////结束
	// 选项菜单编号
	private static final int MENU_QUIT = Menu.FIRST;
	private static final int MENU_CALLCENTER = Menu.FIRST + 1;
	private static final String APP_KEY = "fb5ef7b9e73f6b49";
	private static final String SECRET_KEY = "mV6MH4NQDF7vXYKtPaTcQpbgkZMmRRJJ";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WiOffer.init(this, APP_KEY, SECRET_KEY);
        setContentView(R.layout.main);
        SkyMeetingUtil.init(this);
        // 检查网络状态
        boolean flag = checkNetwork();
        // 检查更新 
//        checkVersion();
        if (flag) 
        	simpleAutoUpdate();
        
        String versionName = getAppVersionName(this);
        ((TextView)findViewById(R.id.app_version)).setText("飞友" + versionName);
        // 免费体验
        TextView tv = (TextView)findViewById(R.id.txtFreeTrial);
        tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FreeTrialActivity.class);
				startActivity(intent);
			}
        });
        
        GridView mainGridView = (GridView)findViewById(R.id.gridview);
        int [] arrayOfImgs = {R.drawable.phone, R.drawable.setting, R.drawable.calllist, R.drawable.calllist, 
        							   R.drawable.charge, R.drawable.intro, R.drawable.gift,
        							   R.drawable.down, R.drawable.application, R.drawable.map, R.drawable.help};
        String [] arrayOfItemValue = getResources().getStringArray(R.array.main_item_name);
        
        String[] arrayOfPerItem = {"ItemImage", "ItemText"};
        int [] arrayOfPerItemId = {R.id.ItemImage, R.id.ItemText};
        ArrayList<HashMap<String, Object>> itemArrayList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < arrayOfItemValue.length; i++) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put(arrayOfPerItem[0], arrayOfImgs[i]);
        	map.put(arrayOfPerItem[1], arrayOfItemValue[i]);
        	itemArrayList.add(map);
        }
        SimpleAdapter localSimpleAdapter = new SimpleAdapter(
        		this, itemArrayList, R.layout.night_item, 
        		arrayOfPerItem, arrayOfPerItemId);
        
        mainGridView.setAdapter(localSimpleAdapter);
        mainGridView.setOnItemClickListener(this);
        
        if (SkyMeetingUtil.getPreference(SkyMeetingUtil.FIRSTIN).equals("1") || SkyMeetingUtil.getPreference(SkyMeetingUtil.FIRSTIN).equals("")) {
        	SkyMeetingUtil.setPreference(SkyMeetingUtil.FIRSTIN, "0");
        	
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.notify_content).setTitle(R.string.notify_title);
    		builder.setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MainActivity.this, FreeTrialActivity.class);
					MainActivity.this.startActivity(intent);					
				}
			});
    		builder.create().show();
        }
    }
    
    /**
     * 检查更新
     */
    private void checkVersion() {
    	if (Global.localVersion < Global.serverVersion) {
    		// 发现新版本，提示用户更新
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    		alert.setTitle("软件升级")
    			   .setMessage("发现新版本，建议立即更新使用.")
    			   .setPositiveButton("更新", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 开启更新服务UpdateService
						// 这里为了把upate更好模块化，可以传一些updateService依赖的值
						// 如布局ID，资源ID，动态获取的标题，这里以app_name为例
						Intent updateIntent = new Intent(MainActivity.this, UpdateService.class);
						updateIntent.putExtra("titleId", R.string.application_title);
						startService(updateIntent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
    		alert.create().show();
    	} else {
    		// 清理工作，略去
    		// cheanUpdateFile();
    	}
	}
    
	/**
     *  隐藏免费试用界面
     */
    private void hideFreeTrialControl() {
    	TextView tvFreeTrial = (TextView) findViewById(R.id.txtFreeTrial);
    	View vLine = findViewById(R.id.line);
    	if (SkyMeetingUtil.getPreference(SkyMeetingUtil.ACCOUNT_OK).equals("1")) {  // 本来是1
    		tvFreeTrial.setVisibility(View.INVISIBLE);
    		vLine.setVisibility(View.INVISIBLE);
    	}
    }
    /**
     * 检查网络状态
     */
    private boolean checkNetwork() {
    	boolean flag = false;
    	ConnectivityManager cwjManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetInfo = cwjManager.getActiveNetworkInfo();
    	if (activeNetInfo != null) {
    		flag = activeNetInfo.isAvailable();
    		SkyMeetingUtil.setPreference(SkyMeetingUtil.WAPLINK, "0");
    		if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
    			SkyMeetingUtil.setPreference(SkyMeetingUtil.WAPLINK, "0");
    		} else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
    			String extraInfo = activeNetInfo.getExtraInfo();
    			if (extraInfo.toLowerCase().contains("wap")) {
    				SkyMeetingUtil.setPreference(SkyMeetingUtil.WAPLINK, "1");
    			}
    		}
    	}
    	if (!flag) {
    		Builder b = new AlertDialog.Builder(this).setTitle("没有可用的网络")
    		.setMessage("请开启GPRS或WIFI网络连接");
    		b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent mIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS); // 进入无线网络配置界面
					startActivity(mIntent);
				}
			}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).create();
    		b.show();
    	}
    	return flag;
    }
    
    public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Builder b = new AlertDialog.Builder(MainActivity.this).setTitle("更新")
			    		.setMessage("服务器上有最新版本，请更新！");
			    		b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								simpleDownload();
							}
						}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).create();
			    		b.show();
			}
			super.handleMessage(msg);
		}
    	
    };
    /**
     * 验证用户
     */
    private boolean validateAccount() {
    	if (SkyMeetingUtil.getPreference(SkyMeetingUtil.ACCOUNT_OK).equals("0") ) {
    		DialogUtil.showOperateFailureDialog(this, R.string.main_account_failure);
    		return false;
    	}
    	return true;
    }
    /**
     * 返回当前程序版本名、号
     */
    public String getAppVersionName(Context context) {
    	String versionName = "";
    	try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
    }
    
    /**
     * 返回当前程序版本VersionCode
     */
    public int getAppVersionCode(Context context) {
    	int versionCode = 0;
    	try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
    }
    
    public void simpleDownload() {
        String new_version_url = "http://wap.feelyou.me/album/soft/FeelYou.apk";
        Uri uri = Uri.parse(new_version_url);
        Intent web = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(web);
    }
    
    public void simpleAutoUpdate() {
		StringBuilder sb = new StringBuilder();
		sb.append("http://wap.feelyou.me/api/version.php?versioncode=");
		sb.append(String.valueOf(getAppVersionCode(this)));
	
		new MyNetwork(handler, sb.toString()).start();
    }
    
    @Override
	protected void onStart() {
		super.onStart();
		hideFreeTrialControl();
	}
	// 创建选项菜单
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_CALLCENTER, 0, R.string.callcenter).setIcon(R.drawable.ic_menu_call);
    	menu.add(0, MENU_QUIT, 0, R.string.quite).setIcon(R.drawable.ic_menu_back);
		return super.onCreateOptionsMenu(menu);
	}
    // 响应选项菜单事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_QUIT:
			finish();
			break;
		case MENU_CALLCENTER:
			Uri localUri = Uri.parse("tel:15637172131");  // 飞友客服
			Intent localIntent = new Intent(Intent.ACTION_CALL, localUri);
			startActivity(localIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int itemId, long paramLong) {
		startItemActivity(itemId);
	}
	
	private void startItemActivity(int itemId) {
		Intent intent = null;
		switch (itemId) {
		case 0:
			if (! validateAccount()) 
				return;
			intent = new Intent(this, CallOneActivity.class);
//			intent = new Intent(this, CallActivity.class);
			break;
		case 1:
			intent = new Intent(this, SettingActivity.class);
			break;
		case 2:
			if (! validateAccount()) 
				return;
			intent = new Intent(this, RecordActivity.class);
			break;
		case 3:
			if (! validateAccount())
				return;
			intent = new Intent(this, CallListActivity.class);
			break;
		case 4:
			if (! validateAccount())
				return;
			intent = new Intent(this, ChargeActivity.class);
			break;
		case 5:
			if (! validateAccount())
				return;
			intent = new Intent(this, SpreadActivity.class);
			break;
		case 6:
			intent = new Intent(this, GiftActivity.class);
			break;
		case 7:
			intent = new Intent(this, DownActivity.class);
			break;
		case 8:
			intent = new Intent(this, WapActivity.class);
			break;
		case 9:
			intent = new Intent(this, NearActivity.class);
			break;
		case 10:
			intent = new Intent(this, InfoActivity.class);
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
}