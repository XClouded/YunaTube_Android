package ca.paulshin.yunatube.image.album;

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
import android.widget.GridView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.ObjectLoader;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FlickrDataLoader;
import ca.paulshin.yunatube.image.album.adapter.SetListAdapter;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;
import ca.paulshin.yunatube.main.MainActivity;

public class SetFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<CollectionSet>>, OnItemClickListener {
	public static final String EXTRA_COLLECTION_ID = "extra_collection_id";
	public static final String EXTRA_COLLECTION_TITLE = "extra_collection_title";

	private GridView gridView;
	private TextView empty;

	private String collectionTitle, collectionId;
	private List<CollectionSet> data;

	private BaseAdapter adapter;

	public static SetFragment getInstance(String collectionId, String collectionTitle) {
		SetFragment fragment = new SetFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_COLLECTION_ID, collectionId);
		bundle.putString(EXTRA_COLLECTION_TITLE, collectionTitle);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new SetListAdapter(getActivity(), null);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		getLoaderManager().restartLoader(0, null, this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grid_3_col, null);
		gridView = (GridView) view.findViewById(R.id.gridview);
		empty = (TextView) view.findViewById(R.id.empty);
		empty.setText(R.string.message_server_unavailable);

		collectionId = getArguments().getString(EXTRA_COLLECTION_ID);
		collectionTitle = getArguments().getString(EXTRA_COLLECTION_TITLE);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		CollectionSet set = data.get(position);
		MainActivity activity = (MainActivity) getActivity();

		activity.setFragment(PhotoListFragment.getInstance(collectionTitle, set.getId(), set.getTitle()), true);
	}

	@Override
	public int getTitle() {
		return 0;
	}

	public String getTextTitle() {
		return getArguments().getString(EXTRA_COLLECTION_TITLE);
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "album_set - android";
	}

	@Override
	public Loader<List<CollectionSet>> onCreateLoader(int i, Bundle bundle) {
		return new ObjectLoader<List<CollectionSet>>(getActivity()) {
			@Override
			public List<CollectionSet> loadInBackground() {
				List<CollectionSet> setList = FlickrDataLoader.getInstance().getSetList(collectionId);
				return setList;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<CollectionSet>> loader, List<CollectionSet> data) {
		this.data = data;
		gridView.setAdapter(new SetListAdapter(getActivity(), data));
		if (data == null)
			Utils.showToast(getActivity(), R.string.message_server_unavailable);
		else
			empty.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<List<CollectionSet>> loader) {
		gridView.setAdapter(null);
	}

	@Override
	public void onResume() {
		super.onResume();

		MainActivity activity = (MainActivity) getActivity();
		activity.updateFragmentInfo(this);
	}
}
