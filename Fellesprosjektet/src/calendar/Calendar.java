package calendar;

import java.util.Date;
import java.util.List;

import no.ntnu.fp.model.Person;

public class Calendar extends DBObject {
	private long calendarId;
	private Person owner;
	
	public Calendar(Person owner){
		this.owner = owner;
	}
	
	public List<Appointment> getAppointments(Date start, Date end){
		
	}
}