package ca.paulshin.yunatube.common;

import static ca.paulshin.yunatube.database.YunaTubeSqliteDatabase.getInstance;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class Persistent {

	private static final String TABLE_NAME = "Persistent";
	private static final String KEY1 = "key1_column";
	private static final String KEY2 = "key2_column";
	private static final String VALUE = "value_column";
	
	public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY1 + " TEXT, " + KEY2 + " TEXT, " + VALUE + " TEXT, UNIQUE(" + KEY1 + ", " + KEY2 + ") ON CONFLICT REPLACE)";

	private Persistent() { }
	
	public static boolean contains(String key1, String key2) {
		
		boolean[] check = {!TextUtils.isEmpty(key1), !TextUtils.isEmpty(key2)};
		if(!check[0] && !check[1]) throw new IllegalArgumentException();
		
		Cursor cursor = null;
		boolean result = false;
		
		if(check[0] && check[1]) {
			
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY1+"=? AND "+KEY2+"=?", new String[]{key1, key2}, null, null, null);
			result = cursor.moveToFirst();
		}
		
		if(check[0] && !check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY1+"=?", new String[]{key1}, null, null, null);
			result = cursor.moveToFirst();
		}
		
		if(!check[0] && check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY2+"=?", new String[]{key2}, null, null, null);
			result = cursor.moveToFirst();
		}
		
		if(cursor != null) cursor.close();
		
		return result;
	}
	
	public static long put(String key, String value) {
		return put(null, key, value);
	}
	
	public static long put(String key1, String key2, String value) {
		
		boolean[] check = {!TextUtils.isEmpty(key1), !TextUtils.isEmpty(key2)};
		if(!check[0] && !check[1]) throw new IllegalArgumentException();
		
		remove(key1, key2);
		
		ContentValues values = new ContentValues();
		if(check[0]) values.put(KEY1, key1);
		if(check[1]) values.put(KEY2, key2);
		values.put(VALUE, value);
		
		return getInstance().insert(TABLE_NAME, values);
	}
	
	public static String getString(String key1, String key2) {
		List<String> list = get(key1, key2);
		if(list.isEmpty()) {
			return "";
		
		} else {
			return list.get(0);
		}
	}
	
	public static List<String> getKeys(String key1, String key2) {
		
		boolean[] check = {!TextUtils.isEmpty(key1), !TextUtils.isEmpty(key2)};
		if(check[0] == check[1]) throw new IllegalArgumentException();
		
		Cursor cursor = null;
		
		LinkedList<String> list = new LinkedList<String>();
		
		if(check[0] && !check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{KEY2}, KEY1+"=?", new String[]{key1}, null, null, null);
		}
		
		if(!check[0] && check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{KEY1}, KEY2+"=?", new String[]{key2}, null, null, null);
		}
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				list.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		
		if(cursor != null) cursor.close();
		
		return list;
	}
	
	public static List<String> get(String key1, String key2) {
		
		boolean[] check = {!TextUtils.isEmpty(key1), !TextUtils.isEmpty(key2)};
		if(!check[0] && !check[1]) throw new IllegalArgumentException();
		
		Cursor cursor = null;
		
		LinkedList<String> list = new LinkedList<String>();
		
		if(check[0] && check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY1+"=? AND "+KEY2+"=?", new String[]{key1, key2}, null, null, null);
		}
		
		if(check[0] && !check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY1+"=?", new String[]{key1}, null, null, null);
		}
		
		if(!check[0] && check[1]) {
			cursor = getInstance().query(TABLE_NAME, new String[]{VALUE}, KEY2+"=?", new String[]{key2}, null, null, null);
		}
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				list.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		
		if(cursor != null) cursor.close();
		
		return list;
	}
	
	public static int remove(String key1, String key2) {
		
		boolean[] check = {!TextUtils.isEmpty(key1), !TextUtils.isEmpty(key2)};
		if(!check[0] && !check[1]) throw new IllegalArgumentException();
		
		if(check[0] && check[1]) {
			return getInstance().delete(TABLE_NAME, KEY1+"=? AND "+KEY2+"=?", new String[]{key1, key2});
		}
		
		if(check[0] && !check[1]) {
			return getInstance().delete(TABLE_NAME, KEY1+"=?", new String[]{key1});
		}
		
		if(!check[0] && check[1]) {
			return getInstance().delete(TABLE_NAME, KEY2+"=?", new String[]{key2});
		}
		
		return 0;
	}
}