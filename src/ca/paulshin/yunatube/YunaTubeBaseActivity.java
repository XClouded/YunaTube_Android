package ca.paulshin.yunatube;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.common.CustomDialog;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.main.HotNewsActivity;
import ca.paulshin.yunatube.main.MainActivity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class YunaTubeBaseActivity extends BaseActivity {
	protected ActionBar actionBar;
	protected TextView actionBarTitle;
	private Dialog loadingDialog;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		
		customizeActionBar();
	}

	protected void onCreate(Bundle bundle, boolean isFullScreen) {
		super.onCreate(bundle);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

		if (isFullScreen)
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void customizeActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			try {
				ImageView icon = (ImageView) findViewById(android.R.id.home);
				FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
				iconLp.topMargin = iconLp.bottomMargin = 0;
				icon.setLayoutParams(iconLp);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Customize the action bar
		actionBar = getSupportActionBar();
		BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(Utils.isAprilFools() ? R.drawable.fool_actionbar_bg : R.drawable.actionbar_bg);
		bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		actionBar.setBackgroundDrawable(bg);
		actionBar.setLogo(R.drawable.actionbar_icon);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		LayoutInflater inflater = LayoutInflater.from(this);
		View customView = inflater.inflate(R.layout.actionbar_title, null);
		actionBarTitle = (TextView) customView.findViewById(R.id.actionbar_custom_title);
		actionBar.setCustomView(customView);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setTitle("");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	protected int getContentId() {
		return android.R.id.content;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Bundle bundle = getIntent().getExtras();
			if (bundle != null && bundle.getBoolean(HotNewsActivity.EXTRA_FROM_GCM)) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			finish();
			return true;

		default:
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		int index = requestCode >> 16;
		if (index != 0) {

			Fragment fragment = getSupportFragmentManager().findFragmentById(getContentId());
			if (fragment != null) {
				fragment.onActivityResult(requestCode & 0xFFF, resultCode, data);
				return;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void setFragment(YunaTubeBaseFragment fragment, boolean stack) {

		final String name = fragment.getClass().getSimpleName();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (stack) {
			transaction.addToBackStack(name);
		}

		transaction.replace(getContentId(), fragment, name).commit();
	}

	public void displayDialog() {
		if (Utils.isNetworkAvailable()) {
			loadingDialog = new CustomDialog(this);
			loadingDialog.show();
		}
	}

	public void hideDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
	}
}
