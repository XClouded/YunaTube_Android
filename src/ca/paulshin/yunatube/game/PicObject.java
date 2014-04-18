package ca.paulshin.yunatube.game;

public class PicObject {
	public String url;
	public int indexOne, indexTwo;
	public boolean isDone;
	
	public PicObject(String url, int indexOne, int indexTwo) {
		this.url = url;
		this.indexOne = indexOne;
		this.indexTwo = indexTwo;
	}
}
