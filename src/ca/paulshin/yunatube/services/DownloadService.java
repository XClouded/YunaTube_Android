package ca.paulshin.yunatube.services;

import android.app.IntentService;
import android.content.Intent;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.FlickrDataLoader;

public class DownloadService extends IntentService {

	public DownloadService() {
		super(DownloadService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Load Flickr
		YunaTubeApplication application = (YunaTubeApplication) Utils.ctx;
		try {
			application.collectionList = FlickrDataLoader.getInstance().getCollectionList();
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
		}
	}
}
