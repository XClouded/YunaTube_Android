package ca.paulshin.yunatube.game;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Utils;

import com.squareup.picasso.Picasso;

public class GameAdapter extends BaseAdapter {
    private LayoutInflater inflater;
	private Picasso picasso;
	private PicObject [] picIndices;
	private static final int COLUMN = 4;
	private boolean showAll;
    
    private final int cellWidth;
	
	public GameAdapter(Context context, PicObject [] picIndices) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.picIndices = picIndices;
		this.picasso = Picasso.with(context);
		this.showAll = true;
		
		// Get the width of the device to calculate the cell width
		Resources src = context.getResources();
		int gridMargins = (int) (src.getDimension(R.dimen.grid_padding) * 2);
		int cellMargins = (int) (src.getDimension(R.dimen.grid_thumbnail_padding) * 2);
		cellWidth = ((Utils.getScreenSize()[0] - gridMargins) / COLUMN) - cellMargins;
	}
	
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
	
	public int getCount() {
		return picIndices.length;
	}
	
	public Object getItem(int position) {
		return picIndices[position];
	}
	
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		PicObject picObject = (PicObject)getItem(position);
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_game_grid, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.stub = (ImageView) convertView.findViewById(R.id.stub);
            if (showAll || picObject.isDone) {
	            holder.thumbnail.setVisibility(View.VISIBLE);
	            holder.stub.setVisibility(View.GONE);
	            if (showAll && picObject.isDone) {
	            	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            		holder.thumbnail.setAlpha(0.5f);
	            }
            }
            else {
	            holder.thumbnail.setVisibility(View.GONE);
	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	holder.thumbnail.setAlpha(1.0f);
	            holder.stub.setVisibility(View.VISIBLE);
            }
            
			convertView.setLayoutParams(new GridView.LayoutParams(cellWidth, cellWidth));
			convertView.setTag(holder);
        }
		else
			holder = (ViewHolder)convertView.getTag();

        if (picObject != null) {
			picasso.load(picObject.url)
        	.centerCrop()
        	.resize(cellWidth, cellWidth)
        	.into(holder.thumbnail);
		}
        return convertView;
	}
    
    static class ViewHolder {
		public ImageView thumbnail, stub;
    }
}