package ca.paulshin.yunatube.services.broadcast;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class DataChangeReceiver extends BroadcastReceiver {
	public interface OnDataChangeListener {
		public void onDataChange(Bundle bundle);
	}

	public static final String ACTION = "ca.paulshin.yunatube.intent.data";

	public static DataChangeReceiver register(OnDataChangeListener listener) {
		return new DataChangeReceiver(listener);
	}

	private final WeakReference<OnDataChangeListener> listener;

	private DataChangeReceiver(OnDataChangeListener listener) {
		this.listener = new WeakReference<DataChangeReceiver.OnDataChangeListener>(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		OnDataChangeListener _listener = listener.get();
		if (_listener != null) {
			if (TextUtils.equals(intent.getAction(), ACTION)) {
				_listener.onDataChange(intent.getExtras());
			}
		}
	}
}