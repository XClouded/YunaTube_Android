package ca.paulshin.yunatube.main;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.FileSaveAsyncTask;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;

import com.google.analytics.tracking.android.EasyTracker;
import com.polites.android.GestureImageView;

public class TodayPhotoActivity extends YunaTubeBaseActivity implements OnClickListener {
	public static final String EXTRA_TODAY_PHOTO_URL = "extra_today_photo_url";
	private static final String DIR = Constants.ALBUM_MY_DIR;
	private String fileName;
	private boolean saveImage = false;
	private File localImageFile;
	private GestureImageView view;
	
	private static final String IS_SAVE_GUIDE_SHOWN = "is_saved_guide_shown";
	
	private static final String [] extensions = {".jpg", ".gif", ".png"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, true);

		setContentView(R.layout.activity_today_photo);

		String url = getIntent().getStringExtra(EXTRA_TODAY_PHOTO_URL);
		fileName = url.substring(url.lastIndexOf('/') + 1);

		new FileSaveAsyncTask(this, false) {
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				displayImage();

				// Show "touch to view in fullscreen mode"
				if (!Preference.contains(IS_SAVE_GUIDE_SHOWN) || !Preference.get(IS_SAVE_GUIDE_SHOWN, true)) {
					Preference.put(IS_SAVE_GUIDE_SHOWN, true);
					Utils.showToast(TodayPhotoActivity.this, R.string.today_picture_save);
				}
			};
		}.execute(url, DIR, fileName);
	}

	private void displayImage() {
		// Get file from local
		localImageFile = new File(Utils.getFilePath(DIR, fileName));
		if (localImageFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(localImageFile.getAbsolutePath());

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			view = new GestureImageView(TodayPhotoActivity.this);
			view.setImageBitmap(bitmap);
			view.setLayoutParams(params);

			ViewGroup layout = (ViewGroup) findViewById(R.id.layout);

			layout.addView(view);

			view.setClickable(true);
			view.setOnClickListener(this);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
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
		// Remove file from my directory
		if (localImageFile != null && localImageFile.exists() & !saveImage)
			localImageFile.delete();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!saveImage) {
			new AlertDialog.Builder(v.getContext()).setMessage(R.string.today_picture_save_confirm).setTitle(R.string.save)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveImage = true;
							
							String extension = null;
							for (String ext : extensions) {
								if (fileName.endsWith(ext)) extension = ext;
							}
							
							File dest = new File(Utils.getFilePath(DIR, String.valueOf(fileName.hashCode()) + extension));
							localImageFile.renameTo(dest);
							Utils.refreshGallery(TodayPhotoActivity.this, dest);
							Utils.showToast(TodayPhotoActivity.this, R.string.album_image_saved);
							dialog.dismiss();
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveImage = false;
							dialog.dismiss();
						}
					}).create().show();
		}
	}
}
