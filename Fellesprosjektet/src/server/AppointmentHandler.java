package server;

import java.util.Date;

import java.util.HashMap;
import java.util.Set;

import no.ntnu.fp.model.Person;

import calendar.Appointment;
import calendar.Day;

import server.Execute;

public class AppointmentHandler {
	
	public void createAppointment(Appointment app) {
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String des = app.getDescription();
		String rawText = app.getDaysAppearing().toString();
		String dayAppearing = rawText.substring(1, rawText.length()-1);
		Date endOfRe = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		boolean isPrivate = app.isPrivate();
		long creatorId = app.getCreator().getId();
		HashMap<Person, Boolean> participants = app.getParticipants();
		
		String querySaveAppointment = 
				"INSERT INTO Appointment VALUES(%s, %s, %s, " +
				"Date, Date, %s, Date,'%s' ,%b, %i)";
			
		Execute.executeUpdate(String.format(querySaveAppointment, place, title, start, end, des, 
				dayAppearing, endOfRe, roomName, isPrivate, creatorId));
	}
	public void updateAppointment(Appointment app) {
		String place = app.getPlace();
		String title = app.getTitle();
		Date start = app.getStartTime();
		Date end = app.getEndTime();
		String des = app.getDescription();
		String rawText = app.getDaysAppearing().toString();
		String dayAppearing = rawText.substring(1, rawText.length()-1);
		Date endOfRe = app.getEndOfRepeatDate();
		String roomName = app.getRoom_name();
		boolean isPrivate = app.isPrivate();
		long creatorId = app.getCreator().getId();
		HashMap<Person, Boolean> participants = app.getParticipants();
		
		String queryUpdateAppointment =
				"UPDATE Appointment SET" +
				" place='%s'" +
				" title='%s'" +
				" description='%s'" +
				" startTime='date'" +
				" endTime='date'" +
				" daysAppearing='set'" +
				" endOfRepeatDate='date'" +
				" roomName='%s'" +
				" isPrivate='%b'" +
				" creatorId='%i'" +
				" WHERE appId='%i'";		
		Execute.executeUpdate(String.format(queryUpdateAppointment, place, title, start, end, des, 
				dayAppearing, endOfRe, roomName, isPrivate, creatorId));
	}
	public String getTitleAppointment(Appointment app) {
		long id = app.getAppId();
		
		String queryGetTitleAppointment =
				"SELECT title FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(queryGetTitleAppointment, id));
	}
	public String getPlace(Appointment app) {
		long id = app.getAppId();
		
		String queryGetPlaceAppointment =
				"SELECT place FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(queryGetPlaceAppointment, id));
	}
	public String getRoom(Appointment app) {
		long id = app.getAppId();
		
		String queryGetRoomAppointment =
				"SELECT roomName FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGet(String.format(queryGetRoomAppointment, id));
	}
	public Date getStart(Appointment app) {
		long id = app.getAppId();
		
		String queryGetStartAppointment =
				"SELECT startTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(queryGetStartAppointment, id));
	}
	public Date getEnd(Appointment app) {
		long id = app.getAppId();
		
		String queryGetEndAppointment =
				"SELECT endTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(queryGetEndAppointment, id));
	}
	public void deleteAppointment(Appointment app) {
		long id = app.getAppId();
		sendMessageToAllParticipants(app);
		String queryDeleteAppointment =
				"DELETE FROM Appointment WHERE appId='%i'";
		
		executeUpdate(String.format(queryGetEndAppointment, id));
	}
	public void updateUserAppointment(Appointment app, Person person, Boolean bool) {
		long appId = app.getAppId();
		long personId = person.getId();
		
		String queryUpdateUserAppointment = 
				"UPDATE UserAppointment SET" +
				" hasAccepted='%b'" +
				" WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(queryUpdateUserAppointment, bool, personId, appId));
	}
	public HashMap getParticipants(Appointment app) {
		long id = app.getAppId();
		
		String queryGetParticipants =
				"SELECT userId, hasAccepted FROM UserAppointment" +
				" WHERE appId='%i'";
		
		Execute.executeGetHashMap(String.format(queryGetParticipants, id));
	}
	public void addUserToAppointment(Appointment app) {
		long id = app.getAppId();
		
		String queryAddUserToAppointment =
				"INSERT INTO UserAppointment VALUES(%i, %i, %b)";
		
		Execute.executeUpdate(String.format(queryAddUserToAppointment, id));
	}
	public void deleteUserFromAppointment(Appointment app) {
		long id = app.getAppId();
		
		String queryDeleteUserFromAppointment = 
				"DELETE FROM UserAppoinment WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(queryDeleteUserFromAppointment, id));
	}
	

}
