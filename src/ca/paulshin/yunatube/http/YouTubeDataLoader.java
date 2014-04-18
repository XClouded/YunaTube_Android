package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.text.TextUtils;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.common.Utils.Network;
import ca.paulshin.yunatube.youtube.Clip;

public class YouTubeDataLoader extends YunaTubeHttpClient {
	private static int numOfMessages;
	private static YouTubeDataLoader loader = null;

	private static final String CATEGORY_URL = "http://paulshin.ca/yunatube/res/youtube/get_category.php";
	private static final String SECTION_URL = "http://paulshin.ca/yunatube/res/youtube/get_section.php";
	private static final String CLIP_URL = "http://paulshin.ca/yunatube/res/youtube/get_clip.php";
	private static final String DETAIL_URL = "http://paulshin.ca/yunatube/res/youtube/get_detail.php";
	private static final String REPORT_URL = "http://paulshin.ca/yunatube/res/youtube/report_clip_blocked.php";
	private static final String RANDOM_URL = "http://paulshin.ca/yunatube/res/youtube/get_random_clip.php";
	private static final String SEARCH_URL = "http://paulshin.ca/yunatube/res/youtube/search.php";

	static {
		if (YunaTubeApplication.debuggable)
			numOfMessages = 20;
		else if (Utils.getConnectivity() == Network.MOBILE)
			numOfMessages = 50;
		else if (Utils.getConnectivity() == Network.WIFI)
			numOfMessages = 100;
		else
			numOfMessages = 20;

		loader = null;
	}

	public static YouTubeDataLoader getLoaderInstance() {
		if (loader == null)
			loader = new YouTubeDataLoader();
		return loader;
	}

	/*
	 * Load sections
	 */
	public synchronized List<String[]> loadSections(String cid) {
		String url = SECTION_URL;
		String order = (TextUtils.equals(cid, "1") || TextUtils.equals(cid, "2") || TextUtils.equals(cid, "3")) ? "desc" : "asc";
		String lo = Utils.getString(R.string.lo);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("cid", cid));
		params.add(new BasicNameValuePair("order", order));
		params.add(new BasicNameValuePair("lo", lo));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray sections = getJSONArray(url);

			List<String[]> sectionList = new ArrayList<String[]>();
			for (Object section : sections) {
				Object _sid = ((JSONObject) section).get("sid");
				String sid = (String) _sid;
				Object _title = ((JSONObject) section).get("title");
				String title = (String) _title;
				sectionList.add(new String[] { cid, sid, title });
			}

			return sectionList;

		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	/*
	 * Load clips
	 */
	public synchronized List<NameValuePair> loadClips(String cid, String sid) {
		String url = CLIP_URL;
		String order = (TextUtils.equals(cid, "2") || TextUtils.equals(cid, "4") || TextUtils.equals(cid, "6")) ? "desc" : "asc";
		String lo = Utils.getString(R.string.lo);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("cid", cid));
		params.add(new BasicNameValuePair("sid", sid));
		params.add(new BasicNameValuePair("order", order));
		params.add(new BasicNameValuePair("lo", lo));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray clips = getJSONArray(url);

			List<NameValuePair> clipList = new ArrayList<NameValuePair>();
			for (Object clip : clips) {
				Object _ytid = ((JSONObject) clip).get("ytid");
				String ytid = (String) _ytid;
				Object _title = ((JSONObject) clip).get("title");
				String title = (String) _title;
				clipList.add(new BasicNameValuePair(ytid, title));
			}

			return clipList;
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	/*
	 * Load details
	 */
	public synchronized Clip loadDetails(String ytid) {
		String url = DETAIL_URL;
		String lo = Utils.getString(R.string.lo);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("ytid", ytid));
		params.add(new BasicNameValuePair("lo", lo));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray details = getJSONArray(url);
			JSONObject detail = (JSONObject) details.get(0);

			Object _cid = ((JSONObject) detail).get("cid");
			String cid = (String) _cid;
			Object _ctitle = ((JSONObject) detail).get("ctitle");
			String ctitle = (String) _ctitle;
			Object _sid = ((JSONObject) detail).get("sid");
			String sid = (String) _sid;
			Object _stitle = ((JSONObject) detail).get("stitle");
			String stitle = (String) _stitle;
			Object _yid = ((JSONObject) detail).get("yid");
			String yid = (String) _yid;
			Object _ytitle = ((JSONObject) detail).get("ytitle");
			String ytitle = (String) _ytitle;

			return new Clip(cid, ctitle, sid, stitle, yid, ytitle, ytid);
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	/*
	 * Report blocked clip
	 */
	public synchronized boolean report(String ytid) {
		String url = REPORT_URL;

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("ytid", ytid));
		url = Utils.getParameterizedUrl(url, params);

