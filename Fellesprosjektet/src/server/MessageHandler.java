package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import no.ntnu.fp.model.Person;

import calendar.Message;

public class MessageHandler {
	
	public void createMessage(Message msg) throws ClassNotFoundException, IOException, SQLException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		
		String querySaveMessage = 
				"INSERT INTO Message VALUES(%s, %s, %s)";
			
		Execute.executeUpdate(String.format(querySaveMessage, dateSent, content, title));
	}
	public String getTitleMessage(Message msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		long id = msgId.getId();
		
		String queryGetTitleMessage =
				"SELECT title FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetString(String.format(queryGetTitleMessage, id));
	}
	public Date getDateSentMessage(Message msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		long id = msgId.getId();
		
		String queryGetDateSentMessage =
				"SELECT dateSent FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetDate(String.format(queryGetDateSentMessage, id));
	}
	public static String getContentMessage(Message msgId, Person user) throws ClassNotFoundException, IOException, SQLException {
		long id = msgId.getId();
		
		String queryGetContentMessage =
				"SELECT content FROM Message WHERE msgId='%i'";
		
		return Execute.executeGetString(String.format(queryGetContentMessage, id));
	}
}
