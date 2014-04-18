package ca.paulshin.yunatube.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;

public class WebViewFragment extends YunaTubeBaseFragment {
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_URL = "extra_url";

	private String url;

	public static WebViewFragment getInstance(int title, String url) {
		WebViewFragment fragment = new WebViewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_TITLE, title);
		bundle.putString(EXTRA_URL, url);
		fragment.setArguments(bundle);
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_webview, null);

		url = getArguments().getString(EXTRA_URL);

		if (Utils.isNetworkAvailable()) {
			showDialog();
		}
		
		WebView mWebView = (WebView) view.findViewById(R.id.view);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.contains(".pdf")) {
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

		mWebView.loadUrl(url);

		return view;
	}

	@Override
	public int getTitle() {
		return getArguments().getInt(EXTRA_TITLE);
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return String.format("webview_%s - android", getString(getTitle()));
	}
}