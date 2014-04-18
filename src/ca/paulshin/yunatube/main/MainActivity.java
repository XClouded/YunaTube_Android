package ca.paulshin.yunatube.main;

import java.lang.reflect.Constructor;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import ca.paulshin.yunatube.GCMActivity;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.album.PhotoListFragment;
import ca.paulshin.yunatube.services.DownloadService;
import ca.paulshin.yunatube.services.GCMIntentService;
import ca.paulshin.yunatube.widget.slidingmenu.SlidingMenu;
import ca.paulshin.yunatube.youtube.ClipListFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends GCMActivity {

	private YunaTubeBaseFragment mContent;
	protected Fragment mFrag;

	private SlidingMenu menu;

	private Handler mHandler;
	private boolean closeAppMsgFlag;

	private static final String IS_RATE_DONE = "is_rate_done";
	private static final String VISIT_NUMBER = "visit_number";

	public static final String EXTRA_FRAGMENT = "extra_fragment";

	public interface BackPressed {
		public boolean onBackPressed();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		actionBar.setLogo(Utils.isAprilFools() ? R.drawable.fool_actionbar_logo : R.drawable.actionbar_logo);
		setContentView(R.layout.activity_main);

		// Track visit number
		Preference.put(VISIT_NUMBER, Preference.contains(VISIT_NUMBER) ? Preference.getInt(VISIT_NUMBER) + 1 : 1);

		// Show rate dialog
		showRateDialog();

		// Push setting
		if (!Preference.contains(Constants.NOTIFICATION)) {
			Preference.put(Constants.NOTIFICATION, true);
		}

		// Check if the content frame contains the menu frame
		menu = getSlidingMenu();

		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = getSupportFragmentManager().beginTransaction();
			mFrag = new MainMenuFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.addToBackStack(mFrag.getClass().getSimpleName());
			t.commit();
		} else {
			mFrag = (Fragment) getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// Set Main Content
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(EXTRA_FRAGMENT)) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends YunaTubeBaseFragment> clazz = (Class<? extends YunaTubeBaseFragment>) bundle.getSerializable(EXTRA_FRAGMENT);
				Constructor<? extends YunaTubeBaseFragment> constructor = clazz.getConstructor((Class<?>[]) null);
				YunaTubeBaseFragment fragment = constructor.newInstance((Object[]) null);
				Bundle arg = new Bundle();
				arg.putAll(bundle);
				mContent = fragment.setBundle(arg);
			} catch (Exception e) {
				if (YunaTubeApplication.debuggable)
					mContent = new MainContentFragment();
			}
		} else
			mContent = new MainContentFragment();

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		setSlidingActionBarEnabled(false);

		// Configure the SlidingMenu
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setMenu(R.layout.menu_frame);

		// Get screen width
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int width, height;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}
		menu.setBehindWidth(Math.min(width, height) * Utils.getInteger(R.integer.factor_1) / Utils.getInteger(R.integer.factor_2));

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					closeAppMsgFlag = false;
				}
			}
		};

		if (savedInstanceState == null) {
			startDownload();
		}
	}

	private void startDownload() {
		if (Utils.isNetworkAvailable()) {
			Utils.debug("Starting XMLDownloadService");
			Intent intent = new Intent(this, DownloadService.class);
			startService(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Preference.getBoolean(GCMIntentService.EXTRA_DO_UPDATE)) {
			Preference.remove(GCMIntentService.EXTRA_DO_UPDATE);
			if (YunaTubeApplication.debuggable)
				Utils.showToast(this, "Updaing due to Update flag");
			startDownload();
		}

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GCMIntentService.NOTIFICATION_ID);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("service", true);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			if (mContent instanceof BackPressed) {
				super.onBackPressed();
			} else {
				if (!closeAppMsgFlag) {
					Utils.showToast(this, R.string.message_back_pressed);
					closeAppMsgFlag = true;
					mHandler.sendEmptyMessageDelayed(0, 2000);
				} else {
					finish();
				}
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			menu.removeItem(R.id.search);
			return true;
		}
		else {
			return super.onCreateOptionsMenu(menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			if (menu.isMenuShowing())
				menu.showContent();
			else
				menu.showMenu();
			break;

		case R.id.search:
			startActivity(new Intent(this, SearchActivity.class));

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void setFragment(final YunaTubeBaseFragment fragment, boolean addToStack) {
		if (fragment instanceof MainContentFragment)
			actionBar.setLogo(R.drawable.actionbar_logo);
		else
			actionBar.setLogo(R.drawable.actionbar_icon);

		if (!isCurrentContent(fragment)) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (fragment instanceof PhotoListFragment || fragment instanceof ClipListFragment) {
				ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
			}
			ft.replace(R.id.content_frame, fragment);
			if (addToStack)
				ft.addToBackStack(fragment.getClass().getSimpleName());

			ft.commit();

			updateFragmentInfo(fragment);
		}

		menu.showContent();
	}

	public void updateFragmentInfo(final YunaTubeBaseFragment fragment) {
		mContent = fragment;
		int title = fragment.getTitle();
		if (title != 0)
			actionBarTitle.setText(title);
		else
			actionBarTitle.setText(fragment.getTextTitle());
	}

	public boolean isCurrentContent(final YunaTubeBaseFragment fragment) {
		if (fragment.allowFragmentReplace())
			return false;

		int mContentTitle = mContent.getTitle();
		int fragmentTitle = fragment.getTitle();

		if ((mContentTitle == 0 && fragmentTitle != 0) || (mContentTitle != 0 && fragmentTitle == 0))
			return false;
		if (mContentTitle != 0 && fragmentTitle != 0)
			return (mContentTitle == fragmentTitle);
		return TextUtils.equals(mContent.getTextTitle(), fragment.getTextTitle());
	}

	private void showRateDialog() {
		if (Preference.getInt(VISIT_NUMBER) > 2 && !Preference.get(IS_RATE_DONE, false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.rate_yunatube_title).setMessage(R.string.message_rate_yunatube).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String appPackageName = getPackageName();
					Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
					marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					startActivity(marketIntent);

					Preference.put(IS_RATE_DONE, true);
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.rate_later, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Preference.put(IS_RATE_DONE, false);
					dialog.dismiss();
				}
			});
			// Create the AlertDialog object and return it
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
}