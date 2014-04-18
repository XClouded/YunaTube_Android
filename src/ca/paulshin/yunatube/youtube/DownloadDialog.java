package ca.paulshin.yunatube.youtube;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import ca.paulshin.yunatube.R;

public class DownloadDialog extends Dialog implements OnClickListener {
	public DownloadDialog(final Activity activity) {
		super(activity, R.style.Theme_YunaTubeDialog);
		this.setContentView(R.layout.dialog_download);
		this.setCancelable(true);
		findViewById(R.id.random_close).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.random_close:
			dismiss();
			break;
		}
	}
}