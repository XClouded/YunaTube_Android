package ca.paulshin.yunatube.common;

import android.app.Activity;
import android.app.Dialog;
import ca.paulshin.yunatube.R;

public class CustomDialog extends Dialog {

	public CustomDialog(final Activity activity) {
		super(activity, R.style.Theme_YunaTubeDialog);
		setContentView(R.layout.dialog_loading);
		setCancelable(true);
	}
}
