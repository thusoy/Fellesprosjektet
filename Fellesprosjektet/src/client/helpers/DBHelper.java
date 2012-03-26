package client.helpers;

import java.io.IOException;

import server.PersonHandler;
import calendar.DBCommunicator;
import calendar.Person;

public class DBHelper extends DBCommunicator{

	private static PersonHandler personHandler;
	
	static {
		personHandler = (PersonHandler) getHandler(PersonHandler.SERVICE_NAME);
	}
	
	public static Person getPersonFromEmail(String email) throws IOException{
		long id = getUserIdFromEmail(email);
		return personHandler.getPerson(id);
	}
	
	public static long getUserIdFromEmail(String email) throws IOException{
		return personHandler.getUserIdFromEmail(email);
	}
	
	public static boolean isValidEmail(String email) throws IOException{
		return personHandler.isValidEmail(email);
	}
}
