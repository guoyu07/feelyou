/*package com.feelyou.util;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpRequester {
	public static String post(String actionUrl, Map<String, String>params, FormFile[] files) {
		try {
			String BOUNDARY = "---------7d4a6d159c9"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data"; 
			URL url = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setDoInput(true); // 允许输入
			conn.setDoOutput(true); // 允许输出
			conn.setUseCaches(false); // 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA +";boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) { // 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes()); // 发送表单字段数据
			for (FormFile file: files) { // 发送文件数据
				StringBuilder split = new StringBuilder();
				split.append("--");
				split.append(BOUNDARY);
				split.append("\r\n");
				split.append("Content-Disposition: form-data;name=\"" + file.get)
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
*/