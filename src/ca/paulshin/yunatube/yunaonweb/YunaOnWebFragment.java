package ca.paulshin.yunatube.yunaonweb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;

public class YunaOnWebFragment extends YunaTubeBaseFragment implements OnClickListener {
	private RadioGroup searchTypeGroup, searchKeywordGroup;
	private String[] keywordsValues;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_yuna_on_web, null);

		searchTypeGroup = (RadioGroup) view.findViewById(R.id.search_type);
		searchKeywordGroup = (RadioGroup) view.findViewById(R.id.search_keyword);
		keywordsValues = Utils.ctx.getResources().getStringArray(R.array.search_keyword_value);

		view.findViewById(R.id.search_youtube).setOnClickListener(this);
		view.findViewById(R.id.search_google).setOnClickListener(this);
		view.findViewById(R.id.search_daum).setOnClickListener(this);
		view.findViewById(R.id.search_naver).setOnClickListener(this);
		view.findViewById(R.id.search_bing).setOnClickListener(this);
		view.findViewById(R.id.search_nate).setOnClickListener(this);

		return view;
	}

	@Override
	public int getTitle() {
		return R.string.supermenu_search;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return "yuna_on_web - android";
	}

	@Override
	public void onClick(View v) {
		int checkedTypeId = searchTypeGroup.getCheckedRadioButtonId();
		int checkedKeywordId = searchKeywordGroup.getCheckedRadioButtonId();

		String keyword = keywordsValues[checkedKeywordId == R.id.keyword_ko ? 0 : 1];
		String urlString = "";
		String searchUrlFormat = null;

		switch (v.getId()) {
		case R.id.search_youtube:
			if (checkedTypeId == R.id.search_news) {
				String warning = getString(R.string.search_news_unavailable, "YouTube");
				Utils.showToast(getActivity(), warning);
				return;
			} else {
				searchUrlFormat = Constants.SEARCH_URL_YOUTUBE;
				urlString = String.format(searchUrlFormat, keyword);
			}
			break;
		case R.id.search_google:
			if (checkedTypeId == R.id.search_news) {
				searchUrlFormat = Constants.NEWS_URL_GOOGLE;
				urlString = String.format(searchUrlFormat, keyword);
			} else {
				searchUrlFormat = Constants.SEARCH_URL_GOOGLE;
				urlString = String.format(searchUrlFormat, keyword, keyword, keyword);
			}
			break;
		case R.id.search_daum:
			searchUrlFormat = checkedTypeId == R.id.search_news ? Constants.NEWS_URL_DAUM : Constants.SEARCH_URL_DAUM;
			urlString = String.format(searchUrlFormat, keyword);
			break;
		case R.id.search_naver:
			searchUrlFormat = checkedTypeId == R.id.search_news ? Constants.NEWS_URL_NAVER : Constants.SEARCH_URL_NAVER;
			urlString = String.format(searchUrlFormat, keyword);
			break;
		case R.id.search_bing:
			if (checkedTypeId == R.id.search_news) {
				String warning = getString(R.string.search_news_unavailable, "Bing");
				Utils.showToast(getActivity(), warning);
				return;
			} else {
				searchUrlFormat = Constants.SEARCH_URL_BING;
				urlString = String.format(searchUrlFormat, keyword);
			}
			break;
		case R.id.search_nate:
			searchUrlFormat = checkedTypeId == R.id.search_news ? Constants.NEWS_URL_NATE : Constants.SEARCH_URL_NATE;
			urlString = String.format(searchUrlFormat, keyword);
			break;
		}
		Uri uri = Uri.parse(urlString);
		getActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}
}
