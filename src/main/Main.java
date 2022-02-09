package main;

import java.util.Scanner;

import org.kohsuke.args4j.Option;

public class Main {

	//@Option(name = "-m", aliases = "--masterip", usage = "master", required = true)
	private static String master;

	//@Option(name = "-id", aliases = "--identifierpeer", usage = "id", required = true)
	private static int id;

	public static void main(String[] args) throws Exception {
		if (args.length != 2 && (System.getenv("-m").isEmpty() || System.getenv("-id").isEmpty())) {
			System.out.println("Number of arguments is wrong");

			return;
		}

		master = args.length == 0 ? System.getenv("-m") : args[0];
		id = args.length == 0 ? Integer.parseInt(System.getenv("-id")) : Integer.parseInt(args[1]);
		MessageListener ml = new MessageListener(id);
		AnonymousUser pr = new AnonymousUser(id, master, ml);
		Scanner sc = new Scanner(System.in);

		System.out.println("Starting new peer with id: " + id);

		while (true) {
			int ch;

			menu();

			ch = sc.nextInt();
			sc.nextLine();
			while (ch < 1 || ch > 7) {
				System.out.println("Choice not valid, retry");
				ch = sc.nextInt();
			}

			switch (ch) {
			case 1:
				System.out.println("Enter name of room: ");
				String newRoom = sc.nextLine();

				if (pr.createRoom(newRoom))
					System.out.println("Room created successfully");
				else
					System.out.println("Error during room creation");

				break;
			case 2:
				System.out.println("Enter room to join: ");
				String enterRoom = sc.nextLine();

				if (pr.subscribeToRoom(enterRoom))
					System.out.println("Subscription complete");
				else
					System.out.println("Error during subscription");

				break;
			case 3:
				System.out.println("Enter name of room to leave: ");
				String leaveRoom = sc.nextLine();

				if (pr.leaveRoom(leaveRoom))
					System.out.println("Unsubscription completed");
				else
					System.out.println("Error during unsubscription");

				break;
			case 4:
				System.out.println("Enter name of room to send a message to: ");
				String msgRoom = sc.nextLine();

				System.out.println("Enter message: ");
				String msg = sc.nextLine();

				if (pr.sendMessage(msgRoom, msg))
					System.out.println("Message sent");
				else
					System.out.println("Error while sending message");

				break;
			case 5:
				if(!pr.roomsList())
					System.out.println("No subscription found");
				
				break;
			case 6:
				System.out.println("Available rooms:");

				pr.listOfAvailableRooms();

				break;
			case 7:
				System.out.println("Leaving network...");

				if (pr.exitNetwork())
					System.out.println("Network left succesfully");

				sc.close();

				System.exit(0);
			}
		}
	}

	public static void menu() {
		System.out.println("*********************");
		System.out.println("      CHAT MENU      ");
		System.out.println("*********************");
		System.out.println("1)Create room");
		System.out.println("2)Enter room");
		System.out.println("3)Leave room");
		System.out.println("4)Send message");
		System.out.println("5)List of subscriptions");
		System.out.println("6)List of available rooms");
		System.out.println("7)Exit network");
	}
}
