package client.helpers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import calendar.Person;

import server.Execute;
import server.PersonHandler;

public class DBHelper {

	public static Person getPersonFromEmail(String email) throws IOException{
		long id = getUserIdFromEmail(email);
		return PersonHandler.getPerson(id);
	}
	
	public static long getUserIdFromEmail(String email) throws IOException{
		String query = "SELECT userId FROM User WHERE email=?";
		try {
			PreparedStatement ps = Execute.getPreparedStatement(query);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return rs.getLong("userId");
			}
			throw new IllegalArgumentException("Ugyldig e-postadresse!");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Ugyldig e-postadresse!"); 
		}
	}
	
	public static boolean isValidEmail(String email) throws IOException{
		String query = "SELECT * FROM User WHERE email=?";
		try {
			PreparedStatement ps = Execute.getPreparedStatement(query);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i sQL!");
		}
	}
}
