package ca.paulshin.yunatube.links;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;

public abstract class WebsiteBaseFragment extends YunaTubeBaseFragment implements OnItemClickListener {
	protected List<Website> websites;
	protected ListView listView;
	
	protected abstract String getPrefix();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_website, container, false);
		listView = (ListView) view.findViewById(android.R.id.list);
		ListAdapter listAdapter = new WebsiteListAdapter(getActivity(), R.layout.fragment_website, websites);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
		
		return view;
	}
	
	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		String url = "http://" + websites.get(position).getUrl();

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	protected Website createWebsite(String title) {
		title = getPrefix() + title;
		int thumbnailResId = getResources().getIdentifier(title, "drawable", this.getActivity().getPackageName());
		Bitmap thumbnail = BitmapFactory.decodeResource(getResources(), thumbnailResId);
		int titleResId = getResources().getIdentifier(title, "string", this.getActivity().getPackageName());
		int urlResId = getResources().getIdentifier(title + "_url", "string", this.getActivity().getPackageName());
		int descResId = getResources().getIdentifier(title + "_desc", "string", this.getActivity().getPackageName());
		getString(titleResId);
		getString(urlResId);
		getString(descResId);
		Website website = new Website(thumbnail, getString(titleResId), getString(urlResId), getString(descResId));
		return website;
	}
}
