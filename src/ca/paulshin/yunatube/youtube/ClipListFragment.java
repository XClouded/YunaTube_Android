package ca.paulshin.yunatube.youtube;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.ObjectLoader;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.main.MainActivity.BackPressed;

public class ClipListFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<NameValuePair>>, OnItemClickListener, BackPressed {
	public static final String EXTRA_CID = "cid";
	public static final String EXTRA_SID = "sid";
	public static final String EXTRA_TITLE = "title";

	private ListView listView;
	private ProgressBar loading;
	private BaseAdapter adapter;
	private List<NameValuePair> data;

	public static ClipListFragment getInstance(String cid, String sid, String title) {
		ClipListFragment fragment = new ClipListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CID, cid);
		bundle.putString(EXTRA_SID, sid);
		bundle.putString(EXTRA_TITLE, title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new ClipListAdapter(getActivity(), null);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		if (!Utils.isNetworkAvailable())
			Utils.showToast(getActivity(), R.string.message_network_unavailable);
		else
			getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_clip_list, null);

		listView = (ListView) view.findViewById(R.id.list);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		return view;
	}

	@Override
	public int getTitle() {
		return 0;
	}

	@Override
	public String getTextTitle() {
		return getArguments().getString(EXTRA_TITLE);
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "youtube_clip - android";
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		NameValuePair item = data.get(position);
		Bundle bundle = new Bundle();
		bundle.putString(YouTubeActivity.EXTRA_YOUTUBE_YTID, item.getName());
		Intent intent = new Intent(getActivity(), YouTubeActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public Loader<List<NameValuePair>> onCreateLoader(int i, Bundle bundle) {
		return new ObjectLoader<List<NameValuePair>>(getActivity()) {
			@Override
			public List<NameValuePair> loadInBackground() {
				List<NameValuePair> clips = YouTubeDataLoader.getLoaderInstance().loadClips(getArguments().getString(EXTRA_CID), getArguments().getString(EXTRA_SID));
				return clips;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<NameValuePair>> loader, List<NameValuePair> data) {
		listView.setAdapter(new ClipListAdapter(getActivity(), data));
		this.data = data;
		if (data == null)
			Utils.showToast(getActivity(), R.string.message_server_unavailable);
		else
			loading.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<List<NameValuePair>> loader) {
		listView.setAdapter(null);
		loading.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();

		MainActivity activity = (MainActivity) getActivity();
		activity.updateFragmentInfo(this);
	}

	@Override
	public boolean onBackPressed() {
		return true;
	}
}
