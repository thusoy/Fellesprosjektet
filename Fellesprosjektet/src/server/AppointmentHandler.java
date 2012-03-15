package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.ntnu.fp.model.Person;

import calendar.Appointment;
import calendar.Day;
import calendar.Message;

import server.Execute;
import server.PersonHandler;

public class AppointmentHandler {
	
	public static void createAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
	
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
				"INSERT INTO Appointment(title, place, startTime, endTime, description" +
				" , daysAppearing, endOfRepeatDate, roomName, isPrivate, creatorId) VALUES('%s', '%s', '%s', " +
				"'%s', '%s', '%s', '%s', '%s', '%b', '%d')";
			
		Execute.executeUpdate(String.format(query, title, place, start, end, des, 
				daysAppearing, endOfRe, roomName, isPrivate, creatorId));
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
				" creatorId='%i'" +
				" WHERE appId='%i'";		
		Execute.executeUpdate(String.format(query, place, title, start, end, des, 
				daysAppearing, endOfRe, roomName, isPrivate, creatorId));
	}
	public static String getTitleAppointment(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT title FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, appId));
	}
	public static String getPlace(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT place FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, appId));
	}

	public static Date getStart(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT startTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, appId));
	}
	public static Date getEnd(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT endTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, appId));
	}
	public static String getDescription(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT description FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, appId));
	}
	public static String getDaysAppearing(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT daysAppearing FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, appId));
	}
	public static Date getEndOfRepeatDate(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT endOfRepeatDate FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, appId));
	}
	public static String getRoomName(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT roomName FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, appId));
	}
	public static boolean getIsPrivate(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT isPrivate FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetBoolean(String.format(query, appId));
	}
	public static long getCreatorId(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT creatorId FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetLong(String.format(query, appId));
	}
	public void deleteAppointment(long appId) throws ClassNotFoundException, IOException, SQLException {
		Appointment app = getAppointment(appId);
		sendMessageToAllParticipants(app, "Avtale slettet.", "Denne avtalen er blitt slettet");
		String query =
				"DELETE FROM Appointment WHERE appId='%i'";
		
		Execute.executeUpdate(String.format(query, appId));
	}
	public static void updateUserAppointment(long appId, Person person, Boolean bool) throws ClassNotFoundException, IOException, SQLException {
		long personId = person.getId();
		
		String query = 
				"UPDATE UserAppointment SET" +
				" hasAccepted='%b'" +
				" WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(query, bool, personId, appId));
	}
	public static Map<Integer, Boolean> getParticipants(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query =
				"SELECT userId, hasAccepted FROM UserAppointment" +
				" WHERE appId='%i'";
		
		return Execute.executeGetHashMap(String.format(query, appId));
	}
	public static void addUserToAppointment(long appId) throws ClassNotFoundException, IOException, SQLException {
		String queryAddUserToAppointment =
				"INSERT INTO UserAppointment VALUES(%i, %i, %b)";
		
		Execute.executeUpdate(String.format(queryAddUserToAppointment, appId));
	}
	public static void deleteUserFromAppointment(long appId) throws ClassNotFoundException, IOException, SQLException {
		String query = 
				"DELETE FROM UserAppoinment WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(query, appId));
	}
	
	private static void sendMessageToAllParticipants(Appointment app, String title,
			String content) throws ClassNotFoundException, IOException, SQLException {
		Set<Person> participants = app.getParticipants().keySet();
		Date dateSent = new Date(System.currentTimeMillis());
		Message msg = new Message(title, content, dateSent);
		msg.save();
		long msgId = msg.getId();
		String query = 
				"INSERT INTO UserMessage VALUES(%i, %i, %b)";
		for (Person user: participants) {
			long userId = user.getId();
			Execute.executeUpdate(String.format(query, userId, msgId, false));
		}		
	}
	
	public static Appointment getAppointment(long appId) throws ClassNotFoundException, IOException, SQLException {		
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
		Map<Person, Boolean> participants = null;
		
		for (Integer i: p.keySet()){
			participants.put(PersonHandler.getPerson(i), p.get(i));
		}
		
		Appointment appointment = new Appointment(title, start, 
				end, isPrivate, participants);
		appointment.setPlace(place);
		appointment.setDescription(des);
//		appointment.setDaysAppearing(daysAppearing);
		appointment.setEndOfRepeatDate(endOfRe);
		appointment.setRoom_name(roomName);		
		
		return appointment;
	}

}
