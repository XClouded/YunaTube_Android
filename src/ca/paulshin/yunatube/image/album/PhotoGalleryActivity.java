package ca.paulshin.yunatube.image.album;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.FileSaveAsyncTask;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FlickrDataLoader;
import ca.paulshin.yunatube.image.album.adapter.PhotoGalleryPagerAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class PhotoGalleryActivity extends YunaTubeBaseActivity {

	public static final String EXTRA_IMAGE_POSITION = "extra_position";
	public static final String EXTRA_COLLECTION_SET_TITLE = "extra_collection_title";
	private static final String IS_FULLSCREEN_GUIDE_SHOWN = "is_fullscreen_guide_shown";
	
	private static final String DIR = Constants.ALBUM_MY_DIR;
	
	private ViewPager gallery;
	private String[] imageUrls;

	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gallery);
		Bundle bundle = getIntent().getExtras();
		imageUrls = FlickrDataLoader.getInstance().photoUrls;
		int galleryPosition = bundle.getInt(EXTRA_IMAGE_POSITION, 0);
		// String title = bundle.getString(EXTRA_COLLECTION_SET_TITLE);

		gallery = (ViewPager) findViewById(R.id.pager);
		gallery.setAdapter(new PhotoGalleryPagerAdapter(imageUrls));
		gallery.setCurrentItem(galleryPosition);
		
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		
		// Show "touch to view in fullscreen mode"
		if (!Preference.contains(IS_FULLSCREEN_GUIDE_SHOWN) || !Preference.get(IS_FULLSCREEN_GUIDE_SHOWN, true)) {
			Preference.put(IS_FULLSCREEN_GUIDE_SHOWN, true);
			Utils.showToast(this, R.string.album_view_fullscreen);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_photo_gallery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		final String url = imageUrls[gallery.getCurrentItem()];
		final String fileName = new File(url).getName();
		switch (itemId) {
		case R.id.save:
			new FileSaveAsyncTask(this, true) {
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);

					Utils.showToast(PhotoGalleryActivity.this, R.string.album_image_saved);
				};
			}.execute(url, DIR, fileName);
			break;

		case R.id.share:
			new FileSaveAsyncTask(this, true) {
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);

					File file = new File(Utils.getFilePath(DIR, fileName));

					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("image/jpeg");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

					startActivity(Intent.createChooser(intent, getString(R.string.actionbar_share_with)));
				};
			}.execute(url, DIR, fileName);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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