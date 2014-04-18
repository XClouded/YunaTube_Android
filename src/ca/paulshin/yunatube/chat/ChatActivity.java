package ca.paulshin.yunatube.chat;

import static ca.paulshin.yunatube.YunaTubeApplication.broadcast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import ca.paulshin.yunatube.GCMActivity;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.ChatDataLoader;
import ca.paulshin.yunatube.services.broadcast.ChatDataChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.ChatDataChangeReceiver.OnChatDataChangeListener;
import ca.paulshin.yunatube.services.broadcast.ChatUserChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.ChatUserChangeReceiver.OnChatUserChangeListener;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver.OnDataChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class ChatActivity extends YunaTubeBaseActivity implements OnChatUserChangeListener, OnChatDataChangeListener, OnDataChangeListener, OnClickListener {

	private ChatDataLoader loader;
	private String userName, deviceId, regId;

	private boolean stickerOff;

	private static final String IN = "IN";
	private static final String OUT = "OUT";
	private static final String NICKNAME = "NICKNAME";
	private static final Integer STICKER_OFFSET = 8511100;
	public static final String AVATAR_ID = "avatar_id";
	public static final String EXTRA_REFRESH_AVATAR = "refresh_avatar";

	private List<String> currentUsers;
	private ChatUserChangeReceiver chatUserChangeReceiver = null;
	private ChatDataChangeReceiver chatDataChangeReceiver = null;
	private SimpleDateFormat dateFormat = null;
	private SimpleDateFormat timeFormat = null;

	private InputMethodManager imm;

	private LinearLayout stickerLayout;
	private ListView chatList;
	private EditText text;
	private ChatAdapter chatAdapter;

	private Map<Integer, Integer> stickerMap;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		actionBarTitle.setText(R.string.supermenu_chat);

		chatDataChangeReceiver = ChatDataChangeReceiver.register((OnChatDataChangeListener) this);
		broadcast.registerReceiver(chatDataChangeReceiver, new IntentFilter(ChatDataChangeReceiver.ACTION));

		chatUserChangeReceiver = ChatUserChangeReceiver.register((OnChatUserChangeListener) this);
		broadcast.registerReceiver(chatUserChangeReceiver, new IntentFilter(ChatUserChangeReceiver.ACTION));

		setContentView(R.layout.item_chat_bg);

		// set sticker off
		Preference.remove(Constants.CHAT_STICKER_OFF);
		stickerOff = false;

		int height = Utils.getScreenSize()[1];
		LinearLayout bgLayout = (LinearLayout) findViewById(R.id.bg_layout);
		LayoutParams params = bgLayout.getLayoutParams();
		params.height = height - Utils.getPxFromDp(46);

		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.activity_chat, null);
		getWindow().addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		stickerMap = new HashMap<Integer, Integer>();
		for (int i = 1, offset = 8511100; i < 25; i++) {
			stickerMap.put(offset + i, Utils.getResId("sticker_" + i, R.drawable.class));
		}

		List<ChatData> chatData = new ArrayList<ChatData>();
		chatAdapter = new ChatAdapter(this, chatData, stickerMap);

		chatList = (ListView) findViewById(R.id.chat_list);
		chatList.setAdapter(chatAdapter);
		chatList.setDividerHeight(0);

		stickerLayout = (LinearLayout) findViewById(R.id.sticker_layout);
		stickerLayout.setVisibility(View.GONE);
		GridView stickerGrid = (GridView) stickerLayout.findViewById(R.id.sticker_grid);
		stickerGrid.setAdapter(new StickerAdapter(this));
		stickerGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				final String avatarId = Preference.get(AVATAR_ID, "1");
				final String message = String.valueOf(STICKER_OFFSET + (position + 1));

				ChatData data = new ChatData((byte) 1, userName, message, avatarId, timeFormat.format(new Date()));
				chatAdapter.add(data);
				chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);

				new AsyncTask<String, Void, Boolean>() {
					@Override
					protected Boolean doInBackground(String... params) {
						return loader.sendMessage(deviceId, userName, regId, avatarId, message, String.valueOf(System.currentTimeMillis() / 1000));
					}

					@Override
					protected void onPostExecute(Boolean result) {
						if (!result) {
							Utils.showToast(ChatActivity.this, R.string.chat_message_error);
							finish();
						}
					}
				}.execute();
			}
		});

		text = (EditText) findViewById(R.id.text);
		text.setInputType(0);
		text.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				text.setInputType(1);
				imm.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);

				stickerLayout.setVisibility(View.GONE);
			}
		});

		ImageButton icons = (ImageButton) findViewById(R.id.sticker);
		icons.setOnClickListener(this);
		ImageButton send = (ImageButton) findViewById(R.id.send);
		send.setOnClickListener(this);

		dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
		timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);

		loader = ChatDataLoader.getLoaderInstance();
		userName = Preference.getString(Constants.NICKNAME);
		// userName = TextUtils.isEmpty(userName) ? getString(R.string.nickname_noname) : userName;
		deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		regId = Preference.get(GCMActivity.PROPERTY_REG_ID, "");

		if (regId == null || TextUtils.isEmpty(regId)) {
			// error
		} else {
			new AsyncTask<String, Void, List<String>>() {
				@Override
				protected List<String> doInBackground(String... params) {
					return loader.registerUser(deviceId, userName, regId);
				}

				@Override
				protected void onPostExecute(List<String> result) {
					if (result == null) {
						Utils.showToast(ChatActivity.this, R.string.chat_register_error);
						finish();
						return;
					}
					currentUsers = result;

					showCurrentUsers();
				}
			}.execute();
		}

		// Etiquette
		ChatData data = new ChatData((byte) 2, null, getString(R.string.chat_etiquette), null, null);
		chatAdapter.add(data);
		chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);

		if (userName.startsWith(getString(R.string.nickname_noname) + "@")) {
			data = new ChatData((byte) 2, null, getString(R.string.chat_set_nickname, userName), null, null);
			chatAdapter.add(data);
			chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);
		}
	}

	@Override
	protected void onDestroy() {
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				return loader.unregisterUser(deviceId, userName, regId);
			}
		}.execute();

		if (chatUserChangeReceiver != null) {
			broadcast.unregisterReceiver(chatUserChangeReceiver);
		}

		if (chatDataChangeReceiver != null) {
			broadcast.unregisterReceiver(chatDataChangeReceiver);
		}

		super.onDestroy();
	}

	@Override
	public void onDataChange(Bundle bundle) {
		if (bundle != null && bundle.getBoolean(EXTRA_REFRESH_AVATAR)) {
			chatAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onChatUserChange(Bundle bundle) {
		if (bundle == null || currentUsers == null)
			return;

		String user = bundle.getString(ChatUserChangeReceiver.EXTRA_USER);
		ChatData data = null;
		String status = bundle.getString(ChatUserChangeReceiver.EXTRA_STATUS);
		if (!user.equals(userName)) {
			if (status.equals(IN)) {
				currentUsers.add(user);
				data = new ChatData((byte) 2, null, getString(R.string.chat_in, user), null, null);
			} else if (status.equals(OUT)) {
				currentUsers.remove(user);
				data = new ChatData((byte) 2, null, getString(R.string.chat_out, user), null, null);
			} else if (status.equals(NICKNAME)) {
				String to = bundle.getString(ChatUserChangeReceiver.EXTRA_TO);
				currentUsers.remove(user);
				currentUsers.add(to);
				if (TextUtils.equals(userName, to))
					return;
				data = new ChatData((byte) 2, null, getString(R.string.chat_nick_change, user, to), null, null);
			}
			Collections.sort(currentUsers);
			chatAdapter.add(data);
			chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);
		}
	}

	@Override
	public void onChatDataChange(Bundle bundle) {
		if (bundle == null)
			return;

		String talker = bundle.getString(ChatDataChangeReceiver.EXTRA_USER);
		String message = bundle.getString(ChatDataChangeReceiver.EXTRA_TEXT);
		if (stickerOff && message.startsWith("85111") && !TextUtils.equals(talker, getString(R.string.chat_toseung)))
			return;

		if (!talker.equals(userName)) {
			ChatData data = new ChatData((byte) 0, talker, message, bundle.getString(ChatDataChangeReceiver.EXTRA_ICON_ID), timeFormat.format(new Date()));
			chatAdapter.add(data);
			chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sticker:
			text.setInputType(0);
			imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
			stickerLayout.setVisibility(View.VISIBLE);
			break;

		case R.id.send:
			final String message = text.getText().toString();
			if (TextUtils.isEmpty(message))
				return;

			final String avatarId = Preference.get(AVATAR_ID, "1");

			ChatData data = new ChatData((byte) 1, userName, message, avatarId, timeFormat.format(new Date()));
			chatAdapter.add(data);
			chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);

			text.setText("");
			new AsyncTask<String, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(String... params) {
					return loader.sendMessage(deviceId, userName, regId, avatarId, message, String.valueOf(System.currentTimeMillis() / 1000));
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if (!result) {
						Utils.showToast(ChatActivity.this, R.string.chat_message_error);
						finish();
					}
				}
			}.execute();

			break;
		}
	}

	private void showCurrentUsers() {
		if (currentUsers == null || currentUsers.size() == 0)
			return;

		StringBuilder users = new StringBuilder();
		for (int i = 0; i < currentUsers.size(); i++) {
			if (TextUtils.equals(currentUsers.get(i), userName))
				users.append("[").append(currentUsers.get(i)).append("]");
			else
				users.append(currentUsers.get(i));
			if (i < currentUsers.size() - 1)
				users.append(", ");
		}

		ChatData data = new ChatData((byte) 2, null, getString(R.string.chat_current_users, currentUsers.size(), users.toString()), null, null);
		chatAdapter.add(data);
		chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_chat, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.sticker);
		if (item != null) {
			item.setChecked(stickerOff);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			handleClosing();
			return true;

		case R.id.chat_avatar:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.chat_set_avatar_title);
			final AlertDialog dialog = builder.create();

			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.dialog_chat_avatar_grid, null);
			GridView avatars = (GridView) view.findViewById(R.id.avatar_grid);
			avatars.setAdapter(new AvatarAdapter(ChatActivity.this));
			avatars.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Preference.put(AVATAR_ID, String.valueOf(position + 1));

					Intent intent = new Intent();
					intent.setAction(DataChangeReceiver.ACTION);
					intent.putExtra(EXTRA_REFRESH_AVATAR, true);
					YunaTubeApplication.broadcast.sendBroadcast(intent);
					dialog.dismiss();
				}
			});

			dialog.setView(view);
			dialog.show();
			return true;

		case R.id.chat_users:
			showCurrentUsers();
			return true;

		case R.id.nickname:
			showNicknameDialog();
			return true;

		case R.id.sticker:
			stickerOff = !stickerOff;
			item.setChecked(stickerOff);
			Utils.showToast(this, stickerOff ? R.string.chat_sticker_off : R.string.chat_sticker_on);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (stickerLayout.getVisibility() == View.VISIBLE)
			stickerLayout.setVisibility(View.GONE);
		else
			handleClosing();
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	private void handleClosing() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.chat_close_title).setMessage(R.string.chat_close_message).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
				dialog.dismiss();
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showNicknameDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
		input.setLines(1);
		input.setHint(R.string.nickname_length);
		if (Preference.contains(Constants.NICKNAME))
			input.setText(Preference.getString(Constants.NICKNAME));
		alert.setTitle(R.string.nickname_set);
		alert.setMessage(R.string.nickname_desc);
		alert.setView(input);
		alert.setCancelable(false);
		alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				final String newNick = input.getText().toString().trim();
				final String oldNick = userName;
				if (!TextUtils.isEmpty(newNick) && !TextUtils.equals(newNick, oldNick)) {
					Preference.put(Constants.NICKNAME, newNick);
					userName = newNick;
					new AsyncTask<String, Void, Boolean>() {
						@Override
						protected Boolean doInBackground(String... params) {
							return loader.nickChange(deviceId, regId, oldNick, newNick);
						}

						@Override
						protected void onPostExecute(Boolean result) {
							// if (result) {
							ChatData data = new ChatData((byte) 2, null, getString(R.string.chat_nick_change, oldNick, newNick), null, null);
							chatAdapter.add(data);
							chatList.smoothScrollToPosition(chatAdapter.getCount() - 1);
							// }
						}
					}.execute();
				}
			}
		});
		alert.show();
	}
}