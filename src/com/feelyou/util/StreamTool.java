package com.feelyou.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
	/**
	 * 从输入流获取数据
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inputStream.close();
		return outStream.toByteArray();
	}

}
