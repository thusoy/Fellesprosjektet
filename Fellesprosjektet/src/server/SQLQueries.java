package server;

public class SQLQueries {
	
	/**
	 * MESSAGE : query for Œ oppdatere hvem som har lest et Message-objekt.
	 * trenger parametrene userId og msgId
	 */
	String queryReadMessage = 
			"UPDATE UserMessages SET hasBeenRead='true' WHERE userId='%s' AND msgId='%s'";
	
	/**
	 * APPOINTMENT : query for Œ legge tl en appointment
	 * brukes til funksjonen save()
	 * rekkef¿lge--> id, place, title, descr, start, end, daysAppearing, endOfRe,
	 * isPrivate, creator, participants, room_name 
	 */
	//IKKE FERDIG
	public void createString(Set<Day> daysAppearing)){
		
	}
	String querySaveAppointment = 
			"INSERT INTO appointment VALUES(%i, %s, %s, %s, date, date, set, date" +
			"%b, person, hashmap, %s)";
}
