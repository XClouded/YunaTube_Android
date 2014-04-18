package ca.paulshin.yunatube.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class YunaTubeDatabase {
	
	protected SQLiteOpenHelper helper;
	protected SQLiteDatabase db;
	protected abstract void init();

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		
		init();
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
		
		init();
		Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public long insertOrUpdate(String table, ContentValues values, String idColumn, String idValue) {
		init();

		Cursor cursor = null;
		try {
			cursor = db.query(table, new String[]{ idColumn }, idColumn + "=?", new String[]{ idValue }, null, null, null);
			if(cursor.getCount() == 0) {
				return db.insert(table, null, values);
			} else {
				return db.update(table, values, idColumn + "=?", new String[]{ idValue });
			}
		} finally {
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	
	public long insert(String table, ContentValues values) {
		init();
		
		try {
			return db.insert(table, null, values);
		} catch(SQLException e) {
			return -1;
		}
	}
	
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		init();
		
		try {
			return db.update(table, values, whereClause, whereArgs);
		} catch(SQLException e) {
			return -1;
		}
	}
	
	public int delete(String table, String whereClause, String[] whereArgs) {
		init();
		
		try {
			return db.delete(table, whereClause, whereArgs);
		} catch(SQLException e) {
			return -1;
		}
	}
	
	public int delete(String table) {
		init();
		
		try {
			return db.delete(table, null, null);
		} catch(SQLException e) {
			return -1;
		}
	}
	
	public void execSQL(String sql) {
		init();
		
		try {
			db.execSQL(sql);
		} catch(SQLException e) { }
	}
	
	public void execSQL(String sql, Object[] bindArgs) {
		init();
		
		try {
			db.execSQL(sql, bindArgs);
		} catch(SQLException e) { }
	}
	
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		init();
		return db.rawQuery(sql, selectionArgs);
	}
}