package ca.paulshin.yunatube.image.album.pojo;

import java.util.List;

public class CollectionSet {
	private String id, title, description, iconlarge, iconsmall;
	private List<CollectionSet> sets;
	
	public CollectionSet(String id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.iconlarge = null;
		this.iconsmall = null;
		this.sets = null;
	}
	
	public CollectionSet(String id, String title, String description,
			String iconlarge, String iconsmall, List<CollectionSet> sets) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.iconlarge = iconlarge;
		this.iconsmall = iconsmall;
		this.sets = sets;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconlarge() {
		return iconlarge;
	}

	public void setIconlarge(String iconlarge) {
		this.iconlarge = iconlarge;
	}

	public String getIconsmall() {
		return iconsmall;
	}

	public void setIconsmall(String iconsmall) {
		this.iconsmall = iconsmall;
	}

	public List<CollectionSet> getSets() {
		return sets;
	}

	public void setLowLevelIds(List<CollectionSet> sets) {
		this.sets = sets;
	}
}