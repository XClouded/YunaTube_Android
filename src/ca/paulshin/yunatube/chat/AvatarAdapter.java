package ca.paulshin.yunatube.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ca.paulshin.yunatube.R;

public class AvatarAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.chat_avatar_1, R.drawable.chat_avatar_2,
            R.drawable.chat_avatar_3, R.drawable.chat_avatar_4,
            R.drawable.chat_avatar_5, R.drawable.chat_avatar_6,
            R.drawable.chat_avatar_7, R.drawable.chat_avatar_8,
            R.drawable.chat_avatar_9
    };
 
    // Constructor
    public AvatarAdapter(Context c){
        mContext = c;
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
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    } 
}