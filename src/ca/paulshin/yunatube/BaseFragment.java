package ca.paulshin.yunatube;

import static ca.paulshin.yunatube.YunaTubeApplication.broadcast;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver.OnDataChangeListener;
import ca.paulshin.yunatube.services.broadcast.DownloadReceiver;
import ca.paulshin.yunatube.services.broadcast.DownloadReceiver.OnDownloadListener;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseFragment extends SherlockFragment {

	private DownloadReceiver downloadReceiver = null;
	private DataChangeReceiver dataChangeReceiver = null;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (OnDownloadListener.class.isInstance(this)) {
			downloadReceiver = DownloadReceiver.register((OnDownloadListener) this);
			broadcast.registerReceiver(downloadReceiver, new IntentFilter(DownloadReceiver.ACTION));
		}
		
		if (OnDataChangeListener.class.isInstance(this)) {
			dataChangeReceiver = DataChangeReceiver.register((OnDataChangeListener) this);
			broadcast.registerReceiver(dataChangeReceiver, new IntentFilter(DataChangeReceiver.ACTION));
		}
	}
	
	@Override
	public void onDestroyView() {
		
		if (downloadReceiver != null) {
			broadcast.unregisterReceiver(downloadReceiver);
		}
		
		if (dataChangeReceiver != null) {
			broadcast.unregisterReceiver(dataChangeReceiver);
		}
		
		super.onDestroyView();
	}
}