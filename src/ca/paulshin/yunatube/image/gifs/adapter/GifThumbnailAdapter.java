package ca.paulshin.yunatube.image.gifs.adapter;

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

import com.squareup.picasso.Picasso;

public class GifThumbnailAdapter extends BaseAdapter {
    private LayoutInflater inflater;
	private Picasso picasso;
	private List<String> gifThumbnailList;
	private static final int COLUMN = 4;
    
    private final int cellWidth;
	
	public GifThumbnailAdapter(Context context, List<String> gifThumbnailList) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.picasso = Picasso.with(context);
		this.gifThumbnailList = gifThumbnailList;
		
		// Get the width of the device to calculate the cell width
		Resources src = context.getResources();
		int gridMargins = (int) (src.getDimension(R.dimen.grid_padding) * 2);
		int cellMargins = (int) (src.getDimension(R.dimen.grid_thumbnail_padding) * 2);
		cellWidth = ((Utils.getScreenSize()[0] - gridMargins) / COLUMN) - cellMargins;
	}
	
	public int getCount() {
		return gifThumbnailList == null ? 0 : gifThumbnailList.size();
	}
	
	public Object getItem(int position) {
		return null;
	}
	
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		String imageName = gifThumbnailList.get(position);
		String url = String.format(Constants.GIF_THUMBNAIL_LIST_URL, imageName, cellWidth, cellWidth, (int)Math.random() * 1000000);

		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_caption, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.text.setVisibility(View.GONE);
            
			convertView.setLayoutParams(new GridView.LayoutParams(cellWidth, cellWidth));
			convertView.setTag(holder);
        }
		else
			holder = (ViewHolder)convertView.getTag();

		picasso.load(url)
    	.placeholder(R.drawable.stub)
    	.centerCrop()
    	.resize(cellWidth, cellWidth)
    	.into(holder.thumbnail);
        return convertView;
	}
	
	static class ViewHolder {
		public ImageView thumbnail;
		public TextView text;
    }
}
