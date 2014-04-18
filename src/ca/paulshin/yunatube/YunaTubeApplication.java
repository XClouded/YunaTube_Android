package ca.paulshin.yunatube;

import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.database.YunaTubeSqliteDatabase;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;

public class YunaTubeApplication extends Application {
	public static final boolean debuggable = false;

	public static int versionCode;
	public static int memoryClass;
	public static float densityDpi;
	
	public List<CollectionSet> collectionList;
	public static LocalBroadcastManager broadcast;

	@Override
	public void onCreate() {
		final Context ctx = getApplicationContext();

		YunaTubeSqliteDatabase.init(ctx);
		Preference.init(ctx);
		Utils.init(ctx);

		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (Exception e) {
			versionCode = 0;
		}

		memoryClass = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryClass();
		densityDpi = getResources().getDisplayMetrics().densityDpi / 160f;
		
		broadcast = LocalBroadcastManager.getInstance(ctx);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
