package ca.paulshin.yunatube.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;

public class QuizFragment extends YunaTubeBaseFragment {
	private static final int FULL_SCORE = 4;
	private static final int VERSION_1_START = 1;
	private static final int VERSION_2_START = 101;
	private static final int VERSION_3_START = 201;
	private static final int VERSION_4_START = 301;

	private int score;
	private int tempScore = FULL_SCORE;
	private int time;
	private int currentQuestion;
	private int currentVersion;

	private TextView scoreView, resultView;
	private ImageButton startButton, rankButton, startButtonTwo, rankButtonTwo, startButtonThree, rankButtonThree, startButtonFour, rankButtonFour;
	private Button submitButton;
	private EditText enterNickname;
	private WebView webview;
	private LinearLayout ll_ranking;
	private RelativeLayout ll_result;
	private ScrollView ll_intro, ll_quiz;
	private ImageView correct, wrong;

	private boolean startFlag = false;

	private Handler mHandler;

	private View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_quiz, null);

		scoreView = (TextView) view.findViewById(R.id.score);
		resultView = (TextView) view.findViewById(R.id.score_result);

		ll_intro = (ScrollView) view.findViewById(R.id.intro);
		ll_quiz = (ScrollView) view.findViewById(R.id.quiz);
		ll_result = (RelativeLayout) view.findViewById(R.id.result);
		ll_ranking = (LinearLayout) view.findViewById(R.id.ranking);

		correct = (ImageView) view.findViewById(R.id.quiz_correct);
		wrong = (ImageView) view.findViewById(R.id.quiz_wrong);

		webview = (WebView) view.findViewById(R.id.rankingview);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				hideDialog();
			}
		});

		enterNickname = (EditText) view.findViewById(R.id.enter_nickname);
		startButton = (ImageButton) view.findViewById(R.id.quiz_start);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_quiz.setVisibility(View.VISIBLE);

				resumeTimer();
				startFlag = true;
				currentQuestion = VERSION_1_START;
				currentVersion = 1;
				new LoadQuiz().execute(currentQuestion);
			}
		});

		rankButton = (ImageButton) view.findViewById(R.id.quiz_rank);
		rankButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_ranking.setVisibility(View.VISIBLE);

				showDialog();
				webview.loadUrl(Constants.QUIZ_RANKING + "1");
			}
		});

		startButtonTwo = (ImageButton) view.findViewById(R.id.quiz_start_two);
		startButtonTwo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_quiz.setVisibility(View.VISIBLE);

				resumeTimer();
				startFlag = true;
				currentQuestion = VERSION_2_START;
				currentVersion = 2;
				new LoadQuiz().execute(currentQuestion);
			}
		});

		rankButtonTwo = (ImageButton) view.findViewById(R.id.quiz_rank_two);
		rankButtonTwo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_ranking.setVisibility(View.VISIBLE);

				showDialog();
				webview.loadUrl(Constants.QUIZ_RANKING + "2");
			}
		});
		
		startButtonThree = (ImageButton) view.findViewById(R.id.quiz_start_three);
		startButtonThree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_quiz.setVisibility(View.VISIBLE);

				resumeTimer();
				startFlag = true;
				currentQuestion = VERSION_3_START;
				currentVersion = 3;
				new LoadQuiz().execute(currentQuestion);
			}
		});

		rankButtonThree = (ImageButton) view.findViewById(R.id.quiz_rank_three);
		rankButtonThree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_ranking.setVisibility(View.VISIBLE);

				showDialog();
				webview.loadUrl(Constants.QUIZ_RANKING + "3");
			}
		});
		
		startButtonFour = (ImageButton) view.findViewById(R.id.quiz_start_four);
		startButtonFour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_quiz.setVisibility(View.VISIBLE);

				resumeTimer();
				startFlag = true;
				currentQuestion = VERSION_4_START;
				currentVersion = 4;
				new LoadQuiz().execute(currentQuestion);
			}
		});

		rankButtonFour = (ImageButton) view.findViewById(R.id.quiz_rank_four);
		rankButtonFour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ll_intro.setVisibility(View.GONE);
				ll_ranking.setVisibility(View.VISIBLE);

				showDialog();
				webview.loadUrl(Constants.QUIZ_RANKING + "4");
			}
		});

		submitButton = (Button) view.findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String nickname = enterNickname.getText().toString().trim();
				if (nickname.length() == 0) {
					Utils.showToast(getActivity(), getString(R.string.quiz_enter_nickname), true);
				} else if (nickname.contains(getString(R.string.quiz_filter_a)) && nickname.contains(getString(R.string.quiz_filter_n)) && nickname.contains(getString(R.string.quiz_filter_k))) {
					Utils.showToast(getActivity(), getString(R.string.quiz_reject), true);
				} else if (nickname.contains(getString(R.string.quiz_filter_yun)) && nickname.contains(getString(R.string.quiz_filter_ah)) && nickname.contains(getString(R.string.quiz_filter_ne))
						&& nickname.contains(getString(R.string.quiz_filter_ko))) {
					Utils.showToast(getActivity(), getString(R.string.quiz_reject), true);
				} else if (nickname.contains(getString(R.string.quiz_filter_nk))) {
					Utils.showToast(getActivity(), getString(R.string.quiz_reject), true);
				} else if (nickname.contains(getString(R.string.quiz_filter_neko))) {
					Utils.showToast(getActivity(), getString(R.string.quiz_reject), true);
				} else {
					submitButton.setEnabled(false);
					new SendNickname().execute(nickname);
				}
			}
		});

		init();

		this.view = view;
		return view;
	}

	private void init() {
		score = 0;
		time = 0;

		submitButton.setEnabled(true);
		enterNickname.setText("");

		ll_intro.setVisibility(View.VISIBLE);
		ll_ranking.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mHandler != null) {
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (startFlag) {
			resumeTimer();
		}
	}

	private void resumeTimer() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					sendEmptyMessageDelayed(0, 1000);
					updateTimer();
				} else if (msg.what == 1) {
					removeMessages(0);
				}
			}
		};
		mHandler.sendEmptyMessage(0);
	}

	private class LoadQuiz extends AsyncTask<Integer, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected JSONObject doInBackground(Integer... params) {
			return getJSON(params[0]);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			correct.setVisibility(View.GONE);
			TextView questionNumberView = (TextView) view.findViewById(R.id.question_number);
			TextView questionView = (TextView) view.findViewById(R.id.question);
			LinearLayout optionsContainer = (LinearLayout) view.findViewById(R.id.options);
			int numOptions = optionsContainer.getChildCount();
			Button optionsViews[] = new Button[numOptions];
			for (int i = 0; i < numOptions; i++) {
				optionsViews[i] = (Button) optionsContainer.getChildAt(i);
			}

			try {
				JSONObject quizSet = result.getJSONObject("quiz");
				String quizNum = quizSet.getString("number");
				quizNum = String.valueOf(Integer.parseInt(quizNum) - (currentVersion - 1) * 100);
				questionNumberView.setText(getString(R.string.quiz_num) + " : " + quizNum + " / 100");
				String question = quizSet.getString("question");
				questionView.setText(question);
				JSONArray options = quizSet.getJSONArray("options");
				for (int i = 0; i < options.length(); i++) {
					JSONObject childJSONObject = options.getJSONObject(i);
					String name = childJSONObject.getString("option");
					optionsViews[i].setText(name);
					final boolean isAnswer = "1".equals(childJSONObject.getString("is_answer"));
					optionsViews[i].setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (correct.getVisibility() != View.VISIBLE && wrong.getVisibility() != View.VISIBLE) {							
								if (isAnswer) {
									correct.setVisibility(View.VISIBLE);
	
									score += tempScore;
									tempScore = FULL_SCORE;
									scoreView.setText(getString(R.string.quiz_score) + " : " + String.valueOf(score));
									if (currentQuestion < currentVersion * 100) {
										new LoadQuiz().execute(++currentQuestion);
									} else {
										mHandler.sendEmptyMessage(1);
										resultView.setText(getString(R.string.quiz_result, score, getFormattedTime(), String.valueOf(getTotalScore())));
										ll_quiz.setVisibility(View.GONE);
										ll_result.setVisibility(View.VISIBLE);
									}
								} else {
									wrong.setVisibility(View.VISIBLE);
									tempScore--;
								}
							}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private JSONObject getJSON(Integer quizNum) {
			JSONObject result = null;
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://paulshin.ca/yunatube/res/quiz/quiz.php?q=" + quizNum + "&version=" + currentVersion);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					result = new JSONObject(builder.toString());
				} else {
					Utils.debug("Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}
	}

	private class SendNickname extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			sendNickname(params[0]);
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
			ll_result.setVisibility(View.GONE);
			ll_ranking.setVisibility(View.VISIBLE);

			showDialog();

			webview.loadUrl("http://paulshin.ca/yunatube/res/quiz/quiz_ranking.php?version=" + currentVersion + "&nickname=" + result + "#" + result);

			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(enterNickname.getWindowToken(), 0);
		}

		private void sendNickname(String nickname) {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://paulshin.ca/yunatube/res/quiz/quiz_submit_id.php?version=" + currentVersion + "&nickname=" + nickname.replace(" ", "%20") + "&time=" + time
					+ "&score=" + score);
			try {
				HttpResponse response = client.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void updateTimer() {
		if (wrong.getVisibility() == View.VISIBLE) {
			wrong.setVisibility(View.GONE);
		}

		TextView timeRemainingView = (TextView) view.findViewById(R.id.time_remaining);

		timeRemainingView.setText(getString(R.string.quiz_time) + " : " + getFormattedTime());
		time++;
	}

	private String getFormattedTime() {
		int min = time / 60;
		int sec = time % 60;
		return String.format("%02d : %02d", min, sec);
	}

	private int getTotalScore() {
		return (int) Math.round(score * 1200 / time);
	}

	@Override
	public int getTitle() {
		return R.string.supermenu_quiz;
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "quiz";
	}
}
