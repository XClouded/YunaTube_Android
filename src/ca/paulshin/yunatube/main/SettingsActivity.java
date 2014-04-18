package ca.paulshin.yunatube.main;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;

import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;

public class SettingsActivity extends YunaTubeBaseActivity implements OnClickListener, OnCheckedChangeListener {

	private CheckBox push;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		actionBarTitle.setText(R.string.actionbar_settings);
		
		if (TextUtils.equals(getString(R.string.lo), "1")) {
			findViewById(R.id.push_title).setVisibility(View.GONE);
			findViewById(R.id.set_push).setVisibility(View.GONE);
		}

		setUsername();

		// Push setting
		boolean notification = Preference.getBoolean(Constants.NOTIFICATION);
		
		findViewById(R.id.set_username).setOnClickListener(this);
		findViewById(R.id.set_push).setOnClickListener(this);
		findViewById(R.id.feedback).setOnClickListener(this);
		findViewById(R.id.rate).setOnClickListener(this);
		
		if (!YunaTubeApplication.debuggable)
			findViewById(R.id.language).setVisibility(View.GONE);
		else
			findViewById(R.id.language).setOnClickListener(this);

		push = (CheckBox) findViewById(R.id.push);
		push.setChecked(notification);
		push.setOnCheckedChangeListener(this);
	}

	private void setUsername() {
		TextView username = (TextView) findViewById(R.id.username);
		username.setText(Preference.getString(Constants.NICKNAME));
	}

	private void setPush(boolean notification) {
		Preference.put(Constants.NOTIFICATION, notification);
		Utils.showToast(this, notification ? R.string.notification_set : R.string.notification_canceled);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setPush(isChecked);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_username:
			showNicknameDialog();
			break;

		case R.id.set_push:
			push.setChecked(!push.isChecked());
			break;

		case R.id.feedback:
			Utils.emailToMe(this, getString(R.string.feedback_subject), getString(R.string.feedback_text));
			break;

		case R.id.rate:
			String appPackageName = getPackageName();
			Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
			marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(marketIntent);
			break;

		case R.id.language:
			setLocale(Utils.isAppLocaleKorean() ? "en" : "ko");
			break;

		}
	}

	private void setLocale(String lang) {
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		Intent refresh = new Intent(this, MainActivity.class);
		startActivity(refresh);
		finish();
	}

	private void showNicknameDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
		input.setLines(1);
		input.setHint(R.string.nickname_length);
		if (Preference.contains(Constants.NICKNAME))
			input.setText(Preference.getString(Constants.NICKNAME));
		alert.setTitle(R.string.nickname_set);
		alert.setMessage(R.string.nickname_desc);
		alert.setView(input);
		alert.setCancelable(false);
		alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				if (TextUtils.isEmpty(value)) {
					Utils.showToast(SettingsActivity.this, R.string.message_enter_data);
				} else {
					Preference.put(Constants.NICKNAME, value);
					Utils.showToast(SettingsActivity.this, R.string.nickname_successful);
					setUsername();
				}
			}
		});
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
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
