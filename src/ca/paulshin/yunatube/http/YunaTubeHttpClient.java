package ca.paulshin.yunatube.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;

import android.text.TextUtils;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Utils;

public class YunaTubeHttpClient {
	private static final boolean SHOW_REPORT = true;

	protected synchronized JSONObject getJSONObject(final String url) {
		return (JSONObject) (JSONValue.parse(getJSON(url)));
	}

	protected synchronized JSONArray getJSONArray(final String url) {
		return (JSONArray) (JSONValue.parse(getJSON(url)));
	}
	
	protected synchronized boolean post(String url) {
		return TextUtils.equals(getJSON(url), "success");
	}

	private String getJSON(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		String jsonString = null;

		try {
			response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line != null && !line.equals(""))
					sb.append(line);
			}

			jsonString = sb.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (SHOW_REPORT)
			Utils.debug("Loaded from " + url + ": " + jsonString);

		return jsonString;
	}
	
	protected Document getXMLDocument(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response;

		try {
			response = client.execute(request);
			InputStream in = response.getEntity().getContent();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);
			
			in.close();
			return doc;
		} catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
		}

		return null;
	}
}
