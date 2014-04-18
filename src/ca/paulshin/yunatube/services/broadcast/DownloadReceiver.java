package ca.paulshin.yunatube.services.broadcast;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class DownloadReceiver extends BroadcastReceiver {
	public interface OnDownloadListener {
		public void onDownload(Bundle bundle);
	}

	public static final String ACTION = "ca.paulshin.yunatube.intent.download";

	public static DownloadReceiver register(OnDownloadListener listener) {
		return new DownloadReceiver(listener);
	}

	private final WeakReference<OnDownloadListener> listener;

	private DownloadReceiver(OnDownloadListener listener) {
		this.listener = new WeakReference<DownloadReceiver.OnDownloadListener>(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		OnDownloadListener _listener = listener.get();
		if (_listener != null) {
			if (TextUtils.equals(intent.getAction(), ACTION)) {
				_listener.onDownload(intent.getExtras());
			}
		}
	}
}