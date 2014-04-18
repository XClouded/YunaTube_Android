package ca.paulshin.yunatube.image.gifs;

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
import ca.paulshin.yunatube.http.GifThumbnailDataLoader;
import ca.paulshin.yunatube.image.gifs.adapter.GifThumbnailAdapter;
import ca.paulshin.yunatube.image.gifs.GifGalleryActivity;

public class GifThumbnailsFragment extends YunaTubeBaseFragment implements LoaderCallbacks<List<String>>, OnItemClickListener {
	private GridView gridView;
	private LinearLayout ll_loading;
	private GifThumbnailAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new GifThumbnailAdapter(getActivity(), null);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		getLoaderManager().restartLoader(0, null, this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photos_grid, null);

		gridView = (GridView) view.findViewById(R.id.image_list_gridview);
		ll_loading = (LinearLayout) view.findViewById(R.id.image_list_loading);

		TextView navi = (TextView) view.findViewById(R.id.image_list_navi);
		navi.setVisibility(View.GONE);

		Utils.showToast(getActivity(), getString(R.string.gif_warning), false);

		return view;
	}

	@Override
	public int getTitle() {
		return R.string.submenu_gifs;
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "gifs - android";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if (!Utils.isNetworkAvailable()) {
			Utils.showToast(getActivity(), R.string.message_network_unavailable);
		} else {
			Intent intent = new Intent(getActivity(), GifGalleryActivity.class);
			intent.putExtra(GifGalleryActivity.EXTRA_IMAGE_POSITION, position);
			startActivity(intent);
		}
	}

	@Override
	public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
		return new ObjectLoader<List<String>>(getActivity()) {
			@Override
			public List<String> loadInBackground() {
				if (!Utils.isNetworkAvailable())
					return null;
				else {
					List<String> list = GifThumbnailDataLoader.getInstance().getGifThumbnailList();
					return list;
				}
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
		if (data != null) {
			gridView.setAdapter(new GifThumbnailAdapter(getActivity(), data));
			ll_loading.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
		}
		else
			Utils.showToast(getActivity(), R.string.message_network_unavailable);
	}

	@Override
	public void onLoaderReset(Loader<List<String>> loader) {
		gridView.setAdapter(null);
		ll_loading.setVisibility(View.VISIBLE);
	}
}