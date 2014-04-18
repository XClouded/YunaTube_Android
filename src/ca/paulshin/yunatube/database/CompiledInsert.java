package ca.paulshin.yunatube.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public abstract class CompiledInsert <E> {
	
	protected static final boolean isHoneycomb = Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT;
	
	public abstract long insert(E entity);
	public CompiledInsert(SQLiteDatabase db) { }
	
	protected final String emptyIfNull(String string) {
		return string == null ? "" : string;
	}
}