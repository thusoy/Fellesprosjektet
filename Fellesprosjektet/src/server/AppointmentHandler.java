package server;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.ntnu.fp.model.Person;
import calendar.Appointment;
import calendar.Message;

public class AppointmentHandler {
	
	public static void createAppointment(Appointment app) throws IOException {
	
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String description = app.getDescription();
		String rawText = app.getDaysAppearing() != null ? app.getDaysAppearing().toString() : null;
		String daysAppearing = rawText != null ? rawText.substring(1, rawText.length()-1) : null;
		Date endOfRepeat = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		boolean isPrivate = app.isPrivate();
		long creatorId = app.getCreator() != null ? app.getCreator().getId() : 0;
		long appId = System.currentTimeMillis();
		app.setAppId(appId);
		/*
		 * Alle verdier som kan være null må settes inn ved bruk av PreparedStatement sine settere.
		 */
		String query = 
				"INSERT INTO Appointment(appId, title, place, startTime, endTime, description" +
				" , daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId) VALUES(%d, '%s', ?, ?, " +
				"?, ?, ?, ?, ?, %b, %d)";
		try {
			String formatted = String.format(query, appId, title, isPrivate, creatorId);
			PreparedStatement ps = Execute.getPreparedStatement(formatted);
			ps.setString(1, place);
			ps.setTimestamp(2, new Timestamp(start.getTime()));
			ps.setTimestamp(3, new Timestamp(end.getTime()));
			ps.setString(4, description);
			ps.setString(5, daysAppearing);
			ps.setDate(6, endOfRepeat);
			ps.setString(7, roomName);
			ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static void updateAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String description = app.getDescription();
		String rawText = app.getDaysAppearing() != null ? app.getDaysAppearing().toString() : null;
		String daysAppearing = rawText != null ? rawText.substring(1, rawText.length()-1) : null;
		Date endOfRepeat = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		boolean isPrivate = app.isPrivate();
		long creatorId = app.getCreator() != null ? app.getCreator().getId() : 0;
		
		String query =
				"UPDATE Appointment SET" +
				" place=?" +
				" title='%s'" +				
				" startTime=?" +
				" endTime=?" +
				" description=?" +
				" daysAppearing=?" +
				" endOfRepeatDate=?" +
				" roomName=?" +
				" isPrivate=%b" +
				" creatorId=%d" +
				" WHERE appId=%d";		

		try {
			String formatted = String.format(query, title, isPrivate, creatorId);
			PreparedStatement ps = Execute.getPreparedStatement(formatted);
			ps.setString(1, place);
			ps.setTimestamp(2, new Timestamp(start.getTime()));
			ps.setTimestamp(3, new Timestamp(end.getTime()));
			ps.setString(4, description);
			ps.setString(5, daysAppearing);
			ps.setDate(6, endOfRepeat);
			ps.setString(7, roomName);
			ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static String getTitleAppointment(long appId) throws IOException {
		String query =
				"SELECT title FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static String getPlace(long appId) throws IOException {
		String query =
				"SELECT place FROM Appointment WHERE appId=%d";
		try {
			return Execute.executeGetString(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}

	public static Date getStart(long appId) throws IOException {
		String query =
				"SELECT startTime FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetDatetime(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static Date getEnd(long appId) throws IOException {
		String query =
				"SELECT endTime FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetDatetime(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static String getDescription(long appId) throws IOException {
		String query =
				"SELECT description FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static String getDaysAppearing(long appId) throws IOException {
		String query =
				"SELECT daysAppearing FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static Date getEndOfRepeatDate(long appId) throws IOException {
		String query =
				"SELECT endOfRepeatDate FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetDate(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static String getRoomName(long appId) throws IOException {
		String query =
				"SELECT roomName FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetString(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static boolean getIsPrivate(long appId) throws IOException {
		String query =
				"SELECT isPrivate FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetBoolean(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static long getCreatorId(long appId) throws IOException {
		String query =
				"SELECT creatorId FROM Appointment WHERE appId=%d";
		
		try {
			return Execute.executeGetLong(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static void deleteAppointment(long appId) throws IOException {
		Appointment app = getAppointment(appId);
		sendMessageToAllParticipants(app, "Avtale slettet.", "Denne avtalen er blitt slettet");
		String query =
				"DELETE FROM Appointment WHERE appId=%d";
		
		try {
			Execute.executeUpdate(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static void updateUserAppointment(long appId, Person person, Boolean bool) throws IOException {
		long personId = person.getId();
		
		String query = 
				"UPDATE UserAppointments SET" +
				" hasAccepted='%b'" +
				" WHERE userId=%d AND msgId=%d";
		
		try {
			Execute.executeUpdate(String.format(query, bool, personId, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static Map<Integer, Boolean> getParticipants(long appId) throws IOException {
		String query =
				"SELECT userId, hasAccepted FROM UserAppointments" +
				" WHERE appId=%d";
		
		try {
			return Execute.executeGetHashMap(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static void addUserToAppointment(long appId) throws IOException {
		String queryAddUserToAppointment =
				"INSERT INTO UserAppointments VALUES(%d, %d, %b)";
		
		try {
			Execute.executeUpdate(String.format(queryAddUserToAppointment, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static void deleteUserFromAppointment(long appId) throws IOException {
		String query = 
				"DELETE FROM UserAppoinments WHERE userId=%d AND msgId=%d";
		
		try {
			Execute.executeUpdate(String.format(query, appId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	private static void sendMessageToAllParticipants(Appointment app, String title,
			String content) throws IOException {
		Set<Person> participants = app.getParticipants().keySet();
		Date dateSent = new Date(System.currentTimeMillis());
		Message msg = new Message(title, content, dateSent, false);
		msg.save();
		long msgId = msg.getId();
		String query = 
				"INSERT INTO UserMessages VALUES(%d, %d, %b)";
		for (Person user: participants) {
			long userId = user.getId();
			try {
				Execute.executeUpdate(String.format(query, userId, msgId, false));
			} catch (SQLException e) {
				throw new RuntimeException("Feil i SQL!");
			}
		}		
	}
	
	public static Appointment getAppointment(long appId) throws IOException {		
		String place = getPlace(appId);
		String title = getTitleAppointment(appId);
		Date start = getStart(appId);
		Date end = getEnd(appId);
		String des = getDescription(appId);
		String daysAppearing = getDaysAppearing(appId);
		Date endOfRe = getEndOfRepeatDate(appId);
		String roomName = getRoomName(appId);
		boolean isPrivate = getIsPrivate(appId);
		long creatorId = getCreatorId(appId);
		Map<Integer, Boolean> p = getParticipants(appId);
		Map<Person, Boolean> participants = new HashMap<Person, Boolean>();
		for (Integer i: p.keySet()){
			participants.put(PersonHandler.getPerson(i), p.get(i));
		}
		Appointment appointment = new Appointment(title, start, 
				end, isPrivate, participants, true);
		appointment.setPlace(place);
		appointment.setId(appId);
		appointment.setDescription(des);
//		appointment.setDaysAppearing(daysAppearing);
		appointment.setEndOfRepeatDate(endOfRe);
		appointment.setRoom_name(roomName);		
		
		return appointment;
	}

}
