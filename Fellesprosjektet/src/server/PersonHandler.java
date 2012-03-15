package server;

import java.io.IOException;
import java.sql.SQLException;

import no.ntnu.fp.model.Person;

public class PersonHandler {
	
	public static void createUser(Person person) throws IOException{
		String firstname = person.getFirstname();
		String lastname = person.getLastname();
		String email = person.getLastname();
		String department = person.getDepartment();
		String passwordHash = person.getPasswordHash();
		
		String query =
				"INSERT INTO Person( email, firstname, lastname, department, passwordHash) " +
				"VALUES('%s', '%s', '%s', '%s', '%s')";
		
		try {
			Execute.executeUpdate(String.format(query, email, firstname, lastname, department, passwordHash));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
		
	}
	public static void updateUser(Person person) throws IOException {
		String firstname = person.getFirstname();
		String lastname = person.getLastname();
		String email = person.getLastname();
		String department = person.getDepartment();
		String passwordHash = person.getPasswordHash();
		
		String query =
				"UPDATE Person SET" +
						" email='%s'" +
						" firstname='%s'" +
						" lastname='%s'" +
						" department='%s'" +
						" passwordHash='%s'";
		
		try{	
			Execute.executeUpdate(String.format(query, email, firstname, lastname, department, passwordHash));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static void deleteUser(long personId) throws IOException{
		String query =
				"DELETE FROM Person WHERE userId='%d'";
		try {
			Execute.executeUpdate(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static String getFirstname(long personId) throws IOException {
		String query =
				"SELECT firstname FROM User WHERE userId='%d'";
		
		try {
			return Execute.executeGetString(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static String getLastname(long personId) throws IOException {
		String query =
				"SELECT lastname FROM User WHERE userId='%d'";
		
		try {
			return Execute.executeGetString(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");		}
	}
	public static String getEmail(long personId) throws IOException {
		String query =
				"SELECT email FROM User WHERE userId='%d'";
		
		try {
			return Execute.executeGetString(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static String getDepartment(long personId) throws IOException {
		String query =
				"SELECT department FROM User WHERE userId='%d'";
		
		try {
			return Execute.executeGetString(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static String getPasswordHash(long personId) throws IOException {
		String query =
				"SELECT passwordHash FROM User WHERE userId='%d'";
		
		try {
			return Execute.executeGetString(String.format(query, personId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i sql");
		}
	}
	public static Person getPerson(long personId) throws IOException {
		String firstname = getFirstname(personId);
		String lastname = getLastname(personId);
		String email = getEmail(personId);
		String department = getDepartment(personId);
		String passwordHash = getPasswordHash(personId);
		
		Person person = new Person(firstname, lastname, email, department, passwordHash);
		return person;
	}
}
