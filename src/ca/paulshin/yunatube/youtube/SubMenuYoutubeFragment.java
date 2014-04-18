package ca.paulshin.yunatube.youtube;

import android.app.Dialog;
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

public class SubMenuYoutubeFragment extends BaseSubMenuFragment implements OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_menu_youtube, null);

		RelativeLayout back = (RelativeLayout) view.findViewById(R.id.submenu_youtube_back);
		back.setOnClickListener(this);

		RelativeLayout myclips = (RelativeLayout) view.findViewById(R.id.submenu_youtube_myclips);
		myclips.setOnClickListener(this);

		RelativeLayout random = (RelativeLayout) view.findViewById(R.id.submenu_youtube_random);
		random.setOnClickListener(this);

		RelativeLayout greetings = (RelativeLayout) view.findViewById(R.id.submenu_youtube_greetings);
		greetings.setOnClickListener(this);

		RelativeLayout competitions = (RelativeLayout) view.findViewById(R.id.submenu_youtube_competitions);
		competitions.setOnClickListener(this);

		RelativeLayout iceshows = (RelativeLayout) view.findViewById(R.id.submenu_youtube_iceshows);
		iceshows.setOnClickListener(this);

		RelativeLayout offtheice = (RelativeLayout) view.findViewById(R.id.submenu_youtube_offtheice);
		offtheice.setOnClickListener(this);

		RelativeLayout knc = (RelativeLayout) view.findViewById(R.id.submenu_youtube_knc);
		knc.setOnClickListener(this);

		RelativeLayout jukebox = (RelativeLayout) view.findViewById(R.id.submenu_youtube_jukebox);
		jukebox.setOnClickListener(this);

		RelativeLayout muhan = (RelativeLayout) view.findViewById(R.id.submenu_youtube_muhan);
		muhan.setOnClickListener(this);

		RelativeLayout guru = (RelativeLayout) view.findViewById(R.id.submenu_youtube_guru);
		guru.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity) getActivity();

		switch (v.getId()) {
		case R.id.submenu_youtube_back:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.menu_frame, new MainMenuFragment(), null);
			ft.commit();
			break;

		case R.id.submenu_youtube_myclips:
			activity.setFragment(new MyFavesFragment(), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_random:
			Dialog randomDialog = new RandomDialog(getActivity());
			randomDialog.show();
			break;

		case R.id.submenu_youtube_greetings:
			activity.setFragment(ClipListFragment.getInstance("1", "1", getString(R.string.submenu_youtube_greetings)), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_competitions:
			activity.setFragment(SectionListFragment.getInstance("2", R.string.submenu_youtube_competitions), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_iceshows:
			activity.setFragment(SectionListFragment.getInstance("3", R.string.submenu_youtube_iceshows), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_offtheice:
			activity.setFragment(SectionListFragment.getInstance("4", R.string.submenu_youtube_offtheice), false);
			selectMenu(v, false);
			return;

		case R.id.submenu_youtube_knc:
			activity.setFragment(SectionListFragment.getInstance("5", R.string.submenu_youtube_knc), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_jukebox:
			activity.setFragment(ClipListFragment.getInstance("6", "1", getString(R.string.submenu_youtube_jukebox)), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_muhan:
			activity.setFragment(SectionListFragment.getInstance("7", R.string.submenu_youtube_muhan), false);
			selectMenu(v, false);
			break;

		case R.id.submenu_youtube_guru:
			activity.setFragment(SectionListFragment.getInstance("8", R.string.submenu_youtube_guru), true);
			selectMenu(v, true);
			break;
		}
	}
}
