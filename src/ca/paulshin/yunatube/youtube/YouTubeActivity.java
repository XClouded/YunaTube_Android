package ca.paulshin.yunatube.youtube;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static ca.paulshin.yunatube.database.YunaTubeSqliteDatabase.getInstance;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.services.GCMIntentService;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubeActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnFullscreenListener, OnClickListener, OnCheckedChangeListener {
	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

	public static final String EXTRA_YOUTUBE_YTID = "extra_ytid";
	public static final String EXTRA_PUSH_YTID = "push_ytid";
	public static final String EXTRA_MYCLIP_SAVE = "extra_myclip_save";

	private static final String ROTATION = "rotation";
	private static final String ROTATION_GUIDE = "rotation_guide";
	private static final String DOWNLOAD_GUIDE = "download_guide";
	
	private String sTitle, yTitle, ytid;

	private ImageView myClips;

	private boolean fullscreen, isSaved;
	private YouTubePlayerView playerView;

	private YouTubeCommentListView listView;
	private TextView emptyView;
	
	private YouTubePlayer player;
	private CheckBox rotation;
	
	private int playerControlFlags;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_youtube);
		ytid = Preference.contains(EXTRA_PUSH_YTID) ? Preference.getString(EXTRA_PUSH_YTID) : getIntent().getStringExtra(EXTRA_YOUTUBE_YTID);
		Preference.remove(EXTRA_PUSH_YTID);
		
		rotation = (CheckBox)findViewById(R.id.rotation);
		if (!Preference.contains(ROTATION))
			Preference.put(ROTATION, true);

		// Load Detail
		new AsyncTask<String, Void, Clip>() {
			@Override
			protected Clip doInBackground(String... params) {
				return YouTubeDataLoader.getLoaderInstance().loadDetails(params[0]);
			}

			protected void onPostExecute(Clip clip) {
				if (clip == null) {
					Utils.showToast(YouTubeActivity.this, R.string.message_server_unavailable);
					finish();
				}
				else {
					playerView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, YouTubeActivity.this);
					sTitle = clip.getStitle();
					yTitle = clip.getYtitle();

					findViewById(R.id.loading).setVisibility(View.GONE);
					
					TextView category = (TextView) findViewById(R.id.youtube_category);
					category.setText(sTitle);

					TextView title = (TextView) findViewById(R.id.youtube_title);
					title.setText(yTitle);
				}
			};
		}.execute(ytid);

		// Load comments
		emptyView = (TextView) findViewById(R.id.empty);
		playerView = (YouTubePlayerView) findViewById(R.id.player);
		myClips = (ImageView) findViewById(R.id.youtube_add_to_myclips);

		View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_footer_message, null, false);
		listView = (YouTubeCommentListView) findViewById(android.R.id.list);
		listView.init(footerView);
		listView.setDivider(null);
		listView.setDividerHeight(0);
		listView.setEmptyView(emptyView);
		listView.setYoutubeUrl(ytid);
		listView.load();

		// Check if this is already in myClips
		resetSaveButtonState();

		// Set Button listeners
		myClips.setOnClickListener(this);

		ImageView webview = (ImageView) findViewById(R.id.youtube_webview);
		webview.setOnClickListener(this);

		ImageView comment = (ImageView) findViewById(R.id.youtube_comment);
		comment.setOnClickListener(this);

		ImageView share = (ImageView) findViewById(R.id.youtube_share);
		share.setOnClickListener(this);

		ImageView report = (ImageView) findViewById(R.id.youtube_report);
		report.setOnClickListener(this);
		
		if (!Preference.contains(ROTATION_GUIDE) || !Preference.get(ROTATION_GUIDE, true)) {
			Preference.put(ROTATION_GUIDE, true);
			Utils.showToast(this, R.string.rotation_guide, false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GCMIntentService.NOTIFICATION_ID);
	}

	/**
	 * YouTube API
	 */
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		this.player = player;
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		if (!wasRestored) {
			player.loadVideo(ytid);
		}

		setRequestedOrientation(PORTRAIT_ORIENTATION);
		playerControlFlags = player.getFullscreenControlFlags();

		rotation.setOnCheckedChangeListener(this);
		rotation.setChecked(Preference.getBoolean(ROTATION));
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return playerView;
	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		fullscreen = isFullscreen;
		doLayout();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		fullscreen = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
		doLayout();
	}

	private void doLayout() {
		LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
		if (fullscreen) {
			// When in fullscreen, the visibility of all other views than the player should be set to
			// GONE and the player should be laid out across the whole screen.
			playerParams.width = LayoutParams.MATCH_PARENT;
			playerParams.height = LayoutParams.MATCH_PARENT;
		} else {
			ViewGroup.LayoutParams otherViewsParams = listView.getLayoutParams();
			playerParams.width = otherViewsParams.width = MATCH_PARENT;
			playerParams.height = WRAP_CONTENT;
			playerParams.weight = 0;
			otherViewsParams.height = WRAP_CONTENT;
		}
	}

	private void resetSaveButtonState() {
		isSaved = getInstance().isInMyFaves(ytid);
		myClips.setImageResource(isSaved ? R.drawable.ic_star_full : R.drawable.ic_star_empty);
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

	private void openCommentDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(140) });
		input.setLines(4);
		input.setHint(R.string.message_hint);
		alert.setTitle(R.string.youtube_comment);
		alert.setView(input);
		alert.setCancelable(false);
		alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				if (TextUtils.isEmpty(value)) {
					Utils.showToast(YouTubeActivity.this, R.string.message_enter_data);
				} else {
					String userName = Preference.getString(Constants.NICKNAME);
					if (TextUtils.isEmpty(userName)) {
						Utils.showToast(YouTubeActivity.this, R.string.nickname_must);
						return;
					}
					userName = TextUtils.isEmpty(userName) ? getString(R.string.nickname_noname) : userName;
					String deviceId = Secure.getString(YouTubeActivity.this.getContentResolver(), Secure.ANDROID_ID);
					listView.update(ytid, userName, value, String.valueOf(System.currentTimeMillis() / 1000), deviceId);
				}
			}
		});

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.youtube_add_to_myclips:
			if (isSaved) {
				if (getInstance().removeMyClip(ytid)) {
					// Signal myClip change
					Intent finishIntent = new Intent();
					finishIntent.setAction(DataChangeReceiver.ACTION);
					finishIntent.putExtra(EXTRA_MYCLIP_SAVE, true);
					YunaTubeApplication.broadcast.sendBroadcast(finishIntent);

					Utils.showToast(YouTubeActivity.this, R.string.cmenu_pop_remove_successful);
					resetSaveButtonState();
				}
			} else {
				long id = getInstance().insertToMyFaves(sTitle, yTitle, ytid);
				if (id > 0) {
					// Signal myClip change
					Intent finishIntent = new Intent();
					finishIntent.setAction(DataChangeReceiver.ACTION);
					finishIntent.putExtra(EXTRA_MYCLIP_SAVE, true);
					YunaTubeApplication.broadcast.sendBroadcast(finishIntent);

					Utils.showToast(YouTubeActivity.this, getString(R.string.cmenu_my_faves_add_successful, yTitle));
					resetSaveButtonState();
				} else
					Utils.showToast(YouTubeActivity.this, "ERROR: " + getString(R.string.cmenu_my_faves_add_failure, yTitle));
			}
			break;

		case R.id.youtube_webview:
			String url = "http://www.youtube.com/v/" + ytid;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;

		case R.id.youtube_comment:
			if (!listView.isBusy)
				openCommentDialog();
			else
				Utils.showToast(YouTubeActivity.this, R.string.youtube_wait);
			break;

		case R.id.youtube_share:
