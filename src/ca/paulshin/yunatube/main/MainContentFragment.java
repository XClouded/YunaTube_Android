package ca.paulshin.yunatube.main;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.MainDataLoader;
import ca.paulshin.yunatube.jukebox.JukeboxActivity;
import ca.paulshin.yunatube.youtube.YouTubeActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

public class MainContentFragment extends YunaTubeBaseFragment implements OnClickListener/* , OnItemClickListener */{
	private static final int CARD_ALPHA = 150;
	private static final String IS_GUIDE_SHOWN = "is_guide_shown";

	private ImageView todayPhoto, slide;
	private String todayPhotoUrl;

	private Picasso picasso;
	private View view;
	
	private static boolean fool = Utils.isAprilFools();

	private static final int GUIDE_DISAPPEAR = 10001;
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == GUIDE_DISAPPEAR) {
				Animation animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
				slide.startAnimation(animationFadeOut);
				slide.setVisibility(View.GONE);
				Preference.put(IS_GUIDE_SHOWN, true);
			}
			super.handleMessage(msg);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(fool ? R.layout.fool_fragment_main_content : R.layout.fragment_main_content, null);

		view.findViewById(R.id.hot_news).setOnClickListener(this);
		view.findViewById(R.id.fact_more).setOnClickListener(this);
		view.findViewById(R.id.jukebox).setOnClickListener(this);
		view.findViewById(R.id.faves).setOnClickListener(this);
		todayPhoto = (ImageView) view.findViewById(R.id.today_photo);
		todayPhoto.setOnClickListener(this);

		picasso = Picasso.with(getActivity());

		// Show the slider guide
		slide = (ImageView) view.findViewById(R.id.slide_guide);
		if (!Preference.contains(IS_GUIDE_SHOWN) || !Preference.get(IS_GUIDE_SHOWN, true)) {
			handler.sendEmptyMessageDelayed(GUIDE_DISAPPEAR, 1500);
		} else {
			slide.setVisibility(View.GONE);
		}

