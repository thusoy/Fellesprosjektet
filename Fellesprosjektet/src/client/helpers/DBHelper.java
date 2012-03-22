package client.helpers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import no.ntnu.fp.model.Person;
import server.Execute;
import server.PersonHandler;

public class DBHelper {

	public static Person getPersonFromEmail(String email) throws IOException{
		long id = getUserIdFromEmail(email);
		Person p = PersonHandler.getPerson(id);
		return p;
	}
	
	public static long getUserIdFromEmail(String email) throws IOException{
		String query = "SELECT userId FROM User WHERE email='%s'";
		long id = Execute.executeGetLong(String.format(query, email));
		return id;
	}
	
	public static boolean isValidEmail(String email) throws IOException{
		String query = "SELECT * FROM User WHERE email='%s'";
		ResultSet rs = Execute.getResultSet(String.format(query, email));
		try {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i sQL!");
		}
	}
}
