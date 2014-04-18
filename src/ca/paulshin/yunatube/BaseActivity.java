package ca.paulshin.yunatube;

import static ca.paulshin.yunatube.YunaTubeApplication.broadcast;
import android.content.IntentFilter;
import android.os.Bundle;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.DataChangeReceiver.OnDataChangeListener;
import ca.paulshin.yunatube.services.broadcast.DownloadReceiver;
import ca.paulshin.yunatube.services.broadcast.DownloadReceiver.OnDownloadListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseActivity extends SherlockFragmentActivity {

	private DownloadReceiver downloadReceiver = null;
	private DataChangeReceiver dataChangeReceiver = null;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
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
	protected void onDestroy() {
		
		if (downloadReceiver != null) {
			broadcast.unregisterReceiver(downloadReceiver);
		}
		
		if (dataChangeReceiver != null) {
			broadcast.unregisterReceiver(dataChangeReceiver);
		}
		
		super.onDestroy();
	}
}
