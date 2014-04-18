package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Utils;

public class ChatDataLoader extends YunaTubeHttpClient {
	private static ChatDataLoader loader = null;
	
	public static ChatDataLoader getLoaderInstance() {
		if (loader == null)
			loader = new ChatDataLoader();
		return loader;
	}
	
	public synchronized List<String> registerUser(String deviceId, String userName, String regId) {
		String url = Constants.CHAT_USERS;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "insert"));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("regId", regId));
		url = Utils.getParameterizedUrl(url, params);
		
		List<String> userList;
		Utils.debug("Insert url:" + url);

		try {
			JSONArray users = getJSONArray(url);
		
			userList = new ArrayList<String>();
			for (Object user : users) {
				Object userObj = ((JSONObject) user).get("username");
				String username = (String) userObj;
				userList.add(username);
			}
	
			return userList;
		}
		catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
	}
	
	public synchronized boolean unregisterUser(String deviceId, String userName, String regId) {
		String url = Constants.CHAT_USERS;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "remove"));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("regId", regId));
		url = Utils.getParameterizedUrl(url, params);
		
		try {
			getJSONObject(url);
			return true;
		}
		catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return false;
		}
	}
	
	public synchronized boolean sendMessage(String deviceId, String userName, String regId, String iconId, String text, String time) {
		String url = Constants.CHAT_DATA;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("regId", regId));
		params.add(new BasicNameValuePair("iconId", iconId));
		params.add(new BasicNameValuePair("text", text));
		params.add(new BasicNameValuePair("time", time));
		url = Utils.getParameterizedUrl(url, params);
		
		try {
			JSONObject resultObj = getJSONObject(url);
			boolean result = (Boolean) (resultObj.get("success"));
			return result;
		}
		catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return false;
		}
	}
	
	public synchronized boolean nickChange(String deviceId, String regId, String from, String to) {
		String url = Constants.CHAT_USERS;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "nickname"));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("regId", regId));
		params.add(new BasicNameValuePair("username", from));
		params.add(new BasicNameValuePair("to", to));
		url = Utils.getParameterizedUrl(url, params);
		Utils.debug(url);
		
		try {
			JSONObject resultObj = getJSONObject(url);
			boolean result = (Boolean) (resultObj.get("success"));
			return result;
		}
		catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return false;
		}
	}
}