package com.feelyou.manager;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordManager {
	DataBaseHelper helper = null;
	
	public RecordManager(Context context) {
		helper = new DataBaseHelper(context);
	}
	
	public void clear() {
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			sqliteDatabase.execSQL("delete from calllog");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
		}
	}
	
	public void deleteByIds(String aIds) {
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			String sql = "delete from calllog where _id in (" + aIds + ")";
			sqliteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
		}		
	}
	
	public String getPhonesByIds(String aIds) {
		StringBuilder sb = new StringBuilder();
		Cursor cursor = null;
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			String sql = "select number from calllog where _id in (" + aIds + ")";
			cursor = sqliteDatabase.rawQuery(sql, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				sb.append(cursor.getString(0)).append("#");
			}
			sb.deleteCharAt(sb.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
			if (cursor != null)
				cursor.close();
		}
		return sb.toString();
	}
	
	public HashMap<String, String> getRecordById(int id) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		Cursor cursor = null;
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			String sql = "select _id, number, time, note from calllog where _id =?";
			cursor = sqliteDatabase.rawQuery(sql, new String[]{String.valueOf(id)});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				hashMap.put("number", cursor.getString(1));
				hashMap.put("time", cursor.getString(2));
				hashMap.put("note", cursor.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
			if (cursor != null)
				cursor.close();
		}		
		return hashMap;
	}
	
	public HashMap<Integer, String> getRecords() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		Cursor cursor = null;
		SQLiteDatabase sqliteDatabase = this.helper.getReadableDatabase();
		try {
			String sql = "select _id,case when note='' then number else note end as note1 from calllog order by time desc limit 0,50";
			cursor = sqliteDatabase.rawQuery(sql, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				hashMap.put(cursor.getInt(0), cursor.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
			if (cursor != null)
				cursor.close();
		}				
		return hashMap;
	}
	
	public void insertRecord(String phones) {
		String[] objs = new String[1];
		Cursor cursor = null;
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			String sqlInsert = "insert into calllog values(null, ?, datetime('now'), '')";
			String sqlUpdate = "update calllog set time = datetime('now') where number = ?";
			String sqlIsExist = "select * from calllog where number = ?";
			if (!phones.contains("#")) {
				objs[0] = phones;
				cursor = sqliteDatabase.rawQuery(sqlIsExist, objs);
				if (cursor.getCount() != 0) {
					sqliteDatabase.execSQL(sqlUpdate, objs);
				} else {
					sqliteDatabase.execSQL(sqlInsert, objs);
				}
			} else {
				String[] strs = phones.split("#");
				for (int i = 0; i < strs.length; i++) {
					objs[0] = strs[i];
					cursor = sqliteDatabase.rawQuery(sqlIsExist, objs);
					if (cursor.getCount() == 0) {
						sqliteDatabase.execSQL(sqlUpdate, objs);
					} else {
						sqliteDatabase.execSQL(sqlInsert, objs);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
			if (cursor != null) 
				cursor.close();
		}			
	}
	
	public void updateRemark(int id, String note) {
		Object[] objs = new Object[2];
		SQLiteDatabase sqliteDatabase = this.helper.getWritableDatabase();
		try {
			objs[0] = note;
			objs[1] = String.valueOf(id);
			sqliteDatabase.execSQL("update calllog set note=? where _id=?", objs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqliteDatabase != null)
				sqliteDatabase.close();
		}		
	}
	public class DataBaseHelper extends SQLiteOpenHelper {
		private static final String CREATE_USERLOG_SQL = "create table calllog(_id integer primary key, number text, time timestamp, note text)";
		private static final String DROP_USERLOG_SQL = "drop table if exists calllog";
		public static final String DB_NAME = "feelyou.db";
		public static final int DB_VERSION = 1;
		
		public DataBaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase sqliteDatabase) {
			sqliteDatabase.execSQL(CREATE_USERLOG_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_USERLOG_SQL);
			onCreate(db);
		}
	}
}
