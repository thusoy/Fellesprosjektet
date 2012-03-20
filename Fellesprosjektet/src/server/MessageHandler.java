package server;

import static calendar.Message.recreateMessage;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import no.ntnu.fp.model.Person;
import calendar.Appointment;
import calendar.Message;

public class MessageHandler {
	
	public static void createMessage(Message msg) throws IOException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		long msgId = System.currentTimeMillis();
		msg.setId(msgId);
		String query = 
				"INSERT INTO Message(msgId, dateSent, content, title) VALUES(%d, '%s', '%s', '%s')";
			
		Execute.executeUpdate(String.format(query, msgId, dateSent, content, title));
	}
	
	public static void sendMessageToUser(long msgId, long userId) throws IOException {
		String query = 
				"INSERT INTO UserMessages(userId, msgId, hasBeenRead) VALUES(%d, %d, %b)";
		Execute.executeUpdate(String.format(query, userId, msgId, false));
	}
	public static void sendMessageToAllParticipants(Appointment app, String title,
			String content) throws IOException {
			Set<Person> participants = app.getParticipants().keySet();
			Message msg = new Message(title, content);
			msg.setReceivers(Arrays.asList(participants.toArray(new Person[0])));	
		}
	public static void setMessageAsRead(long msgId, long userId) throws IOException {
		String query =
				"UPDATE UserMessages SET hasBeenRead=%b WHERE msgId=%d AND userId=%d";
		Execute.executeUpdate(String.format(query, true, msgId, userId));
	}
	
	public static Message getMessage(long msgId) throws IOException {
		String query = "SELECT msgId, title, content, dateSent FROM Message WHERE msgId=%d";
		ResultSet rs = Execute.getResultSet(String.format(query, msgId));
		Message msg;
		try {
			if(rs.next()){
				String title = rs.getString("title");
				String content = rs.getString("content");
				long id = rs.getLong("msgId");
				Date dateSent = rs.getDate("dateSent");
				msg = recreateMessage(id, title, content, dateSent);
				msg.setId(id);
			} else {
				throw new IllegalArgumentException("No such message!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		
		msg.setReceivers(getReceiversOfMessage(msgId));
		return msg;
	}
	
	public static List<Person> getReceiversOfMessage(long msgId) throws IOException{
		String getParticipantsQuery = "SELECT msgId, userId, hasBeenRead FROM UserMessages WHERE msgId=%d";
		List<Person> receivers = new ArrayList<Person>();
		ResultSet rs = Execute.getResultSet(String.format(getParticipantsQuery, msgId));
		try {
			while (rs.next()){
				Person p = PersonHandler.getPerson(rs.getLong("userId"));
				receivers.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		return receivers;
	}
	
	public static List<Message> getUnreadMessagesForUser(Person p) throws IOException{
		String query = "SELECT msgId FROM UserMessages WHERE userId=%d AND hasBeenRead=false"; 
		ResultSet rs = Execute.getResultSet(String.format(query, p.getId()));
		List<Message> unread = new ArrayList<Message>();
		try {
			while(rs.next()){
				long msgId = rs.getLong("msgId");
				Message m = getMessage(msgId);
				unread.add(m);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		return unread;
	}
	
	public static boolean getHasBeenRead(long msgId, long userId) throws IOException{
		String query =
				"SELECT hasBeenRead FROM UserMessages WHERE msgId=%d AND userId=%d";
		try {
			return Execute.executeGetBoolean(String.format(query, msgId, userId));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
	}
}
