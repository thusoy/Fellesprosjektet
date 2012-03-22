package server;

import calendar.RejectedMessage;
import static calendar.Message.recreateMessage;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import no.ntnu.fp.model.Person;
import calendar.Appointment;
import calendar.Message;

public class MessageHandler {
	
	public static void createMessage(Message msg) throws IOException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		long msgId = getUniqueId();
		msg.setId(msgId);
		Long appId = null;
		if (msg instanceof RejectedMessage){
			RejectedMessage rm = (RejectedMessage) msg;
			appId = rm.getApp().getAppId();
		}
		String query = "INSERT INTO Message(msgId, dateSent, content, title, appointment) " + 
						"VALUES(%d, '%s', ?, '%s', ?)";
		try {
			PreparedStatement ps = Execute.getPreparedStatement(String.format(query, msgId, dateSent, title));
			ps.setString(1, content);
			ps.setObject(2, appId, java.sql.Types.BIGINT);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static long getUniqueId() throws IOException {
		long id;
		do {
			id = System.currentTimeMillis();
		} while(idInDb(id));
		return id;
	}
	
	private static boolean idInDb(long id) throws IOException {
		String query = String.format("SELECT * FROM Message WHERE msgId=%d", id);
		try {
			ResultSet rs = Execute.getResultSet(query);
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	public static void sendMessageToUser(long msgId, long userId) throws IOException {
		String query = 
				"INSERT INTO UserMessages(userId, msgId, hasBeenRead) VALUES(%d, %d, %b)";
		Execute.executeUpdate(String.format(query, userId, msgId, false));
		System.out.printf("Sent message %d to user %d!\n", msgId, userId);
	}
	
	public static void sendMessageToAllParticipants(Appointment app, String title,
		String content) throws IOException {
			Set<Person> participants = app.getParticipants().keySet();
//			if(participants.isEmpty()){
//				return;
//			}
			Message msg = new Message(title, content);
			msg.setReceivers(Arrays.asList(participants.toArray(new Person[0])));	
		}
	public static void setMessageAsRead(long msgId, long userId) throws IOException {
		String query =
				"UPDATE UserMessages SET hasBeenRead=%b WHERE msgId=%d AND userId=%d";
		Execute.executeUpdate(String.format(query, true, msgId, userId));
	}
	
	public static Message getMessage(long msgId) throws IOException {
		String query = "SELECT msgId, title, content, dateSent, appointment FROM Message WHERE msgId=%d";
		ResultSet rs = Execute.getResultSet(String.format(query, msgId));
		Message msg;
		try {
			if(rs.next()){
				String title = rs.getString("title");
				String content = rs.getString("content");
				long id = rs.getLong("msgId");
				Date dateSent = rs.getDate("dateSent");
				Long appId = rs.getObject("appointment") == null ? null : rs.getLong("appointment");
				if (appId == null){
					msg = recreateMessage(id, title, content, dateSent);
				} else {
					Appointment app = AppointmentHandler.getAppointment(appId);
					msg = RejectedMessage.recreateRejectedMessage(id, title, content, dateSent, app);
				}
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
