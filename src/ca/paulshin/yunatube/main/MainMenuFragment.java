package ca.paulshin.yunatube.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ca.paulshin.yunatube.BaseSubMenuFragment;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.aboutyuna.SubMenuAboutYunaFragment;
import ca.paulshin.yunatube.chat.ChatActivity;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.common.WebViewFragment;
import ca.paulshin.yunatube.game.GameFragment;
import ca.paulshin.yunatube.http.FlickrDataLoader;
import ca.paulshin.yunatube.image.album.SubMenuAlbumFragment;
import ca.paulshin.yunatube.image.gifs.SubMenuGifs;
import ca.paulshin.yunatube.links.SubMenuLinksFragment;
import ca.paulshin.yunatube.message.MessageFragment;
import ca.paulshin.yunatube.quiz.QuizFragment;
import ca.paulshin.yunatube.youtube.SubMenuYoutubeFragment;
import ca.paulshin.yunatube.yunaonweb.YunaOnWebFragment;

public class MainMenuFragment extends BaseSubMenuFragment implements OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, null);

		RelativeLayout home = (RelativeLayout) view.findViewById(R.id.supermenu_home);
		home.setOnClickListener(this);

		RelativeLayout aboutYuNa = (RelativeLayout) view.findViewById(R.id.supermenu_about_yuna);
		aboutYuNa.setOnClickListener(this);

		RelativeLayout youtube = (RelativeLayout) view.findViewById(R.id.supermenu_youtube);
		youtube.setOnClickListener(this);

		RelativeLayout album = (RelativeLayout) view.findViewById(R.id.supermenu_album);
		album.setOnClickListener(this);

		RelativeLayout gifs = (RelativeLayout) view.findViewById(R.id.supermenu_gifs);
		gifs.setOnClickListener(this);

		RelativeLayout search = (RelativeLayout) view.findViewById(R.id.supermenu_search);
		search.setOnClickListener(this);

		RelativeLayout links = (RelativeLayout) view.findViewById(R.id.supermenu_links);
		links.setOnClickListener(this);

		RelativeLayout toYuna = (RelativeLayout) view.findViewById(R.id.supermenu_to_yuna);
		toYuna.setOnClickListener(this);

		RelativeLayout survey = (RelativeLayout) view.findViewById(R.id.supermenu_survey);
		survey.setOnClickListener(this);

		RelativeLayout quiz = (RelativeLayout) view.findViewById(R.id.supermenu_quiz);
		quiz.setOnClickListener(this);

		RelativeLayout game = (RelativeLayout) view.findViewById(R.id.supermenu_game);
		game.setOnClickListener(this);

		RelativeLayout chat = (RelativeLayout) view.findViewById(R.id.supermenu_chat);
		if (chat != null) {
			chat.setOnClickListener(this);
		}

		return view;
	}

	private void switchMenuFragment(Fragment newFragment) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
		ft.replace(R.id.menu_frame, newFragment, "detailFragment");
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity) getActivity();

		switch (v.getId()) {
		case R.id.supermenu_home:
			activity.setFragment(new MainContentFragment(), false);
			break;

		case R.id.supermenu_about_yuna:
			switchMenuFragment(new SubMenuAboutYunaFragment());
			break;

		case R.id.supermenu_to_yuna:
			if (!Utils.isNetworkAvailable()) {
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			} else {
				activity.setFragment(new MessageFragment(), false);
			}
			break;

		case R.id.supermenu_youtube:
			switchMenuFragment(new SubMenuYoutubeFragment());
			break;

		case R.id.supermenu_album:
			if (!Utils.isNetworkAvailable())
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			else {
				YunaTubeApplication application = (YunaTubeApplication) Utils.ctx;
				if (application.collectionList == null) {
					if (Utils.isNetworkAvailable()) {
						new AsyncTask<String, Void, Integer>() {
							@Override
							protected Integer doInBackground(String... params) {
								try {
									// Load Flickr
									YunaTubeApplication application = (YunaTubeApplication) Utils.ctx;
									application.collectionList = FlickrDataLoader.getInstance().getCollectionList();
									return application.collectionList.size();
								} catch (Exception e) {
									return 0;
								}
							}

							@Override
							protected void onPostExecute(Integer result) {
								if (result > 0)
									switchMenuFragment(new SubMenuAlbumFragment());
							};
						}.execute();
					} else
						Utils.showToast(getActivity(), R.string.message_network_unavailable);
				} else
					switchMenuFragment(new SubMenuAlbumFragment());
			}
			break;

		case R.id.supermenu_gifs:
			switchMenuFragment(new SubMenuGifs());
			break;

		case R.id.supermenu_search:
			activity.setFragment(new YunaOnWebFragment(), false);
			break;

		case R.id.supermenu_links:
			switchMenuFragment(new SubMenuLinksFragment());
			break;

		case R.id.supermenu_survey:
			String deviceId = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
			activity.setFragment(WebViewFragment.getInstance(R.string.supermenu_survey, Constants.SURVEY_URL + deviceId), false);
			break;

		case R.id.supermenu_quiz:
			activity.setFragment(new QuizFragment(), false);
			break;

		case R.id.supermenu_chat:
			if (!Utils.isNetworkAvailable()) {
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			} else {
				String userName = Preference.getString(Constants.NICKNAME);
				if (TextUtils.isEmpty(userName) || userName.startsWith(getString(R.string.nickname_noname))) {
					Utils.showToast(activity, R.string.nickname_must);
					// Random random = new Random();
					// String randomText = Long.toString(Math.abs(random.nextLong()), 36);
					// String tempNickname = getString(R.string.nickname_noname) + "@" + randomText;
					// tempNickname = tempNickname.substring(0, 9);
					// Preference.put(MainActivity.NICKNAME, tempNickname);
				} else {
					startActivity(new Intent(getActivity(), ChatActivity.class));
				}
			}
			break;

		case R.id.supermenu_game:
			if (!Utils.isNetworkAvailable()) {
				Utils.showToast(getActivity(), R.string.message_network_unavailable);
			} else {
				String userName = Preference.getString(Constants.NICKNAME);
				if (TextUtils.isEmpty(userName) || userName.startsWith(getString(R.string.nickname_noname))) {
					Utils.showToast(activity, R.string.nickname_must);
				} else {
					activity.setFragment(new GameFragment(), false);
				}
			}
			break;
		}
	}
}
