package ca.paulshin.yunatube.image.gifs;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ca.paulshin.yunatube.BaseSubMenuFragment;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.main.MainMenuFragment;

public class SubMenuGifs extends BaseSubMenuFragment implements OnClickListener {
	
	private static final String MY_GIF_DIR_PATH = Utils.getFilePath(Constants.GIF_MY_DIR);
	
	private RelativeLayout gifThumbmail, myGifThumbmail;
	private boolean myDirExists;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_menu_gifs, null);

		RelativeLayout back = (RelativeLayout) view.findViewById(R.id.submenu_links_back);
		back.setOnClickListener(this);

		gifThumbmail = (RelativeLayout) view.findViewById(R.id.submenu_gifs);
		gifThumbmail.setOnClickListener(this);

		myGifThumbmail = (RelativeLayout) view.findViewById(R.id.submenu_gifs_my);
		myGifThumbmail.setOnClickListener(this);

		return view;
	}
	
	private void checkMyGifDir() {
		File dir = new File(MY_GIF_DIR_PATH);
		myDirExists = dir.exists();
		Utils.debug("MyGif dir exists? " + myDirExists + " at " + MY_GIF_DIR_PATH);
		if (!myDirExists) {
			myGifThumbmail.setVisibility(View.GONE);
		} else {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				myGifThumbmail.setVisibility(View.VISIBLE);
				gifThumbmail.setBackgroundResource(R.drawable.submenu_menu_selector);
			} else {
				myGifThumbmail.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		checkMyGifDir();
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity)getActivity();
		
		switch (v.getId()) {
		case R.id.submenu_links_back:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.menu_frame, new MainMenuFragment(), null);
			ft.commit();
			break;
			
		case R.id.submenu_gifs:
			activity.setFragment(new GifThumbnailsFragment(), !myDirExists);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_gifs_my:
			activity.setFragment(new MyGifThumbnailsFragment(), true);
			selectMenu(v, true);
			break;
		}
		
	}
}
