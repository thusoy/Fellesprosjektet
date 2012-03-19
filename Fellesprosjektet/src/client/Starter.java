package client;

import static hashtools.Hash.createHash;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

import no.ntnu.fp.model.Person;
import server.Execute;
import server.PersonHandler;
import calendar.Day;

public class Starter {

	public static void main(String[] args) {
		try{
			setUp();
			Person user = authenticateUser();
			if (user != null){
				run(user);
			} else {
				System.out.println("Stikk av!");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void run(Person user){
		System.out.println("running calendarapp!");
		showWeek(user);
	}
	
	private static void showWeek(Person user){
		Day[] days = Day.values();
		for(Day d: days){
			System.out.println(d);
		}
	}
	
	private static void setUp() throws IOException{
		String query = "TRUNCATE TABLE User";
		try {
			Execute.executeUpdate(query);
		} catch (SQLException e){
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL");
		}
		new Person("tarjei", "husøy", "tarjei@roms.no", "komtek", "lol", false);
	}
	
	private static Person authenticateUser() throws IOException{
		String email = getEmail();
		String password = getPassword();
		Person user = authenticationHelper(email, password);
		return user;
	}
	
	private static Person authenticationHelper(String email, String password) throws IOException{
		String salt = getSalt(email);
		System.out.println(salt);
		String passwordHash = createHash(password, salt);
		System.out.println(passwordHash);
		Person user = getUserFromEmailAndPassword(email, passwordHash);
		return user;
	}
	
	/**
	 * Kjøres SERVERSIDE!
	 */
	private static Person getUserFromEmailAndPassword(String email, String passwordHash) throws IOException{
		String query = "SELECT userId FROM User WHERE email='%s'";
		long id;
		try {
			id = Execute.executeGetLong(String.format(query, email));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		Person user = PersonHandler.getPerson(id);
		if (user.getPasswordHash().equals(passwordHash)){
			return user;
		} else {
			return null;
		}
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt;
		try {
			salt = Execute.executeGetString(String.format(query, email));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		return salt;
	}
	
	
	
	private static String getEmail(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Skriv inn e-post: ");
		String email = scanner.nextLine();
		return email;
	}
	
	private static String getPassword(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Skriv inn passord: ");
		String password = scanner.nextLine();
		return password;
	}

}
