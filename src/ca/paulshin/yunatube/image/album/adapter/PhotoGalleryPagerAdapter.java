package ca.paulshin.yunatube.image.album.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import ca.paulshin.yunatube.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.polites.android.GestureImageView;
import com.squareup.picasso.Picasso;

public class PhotoGalleryPagerAdapter extends PagerAdapter {
	private String[] images;

	public PhotoGalleryPagerAdapter(String[] images) {
		this.images = images;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object instantiateItem(View view, int position) {
		final Context context = view.getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final FrameLayout imageLayout = (FrameLayout) inflater.inflate(R.layout.item_gallery_image, null);
		final GestureImageView imageView = new GestureImageView(context);
		
		imageView.setClickable(true);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SherlockFragmentActivity activity = (SherlockFragmentActivity)context;
				if (activity.getSupportActionBar().isShowing()) {
					activity.getSupportActionBar().hide();
					activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				else {
					activity.getSupportActionBar().show();
					activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			}
		});
		imageLayout.addView(imageView);

		Picasso.with(context).load(images[position]).noFade().into(imageView);
		
		((ViewPager) view).addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {
	}
}