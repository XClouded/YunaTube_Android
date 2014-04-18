package ca.paulshin.yunatube.main;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseActivity;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.youtube.ClipListAdapter;
import ca.paulshin.yunatube.youtube.YouTubeActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends YunaTubeBaseActivity implements OnQueryTextListener, OnSuggestionListener, OnItemClickListener {
	// reference: http://pastebin.com/stZT80UJ

	private ListView listView;
	private SearchView searchView;
	private TextView direction;
	private List<NameValuePair> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_clip_list);

		listView = (ListView) findViewById(R.id.list);
		direction = (TextView) findViewById(R.id.direction);
		direction.setVisibility(View.VISIBLE);
		
		findViewById(R.id.loading).setVisibility(View.GONE);
		
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Create the search view
		searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("");
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnSuggestionListener(this);

		menu.add(R.string.actionbar_search).setIcon(R.drawable.search_magnifier).setActionView(searchView).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

		if (TextUtils.isEmpty(query)) {
			Utils.showToast(this, R.string.search_keyword);
		} else if (!Utils.isNetworkAvailable())
			Utils.showToast(this, R.string.message_network_unavailable);
		else {
			new AsyncTask<String, Void, List<NameValuePair>>() {
				@Override
				protected List<NameValuePair> doInBackground(String... params) {
					return YouTubeDataLoader.getLoaderInstance().search(params[0]);
				}

				protected void onPostExecute(List<NameValuePair> result) {
					if (result == null)
						Utils.showToast(SearchActivity.this, R.string.message_server_unavailable);
					else if (result.size() == 0)
						Utils.showToast(SearchActivity.this, R.string.search_no_results);
					else {
						direction.setVisibility(View.GONE);
						SearchActivity.this.data = result;
						listView.setAdapter(new ClipListAdapter(SearchActivity.this, result));
					}
				};

			}.execute(query);
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		NameValuePair item = data.get(position);
		Bundle bundle = new Bundle();
		bundle.putString(YouTubeActivity.EXTRA_YOUTUBE_YTID, item.getName());
		Intent intent = new Intent(this, YouTubeActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}