package com.feelyou.util;

import java.lang.reflect.Method;

import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class PhoneUtil {
	public static ITelephony getITelephony(
			TelephonyManager paramTelephonyManager) throws Exception {
		Class localClass = paramTelephonyManager.getClass();
		Class[] arrayOfClass = new Class[0];
		Method localMethod = localClass.getDeclaredMethod("getITelephony",
				arrayOfClass);
		localMethod.setAccessible(true);
		Object[] arrayOfObject = new Object[0];
		return (ITelephony) localMethod.invoke(paramTelephonyManager,
				arrayOfObject);
		
		/*
		 *         Method getITelephonyMethod = telephony.getClass().getDeclaredMethod("getITelephony");   
        getITelephonyMethod.setAccessible(true);//私有化函数也能使用   
        return (ITelephony)getITelephonyMethod.invoke(telephony);  
		 */
	}
}