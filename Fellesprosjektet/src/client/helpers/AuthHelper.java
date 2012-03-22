package client.helpers;

import static client.helpers.DBHelper.getUserIdFromEmail;
import static client.helpers.IO.getString;
import static hashtools.Hash.createHash;

import java.io.IOException;

import no.ntnu.fp.model.Person;
import server.Execute;
import server.PersonHandler;

public class AuthHelper {

	public static Person authenticateUser() throws IOException {
		String email = getString("E-post: ");
		String password = getString("Passord: ");
		Person user = authenticationHelper(email, password);
		return user;
	}
	
	private static Person authenticationHelper(String email, String password) throws IOException{
		String salt = getSalt(email);
		String passwordHash = createHash(password, salt);
		Person user = getUserFromEmailAndPassword(email, passwordHash);
		return user;
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt = Execute.executeGetString(String.format(query, email));
		return salt;
	}
	
	/**
	 * Kj�res SERVERSIDE!
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
