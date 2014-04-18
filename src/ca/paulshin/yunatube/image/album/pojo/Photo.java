package ca.paulshin.yunatube.image.album.pojo;

import ca.paulshin.yunatube.common.Constants;

public class Photo {
	private String id, secret, server, farm, title, isPrimary;
	
	public Photo(String id, String secret, String server, String farm,
			String title, String isPrimary) {
		this.id = id;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
		this.title = title;
		this.isPrimary = isPrimary;
		
		/**
		 * 	http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
		 *	http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
		 *	http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{o-secret}_o.(jpg|gif|png)
		 *
		 *	s	small square 75x75
		 *	q	large square 150x150
		 *	t	thumbnail, 100 on longest side
		 *	m	small, 240 on longest side
		 *	n	small, 320 on longest side
		 *	-	medium, 500 on longest side
		 *	z	medium 640, 640 on longest side
		 *	c	medium 800, 800 on longest side 
		 *	b	large, 1024 on longest side*
		 *	o	original image, either a jpg, gif or png, depending on source format
		 */
				
	}

	public String getUrl(String size) {
		return String.format(Constants.ALBUM_PHOTO_URL, farm, server, id, secret, size);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getFarm() {
		return farm;
	}

	public void setFarm(String farm) {
		this.farm = farm;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}
}