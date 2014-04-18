package ca.paulshin.yunatube.game;

import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;

public class RankingActivity extends YunaTubeBaseActivity {
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_URL = "extra_url";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		actionBarTitle.setText(R.string.game_score_rank);
		
		setContentView(R.layout.fragment_webview);

		String url = getIntent().getStringExtra(EXTRA_URL);
		WebView mWebView = (WebView) findViewById(R.id.view);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				hideDialog();
			}
		});

		mWebView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		return super.onOptionsItemSelected(item);
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
}
