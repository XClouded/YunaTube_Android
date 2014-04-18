package ca.paulshin.yunatube.youtube;

import java.util.List;

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

public class SectionListFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<String[]>>, OnItemClickListener {
	public static final String EXTRA_CID = "cid";
	public static final String EXTRA_TITLE = "title";

	private ListView listView;
	private ProgressBar loading;
	private BaseAdapter adapter;
	private List<String[]> data;

	public static SectionListFragment getInstance(String cid, int title) {
		SectionListFragment fragment = new SectionListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CID, cid);
		bundle.putInt(EXTRA_TITLE, title);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new SectionListAdapter(getActivity(), null);
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
		return getArguments().getInt(EXTRA_TITLE);
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "youtube_section - android";
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		String[] item = data.get(position);
		MainActivity activity = (MainActivity) getActivity();
		activity.setFragment(ClipListFragment.getInstance(item[0], item[1], item[2]), true);
	}

	@Override
	public Loader<List<String[]>> onCreateLoader(int i, Bundle bundle) {
		return new ObjectLoader<List<String[]>>(getActivity()) {
			@Override
			public List<String[]> loadInBackground() {
				List<String[]> sections = YouTubeDataLoader.getLoaderInstance().loadSections(getArguments().getString(EXTRA_CID));
				return sections;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<String[]>> loader, List<String[]> data) {
		listView.setAdapter(new SectionListAdapter(getActivity(), data));
		this.data = data;
		if (data == null)
			Utils.showToast(getActivity(), R.string.message_server_unavailable);
		else
			loading.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<List<String[]>> loader) {
		listView.setAdapter(null);
		loading.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();

		MainActivity activity = (MainActivity) getActivity();
		activity.updateFragmentInfo(this);
	}
}
