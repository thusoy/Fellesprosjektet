package server;

import java.io.IOException;
import java.sql.SQLException;

import no.ntnu.fp.model.Person;

public class PersonHandler {
	private static long nextId = 1;
	
	public static long createUserAndGetId(Person person) throws ClassNotFoundException, IOException, SQLException{
		String firstname = person.getFirstname();
		String lastname = person.getLastname();
		String email = person.getLastname();
		String department = person.getDepartment();
		String passwordHash = person.getPasswordHash();
		
		String query =
				"INSERT INTO Person(userId, email, firstname, lastname, department, passwordHash) " +
				"VALUES('%d, %s', '%s', '%s', '%s', '%s')";
		
		Execute.executeUpdate(String.format(query, nextId, email, firstname, lastname, department, passwordHash));
		nextId++;
		return nextId-1;
	}
	
	public static void updateUser(Person person) throws ClassNotFoundException, IOException, SQLException {
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
		
		Execute.executeUpdate(String.format(query, email, firstname, lastname, department, passwordHash));
		
	}
	public static void deleteUser(long personId) throws ClassNotFoundException, IOException, SQLException{
		String query =
				"DELETE FROM Person WHERE userId='%d'";
		Execute.executeUpdate(String.format(query, personId));
	}
	public static String getFirstname(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT firstname FROM User WHERE userId='%d'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getLastname(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT lastname FROM User WHERE userId='%d'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getEmail(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT email FROM User WHERE userId='%d'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getDepartment(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT department FROM User WHERE userId='%d'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getPasswordHash(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT passwordHash FROM User WHERE userId='%d'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static Person getPerson(long personId) throws ClassNotFoundException, IOException, SQLException {
		String firstname = getFirstname(personId);
		String lastname = getLastname(personId);
		String email = getEmail(personId);
		String department = getDepartment(personId);
		String passwordHash = getPasswordHash(personId);
		
		Person person = new Person(firstname, lastname, email, department, passwordHash);
		return person;
	}
}
