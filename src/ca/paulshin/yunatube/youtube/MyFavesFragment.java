package ca.paulshin.yunatube.youtube;

import static ca.paulshin.yunatube.database.YunaTubeSqliteDatabase.getInstance;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.database.Schema.TableMyFaves;
import ca.paulshin.yunatube.main.MainActivity.BackPressed;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver.OnDataChangeListener;
import ca.paulshin.yunatube.youtube.MyFavesAdapter.ViewHolder;

public class MyFavesFragment extends YunaTubeBaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnDataChangeListener, BackPressed {

	private static final int CONTEXTMENU_ALIAS = 100;
	private static final int CONTEXTMENU_RESETTITLE = 101;
	private static final int CONTEXTMENU_REMOVE = 102;

	private GridView gridView;
	private TextView empty;
	private MyFavesAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grid_2_col, null);

		gridView = (GridView) view.findViewById(R.id.gridview);
		empty = (TextView) view.findViewById(R.id.empty);
		empty.setText(R.string.faves_no_clip);

		gridView.setOnItemClickListener(this);
		gridView.setOnCreateContextMenuListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new MyFavesAdapter(getActivity(), null);
		gridView.setAdapter(adapter);
		update();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(getActivity()) {
			@Override
			public Cursor loadInBackground() {
				return getInstance().fetchMyFaves();
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
		empty.setVisibility(cursor.getCount() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		adapter.swapCursor(null);
	}

	@Override
	public int getTitle() {
		return R.string.submenu_youtube_my_faves;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return "faves - android";
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder holder = (ViewHolder) view.getTag();

		Bundle bundle = new Bundle();
		bundle.putString(YouTubeActivity.EXTRA_YOUTUBE_YTID, holder.url);
		Intent intent = new Intent(getActivity(), YouTubeActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(getString(R.string.cmenu));
		menu.add(0, CONTEXTMENU_ALIAS, 0, getString(R.string.cmenu_alias));
		menu.add(0, CONTEXTMENU_RESETTITLE, 0, getString(R.string.cmenu_resettitle));
		menu.add(0, CONTEXTMENU_REMOVE, 0, getString(R.string.cmenu_remove));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case CONTEXTMENU_ALIAS: {
			final FrameLayout fl = new FrameLayout(getActivity());
			final EditText input = new EditText(getActivity());
			input.setGravity(Gravity.CENTER);
			input.setHint(getString(R.string.cmenu_pop_alias_hint));

			fl.addView(input, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

			input.setText("");
			new AlertDialog.Builder(getActivity()).setView(fl).setTitle(getString(R.string.cmenu_pop_alias)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int which) {
					d.dismiss();
					if (input.getText().toString().trim().length() == 0)
						Utils.showToast(getActivity(), R.string.cmenu_pop_alias_failed);
					else {
						getInstance().updateMyFaves(gridView.getAdapter().getItemId(menuInfo.position), input.getText().toString());
						Utils.showToast(getActivity(), getString(R.string.cmenu_pop_alias_successful, input.getText().toString()));
						update();
					}
				}
			}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int which) {
					d.dismiss();
				}
			}).create().show();
			return true;
		}

		case CONTEXTMENU_RESETTITLE: {
			Long itemId = gridView.getAdapter().getItemId(menuInfo.position);

			Cursor cur = getInstance().fetchMyFave(itemId);
			String title = cur.getString(cur.getColumnIndexOrThrow(TableMyFaves.title));
			if (getInstance().updateMyFaves(itemId, title)) {
				Utils.showToast(getActivity(), getString(R.string.cmenu_pop_resettitle_successful, title));
				update();
			}

			return true;
		}

		case CONTEXTMENU_REMOVE: {
			Utils.debug("item id:" + menuInfo.id);
			if (getInstance().deleteMyClip(menuInfo.id)) {
				Utils.showToast(getActivity(), R.string.cmenu_pop_remove_successful);
				update();
			}
			return true;
		}
		default:
			return false;
		}
	}

	private void update() {
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onDataChange(Bundle bundle) {
		if (bundle.containsKey(YouTubeActivity.EXTRA_MYCLIP_SAVE))
			update();
	}

	@Override
	public boolean onBackPressed() {
		return true;
	}
}