		JSONObject response;
		try {
			response = getJSONObject(url);
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return false;
		}

		Object _result = ((JSONObject) response).get("result");
		String result = (String) _result;

		return TextUtils.equals(result, "success");
	}

	/*
	 * Get random clip
	 */
	public synchronized Clip getRandom() {
		String url = RANDOM_URL;
		String lo = Utils.getString(R.string.lo);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		;
		params.add(new BasicNameValuePair("lo", lo));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray details = getJSONArray(url);
			JSONObject detail = (JSONObject) details.get(0);

			Object _cid = ((JSONObject) detail).get("cid");
			String cid = (String) _cid;
			Object _ctitle = ((JSONObject) detail).get("ctitle");
			String ctitle = (String) _ctitle;
			Object _sid = ((JSONObject) detail).get("sid");
			String sid = (String) _sid;
			Object _stitle = ((JSONObject) detail).get("stitle");
			String stitle = (String) _stitle;
			Object _yid = ((JSONObject) detail).get("yid");
			String yid = (String) _yid;
			Object _ytitle = ((JSONObject) detail).get("ytitle");
			String ytitle = (String) _ytitle;
			Object _ytid = ((JSONObject) detail).get("ytid");
			String ytid = (String) _ytid;

			return new Clip(cid, ctitle, sid, stitle, yid, ytitle, ytid);
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	/*
	 * Load search
	 */
	public synchronized List<NameValuePair> search(String query) {
		String url = SEARCH_URL;
		String lo = Utils.getString(R.string.lo);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("q", query));
		params.add(new BasicNameValuePair("lo", lo));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray clips = getJSONArray(url);

			List<NameValuePair> clipList = new ArrayList<NameValuePair>();
			for (Object clip : clips) {
				Object _ytid = ((JSONObject) clip).get("ytid");
				String ytid = (String) _ytid;
				Object _title = ((JSONObject) clip).get("ytitle");
				String title = (String) _title;
				clipList.add(new BasicNameValuePair(ytid, title));
			}

			return clipList;
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	/*
	 * Handle comments
	 */
	public synchronized List<String[]> loadComments(List<String[]> commentsList, String ytId, int lastIndex) {
		String url = Constants.YOUTUBE_COMMENT_RETRIEVE;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("ytid", ytId));
		params.add(new BasicNameValuePair("numOfMessages", String.valueOf(numOfMessages)));
		params.add(new BasicNameValuePair("lastIndex", String.valueOf(lastIndex)));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray messages = getJSONArray(url);

			for (Object message : messages) {
				Object _report = ((JSONObject) message).get("report");
				String report = (String) _report;
				if (Integer.parseInt(report) > Constants.MESSAGE_REPORT_FILTER) {
					continue;
				}
				Object _id = ((JSONObject) message).get("id");
				String id = (String) _id;
				Object _username = ((JSONObject) message).get("username");
				String username = (String) _username;
				Object _message = ((JSONObject) message).get("message");
				String msg = (String) _message;
				Object _time = ((JSONObject) message).get("time");
				String time = (String) _time;
				Object _device = ((JSONObject) message).get("device");
				String device = (String) _device;
				Object _isfirst = ((JSONObject) message).get("isfirst");
				String isfirst = (String) _isfirst;
				commentsList.add(new String[] { id, username, msg, time, device, report, isfirst });
			}

			return commentsList;

		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	public synchronized List<String[]> loadMessages(List<String[]> messagesList, int lastIndex) {
		String url = Constants.MESSAGE_RETRIEVE;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("numOfMessages", String.valueOf(numOfMessages)));
		params.add(new BasicNameValuePair("lastIndex", String.valueOf(lastIndex)));
		url = Utils.getParameterizedUrl(url, params);

		try {
			JSONArray messages = getJSONArray(url);

			for (Object message : messages) {
				Object _report = ((JSONObject) message).get("report");
				String report = (String) _report;
				if (Integer.parseInt(report) > Constants.MESSAGE_REPORT_FILTER) {
					continue;
				}
				Object _id = ((JSONObject) message).get("id");
				String id = (String) _id;
				Object _username = ((JSONObject) message).get("username");
				String username = (String) _username;
				Object _message = ((JSONObject) message).get("message");
				String msg = (String) _message;
				Object _time = ((JSONObject) message).get("time");
				String time = (String) _time;
				Object _device = ((JSONObject) message).get("device");
				String device = (String) _device;
				messagesList.add(new String[] { id, username, msg, time, device, report });
			}

			return messagesList;

		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}

	public synchronized Boolean sendMessage(String url) {
		return post(url);
	}
}
