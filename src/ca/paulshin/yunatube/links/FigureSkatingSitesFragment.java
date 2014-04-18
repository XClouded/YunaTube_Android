package ca.paulshin.yunatube.links;

import java.util.ArrayList;

import android.os.Bundle;
import ca.paulshin.yunatube.R;

public class FigureSkatingSitesFragment extends WebsiteBaseFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		websites = new ArrayList<Website>();
		websites.add(createWebsite("isu"));
		websites.add(createWebsite("fsu"));
		websites.add(createWebsite("golden"));
		websites.add(createWebsite("icenetwork"));
	}

	@Override
	public int getTitle() {
		return R.string.submenu_links_figureskating;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}
	
	@Override
	protected String getTrackerId() {
		return "family_site(fs) - android";
	}

	@Override
	protected String getPrefix() {
		return "links_figureskating_";
	}
}
