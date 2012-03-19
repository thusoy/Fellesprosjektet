package server;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.ntnu.fp.model.Person;
import calendar.Appointment;
import calendar.Day;
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
		long appId = getUniqueId();
		app.setAppId(appId);
		/*
		 * Alle verdier som kan v�re null m� settes inn ved bruk av PreparedStatement sine settere.
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
	
	private static long getUniqueId() throws IOException {
		long id;
		do {
			id = System.currentTimeMillis();
		} while(idInDb(id));
		return id;
	}
	
	private static boolean idInDb(long id) throws IOException {
		String query = String.format("SELECT * FROM Appointment WHERE appId=%d", id);
		try {
			ResultSet rs = Execute.getResultSet(query);
			if (rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	public static void updateAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String des = app.getDescription();
		String rawText = app.getDaysAppearing() != null ? app.getDaysAppearing().toString() : null;
		String daysAppearing = rawText != null ? rawText.substring(1, rawText.length()-1) : null;
		Date endOfRe = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		boolean isPrivate = app.isPrivate();
		long creatorId = app.getCreator() != null ? app.getCreator().getId() : 0;
		
		String query =
				"UPDATE Appointment SET" +
				" place='%s'" +
				" title='%s'" +
				" description='%s'" +
				" startTime='%s'" +
				" endTime='%s'" +
				" daysAppearing='%s'" +
				" endOfRepeatDate='%s'" +
				" roomName='%s'" +
				" isPrivate='%b'" +
				" creatorId=%d" +
				" WHERE appId=%d";		
		Execute.executeUpdate(String.format(query, place, title, start, end, des, 
				daysAppearing, endOfRe, roomName, isPrivate, creatorId));
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
		Message msg = new Message(title, content, dateSent);
		msg.save();
		long msgId = msg.getId();
		String query = 
				"INSERT INTO UserMessage VALUES(%d, %d, %b)";
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
		Map<Person, Boolean> participants = convertIdsToPersons(p);
		Appointment appointment = new Appointment(title, start, 
				end, isPrivate, participants, true);
		appointment.setPlace(place);
		appointment.setId(appId);
		appointment.setDescription(des);
//		appointment.setDaysAppearing(daysAppearing);
		appointment.setEndOfRepeatDate(endOfRe);
		appointment.setRoomName(roomName);		
		
		return appointment;
	}
	
	private static Map<Person, Boolean> convertIdsToPersons(Map<Integer, Boolean> participants) throws IOException{
		Map<Person, Boolean> out = new HashMap<Person, Boolean>();
		for (Integer i: participants.keySet()){
			out.put(PersonHandler.getPerson(i), participants.get(i));
		}
		return out;
	}
	
	public static List<Appointment> getAll() throws IOException {
		List<Appointment> all = new ArrayList<Appointment>();
		String query = "SELECT appId, title, place, startTime, endTime, description, " +
				"daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId FROM Appointment " + 
				"ORDER BY startTime";
		ResultSet rs;
		try {
			rs = Execute.getResultSet(query);
			while (rs.next()){
				long id = rs.getLong(1);
				String title = rs.getString(2);
				String place = rs.getString(3);
				Date startTime = new Date(rs.getTimestamp(4).getTime());
				Date endTime = new Date(rs.getTimestamp(5).getTime());
				String description = rs.getString(6);
				String daysAppearing = rs.getString(7);
				Date endOfRepeat = rs.getDate(8);
				String roomName = rs.getString(9);
				boolean isPrivate = rs.getBoolean(10);
				long creatorId = rs.getLong(11);
				Map<Person, Boolean> participants = convertIdsToPersons(getParticipants(id));
				Appointment a = new Appointment(title, startTime, endTime, isPrivate, participants, true);
				a.setPlace(place);
				a.setDescription(description);
				a.setDaysAppearing(Day.fromSetString(daysAppearing));
				a.setEndOfRepeatDate(endOfRepeat);
				a.setRoomName(roomName);
				a.setCreator(PersonHandler.getPerson(creatorId));
				all.add(a);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL, stoopid!");
		}
		return all;
	}

}
