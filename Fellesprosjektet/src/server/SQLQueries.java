package server;


public class SQLQueries {
	
	/**
	 * MESSAGE : query for Œ oppdatere hvem som har lest et Message-objekt.
	 * trenger parametrene userId og msgId
	 * RETURN content
	 */
	String queryReadMessage = 
			"UPDATE UserMessages SET hasBeenRead='true' WHERE userId='%i' AND msgId='%i'";
	String queryGetContentMsg = 
			"SELECT content FROM Message WHERE msgId='%i'";
	
	//Appointment sp¿rringer
	/**
	 * Query for Œ legge tl en appointment
	 * brukes til funksjonen save()
	 * Hvilke users som blir invitert, blir evt lagt til med 'queryAddUserToAppointment'
	 * som ligger lengre nede i dokumentet.
	 * rekkef¿lge--> id (settes selv), place, title, descr, start, end, 
	 * daysAppearing, endOfRe, isPrivate, creatorId, participants, room_name 
	 */

	String querySaveAppointment = 
			"INSERT INTO Appointment VALUES(%s, %s, %s, " +
			"date, date, set, Date,'%s' ,%b, %i)";
	/**
	 * UPDATE a meeting
	 * Oppdaterer et allerede opprettet m¿te (appointment), 
	 * med alle attributter. 
	 */
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
	/**
	 * getAppointment()
	 */
	String queryGetAppointment =
			"SELECT * FROM Appointment WHERE appId='%i'";
	/**
	 * getTitle()
	 */
	String queryGetTitleAppointment =
			"SELECT title FROM Appointment WHERE appId='%i'";
	/**
	 * getPlace()
	 */
	String queryGetPlaceAppointment =
			"SELECT place FROM Appointment WHERE appId='%i'";
	/**
	 * getRoom()
	 */
	String queryGetRoomAppointment =
			"SELECT roomName FROM Appointment WHERE appId='%i'";
	/**
	 * getStart()
	 */
	String queryGetStartAppointment =
			"SELECT startTime FROM Appointment WHERE appId='%i'";
	/**
	 * getEnd()
	 */
	String queryGetEndAppointment =
			"SELECT endTime FROM Appointment WHERE appId='%i'";
	/**
	 * deleteAppointment()
	 */
	String queryDeleteAppointment =
			"DELETE FROM Appointment WHERE appId='%i'";
	/**
	 * acceptInvite(user, boolean)
	 */
	String queryUpdateUserAppointment = 
			"UPDATE UserAppointment SET" +
			" hasAccepted='%b'" +
			" WHERE userId='%i' AND msgId='%i'";
	/**
	 * getParticipants()
	 */
	String queryGetParticipants =
			"SELECT userId, hasAccepted FROM UserAppointment" +
			" WHERE appId='%i'";
	/**
	 * updateParticipants(HashMap)
	 * en funksjon for Œ legge til, og en for Œ slette
	 * rekkef¿lge --> userId, appId, hasAccepted
	 */
	String queryAddUserToAppointment =
			"INSERT INTO UserAppointment VALUES(%i, %i, %b)";
	String queryDeleteUserFromAppointment = 
			"DELETE FROM UserAppoinment WHERE userId='%i' AND msgId='%i'";
	
	
}
