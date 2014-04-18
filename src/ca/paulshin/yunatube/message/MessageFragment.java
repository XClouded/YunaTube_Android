package ca.paulshin.yunatube.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;

import com.actionbarsherlock.view.MenuItem;

public class MessageFragment extends YunaTubeBaseFragment {
	private ProgressBar empty;
	private MessageListView listView;
	private View footerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, null);

		empty = (ProgressBar) view.findViewById(R.id.empty);
		listView = (MessageListView) view.findViewById(android.R.id.list);

		footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_footer_message, null, false);

		listView.init(footerView);
		listView.setEmptyView(empty);
		listView.setFastScrollEnabled(true);
		listView.load();

		return view;
	}

	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.menu_message, menu);
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.search);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.write:
			String userName = Preference.getString(Constants.NICKNAME);
			if (TextUtils.isEmpty(userName)) {
				Utils.showToast(getActivity(), R.string.nickname_must);
			} else {
				final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				final EditText input = new EditText(getActivity());
				input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(140) });
				input.setLines(4);
				input.setHint(R.string.message_hint);
				alert.setTitle(R.string.supermenu_message_desc);
				alert.setView(input);
				alert.setCancelable(false);
				alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString().trim();
						if (TextUtils.isEmpty(value)) {
							Utils.showToast(getActivity(), R.string.message_enter_data);
						} else {
							String userName = Preference.getString(Constants.NICKNAME);
							userName = TextUtils.isEmpty(userName) ? getString(R.string.nickname_noname) : userName;
							String deviceId = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
							listView.update(userName, value, String.valueOf(System.currentTimeMillis() / 1000), deviceId);
						}
					}
				});

				alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
				alert.show();
			}
			break;

		case R.id.refresh:
			listView.load();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public int getTitle() {
		return R.string.supermenu_message;
	}

	@Override
	protected boolean getNetworkCheck() {
		return true;
	}

	@Override
	protected String getTrackerId() {
		return "message_for_yuna - android";
	}
}