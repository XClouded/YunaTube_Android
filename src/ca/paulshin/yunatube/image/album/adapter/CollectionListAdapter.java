package ca.paulshin.yunatube.image.album.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;

public class CollectionListAdapter extends BaseAdapter {
	private List<CollectionSet> collectionList;
	private static LayoutInflater inflater;

	public CollectionListAdapter(Activity activity) {
		YunaTubeApplication application = (YunaTubeApplication) Utils.ctx;
		this.collectionList = application.collectionList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return collectionList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_submenu_album, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.list_title);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder)convertView.getTag();
		
		CollectionSet collection = collectionList.get(position);
		if (collection != null) {
			holder.title.setText(collection.getTitle());
		}
		return convertView;
	}
	
	private static class ViewHolder {
		TextView title;
	}
}