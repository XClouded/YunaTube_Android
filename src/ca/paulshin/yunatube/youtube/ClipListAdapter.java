package ca.paulshin.yunatube.youtube;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;

import com.squareup.picasso.Picasso;

public class ClipListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
	private Picasso picasso;
	private List<NameValuePair> data;
    
	public ClipListAdapter(Context context, List<NameValuePair> data) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.picasso = Picasso.with(context);
	}
	
	public int getCount() {
		return data == null ? 0 : data.size();
	}
	
	public Object getItem(int position) {
		return data.get(position);
	}
	
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final NameValuePair clip = data.get(position);
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_thumbnail_text, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.text = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
        }
		else
			holder = (ViewHolder)convertView.getTag();
		
		holder.text.setText(clip.getValue());
		picasso.load(String.format(Constants.CLIP_THUMBNAIL_URL, clip.getName()))
    	.placeholder(R.drawable.stub)
    	.centerCrop()
    	.resize(162, 107)
    	.into(holder.thumbnail);
        return convertView;
	}
    
    static class ViewHolder {
		public ImageView thumbnail;
		public TextView text;
    }
}