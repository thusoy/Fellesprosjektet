package calendar;

import java.util.Date;
import java.util.List;

import no.ntnu.fp.model.Person;

public class Calendar {
	private Person owner;
	private List<Person> followers;
	
	public Person getOwner() {
		return owner;
	}
	public void setOwner(Person owner) {
		this.owner = owner;
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

}
