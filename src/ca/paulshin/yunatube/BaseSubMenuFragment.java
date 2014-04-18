package ca.paulshin.yunatube;

import android.view.View;
import android.widget.RelativeLayout;

public class BaseSubMenuFragment extends BaseFragment {
	private RelativeLayout selectedMenu;
	private boolean isBottom;

	protected void selectMenu(View v, boolean isBottom) {
		restoreSelectedMenuBackground();
		selectedMenu = (RelativeLayout) v;
		this.isBottom = isBottom;
		if (isBottom)
			selectedMenu.setBackgroundResource(R.drawable.submenu_menu_bottom_down);
		else
			selectedMenu.setBackgroundResource(R.drawable.submenu_menu_down);
	}

	protected void restoreSelectedMenuBackground() {
		if (selectedMenu != null) {
			if (isBottom)
				selectedMenu.setBackgroundResource(R.drawable.submenu_menu_bottom_selector);
			else
				selectedMenu.setBackgroundResource(R.drawable.submenu_menu_selector);
		}
	}
}
