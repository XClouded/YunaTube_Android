package ca.paulshin.yunatube.aboutyuna;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeBaseFragment;

public class ProfileFragment extends YunaTubeBaseFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, null);

		TextView birthday = (TextView) view.findViewById(R.id.birthday);
		birthday.setText(Html.fromHtml(getString(R.string.profile_born)));

		TextView nationality = (TextView) view.findViewById(R.id.nationality);
		nationality.setText(Html.fromHtml(getString(R.string.profile_nationality)));

		TextView occupation = (TextView) view.findViewById(R.id.occupation);
		occupation.setText(Html.fromHtml(getString(R.string.profile_occupation)));

		TextView height = (TextView) view.findViewById(R.id.height);
		height.setText(Html.fromHtml(getString(R.string.profile_height)));

		TextView trainingBase = (TextView) view.findViewById(R.id.trainingBase);
		trainingBase.setText(Html.fromHtml(getString(R.string.profile_base)));

		TextView agent = (TextView) view.findViewById(R.id.agent);
		agent.setText(Html.fromHtml(getString(R.string.profile_agent)));

		TextView family = (TextView) view.findViewById(R.id.family);
		family.setText(Html.fromHtml(getString(R.string.profile_family)));

		TextView titles = (TextView) view.findViewById(R.id.titles);
		titles.setText(Html.fromHtml(getString(R.string.profile_titles)));

		TextView records = (TextView) view.findViewById(R.id.records);
		records.setText(Html.fromHtml(getString(R.string.profile_records)));

		return view;
	}

	@Override
	public int getTitle() {
		return R.string.submenu_about_yuna_profile;
	}

	@Override
	protected boolean getNetworkCheck() {
		return false;
	}

	@Override
	protected String getTrackerId() {
		return "profile - android";
	}
}