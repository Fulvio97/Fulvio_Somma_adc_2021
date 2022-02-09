package main;

public class MessageListener {
	int pId;

	public MessageListener(int id) {
		pId = id;
	}

	public Object parseMessage(Object o, int id) {
		Message mess = (Message) o;
		System.out.println(id + ") Peer [" + mess.getId() + "] said in room [" + mess.getRm() + "]: " + mess.getMsg());

		return 0;
	}
}
