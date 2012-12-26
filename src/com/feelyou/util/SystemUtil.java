package com.feelyou.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;

import com.feelyou.net.MSG;

public class SystemUtil {
	
	public static String getString(Context context, int id) {
		return context.getResources().getString(id);
	}
	
	/**
	 * encoding String to Array by utf-8
	 * 
	 * @param str
	 *            encoding String
	 * @return encoded byte array
	 */
	public static byte[] encoding(String s) {
		try {
			return s.getBytes(Const.Coding);
		} catch (UnsupportedEncodingException ex) {
		}
		return new byte[0];
	}

	public static String decoding(byte[] d) {
		try {
			return new String(d, Const.Coding);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return Const.EMPTY;
	}
	//////////////////////////////////////
	public static String toMd5(String str) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(str.getBytes());
			return toHexString(algorithm.digest(), "");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex).append(separator);
		}
		return hexString.toString();
	}
}
