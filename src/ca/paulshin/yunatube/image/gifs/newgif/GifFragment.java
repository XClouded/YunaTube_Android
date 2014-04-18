package ca.paulshin.yunatube.image.gifs.newgif;

import java.io.File;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FileDownloader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.droidtools.android.graphics.GifDrawable;

public class GifFragment extends YunaTubeBaseFragment implements OnClickListener {
	private String fileName;

	private ProgressBar loading;
	private ImageView imageView;
	private Drawable currDrawable;
	
	private Handler mHandler;
	private Runnable mUpdateTimeTask;
	private long startTime = 0;
	
	public static GifFragment init(String path) {
		GifFragment fragment = new GifFragment();
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fileName = getArguments() != null ? getArguments().getString("path") : null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gif, container, false);
		
		imageView = (ImageView) view.findViewById(R.id.image);
        loading = (ProgressBar) view.findViewById(R.id.loading);

    	imageView.setClickable(true);
        imageView.setOnClickListener(this);
		
		/* You should always load images in the background */
        BitmapWorkerTask task = new BitmapWorkerTask();
//        if(Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
//        	task.executeOnExecutor(Executor.THREAD_POOL_EXECUTOR, fileName);
//		} else {
			task.execute(fileName);
//		}
        
        
        /*
         * You could also have a similar loop in an 
         * in the onDraw() method of an ImageView.
         */
        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {    	
    	   public void run() {
    		   long time = System.currentTimeMillis();
	   			if (currDrawable != null) {
	   				if (currDrawable.setLevel((int)(((time - startTime)/10) % 10000))) {
	   					imageView.postInvalidate();
	   				}
	   			}
	   			mHandler.postDelayed(this, 10);
    	   }
    	};
    	mHandler.post(mUpdateTimeTask);
    	
		return view;
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, GifDrawable> {

	    public BitmapWorkerTask() {
			loading.setVisibility(View.VISIBLE);
	    	imageView.setImageDrawable(null);
	    }

	    // Decode image in background.
	    @Override
	    protected GifDrawable doInBackground(String... params) {
	        String fileName = params[0];
			String localTempPath = Utils.getFilePath(Constants.FILE_CACHE_DIR, fileName);
			
			File dest = new File(localTempPath);
			if (dest.exists()) {
				Utils.debug("File exists at " + localTempPath);
			}
			else {
		        // Download image
		        String url = Constants.GIF_FOLDER_URL + fileName;
		        FileDownloader.download(url, Constants.FILE_CACHE_DIR, fileName);
				Utils.debug("saved to : " + Constants.FILE_CACHE_DIR + "/" + fileName);
			}
			
			Utils.debug("loading from : " + localTempPath);
			
			if (getResources() == null)
				return null;
	        return GifDrawable.gifFromFile(getResources(), localTempPath);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(GifDrawable drawable) {
	        if (drawable != null) {
	            if (currDrawable != null) {
	            	((GifDrawable) currDrawable).recycle();
	            }
	            currDrawable = drawable;
	            imageView.setImageDrawable(drawable);
	            startTime = System.currentTimeMillis();

//	    		if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
//	    			ObjectAnimator anim = ObjectAnimator.ofInt(drawable, "level", 0, 10000);
//	    			anim.setRepeatCount(ObjectAnimator.INFINITE);
//	    			anim.start();
//	    		}
	            
	            loading.setVisibility(View.GONE);
	        } else {
//	        	Utils.showToast(getActivity(), "Error loading gif.");
	        }
	    }
	}
	
	@Override
	public int getTitle() {
		return 0;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return null;
	}

	@Override
	public void onClick(View v) {
		SherlockFragmentActivity activity = (SherlockFragmentActivity)getActivity();
		if (activity.getSupportActionBar().isShowing()) {
			activity.getSupportActionBar().hide();
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else {
			activity.getSupportActionBar().show();
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}