//			Utils.share(YouTubeActivity.this, getString(R.string.youtube_share_title), getString(R.string.youtube_share_subject), getString(R.string.youtube_share_text, yTitle, ytid));
//			Preference.put(DOWNLOAD_GUIDE, false);
			if (!Preference.contains(DOWNLOAD_GUIDE) || !Preference.get(DOWNLOAD_GUIDE, true)) {
				Preference.put(DOWNLOAD_GUIDE, true);
				Dialog downloadDialog = new DownloadDialog(this);
				downloadDialog.show();
			}
			else {
				String dlUrl = "http://ssyoutube.com/watch?v=" + ytid;
				Intent dlIntent = new Intent(Intent.ACTION_VIEW);
				dlIntent.setData(Uri.parse(dlUrl));
				startActivity(dlIntent);
			}
			break;

		case R.id.youtube_report:
			report();
		}
	}

	private void report() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.report_title).setMessage(R.string.report_message).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Load Detail
				new AsyncTask<String, Void, Boolean>() {
					@Override
					protected Boolean doInBackground(String... params) {
						return YouTubeDataLoader.getLoaderInstance().report(params[0]);
					}

					protected void onPostExecute(Boolean result) {
						if (result) {
							Utils.showToast(YouTubeActivity.this, R.string.report_successful);
						} else {
							Utils.showToast(YouTubeActivity.this, R.string.message_server_unavailable);
						}
					};
				}.execute(ytid);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		// Create the AlertDialog object and return it
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (player != null) {
			if (isChecked) {
				player.setFullscreenControlFlags(playerControlFlags | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
				Preference.put(ROTATION, true);
			}
			else {
				player.setFullscreenControlFlags(playerControlFlags);
				Preference.put(ROTATION, false);
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
	}
}
