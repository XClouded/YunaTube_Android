package ca.paulshin.yunatube.services.broadcast;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class ChatDataChangeReceiver extends BroadcastReceiver {
	public static final String EXTRA_USER = "user";
	public static final String EXTRA_ICON_ID = "icon_id";
	public static final String EXTRA_TEXT = "text";
	
	public interface OnChatDataChangeListener {
		public void onChatDataChange(Bundle bundle);
	}

	public static final String ACTION = "ca.paulshin.yunatube.intent.chatdata";

	public static ChatDataChangeReceiver register(OnChatDataChangeListener listener) {
		return new ChatDataChangeReceiver(listener);
	}

	private final WeakReference<OnChatDataChangeListener> listener;

	private ChatDataChangeReceiver(OnChatDataChangeListener listener) {
		this.listener = new WeakReference<ChatDataChangeReceiver.OnChatDataChangeListener>(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		OnChatDataChangeListener _listener = listener.get();
		if (_listener != null) {
			if (TextUtils.equals(intent.getAction(), ACTION)) {
				_listener.onChatDataChange(intent.getExtras());
			}
		}
	}
}