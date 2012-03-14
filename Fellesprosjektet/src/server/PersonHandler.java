package server;

import java.io.IOException;
import java.sql.SQLException;

import no.ntnu.fp.model.Person;

public class PersonHandler {
	
	public static String getFirstname(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT firstname FROM User WHERE userId='%i'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getLastname(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT lastname FROM User WHERE userId='%i'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getEmail(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT email FROM User WHERE userId='%i'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static String getDepartment(long personId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT department FROM User WHERE userId='%i'";
		
		return Execute.executeGetString(String.format(query, personId));
	}
	public static Person getPerson(long personId) throws ClassNotFoundException, IOException, SQLException {
		String firstname = getFirstname(personId);
		String lastname = getLastname(personId);
		String email = getEmail(personId);
		String department = getDepartment(personId);
		
		Person person = new Person(firstname, lastname, email, department);
		return person;
	}
}
