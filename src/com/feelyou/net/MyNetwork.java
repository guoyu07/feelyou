package com.feelyou.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

import com.feelyou.util.SkyMeetingUtil;

/**
Network connection
以数字形式返回
返回值放入msg.what变量中 
*/
public class MyNetwork extends Thread {
	private HttpURLConnection conn;
	private Handler handler;
	private String urlStr;
	
//	private final static String REQHOST = "http://220.248.185.86:8000";
//	private final static String wap_proxy = "http://10.0.0.172:80/";
	private final static String wap_host = "10.0.0.172";
	private final static int wap_port = 80;


	public MyNetwork(Handler handler, String url) {
		this.urlStr = url;
		this.handler = handler;
	}

	public void run() {
		InputStream is = null;
		OutputStream os = null;
		conn = null;
		try {
//			URL url = new URL(urlStr);
//			if (SkyMeetingUtil.getPreference(SkyMeetingUtil.WAPLINK).equals("1")) {
//				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(wap_host, wap_port));
//				conn = (HttpURLConnection)url.openConnection(proxy);
//			} else {
//				conn = (HttpURLConnection)url.openConnection();
//			}
			conn = getURLConnection(urlStr);
			
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
//			conn.setDoOutput(true);
//			conn.setRequestProperty(http_ct, http_ct_d );
//			conn.setRequestProperty(http_cl, data.length + "");
			is = conn.getInputStream();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte [] b = new byte[128];
	        int count = is.read(b);
	        while (count > 0) {
	        	baos.write(b, 0, count);
	            count = is.read(b);
	        }
	        String msg = new String(baos.toByteArray());
	        
	        int code = 0;
	        if (! ("".equals(msg)))
	        	code = Integer.valueOf(msg);
	            
			Message message = handler.obtainMessage();
			message.what = code;
			handler.sendMessage(message);
		
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (ConnectException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e4) {
			}
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e5) {
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;				
			}
		}
	}
	
	private HttpURLConnection getURLConnection(String url) throws Exception {
		String proxyHost = android.net.Proxy.getDefaultHost();
		if (proxyHost != null) {
			java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
			return (HttpURLConnection) new URL(url).openConnection(p);
		} else {
			return (HttpURLConnection) new URL(url).openConnection();
		}
	}
	
}