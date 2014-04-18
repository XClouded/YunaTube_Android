package ca.paulshin.yunatube.image.album;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import ca.paulshin.yunatube.BaseSubMenuFragment;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.album.adapter.CollectionListAdapter;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.main.MainMenuFragment;

public class SubMenuAlbumFragment extends BaseSubMenuFragment implements OnItemClickListener, OnClickListener {
	private static final String MY_PICS_DIR_PATH = Utils.getFilePath("YuNaTube");

	private RelativeLayout myPictures;
	private ListView listView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_menu_album, null);

		listView = (ListView) view.findViewById(android.R.id.list);

		listView.setAdapter(new CollectionListAdapter(getActivity()));
		listView.setOnItemClickListener(this);

		RelativeLayout back = (RelativeLayout) view.findViewById(R.id.submenu_album_back);
		back.setOnClickListener(this);

		myPictures = (RelativeLayout) view.findViewById(R.id.submenu_album_myalbum);
		myPictures.setOnClickListener(this);

		return view;
	}

	private void checkIfMyPicsDirExists() {
		File dir = new File(MY_PICS_DIR_PATH);
		if (!dir.exists()) {
			myPictures.setVisibility(View.GONE);
		} else {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				myPictures.setVisibility(View.VISIBLE);
			} else {
				myPictures.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		checkIfMyPicsDirExists();
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		YunaTubeApplication application = (YunaTubeApplication) this.getActivity().getApplicationContext();
		if (application.collectionList == null)
			return;
		CollectionSet collection = application.collectionList.get(position);
		MainActivity activity = (MainActivity) getActivity();

		activity.setFragment(SetFragment.getInstance(collection.getId(), collection.getTitle()), false);
		// selectMenu(v, position == application.collectionList.size() - 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submenu_album_back:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.menu_frame, new MainMenuFragment(), null);
			ft.commit();
			break;

		case R.id.submenu_album_myalbum:
			try {
				Uri targetUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				targetUri = targetUri.buildUpon().appendQueryParameter("bucketId", String.valueOf(MY_PICS_DIR_PATH.toLowerCase().hashCode())).build();
				Intent intent = new Intent(Intent.ACTION_VIEW, targetUri);
				startActivity(intent);
			} catch (Exception e) {
				Utils.showToast(getActivity(), getString(R.string.album_cannot_see_local), true);
			}

			break;
		}
	}
}