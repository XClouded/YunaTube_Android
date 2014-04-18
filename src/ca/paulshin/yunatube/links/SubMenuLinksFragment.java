package ca.paulshin.yunatube.links;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ca.paulshin.yunatube.BaseSubMenuFragment;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.main.MainMenuFragment;

public class SubMenuLinksFragment extends BaseSubMenuFragment implements OnClickListener {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_menu_links, null);

		RelativeLayout back = (RelativeLayout)view.findViewById(R.id.submenu_links_back);
		back.setOnClickListener(this);
		
		final RelativeLayout official = (RelativeLayout)view.findViewById(R.id.submenu_links_official);
		official.setOnClickListener(this);
		
		final RelativeLayout fan = (RelativeLayout)view.findViewById(R.id.submenu_links_fan);
		fan.setOnClickListener(this);
		
		RelativeLayout figureskating = (RelativeLayout)view.findViewById(R.id.submenu_links_figureskating);
		figureskating.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity)getActivity();
		
		switch (v.getId()) {
		case R.id.submenu_links_back:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.menu_frame, new MainMenuFragment(), null);
			ft.commit();
			break;
			
		case R.id.submenu_links_official:
			activity.setFragment(new OfficialPagesFragment(), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_links_fan:
			activity.setFragment(new FanPagesFragment(), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_links_figureskating:
			activity.setFragment(new FigureSkatingSitesFragment(), false);
			selectMenu(v, true);
			break;
		}
	}
}
