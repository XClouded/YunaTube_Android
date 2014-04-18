package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;

public class MainDataLoader extends YunaTubeHttpClient {

	private static MainDataLoader loader = null;

	public static MainDataLoader getLoaderInstance() {
		if (loader == null)
			loader = new MainDataLoader();
		return loader;
	}

	public synchronized String[] loadMainFile() {
		Random random = new Random();
		final String url = String.format(Constants.MAIN_URL, Utils.getString(R.string.language)) + "?rand=" + Long.toString(Math.abs(random.nextLong()), 36);

		JSONObject mainObj;
		try {
			mainObj = getJSONObject(url);
			if (mainObj == null)
				return null;
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}

		String notice = (String) (mainObj.get("notice-android"));
		String fact = (String) (mainObj.get("fact"));
		String todayPhoto = (String) (mainObj.get("today_photo"));

		return new String[] { notice, fact, todayPhoto };
	}

	public synchronized List<NameValuePair> loadNewClips() {
		String url = Constants.NEW_CLIPS;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("lo", Utils.getString(R.string.lo)));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray messages = getJSONArray(url);
			if (messages == null)
				return null;

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Object message : messages) {
				Object _ytid = ((JSONObject) message).get("ytid");
				String ytid = (String) _ytid;
				Object _title = ((JSONObject) message).get("title");
				String title = (String) _title;
				list.add(new BasicNameValuePair(ytid, title));
			}

			return list;

		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}
}
