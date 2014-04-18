package ca.paulshin.yunatube.http;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.image.album.pojo.CollectionSet;
import ca.paulshin.yunatube.image.album.pojo.Photo;

public class FlickrDataLoader extends YunaTubeHttpClient {
	private static FlickrDataLoader loader = null;

	public List<Photo> photoList = new ArrayList<Photo>();
	public String[] photoUrls;
	public List<CollectionSet> collectionList = new ArrayList<CollectionSet>();
	private JSONObject photoJSON;

	protected FlickrDataLoader() {
	}

	public static FlickrDataLoader getInstance() {
		if (loader == null)
			loader = new FlickrDataLoader();
		return loader;
	}

	public synchronized List<CollectionSet> getCollectionList() {
		if (collectionList != null && collectionList.size() > 0)
			return collectionList;

		if (photoJSON == null) {
			try {
				photoJSON = getJSONObject(Constants.ALBUM_FLICKR_COLLECTIONS_GETTREE);
			}
			catch (Exception e) {
				if (YunaTubeApplication.debuggable)
					e.printStackTrace();
				return null;
			}
		}

		JSONObject collections = (JSONObject) (photoJSON.get("collections"));
		JSONArray collection = (JSONArray) (collections.get("collection"));
		List<CollectionSet> sets;

		for (Object obj : collection) {
			Object idObj = ((JSONObject) obj).get("id");
			String id = (String) idObj;
			Object titleObj = ((JSONObject) obj).get("title");
			String title = (String) titleObj;
			title = title.replace("Olympic", "Olympics");
			Object descObj = ((JSONObject) obj).get("description");
			String desc = (String) descObj;
			Object iconLargeObj = ((JSONObject) obj).get("iconlarge");
			String iconLarge = (String) iconLargeObj;
			Object iconSmallObj = ((JSONObject) obj).get("iconsmall");
			String iconSmall = (String) iconSmallObj;

			JSONArray photoSet = (JSONArray) (((JSONObject) obj).get("set"));

			sets = new ArrayList<CollectionSet>();
			for (Object obj2 : photoSet) {
				Object setIdObj = ((JSONObject) obj2).get("id");
				String setId = (String) setIdObj;
				Object setTitleObj = ((JSONObject) obj2).get("title");
				String setTitle = (String) setTitleObj;
				setTitle = setTitle.replace("Olympic", "Olympics");
				Object setDescObj = ((JSONObject) obj2).get("description");
				String setDesc = (String) setDescObj;
				sets.add(new CollectionSet(setId, setTitle, setDesc));
			}

			CollectionSet newCollection = new CollectionSet(id, title, desc, iconLarge, iconSmall, sets);
			collectionList.add(newCollection);
		}
		return collectionList;
	}

	public synchronized List<CollectionSet> getSetList(String collectionId) {
		if (photoJSON == null) {
			try {
				photoJSON = getJSONObject(Constants.ALBUM_FLICKR_COLLECTIONS_GETTREE);
			}
			catch (Exception e) {
				if (YunaTubeApplication.debuggable)
					e.printStackTrace();
				return null;
			}
		}

		for (CollectionSet cs : collectionList) {
			if (collectionId.equals(cs.getId())) {
				return cs.getSets();
			}
		}
		return null;
	}

	public synchronized List<Photo> getPhotoList(String setId) {
		photoList.clear();
		List<String> urls = new ArrayList<String>();

		if (setId == null)
			setId = "72157625022971026";

		String url = String.format(Constants.ALBUM_FLICKR_PHOTOSETS_GETPHOTO, setId);
		
		JSONObject photos;
		try {
			photos = getJSONObject(url);
		}
		catch (Exception e) {
			if (YunaTubeApplication.debuggable)
				e.printStackTrace();
			return null;
		}
		
		JSONObject photoset = (JSONObject) photos.get("photoset");
		String photosetId = (String) photoset.get("id");
		String photosetPrimary = (String) photoset.get("primary");
		String photosetOwner = (String) photoset.get("owner");
		String photosetOwnername = (String) photoset.get("ownername");
		JSONArray photosetPhoto = (JSONArray) (photoset.get("photo"));

		for (Object obj : photosetPhoto) {
			Object photoIdObj = ((JSONObject) obj).get("id");
			String photoId = (String) photoIdObj;
			Object photoSecretObj = ((JSONObject) obj).get("secret");
			String photoSecret = (String) photoSecretObj;
			Object photoServerObj = ((JSONObject) obj).get("server");
			String photoServer = (String) photoServerObj;
			Object photoFarmObj = ((JSONObject) obj).get("farm");
			String photoFarm = "" + (Long) photoFarmObj;
			Object photoTitleObj = ((JSONObject) obj).get("title");
			String photoTitle = (String) photoTitleObj;
			Object photoIsPrimaryObj = ((JSONObject) obj).get("isprimary");
			String photoIsPrimary = (String) photoIsPrimaryObj;
			Photo newPhoto = new Photo(photoId, photoSecret, photoServer, photoFarm, photoTitle, photoIsPrimary);
			urls.add(newPhoto.getUrl("b"));
			photoList.add(newPhoto);
		}

		photoUrls = urls.toArray(new String[urls.size()]);
		return photoList;
	}
}