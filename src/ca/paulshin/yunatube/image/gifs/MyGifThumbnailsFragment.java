package ca.paulshin.yunatube.image.gifs;

import java.io.File;
import java.util.ArrayList;
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
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.ObjectLoader;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.gifs.adapter.MyGifThumbnailAdapter;

public class MyGifThumbnailsFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<String>>, OnItemClickListener {
	private List<String> data;
	private GridView gridView;
	private LinearLayout ll_loading;
	private MyGifThumbnailAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new MyGifThumbnailAdapter(getActivity(), null);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		getLoaderManager().restartLoader(0, null, this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photos_grid, null);
		
		gridView = (GridView) view.findViewById(R.id.image_list_gridview);
		ll_loading = (LinearLayout) view.findViewById(R.id.image_list_loading);

		TextView navi = (TextView)view.findViewById(R.id.image_list_navi);
		navi.setVisibility(View.GONE);

		return view;
	}

	@Override
	public int getTitle() {
		return R.string.submenu_gifs_my;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return "gifs - mylist";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent(getActivity(), MyGifGalleryActivity.class);
		intent.putExtra(MyGifGalleryActivity.IMAGE_POSITION, position);
		intent.putExtra(MyGifGalleryActivity.IMAGE_PATH, data.toArray(new String[data.size()]));
		startActivity(intent);
	}

	@Override
	public Loader<List<String>> onCreateLoader(int arg0, Bundle arg1) {
		return new ObjectLoader<List<String>>(getActivity()) {
			@Override
			public List<String> loadInBackground() {				
				File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), Constants.GIF_MY_DIR);
				if (!cacheDir.exists())
					return null;
				File[] files = cacheDir.listFiles();
				if (files == null) {
					return null;
				}

				List<String> data = new ArrayList<String>();
				for (File f : files) {
					Utils.debug("Gif file path: " + f.getPath());
					data.add(f.getPath());
				}
				return data;
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
		this.data = data;
		gridView.setAdapter(new MyGifThumbnailAdapter(getActivity(), data));
		ll_loading.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<List<String>> loader) {
		gridView.setAdapter(null);
		ll_loading.setVisibility(View.VISIBLE);
	}
}