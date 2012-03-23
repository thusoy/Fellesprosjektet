package client.helpers;

import static client.helpers.DBHelper.getUserIdFromEmail;
import static client.helpers.DBHelper.isValidEmail;
import static client.helpers.IO.getString;
import static hashtools.Hash.createHash;

import java.io.IOException;

import server.Execute;
import server.PersonHandler;
import calendar.Person;

public class AuthHelper {

	public static Person authenticateUser() throws IOException, InvalidLoginException, UserAbortException {
		String email = getString("E-post: ");
		String password = getString("Passord: ");
		Person user = authenticationHelper(email, password);
		return user;
	}
	
	private static Person authenticationHelper(String email, String password) throws IOException, InvalidLoginException{
		if (isValidEmail(email)){
			String salt = getSalt(email);
			String passwordHash = createHash(password, salt);
			Person user = getUserFromEmailAndPassword(email, passwordHash);
			return user;
		}
		try{
			Thread.sleep(100);
		} catch (InterruptedException e){ }
		throw new InvalidLoginException();
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt = Execute.getString(String.format(query, email));
		return salt;
	}
	
	/**
	 * Kjøres SERVERSIDE!
	 */
	private static Person getUserFromEmailAndPassword(String email, String passwordHash) throws IOException{
		long id = getUserIdFromEmail(email);
		Person user = PersonHandler.getPerson(id);
		if (user.getPasswordHash().equals(passwordHash)){
			return user;
		} else {
			throw new IllegalArgumentException("Wrong password!");
		}
	}
	
}
