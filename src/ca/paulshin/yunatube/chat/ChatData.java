package ca.paulshin.yunatube.chat;

public class ChatData {
	public byte type;
	public String writer;
	public String message;
	public String iconType;
	public String date;

	public ChatData(byte type, String writer, String message, String iconType, String date) {
		this.type = type;
		this.writer = writer;
		this.message = message;
		this.iconType = iconType;
		this.date = date;
	}
}