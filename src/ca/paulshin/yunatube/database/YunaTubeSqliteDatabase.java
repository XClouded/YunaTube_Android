package ca.paulshin.yunatube.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ca.paulshin.yunatube.common.Persistent;
import ca.paulshin.yunatube.database.Schema.TableMyFaves;
import ca.paulshin.yunatube.database.Schema.YouTubeColumns;

public class YunaTubeSqliteDatabase extends YunaTubeDatabase {

	public static final String DATABASE_NAME = "YunaTube";
	public static final int DATABASE_VERSION = 4;

	private static Context context;

	private static final YunaTubeSqliteDatabase instance = new YunaTubeSqliteDatabase();

	public static YunaTubeSqliteDatabase getInstance() {
		return instance;
	}

	protected SQLiteDatabase getDBConnection() {
		init();
		return db;
	}

	public static void init(Context context) {
		YunaTubeSqliteDatabase.context = context;
		instance.init();
	}

	@Override
	protected void init() {
		if (db == null || !db.isOpen()) {
			if (helper == null) {
				helper = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

					@Override
					public void onCreate(SQLiteDatabase db) {
						db.execSQL(Persistent.CREATE);
						db.execSQL(TableMyFaves.CREATE);
					}

					@Override
					public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						switch (oldVersion) {
						case 1:
							db.execSQL("DROP TABLE IF EXISTS " + TableMyFaves.table);
							db.execSQL(TableMyFaves.CREATE);
						case 2:
							db.execSQL(Persistent.CREATE);
						case 3:
							db.execSQL("DROP TABLE IF EXISTS new");
							db.execSQL("DROP TABLE IF EXISTS greetings");
							db.execSQL("DROP TABLE IF EXISTS competitions");
							db.execSQL("DROP TABLE IF EXISTS iceshows");
							db.execSQL("DROP TABLE IF EXISTS etc");
							db.execSQL("DROP TABLE IF EXISTS knc");
							db.execSQL("DROP TABLE IF EXISTS songs");
							db.execSQL("DROP TABLE IF EXISTS muhan");
							db.execSQL("DROP TABLE IF EXISTS guru");
						}
					}
				};
			}

			db = helper.getWritableDatabase();

			db.rawQuery("PRAGMA synchronous = OFF", new String[] {}).close();
			db.rawQuery("PRAGMA journal_mode = MEMORY", new String[] {}).close();
		}
	}

	public long insertEntity(String table, String category, String title, String url) {
		ContentValues values = new ContentValues();
		values.put(YouTubeColumns.category, category);
		values.put(YouTubeColumns.title, title);
		values.put(YouTubeColumns.url, url);

		return insert(table, values);
	}

	public long insertToMyFaves(String category, String title, String url) {
		ContentValues values = new ContentValues();
		values.put(TableMyFaves.category, category);
		values.put(TableMyFaves.title, title);
		values.put(TableMyFaves.alias, title);
		values.put(TableMyFaves.url, url);

		return insert(TableMyFaves.table, values);
	}

	public Cursor fetchMyFaves() throws SQLException {
		return db.query(TableMyFaves.table, new String[] { TableMyFaves._ID, TableMyFaves.title, TableMyFaves.category, TableMyFaves.alias, TableMyFaves.url }, null, null, null, null,
				TableMyFaves.alias);
	}

	public Cursor fetchMyFave(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, TableMyFaves.table, new String[] { TableMyFaves._ID, TableMyFaves.title, TableMyFaves.category, TableMyFaves.alias, TableMyFaves.url }, TableMyFaves._ID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean isInMyFaves(String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(").append(TableMyFaves.url).append(")").append(" FROM ").append(TableMyFaves.table)
		.append(" WHERE ").append(TableMyFaves.url).append("='").append(url).append("'");
		
		Cursor mCount = db.rawQuery(sb.toString(), null);
		if (mCount != null) {
			mCount.moveToFirst();
			int count = mCount.getInt(0);
			mCount.close();
			return count > 0;
		}
		return false;
	}

	public boolean updateMyFaves(long rowId, String alias) {
		ContentValues args = new ContentValues();
		args.put(TableMyFaves.alias, alias);

		return db.update(TableMyFaves.table, args, TableMyFaves._ID + "=" + rowId, null) > 0;
	}

	public boolean removeMyClip(String url) {
		return db.delete(TableMyFaves.table, TableMyFaves.url + "='" + url + "'", null) > 0;
	}

	public boolean deleteMyClip(long rowId) {
		return db.delete(TableMyFaves.table, TableMyFaves._ID + "=" + rowId, null) > 0;
	}
}