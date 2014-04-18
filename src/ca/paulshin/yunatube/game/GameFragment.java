package ca.paulshin.yunatube.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.game.GameAdapter.ViewHolder;
import ca.paulshin.yunatube.main.MainActivity;

import com.actionbarsherlock.view.MenuItem;

public class GameFragment extends YunaTubeBaseFragment implements OnItemClickListener, OnClickListener {
	private enum Level {
		EASY(8, 1, 3, 6, 3), HARD(10, 1.8, 4, 8, 5);
		
		public int items, hintCount, initTimer, hintTimer;
		public double factor;
		Level(int items, double factor, int hintCount, int initTimer, int hintTimer) {
			this.items = items;
			this.factor = factor;
			this.hintCount = hintCount;
			this.initTimer = initTimer;
			this.hintTimer = hintTimer;
		}
	};

	private Level level;

	private GridView gridView;
	private PicObject[] picIndices, picObjects;

	private TextView timer, correctAttempts, wrongAttempts, hint, result;
	private ImageView verdict;

	private int selectedIndex = -1, matchIndex;
	private int correctAttempt = 0, wrongAttempt = 0;
	private int hintCount, countdown;
	private int time = 0;
	private boolean isPlaying = false, isHintShowing = false, isLaunchingHint = true;

	private Handler handler = new Handler();
	private Handler timerHandler;
	private Handler hintHandler;
	private GameAdapter adapter;

	private void initImages() {
		picIndices = new PicObject[level.items * 2];
		picObjects = new PicObject[level.items];
		
		adapter = new GameAdapter(getActivity(), picIndices);
		int randomOne, randomTwo;
		
		// create random numbers
		List<Integer> list = new ArrayList<Integer>(71);
		for (int i = 0; i <= 70; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		
		for (int i = 0; i < level.items; i++) {
			randomOne = getAvailableRandom(-1);
			randomTwo = getAvailableRandom(randomOne);
			
			PicObject obj = new PicObject("http://paulshin.ca/yunatube/res/game/images/" + list.get(i) + ".jpeg", randomOne, randomTwo); // 0 to 70
			picIndices[randomOne] = obj;
			picIndices[randomTwo] = obj;
			picObjects[i] = obj;
		}
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game, null);

		gridView = (GridView) view.findViewById(R.id.panel);

		verdict = (ImageView) view.findViewById(R.id.result_image);
		view.findViewById(R.id.game_16set).setOnClickListener(this);
		view.findViewById(R.id.game_20set).setOnClickListener(this);

		timer = (TextView) view.findViewById(R.id.timer);
		correctAttempts = (TextView) view.findViewById(R.id.correct_attempts);
		wrongAttempts = (TextView) view.findViewById(R.id.wrong_attempts);
		hint = (TextView) view.findViewById(R.id.hint);
		result = (TextView) view.findViewById(R.id.result);

		return view;
	}

