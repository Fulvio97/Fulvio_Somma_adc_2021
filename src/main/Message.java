package main;
import java.io.Serializable;

public class Message implements Serializable {
	private String rm, msg;
	private int senderId;

	public Message(String room, String message, int id) {
		rm = room;
		msg = message;
		senderId = id;
	}

	public String getRm() {
		return rm;
	}

	public String getMsg() {
		return msg;
	}

	public int getId() {
		return senderId;
	}
}
