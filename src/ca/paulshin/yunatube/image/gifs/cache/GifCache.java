package ca.paulshin.yunatube.image.gifs.cache;

import java.io.File;

import android.content.Context;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;

public class GifCache {

	private File cacheDir;

	public GifCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), Constants.FILE_CACHE_DIR);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
		// Identify images by hashcode. Not a perfect solution, good for the demo.
//		String filename = String.valueOf(url.hashCode()) + ".gif";
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files) {
			if (!f.getPath().contains("loading") && f.getPath().endsWith("gif")) {
				Utils.debug("Deleting " + f.getPath());
				f.delete();
			}
		}
	}
}