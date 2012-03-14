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
	/**
	 * APPOINTMENT : query for Œ legge tl en appointment
	 * brukes til funksjonen save()
	 * rekkef¿lge--> id, place, title, descr, start, end, daysAppearing, endOfRe,
	 * isPrivate, creatorId, participants, room_name 
	 */
	//IKKE FERDIG
	public void createString(Set<Day> daysAppearing)){
		
	}
	String querySaveAppointment = 
			"INSERT INTO Appointment VALUES(%i, %s, %s, %s, date, date, set, date" +
			"%b, %i)";
	//mangler hashmap om room_name
	/**
	 * UPDATE a meeting
	 * Oppdaterer et allerede opprettet m¿te (appointment), 
	 * med alle attributter. 
	 */
	String queryUpdateAppointment =
			"UPDATE Appointment SET" +
			" appId='%i'" +
			" place='%s'" +
			" title='%s'" +
			" description='%s'" +
			" startTime='date'" +
			" endTime='date'" +
			" daysAppearing='set'" +
			" endOfRepeatDate='date'" +
			" isPrivate='%b'" +
			" creatorId='%i'" +
			" WHERE appId='%i'";
	String queryUpdateUserAppointment = 
			"UPDATE UserAppointment SET" +
			" hasAccepted='%b'" +
			" WHERE userId='%i' AND msgId='%i'";
	
}
