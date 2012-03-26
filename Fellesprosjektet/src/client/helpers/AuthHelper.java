package client.helpers;

import static client.helpers.DBHelper.getUserIdFromEmail;
import static client.helpers.DBHelper.isValidEmail;
import static client.helpers.IO.getString;
import static hashtools.Hash.createHash;

import java.io.IOException;

import server.PersonHandler;
import calendar.DBCommunicator;
import calendar.Person;

public class AuthHelper extends DBCommunicator{

	private static PersonHandler personHandler;
	
	static {
		personHandler = (PersonHandler) getHandler(PersonHandler.SERVICE_NAME);
	}
	
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
		return personHandler.getSalt(email);
	}
	
	/**
	 * Kjøres SERVERSIDE!
	 * @throws InvalidLoginException 
	 */
	private static Person getUserFromEmailAndPassword(String email, String passwordHash) 
			throws IOException, InvalidLoginException{
		long id = getUserIdFromEmail(email);
		Person user = personHandler.getPerson(id);
		if (user.getPasswordHash().equals(passwordHash)){
			return user;
		} else {
			throw new InvalidLoginException();
		}
	}
	
}