//		displayCountdown(view);

		return view;
	}

	private void displayCountdown(View view) {
		Date today = new Date();
		final Calendar todayCal = Calendar.getInstance();
		todayCal.setTime(today);

		final Calendar dDayCal = Calendar.getInstance();
		dDayCal.set(2014, 1, 20);

		long remaining = dDayCal.getTimeInMillis() - todayCal.getTimeInMillis();

//		TextView countDown = (TextView) view.findViewById(R.id.olympics_countdown);
//		if (remaining > 0)
//			countDown.setText(getString(R.string.dday_sochi, Utils.convertMillis(remaining)));
//		else
//			countDown.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Utils.isNetworkAvailable())
			loadData();
	}

	private void loadNoticeFactPhoto() {
		AsyncTask<String, Void, String[]> task = new AsyncTask<String, Void, String[]>() {
			@Override
			protected String[] doInBackground(String... params) {
				return MainDataLoader.getLoaderInstance().loadMainFile();
			}

			@Override
			protected void onPostExecute(String[] result) {
				if (!MainContentFragment.this.isDetached() && result != null && result.length == 3) {
					view.findViewById(R.id.notice_progress).setVisibility(View.GONE);
					// set notice
					String noticeText = result[0];
					TextView notice = (TextView) view.findViewById(R.id.notice);
					notice.setText(noticeText);

					// set fact and photo
					String factText = result[1];
					todayPhotoUrl = result[2];

					int padding = Utils.getPxFromDp(7);
					LinearLayout factPhoto = (LinearLayout) view.findViewById(R.id.fact_photo);
					factPhoto.setBackgroundResource(R.drawable.card_background_selector_blue);
					factPhoto.setPadding(padding, padding, padding, padding);
					Drawable background = factPhoto.getBackground();
					background.setAlpha(CARD_ALPHA);

					TextView tv = (TextView) view.findViewById(R.id.fact_text);
					tv.setText(factText);

					picasso.load(todayPhotoUrl).placeholder(R.drawable.stub).centerCrop().resize(300, 300).into(todayPhoto);
					view.findViewById(R.id.fact_progress).setVisibility(View.GONE);
					factPhoto.setVisibility(View.VISIBLE);
				} else {
					Utils.debug("Couldn't load main data");
				}
			}
		};

		if (Build.VERSION.SDK_INT >= 11)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			task.execute();
	}
	
	private void loadFoolClips() {
		List<NameValuePair> data = new LinkedList<NameValuePair>();
		data.add(new BasicNameValuePair("Jp2LLLXPg2U", getString(R.string.fool_1)));
		data.add(new BasicNameValuePair("g2SdmPp75qk", getString(R.string.fool_2)));
		data.add(new BasicNameValuePair("TESyjYOIjTY", getString(R.string.fool_3)));
		data.add(new BasicNameValuePair("bgJyTqI54MM", getString(R.string.fool_4)));
		data.add(new BasicNameValuePair("KtedCLyAb-I", getString(R.string.fool_5)));
		
		displayNewClips(data);
	}

	private void loadNewClips() {
		AsyncTask<String, Void, List<NameValuePair>> task = new AsyncTask<String, Void, List<NameValuePair>>() {
			@Override
			protected List<NameValuePair> doInBackground(String... params) {
				return MainDataLoader.getLoaderInstance().loadNewClips();
			}

			@Override
			protected void onPostExecute(List<NameValuePair> result) {
				if (result == null)
					Utils.showToast(getActivity(), R.string.message_server_unavailable);
				else if(result.size() > 0) {
					if (isAdded())
						displayNewClips(result);
				} else {
					Utils.debug("Couldn't load main data");
				}
			}
		};

		if (Build.VERSION.SDK_INT >= 11)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			task.execute();

	}

	private void displayNewClips(List<NameValuePair> list) {
		LinearLayout newClipsLayout = (LinearLayout) view.findViewById(R.id.new_clips_layout);
		newClipsLayout.removeAllViews();

		String title, ytid;

		final float scale = getResources().getDisplayMetrics().density;
		int width = (int) (80 * scale + 0.5f);
		int height = (int) (50 * scale + 0.5f);

		LinearLayout.LayoutParams youtubeLayoutParams = new LinearLayout.LayoutParams((int) LayoutParams.MATCH_PARENT, (int) LayoutParams.WRAP_CONTENT);
		youtubeLayoutParams.setMargins(0, 6, 0, 6);
		LinearLayout.LayoutParams thumbNailLayoutParams = new LinearLayout.LayoutParams(width, height);
		thumbNailLayoutParams.setMargins(0, 6, 0, 6);
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams((int) LayoutParams.MATCH_PARENT, (int) LayoutParams.WRAP_CONTENT);

		LinearLayout youtubeLayout;
		ImageView thumbnail;
		TextView text;
		String thumbnailUrl;

		for (NameValuePair item : list) {
			ytid = item.getName();
			title = item.getValue();

			int padding = Utils.getPxFromDp(7);

			youtubeLayout = new LinearLayout(getActivity());
			youtubeLayout.setLayoutParams(youtubeLayoutParams);
			youtubeLayout.setGravity(Gravity.CENTER_VERTICAL);
			youtubeLayout.setBackgroundResource(R.drawable.card_background_selector_blue);
			youtubeLayout.setPadding(padding, 3, padding, 3);
			Drawable background = youtubeLayout.getBackground();
			background.setAlpha(CARD_ALPHA);

			thumbnail = new ImageView(getActivity());
			thumbnail.setLayoutParams(thumbNailLayoutParams);
			thumbnailUrl = String.format(Constants.CLIP_THUMBNAIL_URL, ytid);
			picasso.load(thumbnailUrl).placeholder(R.drawable.stub).centerCrop().resize(160, 85).into(thumbnail);
			youtubeLayout.addView(thumbnail);

			text = new TextView(getActivity());
			textParams.setMargins(padding, 0, 0, 0);
			text.setLayoutParams(textParams);
			text.setTextAppearance(getActivity(), android.R.style.TextAppearance_Small);
			text.setText(title);
			youtubeLayout.addView(text);

			final String _ytid = ytid;
			newClipsLayout.addView(youtubeLayout);

			youtubeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (fool) {
						String url = "http://www.youtube.com/v/" + _ytid;
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						startActivity(i);
					}
					else {
						Bundle bundle = new Bundle();
						bundle.putString(YouTubeActivity.EXTRA_YOUTUBE_YTID, _ytid);
						Intent intent = new Intent(getActivity(), YouTubeActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
		}
		view.findViewById(R.id.new_clips_progress).setVisibility(View.GONE);
		newClipsLayout.setVisibility(View.VISIBLE);
	}

	private void loadData() {
		if (fool) {
			todayPhotoUrl = "http://paulshin.ca/temp/sanghwa.jpg";
			picasso.load(todayPhotoUrl).placeholder(R.drawable.stub).centerCrop().resize(300, 300).into(todayPhoto);
			loadFoolClips();
		}
		else {
			loadNoticeFactPhoto();
			loadNewClips();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.hot_news:
			if (!Utils.isNetworkAvailable())
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			else {
				intent = new Intent(getActivity(), HotNewsActivity.class);
				startActivity(intent);
			}
			break;

		case R.id.fact_more:
			Uri uri = Uri.parse(Constants.YUNAFACT);
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			break;

		case R.id.today_photo:
			intent = new Intent(getActivity(), TodayPhotoActivity.class);
			intent.putExtra(TodayPhotoActivity.EXTRA_TODAY_PHOTO_URL, todayPhotoUrl);
			startActivity(intent);
			break;

		case R.id.jukebox:
			intent = new Intent(getActivity(), JukeboxActivity.class);
			startActivity(intent);
			break;

		case R.id.faves:
			// ((MainActivity)getActivity()).setFragment(new MyClipsFragment(), true);
			intent = new Intent(getActivity(), MyFavesActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public int getTitle() {
		return 0;
	}

	@Override
	public String getTextTitle() {
		return "";
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "home - android";
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_settings, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings) {
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}