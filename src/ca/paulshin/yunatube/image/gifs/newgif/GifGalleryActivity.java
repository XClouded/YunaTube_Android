package ca.paulshin.yunatube.image.gifs.newgif;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Window;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.common.Utils.Network;
import ca.paulshin.yunatube.http.FileDownloader;
import ca.paulshin.yunatube.http.GifThumbnailDataLoader;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class GifGalleryActivity extends YunaTubeBaseActivity {

	public static final String EXTRA_IMAGE_POSITION = "extra_position";
	public static final String EXTRA_COLLECTION_SET_TITLE = "extra_collection_title";
	private static final String IS_FULLSCREEN_GUIDE_SHOWN = "is_fullscreen_guide_shown";
	
	private ViewPager gallery;
	private String[] fileNames;
	private GifPagerAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gallery);
		Bundle bundle = getIntent().getExtras();
		fileNames = GifThumbnailDataLoader.getInstance().fileNamesArray;
		int galleryPosition = bundle.getInt(EXTRA_IMAGE_POSITION, 0);

		gallery = (ViewPager) findViewById(R.id.pager);
		adapter = new GifPagerAdapter(getSupportFragmentManager(), fileNames);
		gallery.setAdapter(adapter);
		if (Utils.getConnectivity() == Network.MOBILE)
			gallery.setOffscreenPageLimit(2);
		else if (Utils.getConnectivity() == Network.WIFI)
			gallery.setOffscreenPageLimit(3);
		gallery.setCurrentItem(galleryPosition);
		
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
	      sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
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
//	  
//	  @Override
//	  protected void onDestroy() {
//	    if (gifLoader != null)
//	      gifLoader.clearCache();
//	    
//	    super.onDestroy();
//	  }
}