package ca.paulshin.yunatube.message;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.http.YouTubeDataLoader;
import ca.paulshin.yunatube.widget.InfinityScrollingListView;

public class MessageListView extends InfinityScrollingListView {
	public MessageListView(Context context) {
		super(context);
	}

	public MessageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MessageListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected BaseAdapter getCustomAdapter() {
		return new InfiniteListAdapter(getContext());
	}

	public void load() {
		new AsyncTask<String, Void, List<String[]>>() {
			@Override
			protected void onPreExecute() {
			}

			@Override
			protected List<String[]> doInBackground(String... params) {
				try {
					return YouTubeDataLoader.getLoaderInstance().loadMessages(messagesList, lastIndex);
				} catch (Exception e) {
					if (YunaTubeApplication.debuggable)
						e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<String[]> result) {
				isBusy = false;

				if (result == null || result.size() == 0) {
					removeFooterView(footerView);
					return;
				}

				String[] lastMessage = (String[]) (result.get(result.size() - 1));
				lastIndex = Integer.parseInt(lastMessage[0]);
				if (lastIndex == 1) {
					removeFooterView(footerView);
				}

				adapter.notifyDataSetChanged();
			}
		}.execute();
	}

	public void update(String userName, String value, String time, String deviceId) {
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
					String url = Constants.MESSAGE_SUBMIT;
					List<NameValuePair> parameters = new LinkedList<NameValuePair>();
					parameters.add(new BasicNameValuePair("username", params[0]));
					parameters.add(new BasicNameValuePair("message", params[1]));
					parameters.add(new BasicNameValuePair("time", params[2]));
					parameters.add(new BasicNameValuePair("device", params[3]));
					parameters.add(new BasicNameValuePair("report", "0"));
					url = Utils.getParameterizedUrl(url, parameters);

					return YouTubeDataLoader.getLoaderInstance().sendMessage(url);
				} catch (Exception e) {
					if (YunaTubeApplication.debuggable)
						e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Boolean success) {
				if (success) {
					Utils.showToast((Activity) context, R.string.message_successful);

					if (!isBusy) {
						lastIndex = 0;
						messagesList.clear();

						isBusy = true;
						load();
					}
				} else
					Utils.showToast((Activity) context, R.string.message_failed);
			}
		}.execute(userName, value, time, deviceId);
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
				viewHolder.comment = (RelativeLayout) v.findViewById(R.id.comment);
				viewHolder.userId = (TextView) v.findViewById(R.id.username);
				viewHolder.time = (TextView) v.findViewById(R.id.time);
				viewHolder.message = (TextView) v.findViewById(R.id.message);

				Drawable background = viewHolder.comment.getBackground();
				background.setAlpha(128);

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
		RelativeLayout comment;
		TextView userId;
		TextView time;
		TextView message;
	}
}