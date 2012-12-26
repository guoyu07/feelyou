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
 * Network connection
 *
Network connection
以字符串形式返回
返回值放入msg.obj变量中
适用于服务器给客户端有返回内容时。 
*/
public class MyNetwork2 extends Thread {
	private HttpURLConnection conn;
	private Handler handler;
	private String urlStr;
	public static final int NET_OK = 200;
	public static final int NET_ERROR = 201;
	
	public MyNetwork2(Handler handler, String url) {
		this.urlStr = url;
		this.handler = handler;
	}

	public void run() {
		InputStream is = null;
		OutputStream os = null;
		conn = null;
		try {
			conn = getURLConnection(urlStr);
			
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
//			conn.setRequestProperty(http_ct, http_ct_d );
//			conn.setRequestProperty(http_cl, data.length + "");
			is = conn.getInputStream();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte [] buf = new byte[128];
	        int count = is.read(buf);
	        while (count > 0) {
	        	baos.write(buf, 0, count);
	            count = is.read(buf);
	        }
	        String msg = new String(baos.toByteArray());
	        
			if ((msg == null) || ("".equals(msg)))
				msg = "9";    // 任意不用的数字，用于表示【未知错误的代码】
	            
//			Message message = handler.obtainMessage();
//			message.obj = msg;
//			message.what = NET_OK;
//			handler.sendMessage(message);
			this.sendMsg(NET_OK, msg);
		} catch (SecurityException e1) {
			this.sendMsg(NET_ERROR, "没有连接权限。");
			e1.printStackTrace();
		} catch (ConnectException e2) {  // A ConnectException is thrown if a connection cannot be established to a remote host on a specific port.
			this.sendMsg(NET_ERROR, "无法跟服务器建立连接。");
			e2.printStackTrace();
		} catch (IOException e3) {
			this.sendMsg(NET_ERROR, "读写数据出错。");
			e3.printStackTrace();
		} catch (Exception e) {
			this.sendMsg(NET_ERROR, "未知错误。");
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
	
	private void sendMsg(int type, String msg) {
		Message message = handler.obtainMessage();
		message.obj = msg;
		message.what = type;
		handler.sendMessage(message);
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