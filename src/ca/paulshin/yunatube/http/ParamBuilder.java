package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ParamBuilder {

	private final List<NameValuePair> nameValuePairs;
	
	public ParamBuilder() {
		nameValuePairs = new ArrayList<NameValuePair>();
	}
	
	public ParamBuilder add(String key, String value) {
		nameValuePairs.add(new BasicNameValuePair(key, value));
		return this;
	}
	
	public ParamBuilder add(String key, int value) {
		nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
		return this;
	}
	
	public ParamBuilder add(String key, long value) {
		nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
		return this;
	}
	
	public ParamBuilder add(String key, double value) {
		nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
		return this;
	}
	
	public ParamBuilder add(String key, boolean value) {
		nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
		return this;
	}
	
	public List<NameValuePair> create() {
		return nameValuePairs;
	}
}