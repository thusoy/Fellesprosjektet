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
		
		String query = 
				"INSERT INTO Message VALUES('%s', '%s', '%s')";
			
		try {
			Execute.executeUpdate(String.format(query, dateSent, content, title));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static String getTitleMessage(long msgId) throws IOException {
		
		String query =
				"SELECT title FROM Message WHERE msgId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static Date getDateSentMessage(long msgId) throws IOException {
		
		String query =
				"SELECT dateSent FROM Message WHERE msgId=%d";
		
		try {
			return Execute.executeGetDate(String.format(query, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static String getContentMessage(long msgId) throws IOException {
		
		String query =
				"SELECT content FROM Message WHERE msgId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, msgId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	public static void hasReadMessage(long msgId, long userId) throws IOException {
		String query =
				"UPDATE UserMessages SET hasReadMessage=%b WHERE msgId=%d AND userId=%d";
		try {
			Execute.executeUpdate(String.format(query, true, msgId, userId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
	
	public static Message getMessage(long msgId) throws IOException {
		String title = getTitleMessage(msgId);
		String content = getContentMessage(msgId);
		Date dateSent = getDateSentMessage(msgId);
		Message msg = new Message(title, content, dateSent, true);
		return msg;
	}
}
