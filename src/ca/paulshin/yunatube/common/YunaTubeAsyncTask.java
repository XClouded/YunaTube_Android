package ca.paulshin.yunatube.common;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import ca.paulshin.yunatube.YunaTubeBaseActivity;

public abstract class YunaTubeAsyncTask extends AsyncTask<String, Void, Object> {
	WeakReference<Context> ref;
	
	public YunaTubeAsyncTask(Context context) {
		ref = new WeakReference<Context>(context);
	}
	
	@Override
	protected void onPreExecute() {
		Context context = ref.get();
		if (context != null && context instanceof YunaTubeBaseActivity) {
			((YunaTubeBaseActivity)context).displayDialog();
		}
	}

	@Override
	protected void onPostExecute(Object result) {
		Context context = ref.get();
		if (context != null && context instanceof YunaTubeBaseActivity) {
			((YunaTubeBaseActivity)context).hideDialog();
		}
	}
}