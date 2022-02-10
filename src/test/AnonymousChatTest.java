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
	@Order(2)
	void createRoomTest(){
		assertTrue(p1.createRoom("Room A"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(3)
	void createVoidRoomTest() {
		assertFalse(p2.createRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	@Order(4)
	void subscribeToRoomTest(){
		assertTrue(p2.createRoom("Room B"));
		assertTrue(p2.subscribeToRoom("Room B"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(5)
	void subscribeToNonExistingRoomTest() {
		assertFalse(p1.subscribeToRoom("Room C"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(6)
	void subscribeToVoidRoomTest() {
		assertFalse(p1.subscribeToRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	@Order(7)
	void subscribeTwiceToRoomTest() {
		assertTrue(p2.subscribeToRoom("Room A"));
		assertFalse(p2.subscribeToRoom("Room A"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(8)
	void sendMessageTest() {
		assertTrue(p3.subscribeToRoom("Room B"));
		assertTrue(p3.sendMessage("Room B", msg));
	}
	
	@org.junit.jupiter.api.Test
	@Order(9)
	void sendMsgToNotSubRoomTest() {
		assertFalse(p1.sendMessage("Room B", msg));
	}
	
	@org.junit.jupiter.api.Test
	@Order(10)
	void sendMsgToVoidRoomTest() {
		assertFalse(p4.sendMessage("", msg));
	}
	
	@org.junit.jupiter.api.Test
	@Order(11)
	void roomsListTest() {
		assertTrue(p4.subscribeToRoom("Room B"));
		assertTrue(p4.roomsList());
	}
	
	@org.junit.jupiter.api.Test
	@Order(12)
	void roomsListVoidTest() {
		assertTrue(p4.leaveRoom("Room B"));
		assertFalse(p4.roomsList());
	}
	
	@org.junit.jupiter.api.Test
	@Order(13)
	void listAvailableRoomsTest() throws ClassNotFoundException, IOException {
		assertTrue(p4.createRoom("Room C"));
		assertTrue(p4.listOfAvailableRooms());
	}
	
	@org.junit.jupiter.api.Test
	@Order(14)
	void noAvailableRoomsTest() throws ClassNotFoundException, IOException {
		assertTrue(p2.subscribeToRoom("Room C"));
		assertFalse(p2.listOfAvailableRooms());
	}
	
	@org.junit.jupiter.api.Test
	@Order(15)
	void leaveRoomTest() {
		assertTrue(p3.leaveRoom("Room B"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(16)
	void leaveVoidRoomTest() {
		assertFalse(p2.leaveRoom(""));
	}
	
	@org.junit.jupiter.api.Test
	@Order(17)
	void leaveNotSubscribedRoomTest() {
		assertFalse(p4.leaveRoom("Room A"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(18)
	void leaveNonExistingRoomTest() {
		assertFalse(p4.leaveRoom("Room D"));
	}
	
	@org.junit.jupiter.api.Test
	@Order(19)
	void exitNetworkTest() throws Exception {
		assertTrue(p4.exitNetwork());
	}
	
	@org.junit.jupiter.api.Test
	@Order(20)
	void generalTest() throws ClassNotFoundException, IOException{
		
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
		
		assertTrue(p2.roomsList());
		assertTrue(p2.listOfAvailableRooms());
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
