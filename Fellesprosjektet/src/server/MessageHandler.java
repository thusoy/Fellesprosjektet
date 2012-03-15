package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import no.ntnu.fp.model.Person;

import calendar.Message;

public class MessageHandler {
	
	public static void createMessage(Message msg) throws IOException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		
		String querySaveMessage = 
				"INSERT INTO Message VALUES('%s', '%s', '%s')";
			
		try {
			Execute.executeUpdate(String.format(querySaveMessage, dateSent, content, title));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static String getTitleMessage(long msgId, Person user) throws IOException {
		
		String queryGetTitleMessage =
				"SELECT title FROM Message WHERE msgId='%i'";
		
		try {
			return Execute.executeGetString(String.format(queryGetTitleMessage, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static Date getDateSentMessage(long msgId, Person user) throws IOException {
		
		String queryGetDateSentMessage =
				"SELECT dateSent FROM Message WHERE msgId='%i'";
		
		try {
			return Execute.executeGetDate(String.format(queryGetDateSentMessage, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static String getContentMessage(long msgId, Person user) throws IOException {
		
		String queryGetContentMessage =
				"SELECT content FROM Message WHERE msgId='%i'";
		
		try {
			return Execute.executeGetString(String.format(queryGetContentMessage, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
}
