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
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.image.album.pojo.Photo;

import com.squareup.picasso.Picasso;

public class PhotoListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
	private Picasso picasso;
	private List<Photo> photoList;
	private static final String PHOTO_SIZE_SUFFIX = "q";
	private static final int COLUMN = 4;
    
    private final int cellWidth;
	
	public PhotoListAdapter(Context context, List<Photo> photoList) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.photoList = photoList;
		this.picasso = Picasso.with(context);
		
		// Get the width of the device to calculate the cell width
		Resources src = context.getResources();
		int gridMargins = (int) (src.getDimension(R.dimen.grid_padding) * 2);
		int cellMargins = (int) (src.getDimension(R.dimen.grid_thumbnail_padding) * 2);
		cellWidth = ((Utils.getScreenSize()[0] - gridMargins) / COLUMN) - cellMargins;
	}
	
	public int getCount() {
		return photoList == null ? 0 : photoList.size();
	}
	
	public Object getItem(int position) {
		return photoList.get(position);
	}
	
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final Photo photo = photoList.get(position);
		
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

        if (photo != null) {
			picasso.load(photo.getUrl(PHOTO_SIZE_SUFFIX))
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