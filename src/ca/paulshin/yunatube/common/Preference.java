package ca.paulshin.yunatube.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import ca.paulshin.yunatube.YunaTubeApplication;

public class Preference {

	private static Context context;
	private static SharedPreferences pref;
	private static Editor edit;

	private Preference() { }
	
	public static void init(Context context) {
		Preference.context = context;
		initPreference();
	}

	public static void initPreference() {
		
		if(pref == null || edit == null) {
			
			Preference.pref = PreferenceManager.getDefaultSharedPreferences(context);
			Preference.edit = Preference.pref.edit();
		}
	}

	public static boolean contains(final String key) {

		initPreference();
		return Preference.pref.contains(key);
	}

	public static void put(final String key, final int value) {

		initPreference();
		if(YunaTubeApplication.debuggable) Log.w("Preference", key + " : " + value);
		Preference.edit.putInt(key, value).commit();
	}

	public static void put(final String key, final long value) {

		initPreference();
		if(YunaTubeApplication.debuggable) Log.w("Preference", key + " : " + value);
		Preference.edit.putLong(key, value).commit();
	}

	public static void put(final String key, final float value) {

		initPreference();
		if(YunaTubeApplication.debuggable) Log.w("Preference", key + " : " + value);
		Preference.edit.putFloat(key, value).commit();
	}

	public static void put(final String key, final String value) {

		initPreference();
		if(YunaTubeApplication.debuggable) Log.w("Preference", key + " : " + value);
		Preference.edit.putString(key, value).commit();
	}

	public static void put(final String key, final boolean value) {

		initPreference();
		if(YunaTubeApplication.debuggable) Log.w("Preference", key + " : " + value);
		Preference.edit.putBoolean(key, value).commit();
	}

	public static int get(final String key, final int defValue) {

		initPreference();
		return Preference.pref.getInt(key, defValue);
	}

	public static long get(final String key, final long defValue) {

		initPreference();
		return Preference.pref.getLong(key, defValue);
	}

	public static float get(final String key, final float defValue) {

		initPreference();
		return Preference.pref.getFloat(key, defValue);
	}

	public static String get(final String key, final String defValue) {

		initPreference();
		return Preference.pref.getString(key, defValue);
	}

	public static boolean get(final String key, final boolean defValue) {

		initPreference();
		return Preference.pref.getBoolean(key, defValue);
	}

	public static int getInt(final String key) {

		initPreference();
		return Preference.pref.getInt(key, 0);
	}

	public static long getLong(final String key) {

		initPreference();
		return Preference.pref.getLong(key, 0l);
	}

	public static float getFloat(final String key) {

		initPreference();
		return Preference.pref.getFloat(key, 0f);
	}

	public static String getString(final String key) {

		initPreference();
		return Preference.pref.getString(key, "");
	}

	public static boolean getBoolean(final String key) {

		initPreference();
		return Preference.pref.getBoolean(key, false);
	}

	public static void clear() {
		
		initPreference();
		Preference.edit.clear().commit();
	}

	public static void remove(final String key) {

		initPreference();
		Preference.edit.remove(key).commit();
	}
}