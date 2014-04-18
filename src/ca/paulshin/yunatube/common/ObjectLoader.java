package ca.paulshin.yunatube.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class ObjectLoader<E> extends AsyncTaskLoader<E> {
	
	private E data;
	public ObjectLoader(Context context) {
		super(context);
	}

	@Override
	public void deliverResult(E data) {
		if (isReset()) {
			return;
		}

		this.data = data;
		super.deliverResult(data);
	}

	@Override
	protected void onStartLoading() {
		if (data != null) {
			deliverResult(data);
		}

		if (takeContentChanged() || data == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		data = null;
	}
}