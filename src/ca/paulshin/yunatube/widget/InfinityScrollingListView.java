package ca.paulshin.yunatube.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class InfinityScrollingListView extends ListView implements OnScrollListener {	
	protected abstract BaseAdapter getCustomAdapter();
	public abstract void load();
	
	protected List<String[]> messagesList = new ArrayList<String[]>();
	protected View footerView;
	protected View emptyView;
	protected Context context;
	protected BaseAdapter adapter;

	public boolean isBusy;
	protected int lastIndex = 0;

	public InfinityScrollingListView(Context context) {
		super(context);
		this.context = context;
	}

	public InfinityScrollingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public InfinityScrollingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void init(View footerView) {
		setOnScrollListener(this);
		setFooterView(footerView);
		
		adapter = getCustomAdapter();
		setAdapter(adapter);
		
		setDivider(null);
		setDividerHeight(0);
	}

	private void setFooterView(View footerView) {
		this.footerView = footerView;
		addFooterView(footerView);
	}	

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		boolean loadMore = firstVisible + visibleCount >= totalCount;

		if (firstVisible > 0 && lastIndex != 1 && loadMore && getAdapter().getCount() > 0 && !isBusy) {
			isBusy = true;
			load();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}