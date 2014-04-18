package ca.paulshin.yunatube.image.gifs;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.image.gifs.adapter.GifGalleryPagerAdapter;

import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;

public class MyGifGalleryActivity extends YunaTubeBaseActivity {

	public static final String IMAGE_POSITION = "position";
	public static final String IMAGE_PATH = "path";
	private ViewPager gallery;
	private String[] fileNames;

	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gallery);
		Bundle bundle = getIntent().getExtras();
		fileNames = bundle.getStringArray(IMAGE_PATH);
		int galleryPosition = bundle.getInt(IMAGE_POSITION, 0);

		gallery = (ViewPager) findViewById(R.id.pager);
		gallery.setAdapter(new GifGalleryPagerAdapter(fileNames));
		gallery.setCurrentItem(galleryPosition);

		getWindow().setBackgroundDrawableResource(android.R.color.black);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}