package com.feelyou.manager;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ContactsManager {
	public static HashMap<String, String> getContacts(Context context) {
		HashMap<String, String> contactsMap = new HashMap<String, String>();
		// 得到ContentResolver对象
		ContentResolver contentResolver = context.getContentResolver();
		// 取得电话本中开始一项的光标
//		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// 向下移动光标
//		while (cursor.moveToNext()) {
			// 取得联系人名字
//			int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//			String contact = cursor.getString(nameFieldColumnIndex);
			// 取得电话号码
//			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//			Cursor phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
//					null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//			while (phone.moveToNext()) {
//				String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//				contactsMap.put(contact, phoneNumber);
//			}
//		}
//		cursor.close();
		return contactsMap;
	}
}
