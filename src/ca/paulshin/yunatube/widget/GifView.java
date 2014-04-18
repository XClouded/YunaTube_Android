package ca.paulshin.yunatube.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.webkit.WebView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Utils;

public class GifView extends WebView {
	private GestureDetector mGestureDetector;
	private int mImageHeight = 0;
	private int mImageWidth = 0;
	private String mGifPath;

	public GifView(Context context) {
		this(context, null);
	}

	public GifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
	}

	public String getGifPath() {
		return this.mGifPath;
	}

	// Long clicks weren't registering...not sure if this is
	// proper work around...but it works sooooo...yeah.
	SimpleOnGestureListener mGestListener = new SimpleOnGestureListener() {
		public boolean onDown(MotionEvent event) {
			return true;
		}

		public void onLongPress(MotionEvent event) {
			GifView.this.performLongClick();
		}
	};

	public void loadGif(String gifPath) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(gifPath, o);

		this.mGestureDetector = new GestureDetector(mGestListener);
		
		int screenWidth = Utils.getScreenSize()[0];
		int screenHeight = Utils.getScreenSize()[1];

		this.mImageWidth = o.outWidth - 1;
		this.mImageHeight = o.outHeight - 1;
		this.loadUrl("file://" + gifPath);
		this.mGifPath = gifPath;
	}

	public void unloadGif() {
		this.clearHistory();
		this.loadUrl("");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float parent_width = (float) MeasureSpec.getSize(widthMeasureSpec);
		float parent_height = (float) MeasureSpec.getSize(heightMeasureSpec);

		float viewScale = this.getResources().getDimension(R.dimen.gif_view_scale);
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		viewScale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm) * viewScale;
		float scaleImgWidth = this.mImageWidth * viewScale;
		float scaleImgHeight = this.mImageHeight * viewScale;
		int initialScale = (int) (100 * viewScale);

		// For zoom
		float origImgWidth = scaleImgWidth;
		float origImgHeight = scaleImgHeight;

		if (parent_width < scaleImgWidth && scaleImgWidth > 0) {
			initialScale = (int) (viewScale * 100 * (parent_width / origImgWidth));
			scaleImgHeight = (int) (scaleImgHeight * ((double) parent_width / scaleImgWidth));
			scaleImgWidth = parent_width;
		}

		if (parent_height < scaleImgHeight && scaleImgHeight > 0) {
			initialScale = (int) (viewScale * 100 * (parent_height / origImgHeight));
			scaleImgWidth = (int) (scaleImgWidth * ((double) parent_height / scaleImgHeight));
			scaleImgHeight = parent_height;
		}

		this.setInitialScale(initialScale);
		this.setMeasuredDimension((int) scaleImgWidth, (int) scaleImgHeight);
	}
}
