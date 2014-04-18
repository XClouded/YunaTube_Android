package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;

public class GifThumbnailDataLoader extends YunaTubeHttpClient {
	private static GifThumbnailDataLoader loader = null;

	public String[] fileNamesArray;
	public List<String> fileNames = new ArrayList<String>();

	protected GifThumbnailDataLoader() {
	}

	public static GifThumbnailDataLoader getInstance() {
		if (loader == null)
			loader = new GifThumbnailDataLoader();
		return loader;
	}

	public synchronized List<String> getGifThumbnailList() {
		fileNames = new ArrayList<String>();

		try {
			JSONArray list = getJSONArray(Constants.GIF_LIST_URL);

			for (Object object : list) {
				String fileName = (String) object;
				fileNames.add(fileName);
			}

			fileNamesArray = fileNames.toArray(new String[fileNames.size()]);
			return fileNames;
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}
}
