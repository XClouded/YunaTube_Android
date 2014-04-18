package ca.paulshin.yunatube.youtube;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.widget.InfinityScrollingListView;

public class YouTubeCommentListView extends InfinityScrollingListView {
	private String youtubeUrl;
	private int isLastIndexReached = 0;

	public YouTubeCommentListView(Context context) {
		super(context);
	}

	public YouTubeCommentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YouTubeCommentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected BaseAdapter getCustomAdapter() {
		return new InfiniteListAdapter(getContext());
	}

	public void setYoutubeUrl(String url) {
		youtubeUrl = url;
	}

	@Override
	public void load() {
		new AsyncTask<String, Void, List<String[]>>() {
			@Override
			protected List<String[]> doInBackground(String... params) {
				try {
					return YouTubeDataLoader.getLoaderInstance().loadComments(messagesList, youtubeUrl, lastIndex);
				} catch (Exception e) {
					if (YunaTubeApplication.debuggable) {
						e.printStackTrace();
					}
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<String[]> result) {
				isBusy = false;
				setVisibility(View.VISIBLE);

				if (result == null || result.size() == 0) {
					removeFooterView(footerView);
					return;
				}

				String[] lastMessage = (String[]) (result.get(result.size() - 1));
				lastIndex = Integer.parseInt(lastMessage[0]);
				isLastIndexReached = Integer.parseInt(lastMessage[6]);

				if (isLastIndexReached == 1) {
					removeFooterView(footerView);
				}

				adapter.notifyDataSetChanged();
			}
		}.execute();
	}

	public void update(String youtubeUrl, String userName, String value, String time, String deviceId) {
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				try {
					String url = Constants.YOUTUBE_COMMENT_SUBMIT;
					List<NameValuePair> parameters = new LinkedList<NameValuePair>();
					parameters.add(new BasicNameValuePair("ytid", params[0]));
					parameters.add(new BasicNameValuePair("username", params[1]));
					parameters.add(new BasicNameValuePair("message", params[2]));
					parameters.add(new BasicNameValuePair("time", params[3]));
					parameters.add(new BasicNameValuePair("device", params[4]));
					parameters.add(new BasicNameValuePair("report", "0"));
					url = Utils.getParameterizedUrl(url, parameters);
					return YouTubeDataLoader.getLoaderInstance().sendMessage(url);
				} catch (Exception e) {
					if (YunaTubeApplication.debuggable) {
						e.printStackTrace();
					}
					return null;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					Utils.showToast((Activity) context, R.string.message_successful);

					if (!isBusy) {
						lastIndex = 0;
						messagesList.clear();

						isBusy = true;
						load();
					}
				} else {
					Utils.showToast((Activity) context, R.string.message_failed);
				}
			}
		}.execute(youtubeUrl, userName, value, String.valueOf(System.currentTimeMillis() / 1000), deviceId);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {

		boolean loadMore = firstVisible + visibleCount >= totalCount;

		if (firstVisible > 0 && isLastIndexReached != 1 && loadMore && adapter.getCount() > 0 && !isBusy) {
			isBusy = true;
			load();
		}
	}

	/*
	 * Adapter
	 */
	private class InfiniteListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public InfiniteListAdapter(Context context) {
			mLayoutInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return messagesList.size();
		}

		public Object getItem(int pos) {
			return pos;
		}

		public long getItemId(int pos) {
			return pos;
		}

		public View getView(int pos, View v, ViewGroup p) {
			ViewHolder viewHolder;

			if (v == null) {
				v = mLayoutInflater.inflate(R.layout.row_message, null);

				viewHolder = new ViewHolder();
				viewHolder.userId = (TextView) v.findViewById(R.id.username);
				viewHolder.time = (TextView) v.findViewById(R.id.time);
				viewHolder.message = (TextView) v.findViewById(R.id.message);

				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();
			}

			String[] messageSet = (String[]) messagesList.get(pos);
			if (messageSet != null) {
				viewHolder.userId.setText(messageSet[1]);
				viewHolder.time.setText(Utils.getTime(messageSet[3]));
				viewHolder.message.setText(messageSet[2]);
			}

			return v;
		}
	}

	static class ViewHolder {
		TextView userId;
		TextView time;
		TextView message;
	}
}