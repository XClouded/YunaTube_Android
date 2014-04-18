package ca.paulshin.yunatube.youtube;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ca.paulshin.yunatube.R;

public class SectionListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String[]> data;

	public SectionListAdapter(Context context, List<String[]> data) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
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
		final String[] section = data.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_text, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.text.setText(section[2]);
		return convertView;
	}

	static class ViewHolder {
		public TextView text;
	}
}