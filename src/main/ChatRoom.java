package main;

import java.io.Serializable;
import java.util.HashSet;

import net.tomp2p.peers.PeerAddress;

public class ChatRoom implements Serializable{
	public HashSet<PeerAddress> hs;
	public String roomName;
	
	public ChatRoom(HashSet<PeerAddress> tmp, String rm) {
		hs = tmp;
		roomName = rm;
	}
	
	public HashSet<PeerAddress> getHash(){
		return hs;
	}
	
	public String getName() {
		return roomName;
	}
}
