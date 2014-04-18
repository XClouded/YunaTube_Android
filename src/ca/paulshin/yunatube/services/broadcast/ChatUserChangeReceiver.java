package ca.paulshin.yunatube.services.broadcast;

import java.lang.ref.WeakReference;

import ca.paulshin.yunatube.common.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class ChatUserChangeReceiver extends BroadcastReceiver {
	public static final String EXTRA_STATUS = "status";
	public static final String EXTRA_USER = "user";
	public static final String EXTRA_TO = "to";
	
	public interface OnChatUserChangeListener {
		public void onChatUserChange(Bundle bundle);
	}

	public static final String ACTION = "ca.paulshin.yunatube.intent.chatuser";

	public static ChatUserChangeReceiver register(OnChatUserChangeListener listener) {
		return new ChatUserChangeReceiver(listener);
	}

	private final WeakReference<OnChatUserChangeListener> listener;

	private ChatUserChangeReceiver(OnChatUserChangeListener listener) {
		this.listener = new WeakReference<ChatUserChangeReceiver.OnChatUserChangeListener>(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Utils.debug("-----------" + intent.getAction());
		OnChatUserChangeListener _listener = listener.get();
		if (_listener != null) {
			if (TextUtils.equals(intent.getAction(), ACTION)) {
				_listener.onChatUserChange(intent.getExtras());
			}
		}
	}
}