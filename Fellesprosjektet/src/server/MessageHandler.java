package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import no.ntnu.fp.model.Person;

import calendar.Message;

public class MessageHandler {
	
	public static void createMessage(Message msg) throws ClassNotFoundException, IOException, SQLException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		
		String querySaveMessage = 
				"INSERT INTO Message VALUES('%s', '%s', '%s')";
			
		Execute.executeUpdate(String.format(querySaveMessage, dateSent, content, title));
	}
	public static String getTitleMessage(long msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		
		String queryGetTitleMessage =
				"SELECT title FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetString(String.format(queryGetTitleMessage, msgId));
	}
	public static Date getDateSentMessage(long msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		
		String queryGetDateSentMessage =
				"SELECT dateSent FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetDate(String.format(queryGetDateSentMessage, msgId));
	}
	public static String getContentMessage(long msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		
		String queryGetContentMessage =
				"SELECT content FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetString(String.format(queryGetContentMessage, msgId));
	}
}
