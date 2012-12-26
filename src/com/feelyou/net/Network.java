package com.feelyou.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.feelyou.util.Const;
import com.feelyou.util.SkyMeetingUtil;
import com.feelyou.util.SystemUtil;

/** Network connection */
public class Network extends Thread {
	private byte[] data;
	private String host;
//	private Context context;
	private HttpURLConnection conn;
	private Handler handler;
	
	public static final int NET_OK = 80;
	public static final int NET_ERROR = 81;
	
	private final static String REQHOST = "http://220.248.185.86:8000";
//	private final static String wap_proxy = "http://10.0.0.172:80/";
	private final static String wap_host = "10.0.0.172";
	private final static int wap_port = 80;
	private final static String http_accept = "Accept";
	private final static String http_accept_d = "*/*";
	private final static String http_ct = "Content-Type";
	private final static String http_ct_d = "application/octet-stream";
	private final static String http_con = "Connection";
	private final static String http_con_d = "Keep-Alive";
	private final static String http_cl = "Content-Length";
	private final static String http_v = "Ver";
	private final static String x_online = "X-Online-Host";
	private final static String http_port = ":8000";
	private final static String NOTICE_NETWORK_ERROR = "网络错误";
	private final static String NOTICE_CONNECTING = "正在连接……";
	private final static String NOTICE_NETWORK_EXCEPTION = "连接异常";
	private final static String NOTICE_SERVER_EXCEPTION = "服务器异常";
	private final static String NOTICE_SECURE_EXCEPTION = "您已限制软件使用网络连接。请重新授权。";
	private final static String NOTICE_STATUS_ERROR = "网络信号差，请咨询运营商客服。";

	public Network(Handler handler, byte[] message) {
		data = message;
		this.host = REQHOST;
		this.handler = handler;
	}

	private void dispose() {
		data = null;
		conn = null;
	}

	public void run() {
		InputStream is = null;
		OutputStream os = null;
		DataInputStream dis = null;
		conn = null;
		if (host == null) {
			dispose();
			return;
		}
		try {
			conn = getURLConnection(host);
			
			conn.setRequestMethod("POST");  // POST
			conn.setConnectTimeout(5 * 1000);
			conn.setDoOutput(true);
//			conn.setDoInput(true);
			conn.setRequestProperty(http_ct, http_ct_d );
			conn.setRequestProperty(http_cl, data.length + "");
			conn.setRequestProperty(http_accept, http_accept_d);
			conn.setRequestProperty(http_con, http_con_d);
			
//			conn.setRequestProperty(x_online, REQHOST);
			os = conn.getOutputStream();
			if (data != null) {
				os.write(data);
			}
			os.flush();
			os.close();
			os = null;
			is = conn.getInputStream();
			dis = new DataInputStream(is);
			byte head = 0;
			head = dis.readByte();
			int length = dis.readUnsignedByte();
			length += dis.readUnsignedByte() << 8;
			data = null;
			if (length > 10000 || length < 7) {
				Message msg = new Message();
				msg.what = NET_ERROR;
				msg.obj = "服务器异常";
				handler.sendMessage(msg);
				return;  // "服务器异常";
			}
			data = new byte[length];
			data[0] = head;
			data[1] = (byte)(length & 0x0ff);
			data[2] = (byte)(length >> 8);
			int start = 3;
			int l = length - 3;
			while (l > 0) {
				int b = dis.read(data, start, l);
				if (b > 0) {
					start += b;
					l -= b;
				}
			}
			dis.skip(dis.available());
			task(data);
			data = null;
			dis = null;
		} catch (SecurityException e1) {
			this.sendMsg(NET_ERROR, "没有网络权限");
			e1.printStackTrace();
		} catch (ConnectException e2) {
			this.sendMsg(NET_ERROR, "无法跟服务器建立连接。");
			e2.printStackTrace();
		} catch (IOException e3) {
			this.sendMsg(NET_ERROR, "读写数据出错。");
			e3.printStackTrace();
		} catch (Exception e4) {
			this.sendMsg(NET_ERROR, "未知错误。");
		}finally {
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
			dispose();
		}
	}
	
	public void task(byte[] data) {
		MSG mesg = new MSG();
		Message message = handler.obtainMessage();
		Bundle bundle = new Bundle();
		mesg.decode(data);
		int type = mesg.getType() & 0x0ff;
		int version = mesg.getVersion();
		boolean issucc = false;
//		boolean updatecontact=false;
		String msg = Const.EMPTY;
		if (version == 0) {
			issucc = true;
			msg = SystemUtil.decoding(mesg.getBody());
		} else {
			issucc = mesg.extract(MSG.BYTE)[0] == 0;
			if(type==222){  //用户话单
				msg = SystemUtil.decoding(mesg.extract(MSG.CHAR));
			}else{
				msg = SystemUtil.decoding(mesg.extract(MSG.BYTE));
			}
		}
		if (type >= 200 && type <= 227) {
			bundle.putBoolean("issucc", issucc);
			bundle.putString("msg", msg);
			message.setData(bundle);
			message.what = type;
			handler.sendMessage(message);
		}
		mesg.dispose();
		message = null;
		bundle = null;
		mesg = null;
		msg = null;
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