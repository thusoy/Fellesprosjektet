package server;

import static calendar.Appointment.recreateAppointment;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static void updateAppointment(Appointment app) throws IOException {
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String description = app.getDescription();
		String rawText = app.getDaysAppearing() != null ? app.getDaysAppearing().toString() : null;
		String daysAppearing = rawText != null ? rawText : null;
		Date endOfRepeat = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		Map<Person, Boolean> participants = app.getParticipants();
		boolean isPrivate = app.isPrivate();
		long appId = app.getAppId();
		long creatorId = app.getCreator() != null ? app.getCreator().getId() : 0;
		
		String query =
				"UPDATE Appointment SET place=?, title='%s', startTime=?, endTime=?, " +
				"description=?, daysAppearing=?, endOfRepeatDate=?, roomName=?, isPrivate=%b, " +
				" creatorId=%d WHERE appId=%d";		

		try {
			String formatted = String.format(query, title, isPrivate, creatorId, appId);
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
		if (participants != null) {
			for (Person user: participants.keySet()) {
				AppointmentHandler.addUserToAppointment(appId, user.getId());
			}
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
		Appointment ap = getAppointment(appId);
		Person person = PersonHandler.getPerson(userId);
		Message msg = new Message("Avslag pŒ avtale",
				person.getFirstname()+" "+person.getLastname()+" har avlsatt avtalen: "+ap.getTitle());
		for (Person user: ap.getParticipants().keySet()) {
			MessageHandler.sendMessageToUser(msg.getId(), user.getId());
		}
	}
	
	public static void deleteAppointment(long appId) throws IOException {
		Appointment app = getAppointment(appId);
		MessageHandler.sendMessageToAllParticipants(app, "Avtale: "+app.getTitle(), "Denne avtalen er blitt slettet");
		String query = "DELETE FROM Appointment WHERE appId=%d";
		Execute.executeUpdate(String.format(query, appId));
	}
	
	public static void updateUserAppointment(long appId, long userId, Boolean bool) throws IOException {
		String query = "UPDATE UserAppointments SET hasAccepted=%b WHERE userId=%d AND appId=%d";
		Execute.executeUpdate(String.format(query, bool, userId, appId));
	}
	
	public static Map<Long, Boolean> getParticipants(long appId) throws IOException {
		String query =
				"SELECT userId, hasAccepted FROM UserAppointments" +
				" WHERE appId=%d";
		
		try {
			return Execute.executeGetHashMap(String.format(query, appId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	public static Boolean getInviteStatusOnUser(long appId, long userId) throws IOException {
		String query =
				"SELECT hasAccepted FROM UserAppointments" +
				" WHERE appId=%d AND userId=%d";
		
		try {
			return Execute.executeGetBoolean(String.format(query, appId, userId));
		} catch (SQLException e) {
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	public static void addUserToAppointment(long appId, long userId) throws IOException {
		String query = "INSERT INTO UserAppointments(appId, userId, hasAccepted) VALUES(%d, %d, null)";
		Execute.executeUpdate(String.format(query, appId, userId));
	}
	
	public static void deleteUserFromAppointment(long appId) throws IOException {
		String query = 
				"DELETE FROM UserAppoinments WHERE userId=%d AND msgId=%d";
		
		Execute.executeUpdate(String.format(query, appId));
	}
	
	public static void updateRoomName(long appId, String name) throws IOException {
		String query =
				"UPDATE Appointment SET" +
				" roomName='%s'" +
				" WHERE appId=%d";
		Execute.executeUpdate(String.format(query, name, appId));	
	}
	
	public static Appointment getAppointment(long appId) throws IOException {	
		String query = "SELECT appId, title, place, startTime, endTime, description, " +
				"daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId FROM Appointment " +
				"WHERE appId=%d ORDER BY startTime";
		ResultSet rs = Execute.getResultSet(String.format(query, appId));
		return getListFromResultSet(rs).get(0);
	}
	
	private static Map<Person, Boolean> convertIdsToPersons(Map<Long, Boolean> participants) throws IOException{
		Map<Person, Boolean> out = new HashMap<Person, Boolean>();
		for (Long i: participants.keySet()){
			out.put(PersonHandler.getPerson(i), participants.get(i));
		}
		return out;
	}
	
	public static List<Appointment> getAll() throws IOException {
		String query = "SELECT appId, title, place, startTime, endTime, description, " +
				"daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId FROM Appointment " + 
				"ORDER BY startTime";
		ResultSet rs = Execute.getResultSet(query);
		return getListFromResultSet(rs);
	}
	
	public static List<Appointment> getAllCreated(long userId) throws IOException {
		String query = "SELECT appId, title, place, startTime, endTime, description, " +
				"daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId FROM Appointment WHERE creatorId=%d " + 
				"ORDER BY startTime";
		ResultSet rs = Execute.getResultSet(String.format(query, userId));
		return getListFromResultSet(rs);
	}
	public static List<Appointment> getAllInvited(long userId) throws IOException {
		String query = "SELECT appId FROM UserAppointments WHERE userId=%d";
		List<Long> appIdList = new ArrayList<Long>();
		List<Appointment> appointments = new ArrayList<Appointment>();
		try {
			appIdList = Execute.executeGetLongList(String.format(query, userId));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL feil");
		}
		for (long appId: appIdList) {
			appointments.add(getAppointment(appId));
		}
		return appointments;
	}

	public static List<Appointment> getWeekAppointments(long userId, int weekNum) throws IOException {
		Date startOfWeek = getStartOfWeek(weekNum);
		Date endOfWeek = getEndOfWeek(weekNum);
		String query = "SELECT appId, title, place, startTime, endTime, description, " +
				"daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId FROM Appointment " +
				"WHERE creatorId=%d AND startTime > ? AND endTime < ? " + 
				"ORDER BY startTime";
		List<Appointment> appointments = null;
		try {
			PreparedStatement ps = Execute.getPreparedStatement(String.format(query, userId));
			ps.setDate(1, startOfWeek);
			ps.setDate(2, endOfWeek);
			ResultSet rs = ps.executeQuery();
			appointments = getListFromResultSet(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL, stoopid!");
		}
		return appointments;
	}

	private static Date getStartOfWeek(int weekNum) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		cal.set(Calendar.YEAR, year);
		Date start = new Date(cal.getTimeInMillis());
		return start;
	}

	private static Date getEndOfWeek(int weekNum) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		cal.set(Calendar.YEAR, year);
		int aWeekInMs = 6*24*60*60*1000;
		Date start = new Date(cal.getTimeInMillis() + aWeekInMs);
		return start;
	}

	private static List<Appointment> getListFromResultSet(ResultSet rs) throws IOException {
		List<Appointment> finalList = new ArrayList<Appointment>();
		try {
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
				Person creator = PersonHandler.getPerson(creatorId);
				Map<Person, Boolean> participants = convertIdsToPersons(getParticipants(id));
				Appointment a = recreateAppointment(id, title, startTime, endTime, isPrivate, participants, creator);
				a.setPlace(place);
				a.setDescription(description);
				a.setDaysAppearing(Day.fromSetString(daysAppearing));
				a.setEndOfRepeatDate(endOfRepeat);
				a.setRoomName(roomName);
				finalList.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL, stoopid!");
		}
		return finalList;
	}
	
		
}
