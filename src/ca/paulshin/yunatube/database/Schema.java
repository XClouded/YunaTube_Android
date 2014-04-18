package ca.paulshin.yunatube.database;

import android.provider.BaseColumns;

public class Schema {
	public interface YouTubeColumns extends BaseColumns {
		public String category = "category";
		public String title = "title";
		public String url = "url";
	}
	
	public interface TableMyFaves extends YouTubeColumns {
		
		public String table = "MyClips";

		public String alias = "alias";
		
		public String CREATE = "CREATE TABLE " + table + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ title + " TEXT, "
				+ alias + " TEXT, "
				+ url + " INTEGER, "
				+ category + " TEXT)";
	}
}