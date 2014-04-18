package ca.paulshin.yunatube.links;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;

public class WebsiteListAdapter extends ArrayAdapter<Website> {
	private List<Website> websites;

	static class ViewHolder {
		ImageView thumbnail;
		TextView title;
		TextView url;
		TextView desc;
	}
	
	public WebsiteListAdapter(Context context, int resource, List<Website> websites) {
		super(context, resource, websites);
		this.websites = websites;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater vi = LayoutInflater.from(getContext());
			convertView = vi.inflate(R.layout.row_links, null);
			
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.links_row_thumbnail);
			holder.title = (TextView) convertView.findViewById(R.id.links_row_title);
			holder.url = (TextView) convertView.findViewById(R.id.links_row_url);
			holder.desc = (TextView) convertView.findViewById(R.id.links_row_desc);
			
			convertView.setTag(holder);
		}
		else 
			holder = (ViewHolder)convertView.getTag();

		Website website = websites.get(position);

		if (website != null) {
			
			holder.thumbnail.setImageBitmap(website.getThumbnail());
			holder.title.setText(website.getTitle());
			holder.url.setText(website.getUrl());
			if (website.getSubtitle().equals(""))
				holder.desc.setVisibility(View.GONE);
			else
				holder.desc.setText(website.getSubtitle());
		}

		return convertView;
	}
}