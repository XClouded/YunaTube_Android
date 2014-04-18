package ca.paulshin.yunatube.image.gifs.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Utils;

public class MyGifThumbnailAdapter extends BaseAdapter {
    private LayoutInflater inflater;
	private List<String> gifThumbnailList;
	private static final int COLUMN = 4;
    
    private final int cellWidth;
	
	public MyGifThumbnailAdapter(Context context, List<String> gifThumbnailList) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		File imgFile = new File(imageName);
		if (imgFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			holder.thumbnail.setImageBitmap(myBitmap);
		}
        return convertView;
	}
	
	static class ViewHolder {
		public ImageView thumbnail;
		public TextView text;
    }
}
