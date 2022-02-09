package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import main.AnonymousUser;
import main.MessageListener;

/**
 * @author Fulvio
 *
 */
@TestMethodOrder(OrderAnnotation.class)
class AnonymousChatTest {
	public static AnonymousUser p1, p2, p3, p4;
	public static String msg;
	
	public AnonymousChatTest() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	*/
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Setting initial info");
		
		p1 = new AnonymousUser(1, "127.0.0.1", new MessageListener(1));	
		p2 = new AnonymousUser(2, "127.0.0.1", new MessageListener(2));
		p3 = new AnonymousUser(3, "127.0.0.1", new MessageListener(3));
		p4 = new AnonymousUser(4, "127.0.0.1", new MessageListener(4));
		
		msg = "Test message";
	}
	
	@org.junit.jupiter.api.Test
	@Order(1)
	void noExistingRoomsTest() throws ClassNotFoundException, IOException {
		assertFalse(p4.listOfAvailableRooms());
	}

	@org.junit.jupiter.api.Test
	void createRoomTest(){
		assertTrue(p1.createRoom("TRoomCreate"));
	}
	
	@org.junit.jupiter.api.Test
	void createVoidRoomTest() {
		assertFalse(p2.createRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	void subscribeToRoomTest(){
		assertTrue(p1.createRoom("TRoomSub"));
		assertTrue(p2.subscribeToRoom("TRoomSub"));
	}
	
	@org.junit.jupiter.api.Test
	void subscribeToNonExistingRoomTest() {
		assertFalse(p1.subscribeToRoom("FRoom"));
	}
	
	@org.junit.jupiter.api.Test
	void subscribeToVoidRoomTest() {
		assertFalse(p1.subscribeToRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	void subscribeTwiceToRoomTest() {
		assertTrue(p2.createRoom("Twice room"));
		assertTrue(p2.subscribeToRoom("Twice room"));
		assertFalse(p2.subscribeToRoom("Twice room"));
	}
	
	@org.junit.jupiter.api.Test
	void sendMessageTest() {
		assertTrue(p2.createRoom("TRoomSend"));
		assertTrue(p2.subscribeToRoom("TRoomSend"));
		assertTrue(p2.sendMessage("TRoomSend", msg));
	}
	
	@org.junit.jupiter.api.Test
	void sendMsgToNotSubRoomTest() {
		assertFalse(p1.sendMessage("TRoom", msg));
	}
	
	@org.junit.jupiter.api.Test
	void sendMsgToVoidRoomTest() {
		assertFalse(p1.sendMessage("", msg));
	}
	
	@org.junit.jupiter.api.Test
	void roomsListTest() {
		assertTrue(p4.createRoom("List room"));
		assertTrue(p4.subscribeToRoom("List room"));
		assertTrue(p4.roomsList());
	}
	
	@org.junit.jupiter.api.Test
	void roomsListVoidTest() {
		assertFalse(p4.roomsList());
	}
	
	@org.junit.jupiter.api.Test
	void listAvailableRoomsTest() throws ClassNotFoundException, IOException {
		assertTrue(p4.createRoom("Free room"));
		assertTrue(p4.listOfAvailableRooms());
	}
	
	@org.junit.jupiter.api.Test
	void noAvailableRoomsTest() throws ClassNotFoundException, IOException {
		assertTrue(p4.subscribeToRoom("Free room"));
		assertTrue(p4.listOfAvailableRooms());
	}
	
	@org.junit.jupiter.api.Test
	void leaveRoomTest() {
		assertTrue(p3.createRoom("TRoomLeave"));
		assertTrue(p3.subscribeToRoom("TRoomLeave"));
		assertTrue(p3.leaveRoom("TRoomLeave"));
	}
	
	@org.junit.jupiter.api.Test
	void leaveVoidRoomTest() {
		assertFalse(p2.leaveRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	void leaveNotSUbscribedRoomTest() {
		assertFalse(p2.leaveRoom("NotSubRoom"));
	}
	
	@org.junit.jupiter.api.Test
	void exitNetworkTest() throws Exception {
		assertTrue(p4.exitNetwork());
	}
	
	@org.junit.jupiter.api.Test
	void generalTest(){
		
		assertTrue(p1.createRoom("IT room"));
		assertTrue(p1.subscribeToRoom("IT room"));
		assertTrue(p2.subscribeToRoom("IT room"));
		assertTrue(p3.subscribeToRoom("IT room"));
				
		assertTrue(p3.createRoom("Lecture room"));
		assertTrue(p3.subscribeToRoom("Lecture room"));
		assertTrue(p4.subscribeToRoom("Lecture room"));
							
				
		assertTrue(p1.sendMessage("IT room", "IT is cool"));
		assertFalse(p1.sendMessage("Lecture room", "Hi everyone"));
				
		assertTrue(p2.leaveRoom("IT room"));
		assertFalse(p4.leaveRoom("IT room"));
			
		assertFalse(p3.createRoom("IT room"));
		assertFalse(p1.subscribeToRoom("Engineering room"));
				
		assertTrue(p3.sendMessage("IT room", "Message about IT"));
		assertTrue(p4.sendMessage("Lecture room", "Lecture at 9:00 AM"));
	}

	/**
	 * @throws java.lang.Exception
	*/
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("Terminating test");
		
		p1.exitNetwork();
		p2.exitNetwork();
		p3.exitNetwork();
		p4.exitNetwork();
	}

}
