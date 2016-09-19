package server;

import java.util.Scanner;

import database.DBcontroller;
import database.DBcontrollerInterface;

public class TestingMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			DBcontrollerInterface db = new DBcontroller();
			db.insertEvent("micha", "test1");
			System.out.println("Press \"ENTER\" to continue...");
			scanner.nextLine();
			db.insertEvent("micha2", "test2");
			System.out.println("Press \"ENTER\" to continue...");
			scanner.nextLine();
			System.out.println(db.getAllEvents());
			System.out.println("Press \"ENTER\" to continue...");
			scanner.nextLine();
			db.changePlayerName("micha", "michaChanged");
			db.insertEvent("michaChanged", "test3");
			System.out.println("Press \"ENTER\" to continue...");
			scanner.nextLine();
			System.out.println(db.getEventsByPlayerID("michaChanged"));
			System.out.println("Press \"ENTER\" to continue...");
			scanner.nextLine();
			db.deletePlayer("michaChanged");
			scanner.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
