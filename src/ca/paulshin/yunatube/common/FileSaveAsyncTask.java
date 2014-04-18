package ca.paulshin.yunatube.common;

import java.io.File;
import java.lang.ref.WeakReference;

import android.os.AsyncTask;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.http.FileDownloader;

public class FileSaveAsyncTask extends AsyncTask<String, Void, Integer> {
	WeakReference<YunaTubeBaseActivity> ref;
	private boolean sendBroadcast;

	public FileSaveAsyncTask(YunaTubeBaseActivity activity, boolean sendBroadcast) {
		ref = new WeakReference<YunaTubeBaseActivity>(activity);
		this.sendBroadcast = sendBroadcast;
	}

	private String url, directory, fileName;

	@Override
	protected void onPreExecute() {
		if (ref.get() != null)
			ref.get().displayDialog();
	}

	@Override
	protected Integer doInBackground(String... params) {
		url = params[0];
		directory = params[1];
		fileName = params[2];
		FileDownloader.download(url, directory, fileName);
		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		YunaTubeBaseActivity activity = ref.get();
		if (activity != null) {
			activity.hideDialog();
			if (sendBroadcast) {
				File dest = new File(Utils.getFilePath(directory, fileName));
				Utils.refreshGallery(ref.get(), dest);
			}
		}
	}
}