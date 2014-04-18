package ca.paulshin.yunatube.links;

import android.graphics.Bitmap;

public class Website {
	private String title, url, subtitle;
	private Bitmap thumbnail;

	public Website(Bitmap thumbnail, String title, String url, String desc) {
		this.thumbnail = thumbnail;
		this.title = title;
		this.url = url;
		this.subtitle = desc;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
}
