package server;

import static calendar.Message.recreateMessage;
import static server.AppointmentHandlerImpl.getAppointment;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import calendar.Appointment;
import calendar.Message;
import calendar.Person;
import calendar.RejectedMessage;
import client.helpers.StoopidSQLException;

public class MessageHandler extends Handler{
	
	public static void createMessage(Message msg) throws IOException {
		Date dateSent = msg.getDateSent();
		String content = msg.getContent();
		String title = msg.getTitle();
		long msgId = dbEngine.getUniqueId();
		msg.setId(msgId);
		Long appId = null;
		Long rejectingUserId = null;
		if (msg instanceof RejectedMessage){
			RejectedMessage rm = (RejectedMessage) msg;
			appId = rm.getApp().getId();
			rejectingUserId = rm.getRejectingUser().getId();
		}
		String query = "INSERT INTO Message(msgId, dateSent, content, title, appointment, rejectingUser) " + 
						"VALUES(%d, ?, ?, '%s', ?, ?)";
		try {
			PreparedStatement ps = dbEngine.getPreparedStatement(String.format(query, msgId, title));
			ps.setTimestamp(1, new Timestamp(dateSent.getTime()));
			ps.setString(2, content);
			ps.setObject(3, appId, Types.BIGINT);
			ps.setObject(4, rejectingUserId, Types.BIGINT);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteOldReceivers(long msgId) throws RemoteException, IOException{
		String deleteQuery = "DELETE FROM UserMessages WHERE msgId=?";
		dbEngine.update(deleteQuery, msgId);
	}
	
	public static void sendMessageToUser(long msgId, long userId) throws IOException {
		String query = "INSERT INTO UserMessages(userId, msgId, hasBeenRead) VALUES(?, ?, false)";
		dbEngine.update(query, userId, msgId);
	}
	
	public static void sendMessageToAllParticipants(Message msg) throws IOException {
			List<Person> receivers = msg.getReceivers();
			for (Person p: receivers){
				sendMessageToUser(msg.getId(), p.getId());
			}
	}
	
	public static void setMessageAsRead(long msgId, long userId) throws IOException {
		String query = "UPDATE UserMessages SET hasBeenRead=%b WHERE msgId=? AND userId=?";
		dbEngine.update(String.format(query, true), msgId, userId);
	}

	public static Message getMessage(long msgId) throws IOException {
		String query = "SELECT msgId, title, content, dateSent, appointment, rejectingUser FROM Message WHERE msgId=?";
		ResultSet rs = dbEngine.getResultSet(query, msgId);
		return getMessagesFromResultSet(rs).get(0);
	}
	
	public static List<Person> getReceiversOfMessage(long msgId) throws IOException{
		String getParticipantsQuery = "SELECT msgId, userId, hasBeenRead FROM UserMessages WHERE msgId=?";
		List<Person> receivers = new ArrayList<Person>();
		ResultSet rs = dbEngine.getResultSet(getParticipantsQuery, msgId);
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
		String query = "SELECT msgId, title, content, dateSent, appointment, rejectingUser FROM Message " + 
				"WHERE msgId IN (SELECT msgId FROM UserMessages WHERE userId=? AND hasBeenRead=false)"; 
		ResultSet rs = dbEngine.getResultSet(query, p.getId());
		return getMessagesFromResultSet(rs);
	}
	
	private static List<Message> getMessagesFromResultSet(ResultSet rs) throws IOException{
		List<Message> messages = new ArrayList<Message>();
		try {
			while(rs.next()){
				String title = rs.getString("title");
				String content = rs.getString("content");
				long id = rs.getLong("msgId");
				Date dateSent = new Date(rs.getTimestamp("dateSent").getTime());
				Long appId = rs.getObject("appointment") == null ? null : rs.getLong("appointment");
				Message msg;
				if (appId == null){
					msg = recreateMessage(id, title, content, dateSent);
				} else {
					Long rejectingUserId = rs.getObject("rejectingUser") == null ? null : rs.getLong("rejectingUser");
					Person rejectingUser = PersonHandler.getPerson(rejectingUserId);
					Appointment app = AppointmentHandlerImpl.getAppointment(appId);
					msg = RejectedMessage.recreateRejectedMessage(id, title, content, dateSent, app, rejectingUser);
				}
				msg.setReceivers(getReceivers(id));
				messages.add(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
		return messages;
	}
	
	private static List<Person> getReceivers(long msgId) throws IOException{
		String query = "SELECT userId FROM UserMessages WHERE msgId=?";
		List<Person> receivers = new ArrayList<Person>();
		ResultSet rs = dbEngine.getResultSet(query, msgId);
		try {
			while(rs.next()){
				long userId = rs.getLong(1);
				Person p = PersonHandler.getPerson(userId);
				receivers.add(p);
			}
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
		return receivers;
	}
	
	public static boolean getHasBeenRead(long msgId, long userId) throws IOException{
		String query = "SELECT hasBeenRead FROM UserMessages WHERE msgId=? AND userId=?";
		PreparedStatement ps = dbEngine.getPreparedStatement(query);
		try {
			ps.setLong(1, msgId);
			ps.setLong(2, userId);
			return dbEngine.getBoolean(ps);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static void sendMessageAppointmentInvite(long appId) throws IOException{
		Appointment ap = getAppointment(appId);
		Message msg = new Message("Ny avtale: "+ap.getTitle(),"Du er blitt lagt til i avtalen: "+ ap.getTitle() + ". Beskrivelse: " + ap.getDescription());
		for (Person user: ap.getParticipants().keySet()) {
			MessageHandler.sendMessageToUser(msg.getId(), user.getId());
		}
	}
	
	public static void sendMessageUpdateInfo(long appId) throws IOException {
		Appointment ap = getAppointment(appId);
		Message msg = new Message("Endring i avtalen: "+ap.getTitle(),
				"Denne avtalen har blitt endret. Starttidspunkt: "+ap.getStartTime()+" Sluttidspunkt: "+ap.getEndTime());
		for (Person user: ap.getParticipants().keySet()) {
			MessageHandler.sendMessageToUser(msg.getId(), user.getId());
		}
	}
	
	public static void sendMessageUserHasDenied(long appId, long userId) throws IOException {
		Appointment app = getAppointment(appId);
		Person p = PersonHandler.getPerson(userId);
		String content = "%s %s har avslått avtalen '%s'.";
		String formatted = String.format(content, p.getFirstname(), p.getLastname(), app.getTitle());
		Message msg = new RejectedMessage("Avslag pŒ avtale", formatted, app, p);
		for (Person user: app.getParticipants().keySet()) {
			if (user.getId() == userId){
				continue;
			}
			MessageHandler.sendMessageToUser(msg.getId(), user.getId());
		}
		MessageHandler.sendMessageToUser(msg.getId(), app.getCreator().getId());
	}
	
}
