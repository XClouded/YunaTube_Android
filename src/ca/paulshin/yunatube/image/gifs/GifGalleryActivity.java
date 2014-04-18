package ca.paulshin.yunatube.image.gifs;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FileDownloader;
import ca.paulshin.yunatube.http.GifThumbnailDataLoader;
import ca.paulshin.yunatube.image.gifs.adapter.GifGalleryPagerAdapter;
import ca.paulshin.yunatube.main.TodayPhotoActivity;
import ca.paulshin.yunatube.widget.GifView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class GifGalleryActivity extends YunaTubeBaseActivity {

	public static final String EXTRA_IMAGE_POSITION = "extra_position";
	private GifLoader gifLoader;
	private ViewPager gallery;
	private String[] fileNames;

	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gallery);
		Bundle bundle = getIntent().getExtras();
		fileNames = GifThumbnailDataLoader.getInstance().fileNamesArray;
		int galleryPosition = bundle.getInt(EXTRA_IMAGE_POSITION, 0);

		gallery = (ViewPager) findViewById(R.id.pager);
		gallery.setAdapter(new GifGalleryPagerAdapter(fileNames) {
			@Override
			public Object instantiateItem(View view, int position) {
				final Context context = view.getContext();
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final FrameLayout imageLayout = (FrameLayout) inflater.inflate(R.layout.item_gallery_gif, null);
				final GifView imageView = (GifView) imageLayout.findViewById(R.id.image);

				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						SherlockFragmentActivity activity = (SherlockFragmentActivity) context;
						if (activity.getSupportActionBar().isShowing()) {
							activity.getSupportActionBar().hide();
							activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
						} else {
							activity.getSupportActionBar().show();
							activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						}
					}
				});

				gifLoader.displayImage(Constants.GIF_FOLDER_URL + fileNames[position], imageView);
				((ViewPager) view).addView(imageLayout, 0);
				return imageLayout;
			}
		});
		gallery.setCurrentItem(galleryPosition);

		gifLoader = new GifLoader(getApplicationContext());
		
		getWindow().setBackgroundDrawableResource(android.R.color.black);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_photo_gallery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.save:
			String fileName = fileNames[gallery.getCurrentItem()];
			saveLocalGifFile(fileName);
			break;

		case R.id.share:
			String url = Constants.GIF_FOLDER_URL + fileNames[gallery.getCurrentItem()];
			Utils.share(this, getString(R.string.gif_share), getString(R.string.gif_share_subject), getString(R.string.gif_share_text, url));
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void saveLocalGifFile(String fileName) {
		File dir = new File(Utils.getFilePath(Constants.GIF_MY_DIR));
		if (dir.exists() == false) {
			dir.mkdirs();
		}

		File src = new File(Utils.getFilePath(Constants.FILE_CACHE_DIR, fileName));
		File dest = new File(Utils.getFilePath(Constants.GIF_MY_DIR, fileName + ".gif"));
		try {
			FileDownloader.moveFileLocally(src, dest);
			Utils.showToast(GifGalleryActivity.this, R.string.gif_saved);
			
			// Force the native gallery app to refersh
			Utils.refreshGallery(this, dest);
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
		}
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
	
	@Override
	protected void onDestroy() {
		if (gifLoader != null)
			gifLoader.clearCache();
		
		super.onDestroy();
	}
}