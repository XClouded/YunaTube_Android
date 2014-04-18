package ca.paulshin.yunatube.links;

import java.util.ArrayList;

import android.os.Bundle;
import ca.paulshin.yunatube.R;

public class OfficialPagesFragment extends WebsiteBaseFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		websites = new ArrayList<Website>();
		websites.add(createWebsite("yunakim"));
		websites.add(createWebsite("yunaaaa"));
		websites.add(createWebsite("facebook"));
		websites.add(createWebsite("youtube"));
		websites.add(createWebsite("allthatskate"));
		websites.add(createWebsite("allthatskate_twitter"));
		websites.add(createWebsite("allthatskate_youtube"));
		websites.add(createWebsite("allthatsports"));
	}

	@Override
	public int getTitle() {
		return R.string.submenu_links_official;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}
	
	@Override
	protected String getTrackerId() {
		return "family_site(official) - android";
	}

	@Override
	protected String getPrefix() {
		return "links_official_";
	}
}
