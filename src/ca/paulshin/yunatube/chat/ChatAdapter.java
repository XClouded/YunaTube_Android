package ca.paulshin.yunatube.chat;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;

public class ChatAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;
		private List<ChatData> chatDataList;
		private Map<Integer, Integer> stickerMap;

		public ChatAdapter(Context context, List<ChatData> items, Map<Integer, Integer> stickerMap) {
			chatDataList = items;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.stickerMap = stickerMap;
		}

		public void add(ChatData parm_data) {
			chatDataList.add(parm_data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return chatDataList.size();
		}

		@Override
		public ChatData getItem(int position) {
			return chatDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return chatDataList.get(position).type;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			int type = getItemViewType(position);

			if (convertView == null) {
				switch (type) {
				case 0:
					view = inflater.inflate(R.layout.row_chat_list_other, null);
					break;
				case 1:
					view = inflater.inflate(R.layout.row_chat_list_me, null);
					break;
				case 2:
					view = inflater.inflate(R.layout.row_chat_list_box, null);
					break;
				}
			} else {
				view = convertView;
			}

			ChatData data = chatDataList.get(position);

			if (data != null) {
				if (type == 0) {
					ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
					ImageView sticker = (ImageView) view.findViewById(R.id.sticker);
					TextView writer = (TextView) view.findViewById(R.id.writer);
					TextView message = (TextView) view.findViewById(R.id.message);
					TextView time = (TextView) view.findViewById(R.id.time);
					
					if (data.message.startsWith("85111")) {
						Integer stickerKey = Integer.parseInt(data.message);
						Integer stickerValue = stickerMap.get(stickerKey);
						sticker.setImageResource(stickerValue);
						sticker.setVisibility(View.VISIBLE);
						message.setVisibility(View.GONE);
					}
					else {
						message.setText(data.message);
						message.setMaxWidth(Utils.getPxFromDp(200));
						sticker.setVisibility(View.GONE);
						message.setVisibility(View.VISIBLE);
					}

					writer.setText(data.writer);
					time.setText(data.date);
					
					int avatarRes = Utils.getResId("chat_avatar_" + data.iconType, R.drawable.class);
					Utils.debug("Avatar Id: " + avatarRes);
					avatar.setImageResource(avatarRes);
					
				} else if (type == 1) {
					ImageView avatar = (ImageView)view.findViewById(R.id.avatar);
					ImageView sticker = (ImageView) view.findViewById(R.id.sticker);
					TextView message = (TextView) view.findViewById(R.id.message);
					TextView time = (TextView) view.findViewById(R.id.time);
					
					if (data.message.startsWith("85111")) {
						Integer stickerKey = Integer.parseInt(data.message);
						Integer stickerValue = stickerMap.get(stickerKey);
						sticker.setImageResource(stickerValue);
						sticker.setVisibility(View.VISIBLE);
						message.setVisibility(View.GONE);
					}
					else {
						message.setText(data.message);
						message.setMaxWidth(Utils.getPxFromDp(200));
						sticker.setVisibility(View.GONE);
						message.setVisibility(View.VISIBLE);
					}
					time.setText(data.date);
					
					int avatarRes = Utils.getResId("chat_avatar_" + Preference.get(ChatActivity.AVATAR_ID, "1"), R.drawable.class);
					Utils.debug("Avatar Id: " + avatarRes);
					avatar.setImageResource(avatarRes);
				} else if (type == 2) {
					TextView notice = (TextView) view.findViewById(R.id.notice);
					notice.setText(data.message);
					Drawable background = notice.getBackground();
					background.setAlpha(150);
				}
			}
			return view;
		}
	}