package ca.paulshin.yunatube.common;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;

public class Utils {
	// debugging purpose
	private static final String DEBUG_ID = "YunaTube";

	public enum Network {
		WIFI, MOBILE, NOT_CONNECTED
	};

	public static Context ctx;

	public static void init(Context context) {
		ctx = context;
	}

	/**
	 * Helper Methods
	 */

	public static int getInteger(int resId) {
		return ctx.getResources().getInteger(resId);
	}

	public static String getString(int resId) {
		return ctx.getString(resId);
	}

	public static String getString(int resId, Object... formatArgs) {
		return ctx.getString(resId, formatArgs);
	}

	public static String[] getStringArray(int resourceId) {
		return ctx.getResources().getStringArray(resourceId);
	}

	public static int getColor(int resId) {
		return ctx.getResources().getColor(resId);
	}

	public static int getResId(String resourceName, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(resourceName);
			return idField.getInt(idField);
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable) {
				e.printStackTrace();
				Utils.debug("No resource ID found for: " + resourceName + " / " + c);
			}
			return 1;
		}
	}
	
	public static int getPxFromDp(int dp) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
		return px;
	}

	public static void debug(String msg) {
		if (YunaTubeApplication.debuggable) {
			Log.d(DEBUG_ID, msg);
		}
	}

	public static void debugE(String msg) {
		if (YunaTubeApplication.debuggable) {
			Log.d(DEBUG_ID, msg);
			// debugE(msg);
		}
	}

	public static void debug(String tag, String msg) {
		debug(tag + ": " + msg);
	}

	public static void debugE(String tag, String msg) {
		debugE(tag + ": " + msg);
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void showToast(final Activity activity, int message) {
		showToast(activity, message, true);
	}

	public static void showToast(final Activity activity, int message, boolean isShort) {
		if (activity == null) return;
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_message, (ViewGroup) activity.findViewById(R.id.message_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.message);
		text.setText(message);

		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		if (isShort)
			toast.setDuration(Toast.LENGTH_SHORT);
		else
			toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public static void showToast(final Activity activity, String message) {
		showToast(activity, message, true);
	}

	public static void showToast(final Activity activity, String message, boolean isShort) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_message, (ViewGroup) activity.findViewById(R.id.message_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.message);
		text.setText(message);

		Toast toast = new Toast(activity);
		toast.setGravity(Gravity.BOTTOM, 0, 100);
		if (isShort)
			toast.setDuration(Toast.LENGTH_SHORT);
		else
			toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
	
	public static void share(final Activity activity, String shareTitle, String subject, String text) {
		final Intent intent = new Intent(Intent.ACTION_SEND);

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);

		activity.startActivity(Intent.createChooser(intent, shareTitle));
	}

	public static void emailToMe(final Activity activity, String subject, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { activity.getString(R.string.feedback_email) });
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);

		activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.feedback_chooser)));
	}

	public static boolean isDeviceLocaleKorean() {
		String locale = ctx.getResources().getConfiguration().locale.getDisplayName();
		return locale.contains(getString(R.string.korean));
	}

	public static boolean isAppLocaleKorean() {
		return ctx.getResources().getConfiguration().locale.toString().toLowerCase().contains("ko");
	}

	public static String getTime(String timestamp) {
		Long currentTime = System.currentTimeMillis() / 1000;
		Long prevTime = Long.parseLong(((TextUtils.isEmpty(timestamp)) ? String.valueOf(currentTime) : timestamp));
		StringBuilder sb = new StringBuilder();

		Long interval = currentTime - prevTime;
		if (interval < 60) {
			sb.append(interval).append("s");
		} else if (interval < 3600) {
			sb.append((int) (interval / 60)).append("m");
		} else if (interval < 86400) {
			sb.append((int) (interval / 3600)).append("h");
		} else if (interval < 2592000) {
			sb.append((int) (interval / 86400)).append("d");
		} else if (interval < 80352000) {
			sb.append((int) (interval / 2592000)).append("mo");
		} else {
			sb.append((int) (interval / 964224000)).append("y");
		}

		return sb.toString();
	}

	public static String getFilePath(String... files) {
		StringBuilder sb = new StringBuilder().append(Environment.getExternalStorageDirectory());
		for (String file : files)
			sb.append("/").append(file);
		return sb.toString();
	}

	public static String convertMillis(long millis) {
		// Display meaningful time remaining instead of milliseconds.
		int d = (int) (millis / 24 / 60 / 60 / 1000);
		long remaining = millis - (d * 24 * 60 * 60 * 1000);
		int h = (int) (remaining / 60 / 60 / 1000);
		remaining = remaining - (h * 60 * 60 * 1000);
		int m = (int) (remaining / 60 / 1000);
		remaining = remaining - (m * 60 * 1000);
		int s = (int) (remaining / 1000);

		// return String.format("%d days, %d hours, %d minutes, %d seconds", d, h, m, s);
		// return String.format("%d days", d);
		return String.valueOf(d);
	}

	public static int[] getScreenSize() {

		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return new int[] { display.getWidth(), display.getHeight() };
		} else {
			Point point = new Point();
			display.getSize(point);
			return new int[] { point.x, point.y };
		}
	}

	public static String getParameterizedUrl(String url, List<NameValuePair> params) {
		if (!url.endsWith("?"))
			url += "?";

		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += paramString;
		return url;
	}

	/*
	 * Network
	 */

	public static Network getConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return Network.WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return Network.MOBILE;
		}
		return Network.NOT_CONNECTED;
	}

	public static boolean isNetworkAvailable() {
		return getConnectivity() != Network.NOT_CONNECTED;
	}
	
	public static void refreshGallery(Context context, File file) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri contentUri = Uri.fromFile(file);
	    mediaScanIntent.setData(contentUri);
	    context.sendBroadcast(mediaScanIntent);
	}
	
	public static boolean isAprilFools() {
		Date today = new Date();
		final Calendar cal1 = Calendar.getInstance();
		cal1.setTime(today);

		final Calendar cal2 = Calendar.getInstance();
		cal2.set(2014, 2, 19);

		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		String locale = ctx.getResources().getConfiguration().locale.toString();
		return Locale.KOREA.toString().contains(locale) && sameDay;
	}
}
