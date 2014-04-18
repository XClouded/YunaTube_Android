package ca.paulshin.yunatube.image.gifs;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import ca.paulshin.yunatube.http.FileDownloader;
import ca.paulshin.yunatube.image.gifs.cache.GifCache;
import ca.paulshin.yunatube.image.gifs.cache.GifMemoryCache;
import ca.paulshin.yunatube.widget.GifView;

public class GifLoader {
	GifMemoryCache memoryCache = new GifMemoryCache();
	GifCache gifCache;
	private Map<GifView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<GifView, String>());
	ExecutorService executorService;

	public GifLoader(Context context) {
		gifCache = new GifCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}
	
//	private static final String LOADING = Utils.getFilePath(Constants.FILE_CACHE_DIR, "loading.gif");

	public void displayImage(String url, GifView gifView) {
		imageViews.put(gifView, url);
		String path = memoryCache.get(url);
		if (path != null)
			gifView.loadGif(path);
		else {
			queuePhoto(url, gifView);
//			gifView.loadGif(LOADING);
		}
	}

	private void queuePhoto(String url, GifView gifView) {
		PhotoToLoad p = new PhotoToLoad(url, gifView);
		executorService.submit(new PhotosLoader(p));
	}

	private String getFilePath(String url) {
		File file = gifCache.getFile(url);

		if (file.exists()) {
			return file.getPath();
		}

		try {
			FileDownloader.download(url, file);
			return file.getPath();
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public GifView gifView;

		public PhotoToLoad(String u, GifView i) {
			url = u;
			gifView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			String filePath = getFilePath(photoToLoad.url);
			memoryCache.put(photoToLoad.url, filePath);
			if (imageViewReused(photoToLoad))
				return;
			GifDisplayer bd = new GifDisplayer(filePath, photoToLoad);
			Activity a = (Activity) photoToLoad.gifView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.gifView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	class GifDisplayer implements Runnable {
		String path;
		PhotoToLoad photoToLoad;

		public GifDisplayer(String b, PhotoToLoad p) {
			path = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (path != null) {
				photoToLoad.gifView.setVisibility(View.GONE);
				photoToLoad.gifView.loadGif(path);
				photoToLoad.gifView.reload();
				photoToLoad.gifView.loadGif(path);
				photoToLoad.gifView.setVisibility(View.VISIBLE);
			}
			else {
//				photoToLoad.gifView.loadGif(LOADING);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		gifCache.clear();
	}
}
