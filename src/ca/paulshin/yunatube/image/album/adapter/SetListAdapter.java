package ca.paulshin.yunatube.image.album.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;

import com.squareup.picasso.Picasso;

public class SetListAdapter extends BaseAdapter {
	private static final int COLUMN = 3;
	
	private List<CollectionSet> setList;
    private LayoutInflater inflater;
    
    private final int cellWidth;
    private Picasso picasso;
    
    public SetListAdapter(Context context, List<CollectionSet> collectionList) {
        this.setList = collectionList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        picasso = Picasso.with(context);
		
		// Get the width of the device to calculate the cell width
		Resources src = context.getResources();
		int gridMargins = (int) (src.getDimension(R.dimen.grid_padding) * 2);
		int cellMargins = (int) (src.getDimension(R.dimen.grid_thumbnail_padding) * 2);
		cellWidth = ((Utils.getScreenSize()[0] - gridMargins) / COLUMN) - cellMargins;
    }

    public int getCount() {
        return setList == null ? 0 : setList.size();
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
			convertView = inflater.inflate(R.layout.item_grid_caption, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            
			convertView.setLayoutParams(new GridView.LayoutParams(cellWidth, cellWidth));
			convertView.setTag(holder);
        }
		else
			holder = (ViewHolder)convertView.getTag();
        
        CollectionSet set = setList.get(position);
        if (set != null) {
			holder.text.setText(set.getTitle());
			picasso.load(String.format(Constants.ALBUM_FLICKR_PHOTOSETS_GETCORVER, set.getId()))
        	.placeholder(R.drawable.stub)
        	.centerCrop()
        	.resize(cellWidth, cellWidth)
        	.into(holder.thumbnail);
		}
        return convertView;
    }
    
    static class ViewHolder {
		public ImageView thumbnail;
		public TextView text;
    }
}