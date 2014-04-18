package ca.paulshin.yunatube.youtube;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.database.Schema.TableMyFaves;

import com.squareup.picasso.Picasso;

public class MyFavesAdapter extends CursorAdapter {
	private static final int COLUMN = 2;

	static class ViewHolder {
		public ImageView thumbnail;
		public TextView text;

		public String category;
		public String title;
		public String url;
	}

	final int cellWidth;
	private Picasso picasso;

	public MyFavesAdapter(Context context, Cursor c) {
		super(context, c, 0);

		picasso = Picasso.with(context);

		// Get the width of the device to calculate the cell width
		Resources src = context.getResources();
		int gridMargins = (int) (src.getDimension(R.dimen.grid_padding) * 2);
		int cellMargins = (int) (src.getDimension(R.dimen.grid_thumbnail_padding) * 2);
		cellWidth = ((Utils.getScreenSize()[0] - gridMargins) / COLUMN) - cellMargins;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		holder.text.setText(cursor.getString(cursor.getColumnIndex(TableMyFaves.alias)));
		picasso.load(String.format(Constants.CLIP_HQ_THUMBNAIL_URL, cursor.getString(cursor.getColumnIndex(TableMyFaves.url)))).placeholder(R.drawable.stub_2_3).centerCrop()
				.resize(cellWidth, cellWidth * 2 / 3).into(holder.thumbnail);
		holder.category = cursor.getString(cursor.getColumnIndex(TableMyFaves.category));
		holder.title = cursor.getString(cursor.getColumnIndex(TableMyFaves.title));
		holder.url = cursor.getString(cursor.getColumnIndex(TableMyFaves.url));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.item_grid_caption, parent, false);
		v.setLayoutParams(new GridView.LayoutParams(cellWidth, cellWidth * 2 / 3));

		ViewHolder holder = new ViewHolder();
		holder.thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
		holder.text = (TextView) v.findViewById(R.id.text);
		v.setTag(holder);

		return v;
	}
}