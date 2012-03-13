package calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.ntnu.fp.model.Person;

public class Calendar extends DBObject {
	private Person owner;
	private List<Person> followers;
	private List<Appointment> appointments;
	
	public Calendar(Person owner){
		this.owner = owner;
		appointments = new ArrayList<Appointment>();
	}
	
	public Person getOwner() {
		return owner;
	}
	
	public List<Person> getFollowers() {
		return followers;
	}
	
	public void setFollowers(List<Person> followers) {
		this.followers = followers;
	}
	
	public void getAppointments(Date start, Date end){
//	implementer		
	}
	
	public void addAppointment(Appointment app){
		
	}

}
