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

public class AppointmentHandler {
	
	public void createAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
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
		
		String query = 
				"INSERT INTO Appointment VALUES(%s, %s, %s, " +
				"%s, %s, %s, %s, %s,%b, %i)";
			
		Execute.executeUpdate(String.format(query, place, title, start, end, des, 
				dayAppearing, endOfRe, roomName, isPrivate, creatorId));
	}
	public void updateAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
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
				dayAppearing, endOfRe, roomName, isPrivate, creatorId));
	}
	public String getTitleAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT title FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, id));
	}
	public String getPlace(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT place FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, id));
	}

	public Date getStart(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT startTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, id));
	}
	public Date getEnd(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT endTime FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, id));
	}
	public String getDescription(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT description FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, id));
	}
	public String getDaysAppearing(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT daysAppearing FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, id));
	}
	public Date getEndOfRepeatDate(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT endOfRepeatDate FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetDate(String.format(query, id));
	}
	public String getRoomName(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT roomName FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetString(String.format(query, id));
	}
	public boolean getIsPrivate(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT isPrivate FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetBoolean(String.format(query, id));
	}
	public long getCreatorId(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT creatorId FROM Appointment WHERE appId='%i'";
		
		return Execute.executeGetLong(String.format(query, id));
	}
	public void deleteAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		sendMessageToAllParticipants(app, "Tittel", "Innhold");
		String query =
				"DELETE FROM Appointment WHERE appId='%i'";
		
		Execute.executeUpdate(String.format(query, id));
	}
	public void updateUserAppointment(Appointment app, Person person, Boolean bool) throws ClassNotFoundException, IOException, SQLException {
		long appId = app.getAppId();
		long personId = person.getId();
		
		String query = 
				"UPDATE UserAppointment SET" +
				" hasAccepted='%b'" +
				" WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(query, bool, personId, appId));
	}
	public Map<Integer, Boolean> getParticipants(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query =
				"SELECT userId, hasAccepted FROM UserAppointment" +
				" WHERE appId='%i'";
		
		return Execute.executeGetHashMap(String.format(query, id));
	}
	public void addUserToAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String queryAddUserToAppointment =
				"INSERT INTO UserAppointment VALUES(%i, %i, %b)";
		
		Execute.executeUpdate(String.format(queryAddUserToAppointment, id));
	}
	public void deleteUserFromAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {
		long id = app.getAppId();
		
		String query = 
				"DELETE FROM UserAppoinment WHERE userId='%i' AND msgId='%i'";
		
		Execute.executeUpdate(String.format(query, id));
	}
	
	private void sendMessageToAllParticipants(Appointment app, String title,
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
	
	public Appointment getAppointment(Appointment app) throws ClassNotFoundException, IOException, SQLException {		
		String place = getPlace(app);
		String title = getTitleAppointment(app);
		Date start = getStart(app);
		Date end = getEnd(app);
		String des = getDescription(app);
		String dayAppearing = getDaysAppearing(app);
		Date endOfRe = getEndOfRepeatDate(app);
		String roomName = getRoomName(app);
		boolean isPrivate = getIsPrivate(app);
		long creatorId = getCreatorId(app);
		Map<Integer, Boolean> participants = getParticipants(app);
		
		for (Integer i: participants){
			
		}
		
		Appointment appointment = new Appointment(title, start, 
				end, isPrivate, participants);
		
		return appointment;
	}

}
