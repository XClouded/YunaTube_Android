package ca.paulshin.yunatube.main;

import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;

import android.graphics.Color;
import android.os.Bundle;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.youtube.MyFavesFragment;

public class MyFavesActivity extends YunaTubeBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actionBarTitle.setText(R.string.submenu_youtube_my_faves);

		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
		setFragment(new MyFavesFragment(), false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}