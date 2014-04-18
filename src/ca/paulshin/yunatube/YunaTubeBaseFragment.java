package ca.paulshin.yunatube;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import ca.paulshin.yunatube.common.CustomDialog;
import ca.paulshin.yunatube.common.Utils;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public abstract class YunaTubeBaseFragment extends BaseFragment implements AnimationListener {

	private Dialog loadingDialog;
	
	public abstract int getTitle();
	protected abstract boolean getNetworkCheck();
	protected abstract String getTrackerId();

	public YunaTubeBaseFragment setBundle(Bundle bundle) {
		setArguments(bundle);
		return this;
	}

	protected YunaTubeBaseActivity getBaseActivity() {
		FragmentActivity activity = getActivity();
		if (activity != null && activity instanceof YunaTubeBaseActivity) {
			return (YunaTubeBaseActivity) activity;
		}

		return null;
	}

	protected void invalidateOptionsMenu() {
		YunaTubeBaseActivity activity = getBaseActivity();
		if (activity != null) {
			activity.supportInvalidateOptionsMenu();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (getNetworkCheck() && !Utils.isNetworkAvailable()) {
			Utils.showToast(getActivity(), R.string.message_network_unavailable);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			GoogleAnalytics myInstance = GoogleAnalytics.getInstance(getActivity());
			Tracker tracker = myInstance.getDefaultTracker();
			tracker.sendView(getTrackerId());
		} catch(Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
		}
	}
	
	protected void showDialog() {
		if (Utils.isNetworkAvailable()) {
			if (loadingDialog == null)
				loadingDialog = new CustomDialog(getActivity());
			loadingDialog.show();
		}
	}
	
	protected void hideDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	public String getTextTitle() {
		return getClass().getSimpleName();
	}

	protected final void trackPageView(String page) {
		Log.w("yunatube", "track " + page);
	}

	protected String getPageName() {
		return getClass().getSimpleName();
	}

	protected final void trackPageView() {
		trackPageView(getPageName());
	}
	
	public boolean allowFragmentReplace() {
		return false;
	}
}