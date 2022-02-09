package main;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.replication.SlowReplicationFilter;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

public class AnonymousUser {
	final private Peer peer;
	final private PeerDHT dht;
	final private int DEFAULT_MASTER_PORT = 4000;
	final private int peerId;

	private ArrayList<String> rooms = new ArrayList<String>();
	private IndirectReplication replication;

	public AnonymousUser(int id, String master_peer, final MessageListener listener) throws Exception {
		peerId = id;
		Bindings bindings = new Bindings().listenAny();
		peer = new PeerBuilder(Number160.createHash(id)).bindings(bindings).ports(DEFAULT_MASTER_PORT + id).start();
		dht = new PeerBuilderDHT(peer).start();

		FutureBootstrap fb = this.dht.peer().bootstrap().inetAddress(InetAddress.getByName(master_peer)).ports(4001).start();
		fb.awaitUninterruptibly();

		if (fb.isSuccess() && peerId != 1) {
			dht.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
		}
		
		replication = new IndirectReplication(dht).start();
		replication.replicationFactor(1);
		replication.intervalMillis(10000);
		replication.nRoot();
		replication.addReplicationFilter(new SlowReplicationFilter());
		replication.keepData(true);
		replication.start();

		dht.peer().objectDataReply(new ObjectDataReply() {
			@Override
			public Object reply(PeerAddress sender, Object request) throws Exception {

				return listener.parseMessage(request, peerId);
			}
		});
	}

	public boolean createRoom(String room_name) {
		if (room_name.isEmpty()) {
			System.out.println("Room name not valid");

			return false;
		}

		try {
			FutureGet futureGet = dht.get(Number160.createHash(room_name)).start();
			futureGet.awaitUninterruptibly();

			if (futureGet.isSuccess() && futureGet.isEmpty()) {
				dht.put(Number160.createHash(room_name)).data(new Data(new ChatRoom(new HashSet<PeerAddress>(), room_name))).start().awaitUninterruptibly();
				
				return true;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean leaveRoom(String room_name) {
		if (room_name.isEmpty()) {
			System.out.println("Room name not valid");

			return false;
		}

		try {
			FutureGet futureGet = dht.get(Number160.createHash(room_name)).start();
			futureGet.awaitUninterruptibly();

			if (futureGet.isSuccess()) {

				if (futureGet.isEmpty()) {
					System.out.println("No room named \"" + room_name + "\" found");
					
					return false;
				}

				ChatRoom tmp;
				tmp = (ChatRoom) futureGet.dataMap().values().iterator().next().object();
				
				if(tmp.getHash().contains(dht.peerAddress())) {
					tmp.getHash().remove(dht.peerAddress());
					dht.put(Number160.createHash(room_name)).data(new Data(tmp)).start().awaitUninterruptibly();
					rooms.remove(room_name);
					System.out.println("Room \"" + room_name + "\" left");
				}else {
					System.out.println("No subscription to room named \"" + room_name + "\" found");
					
					return false;
				}
			}else
				return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean subscribeToRoom(String room_name) {
		if (room_name.isEmpty()) {
			System.out.println("Room name not valid");

			return false;
		}

		try {
			FutureGet futureGet = dht.get(Number160.createHash(room_name)).start();
			futureGet.awaitUninterruptibly();

			if (futureGet.isSuccess()) {
				if (futureGet.isEmpty()) {
					System.out.println("No room named \"" + room_name + "\" found");
					
					return false;
				}

				ChatRoom tmp;
				tmp = (ChatRoom) futureGet.dataMap().values().iterator().next().object();
				
				if(tmp.getHash().contains(dht.peerAddress())) {
					System.out.println("Already subscribed to \"" + room_name + "\"");
					
					return false;
				}
				
				tmp.getHash().add(dht.peerAddress());
				dht.put(Number160.createHash(room_name)).data(new Data(tmp)).start().awaitUninterruptibly();
				rooms.add(room_name);

				return true;
			} else {
				System.out.println("Problem during subscription");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean sendMessage(String room_name, String msg) {
		if (room_name.isEmpty()) {
			System.out.println("Room name not valid");

			return false;
		}
		
		if(!rooms.contains(room_name)) {
			System.out.println("No subscription to \"" + room_name + "\" found");
			
			return false;
		}

		try {
			Message ms = new Message(room_name, msg, peerId);
			FutureGet futureGet = dht.get(Number160.createHash(room_name)).start();
			futureGet.awaitUninterruptibly();

			if (futureGet.isSuccess()) {

				ChatRoom tmp;
				tmp = (ChatRoom) futureGet.dataMap().values().iterator().next().object();
				
				for (PeerAddress peer : tmp.getHash()) {
					if (!peer.peerId().equals(dht.peer().peerID())) {
						FutureDirect futureDirect = dht.peer().sendDirect(peer).object(ms).start();
						futureDirect.awaitUninterruptibly();
						if (futureDirect.isFailed())
							System.out.println(futureDirect.failedReason());
					}
				}

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean roomsList() {
		if(rooms.isEmpty()) {
			
			return false;
		}
		
		System.out.println("Current subscriptions:");
		for (String room : rooms)
			System.out.println(room);
		
		return true;
	}
	
	public boolean listOfAvailableRooms() throws ClassNotFoundException, IOException {
		int i = 0;
		Collection<Data> allRooms = dht.storageLayer().get().values();
		
		if(allRooms.isEmpty()) {
			System.out.println("No rooms exist");
			
			return false;
		}
		
		for(Data room : allRooms) {
			ChatRoom currentRoom = (ChatRoom) room.object();
			if(!currentRoom.getHash().contains(dht.peerAddress())) {
				i++;
				System.out.println(currentRoom.getName());
			}
		}
		
		if(i == 0)
			System.out.println("Already subscribed to all rooms");
		
		return true;
			
	}

	public boolean exitNetwork() {
		for (String room : new ArrayList<String>(rooms))
			leaveRoom(room);

		replication.shutdown();
		dht.peer().announceShutdown().start().awaitUninterruptibly();

		return true;
	}
}
