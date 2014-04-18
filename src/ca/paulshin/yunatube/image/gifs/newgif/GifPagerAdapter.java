package ca.paulshin.yunatube.image.gifs.newgif;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GifPagerAdapter extends FragmentPagerAdapter {
	private String [] fileNames;

	public GifPagerAdapter(FragmentManager supportFragmentManager, String[] fileNames) {
		super(supportFragmentManager);
		this.fileNames = fileNames;
	}

	@Override
	public int getCount() {
		return fileNames.length;
	}
	
	@Override
	public Fragment getItem(int position) {
		return GifFragment.init(fileNames[position]);
	}
}
