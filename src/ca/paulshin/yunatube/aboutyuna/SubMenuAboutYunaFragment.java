package ca.paulshin.yunatube.aboutyuna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ca.paulshin.yunatube.BaseSubMenuFragment;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.common.WebViewFragment;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.main.MainMenuFragment;

public class SubMenuAboutYunaFragment extends BaseSubMenuFragment implements OnClickListener {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sub_menu_about_yuna, null);
		
		RelativeLayout back = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_back);
		back.setOnClickListener(this);
		
		RelativeLayout profile = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_profile);
		profile.setOnClickListener(this);
		
		RelativeLayout yunakimCom = (RelativeLayout) view.findViewById(R.id.submenu_about_yuna_yunakimcom);
		yunakimCom.setOnClickListener(this);
		
		RelativeLayout questions = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_questions);
		questions.setOnClickListener(this);
		
		RelativeLayout wikipedia = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_wikipedia);
		wikipedia.setOnClickListener(this);
		
		RelativeLayout programs = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_programs);
		programs.setOnClickListener(this);
		
		RelativeLayout records = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_isu_records);
		records.setOnClickListener(this);
		
		RelativeLayout results = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_results);
		results.setOnClickListener(this);
		
		RelativeLayout awards = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_awards);
		awards.setOnClickListener(this);
		
		RelativeLayout charities = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_charities);
		charities.setOnClickListener(this);
		
		RelativeLayout praises = (RelativeLayout)view.findViewById(R.id.submenu_about_yuna_praises);
		praises.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity)getActivity();
		String language = getString(R.string.language);
		
		switch (v.getId()) {
		case R.id.submenu_about_yuna_back:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.menu_frame, new MainMenuFragment(), null);
			ft.commit();
			break;
			
		case R.id.submenu_about_yuna_profile:
			activity.setFragment(new ProfileFragment(), false);
			selectMenu(v, false);
			break;
		
		case R.id.submenu_about_yuna_yunakimcom:
			if (!Utils.isNetworkAvailable()) 
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			else {
				Uri uri = Uri.parse(Constants.YUNAKIM_COM);
				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
			break;
			
		case R.id.submenu_about_yuna_questions:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_questions, String.format(Constants.AB_YN_20Q20A, language)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_wikipedia:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_wikipedia, getString(R.string.wiki_url)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_programs:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_programs, String.format(Constants.AB_YN_PROGRAMS, language)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_isu_records:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_isu_records, Constants.YUNA_ISU_RESULTS), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_results:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_results, String.format(Constants.AB_YN_COMPETITIONS, language)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_awards:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_awards, String.format(Constants.AB_YN_AWARDS, language)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_charities:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_charities, String.format(Constants.AB_YN_CHARITIES, language)), false);
			selectMenu(v, false);
			break;
			
		case R.id.submenu_about_yuna_praises:
			activity.setFragment(WebViewFragment.getInstance(R.string.submenu_about_yuna_praises, String.format(Constants.AB_YN_PRAISES, language)), false);
			selectMenu(v, true);
			break;
		}
	}
}