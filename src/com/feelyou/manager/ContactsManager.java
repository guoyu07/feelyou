package com.feelyou.manager;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ContactsManager {
	public static HashMap<String, String> getContacts(Context context) {
		HashMap<String, String> contactsMap = new HashMap<String, String>();
		// �õ�ContentResolver����
		ContentResolver contentResolver = context.getContentResolver();
		// ȡ�õ绰���п�ʼһ��Ĺ��
//		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// �����ƶ����
//		while (cursor.moveToNext()) {
			// ȡ����ϵ������
//			int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//			String contact = cursor.getString(nameFieldColumnIndex);
			// ȡ�õ绰����
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
