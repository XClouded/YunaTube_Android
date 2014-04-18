package ca.paulshin.yunatube.jukebox;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.youtube.YouTubeFailureRecoveryActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerView;

public class JukeboxActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnFullscreenListener, OnItemClickListener {
	private static final String JUKEBOX_NOTICE = "jukebox_notice_2";

	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
	private boolean fullscreen;

	private YouTubePlayerView playerView;
	private YouTubePlayer player;
	private TextView titleView;
	private ListView listView;
	private ProgressBar loadingView;
	private MyPlayerStateChangeListener playerStateChangeListener;

	private int currentIndex = 0;
	private int totalIndex = 0;
	private PlaylistAdapter playlistAdapter;

	private List<NameValuePair> songList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jukebox);

		if (!Preference.contains(JUKEBOX_NOTICE)) {
			Preference.put(JUKEBOX_NOTICE, true);
			Utils.showToast(this, R.string.jukebox_notice_2, false);
		}

		playerView = (YouTubePlayerView) findViewById(R.id.player);
		
		if (Utils.isNetworkAvailable())
			loadJukeboxList();
		else
			Utils.showToast(this, R.string.message_network_unavailable);

		titleView = (TextView) findViewById(R.id.title);
		loadingView = (ProgressBar) findViewById(R.id.loading);
		listView = (ListView) findViewById(R.id.playlist);
		listView.setOnItemClickListener(this);
	}
	
	private void loadJukeboxList() {
		// Load Detail
		new AsyncTask<String, Void, List<NameValuePair>>() {
			@Override
			protected List<NameValuePair> doInBackground(String... params) {
				return YouTubeDataLoader.getLoaderInstance().loadClips("6", "1");
			}

			protected void onPostExecute(List<NameValuePair> clips) {
				if (clips != null && clips.size() > 0) {
					songList = clips;
					totalIndex = songList.size();

					playerView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, JukeboxActivity.this);
					playerStateChangeListener = new MyPlayerStateChangeListener();
				}
			};
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		currentIndex = position;
		player.loadVideo(songList.get(position).getName());
		playlistAdapter.notifyDataSetChanged();
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

	/**
	 * YouTube API
	 */
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		this.player = player;
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		if (!wasRestored) {
			playlistAdapter = new PlaylistAdapter();
			listView.setAdapter(playlistAdapter);
			if (songList.size() > 0)
				player.loadVideo(songList.get(0).getName());
			else
				Utils.showToast(this, R.string.message_server_unavailable);
		}

		int controlFlags = player.getFullscreenControlFlags();
		setRequestedOrientation(PORTRAIT_ORIENTATION);
		controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
		player.setFullscreenControlFlags(controlFlags);
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

	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
		String playerState = "UNINITIALIZED";

		@Override
		public void onLoading() {
			loadingView.setVisibility(View.VISIBLE);
			titleView.setVisibility(View.GONE);
			playerState = "LOADING";
			Utils.debug(playerState);
		}

		@Override
		public void onLoaded(String videoId) {
			playlistAdapter.notifyDataSetChanged();
			titleView.setText((currentIndex + 1) + " / " + totalIndex + " " + songList.get(currentIndex).getValue());
			loadingView.setVisibility(View.GONE);
			titleView.setVisibility(View.VISIBLE);
			playerState = String.format("LOADED %s", videoId);
			Utils.debug(playerState);
		}

		@Override
		public void onVideoStarted() {
			playerState = "VIDEO_STARTED @ " + currentIndex;
			Utils.debug(playerState);
		}

		@Override
		public void onVideoEnded() {
			playerState = "VIDEO_ENDED @ " + currentIndex;
			Utils.debug(playerState);
			currentIndex = (currentIndex == totalIndex - 1) ? 0 : currentIndex + 1;
			player.loadVideo(songList.get(currentIndex).getName());
		}

		@Override
		public void onAdStarted() {
		}

		@Override
		public void onError(ErrorReason reason) {
			playerState = "ERROR (" + reason + ")";
			if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
				// When this error occurs the player is released and can no longer be used.
				// player = null;
				// setControlsEnabled(false);
			}
			Utils.debug(playerState);
		}
	}

	/**
	 * Listview adapter
	 */
	private class PlaylistAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return songList.size();
		}

		@Override
		public Object getItem(int position) {
			return songList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.row_jukebox, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.playing = (ImageView) convertView.findViewById(R.id.playing);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.title.setText(songList.get(position).getValue());
			holder.title.setTypeface(null, position == currentIndex ? Typeface.BOLD : Typeface.NORMAL);
			holder.playing.setVisibility(position == currentIndex ? View.VISIBLE : View.GONE);
			return convertView;

		}

		class ViewHolder {
			TextView title;
			ImageView playing;
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
	}
}
