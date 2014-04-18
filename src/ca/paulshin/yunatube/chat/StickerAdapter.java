package ca.paulshin.yunatube.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Utils;

public class StickerAdapter extends BaseAdapter {
    private Context mContext;
    private int side;
 
    // Keep all Images in array
    public Integer[] mThumbIds = {
    		R.drawable.sticker_1, R.drawable.sticker_2, R.drawable.sticker_3, R.drawable.sticker_4,
    		R.drawable.sticker_5, R.drawable.sticker_6, R.drawable.sticker_7, R.drawable.sticker_8,
    		R.drawable.sticker_9, R.drawable.sticker_10, R.drawable.sticker_11, R.drawable.sticker_12,
    		R.drawable.sticker_13, R.drawable.sticker_14, R.drawable.sticker_15, R.drawable.sticker_16,
    		R.drawable.sticker_17, R.drawable.sticker_18, R.drawable.sticker_19, R.drawable.sticker_20,
    		R.drawable.sticker_21, R.drawable.sticker_22, R.drawable.sticker_23, R.drawable.sticker_24
    };
 
    // Constructor
    public StickerAdapter(Context c){
        mContext = c;
        side = (int)(Utils.getScreenSize()[0] / 4);
    }
 
    @Override
    public int getCount() {
        return mThumbIds.length;
    }
 
    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_sticker, null);
			convertView.setLayoutParams(new GridView.LayoutParams(side, side));
			
			ViewHolder holder = new ViewHolder();
			holder.sticker = (ImageView) convertView.findViewById(R.id.sticker);
			convertView.setTag(holder);
		}
    	
    	ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.sticker.setImageResource(mThumbIds[position]);
        return convertView;
    } 
    
    protected class ViewHolder {
		public ImageView sticker;
	}
}