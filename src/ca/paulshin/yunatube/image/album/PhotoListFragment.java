package ca.paulshin.yunatube.image.album;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.ObjectLoader;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FlickrDataLoader;
import ca.paulshin.yunatube.image.album.adapter.PhotoListAdapter;
import ca.paulshin.yunatube.image.album.pojo.Photo;
import ca.paulshin.yunatube.main.MainActivity.BackPressed;

public class PhotoListFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<Photo>>, OnItemClickListener, BackPressed {
	private TextView tv_navi;
	private GridView gridView;
	private LinearLayout ll_loading;
	private PhotoListAdapter adapter;

	public static final String EXTRA_COLLECTION_SET_ID = "extra_collection_set_id";
	public static final String EXTRA_COLLECTION_TITLE = "extra_collection_title";
	public static final String EXTRA_COLLECTION_SET_TITLE = "extra_collection_set_title";

	private String path;
	private String setId;

	public static PhotoListFragment getInstance(String collectionTitle, String setId, String setTitle) {
		PhotoListFragment fragment = new PhotoListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_COLLECTION_SET_ID, setId);
		bundle.putString(EXTRA_COLLECTION_TITLE, collectionTitle);
		bundle.putString(EXTRA_COLLECTION_SET_TITLE, setTitle);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new PhotoListAdapter(getActivity(), null);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		getLoaderManager().restartLoader(0, null, this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photos_grid, null);

		gridView = (GridView) view.findViewById(R.id.image_list_gridview);
		ll_loading = (LinearLayout) view.findViewById(R.id.image_list_loading);
		tv_navi = (TextView) view.findViewById(R.id.image_list_navi);

		path = getArguments().getString(EXTRA_COLLECTION_TITLE) + " > " + getArguments().getString(EXTRA_COLLECTION_SET_TITLE);
		tv_navi.setText(path);

		setId = getArguments().getString(EXTRA_COLLECTION_SET_ID);

		return view;
	}

	@Override
	public int getTitle() {
		return 0;
	}

	public String getTextTitle() {
		return getArguments().getString(EXTRA_COLLECTION_SET_TITLE);
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "album_photo - android";
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), PhotoGalleryActivity.class);
		intent.putExtra(PhotoGalleryActivity.EXTRA_IMAGE_POSITION, position);
		intent.putExtra(PhotoGalleryActivity.EXTRA_COLLECTION_SET_TITLE, getArguments().getString(EXTRA_COLLECTION_SET_TITLE));
		startActivity(intent);
	}

	@Override
	public Loader<List<Photo>> onCreateLoader(int i, Bundle bundle) {
		return new ObjectLoader<List<Photo>>(getActivity()) {
			@Override
			public List<Photo> loadInBackground() {
				List<Photo> photoList = FlickrDataLoader.getInstance().getPhotoList(setId);
				return photoList;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
		gridView.setAdapter(new PhotoListAdapter(getActivity(), data));
		if (data == null)
			Utils.showToast(getActivity(), R.string.message_server_unavailable);
		else
			ll_loading.setVisibility((data != null && data.size() > 0) ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<List<Photo>> arg0) {
		gridView.setAdapter(null);
		ll_loading.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onBackPressed() {
		return true;
	}
}