	@Override
	public int getTitle() {
		return R.string.supermenu_game;
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "game";
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
		if (picIndices[position].isDone)
			return;

		final ViewHolder holder = (ViewHolder) view.getTag();

		if (selectedIndex == -1) {
			selectedIndex = position;
			matchIndex = getMatchIndex(selectedIndex);
			holder.thumbnail.setVisibility(View.VISIBLE);
			holder.stub.setVisibility(View.GONE);
		} else {
			if (position == matchIndex) {
				holder.thumbnail.setVisibility(View.VISIBLE);
				holder.stub.setVisibility(View.GONE);
				picIndices[position].isDone = true;
				correctAttempt++;

				if (correctAttempt < level.items) {
					// result.setText("Correct");
					verdict.setImageResource(R.drawable.game_correct);
					verdict.setVisibility(View.VISIBLE);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// result.setText("");
							verdict.setVisibility(View.GONE);
						}
					}, 500);
				} else {
					pauseTimer();
					isPlaying = false;
					invalidateOptionsMenu();

					// calculate scores earned
					wrongAttempt = (wrongAttempt == 0) ? 1 : wrongAttempt;
					double factor = (double) (hintCount + 1) * correctAttempt / time / (correctAttempt + wrongAttempt);
					factor *= level.factor;
					// Utils.debug("hintcount : " + (hintCount + 1));
					// Utils.debug("correctAttempt : " + correctAttempt);
					// Utils.debug("time : " + time);
					// Utils.debug("wrongAttempt : " + (wrongAttempt + 1));
					// Utils.debug("factor : " + factor);

					final int scoreValue = (int) (factor * 10000);
					String nickname = Preference.getString(Constants.NICKNAME);

					String url = Constants.GAME_SUBMIT;
					List<NameValuePair> parameters = new LinkedList<NameValuePair>();
					parameters.add(new BasicNameValuePair("nickname", nickname));
					parameters.add(new BasicNameValuePair("score", String.valueOf(scoreValue)));
					url = Utils.getParameterizedUrl(url, parameters);
					
					new AsyncTask<String, Void, Boolean>() {
						@Override
						protected Boolean doInBackground(String... params) {
							HttpClient client = new DefaultHttpClient();
							HttpGet httpGet = new HttpGet(params[0]);
							try {
								client.execute(httpGet);
							} catch (Exception e) {
								if (YunaTubeApplication.debuggable)
									e.printStackTrace();
							}
							return true;
						}

						@Override
						protected void onPostExecute(Boolean result) {
							Dialog scoreDialog = new ScoreDialog(scoreValue);
							scoreDialog.show();
						}
					}.execute(url);
				}
			} else {
				holder.thumbnail.setVisibility(View.VISIBLE);
				holder.stub.setVisibility(View.GONE);
				View otherView = parent.getChildAt(selectedIndex);
				wrongAttempt++;

				gridView.setOnItemClickListener(null);

				// result.setText("Wrong");
				verdict.setImageResource(R.drawable.game_wrong);
				verdict.setVisibility(View.VISIBLE);
				final ViewHolder otherHolder = (ViewHolder) otherView.getTag();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						holder.thumbnail.setVisibility(View.GONE);
						otherHolder.thumbnail.setVisibility(View.GONE);
						holder.stub.setVisibility(View.VISIBLE);
						otherHolder.stub.setVisibility(View.VISIBLE);
						// result.setText("");
						verdict.setVisibility(View.GONE);
						gridView.setOnItemClickListener(GameFragment.this);
					}
				}, 500);
			}
			correctAttempts.setText("" + correctAttempt);
			wrongAttempts.setText("" + wrongAttempt);
			selectedIndex = -1;
		}
	}

	private int getMatchIndex(int pressedPosition) {
		PicObject pressedObject = picIndices[pressedPosition];
		return pressedPosition == pressedObject.indexOne ? pressedObject.indexTwo : pressedObject.indexOne;
	}

	private int getAvailableRandom(int except) {
		int random = 0;

		while (true) {
			random = (int) (Math.random() * level.items * 2);
			if (picIndices[random] == null && random != except)
				break;
		}

		return random;
	}

	private void showHint() {
		isPlaying = true;
		invalidateOptionsMenu();
		pauseTimer();
		adapter.setShowAll(true);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(null);
		hintHandler = new Handler() {
			public void handleMessage(Message msg) {
				sendEmptyMessageDelayed(0, 1000);
				result.setText("" + countdown--);
				hint.setText("");
				if (countdown == -1) {
					resumeTimer();
					result.setText("");
					removeMessages(0);
					countdown = level.hintTimer;
					adapter.setShowAll(false);
					gridView.setAdapter(adapter);
					gridView.setOnItemClickListener(GameFragment.this);
					hint.setText(Utils.getString(R.string.game_hint_label, hintCount));
					isHintShowing = false;
					isLaunchingHint = false;
					invalidateOptionsMenu();
				}
			}
		};
		hintHandler.sendEmptyMessage(0);
	}

	private boolean timerPaused;

	private void resumeTimer() {
		timerHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					timerPaused = false;
					sendEmptyMessageDelayed(0, 1000);
					updateTimer();
				} else if (msg.what == 1) {
					timerPaused = true;
					removeMessages(0);
				}
			}
		};
		timerHandler.sendEmptyMessage(0);
	}

	private void pauseTimer() {
		if (timerHandler != null) {
			timerHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		pauseTimer();
	}

	protected void updateTimer() {
		timer.setText(getFormattedTime());
		time++;
	}

	private String getFormattedTime() {
		int min = time / 60;
		int sec = time % 60;
		return String.format("%02d : %02d", min, sec);
	}

	private class ScoreDialog extends Dialog implements OnClickListener {
		public ScoreDialog(int scoreValue) {
			super(getActivity(), R.style.Theme_YunaTubeDialog);
			this.setContentView(R.layout.dialog_score);
			this.setCancelable(false);

			findViewById(R.id.score_replay).setOnClickListener(this);
			findViewById(R.id.score_rank).setOnClickListener(this);
			findViewById(R.id.score_close).setOnClickListener(this);
			TextView score = (TextView) findViewById(R.id.score);
			score.setText(Utils.getString(R.string.game_score_earned, scoreValue));
		}

		@Override
		public void onClick(View v) {
			MainActivity activity = (MainActivity) getActivity();

			switch (v.getId()) {
			case R.id.score_replay:
				dismiss();
				activity.setFragment(new GameFragment(), false);
				break;

			case R.id.score_rank:
				dismiss();
				String nickname = Preference.getString(Constants.NICKNAME);
				Intent intent = new Intent(activity, RankingActivity.class);
				intent.putExtra(RankingActivity.EXTRA_URL, String.format(Constants.GAME_RANKING, nickname, nickname));
				intent.putExtra(RankingActivity.EXTRA_TITLE, Constants.GAME_RANKING);
				startActivity(intent);
				break;

			case R.id.score_close:
				dismiss();
				break;
			}
		}
	}

	@Override
	public boolean allowFragmentReplace() {
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (timerPaused && isPlaying)
			resumeTimer();
	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
		menu.clear();
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.menu_game, menu);
	}

	@Override
	public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		if (isPlaying) {
			if (isHintShowing || hintCount == 0 || isLaunchingHint)
				menu.removeItem(R.id.hint);
			menu.removeItem(R.id.ranking);
		} else
			menu.removeItem(R.id.hint);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MainActivity activity = (MainActivity) getActivity();
		switch (item.getItemId()) {
		case R.id.hint:
			isHintShowing = true;
			invalidateOptionsMenu();
			selectedIndex = -1;
			showHint();
			if (hintCount > 1)
				Utils.showToast(getActivity(), Utils.getString(R.string.game_hint_remaining, --hintCount));
			else if (hintCount == 1) {
				hintCount--;
				Utils.showToast(getActivity(), Utils.getString(R.string.game_hint_nomore));
				hint.setVisibility(View.GONE);
			}
			return true;

		case R.id.refresh:
			activity.setFragment(new GameFragment(), false);
			return true;

		case R.id.ranking:
			String nickname = Preference.getString(Constants.NICKNAME).replace(" ", "%20").replace("<", "%3c").replace(">", "%3e");
			Intent intent = new Intent(activity, RankingActivity.class);
			intent.putExtra(RankingActivity.EXTRA_URL, String.format(Constants.GAME_RANKING, nickname, nickname));
			intent.putExtra(RankingActivity.EXTRA_TITLE, Constants.GAME_RANKING);
			startActivity(intent);
			return true;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		LinearLayout introLayout = (LinearLayout) getView().findViewById(R.id.intro);
		introLayout.setVisibility(View.GONE);

		switch (v.getId()) {
		case R.id.game_16set:
			level = Level.EASY;
			break;

		case R.id.game_20set:
			level = Level.HARD;
			break;
		}

		initImages();
		gridView.setAdapter(adapter);
		countdown = level.initTimer;
		hintCount = level.hintCount;
		showHint();
	}
}
