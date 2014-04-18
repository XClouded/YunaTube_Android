package ca.paulshin.yunatube.links;

import java.util.ArrayList;

import android.os.Bundle;
import ca.paulshin.yunatube.R;

public class FanPagesFragment extends WebsiteBaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		websites = new ArrayList<Website>();
		websites.add(createWebsite("fevers"));
		websites.add(createWebsite("yunacafe"));
		websites.add(createWebsite("yunagall"));
		websites.add(createWebsite("fsgall"));
		websites.add(createWebsite("fsgallbestposts"));
		websites.add(createWebsite("allthatyuna"));
		websites.add(createWebsite("yunaforum"));
		websites.add(createWebsite("russia"));
		websites.add(createWebsite("china"));
	}

	@Override
	public int getTitle() {
		return R.string.submenu_links_fan;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return "family_site(fan) - android";
	}

	@Override
	protected String getPrefix() {
		return "links_fan_";
	}
}
