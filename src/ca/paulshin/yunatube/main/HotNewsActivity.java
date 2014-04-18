package ca.paulshin.yunatube.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.services.GCMIntentService;

import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;

public class HotNewsActivity extends YunaTubeBaseActivity {
	public static final String EXTRA_FROM_GCM = "extra_from_gcm";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_webview);

		actionBarTitle.setText(R.string.hot_news_title);
		
		displayDialog();
		
		// Clear cookie
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		
		WebView mWebView = (WebView) findViewById(R.id.view);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.startsWith("http://")) {
					view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				hideDialog();
			}
		});

		mWebView.loadUrl(String.format(Constants.NEWS_URL, getString(R.string.language)));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GCMIntentService.NOTIFICATION_ID);
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
	public void onBackPressed() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getBoolean(EXTRA_FROM_GCM)) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
		finish();
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
