package calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.ntnu.fp.model.Person;

public class Appointment extends DBObject<Appointment> implements Serializable {
	private static final long serialVersionUID = -5442910434292395380L;
	private int appId;
	private String place;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;
	private Set<Day> daysAppearing;
	private Date endOfRepeatDate;
	private boolean isPrivate;
	private Person creator;
	private Map<Person, Boolean> participants;
	private String room_name;
	
	public Appointment(String title, Date startTime, Date endTime, boolean isPrivate, 
			Map<Person, Boolean> participants) {
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isPrivate = isPrivate;
		this.participants = participants;
	}
	
	public Appointment(){
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Appointment){
			Appointment a2 = (Appointment) obj;
			return appId == a2.appId;
		} else {
			return false;
		}
	}

	public void acceptInvite(Person user, Boolean answer){
		participants.put(user, answer);
		save();		
	}
	public void updateParticipants(HashMap<Person, Boolean> participants){
		this.participants = participants;
		save();
	}
	
	public void setAppId(int appId) {
		this.appId = appId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCreator(Person creator) {
		this.creator = creator;
	}

	public int getAppId() {
		return appId;
	}
	public String getTitle() {
		return title;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Set<Day> getDaysAppearing() {
		return daysAppearing;
	}
	public void setDaysAppearing(Set<Day> daysAppearing) {
		this.daysAppearing = daysAppearing;
	}
	public Date getEndOfRepeatDate() {
		return endOfRepeatDate;
	}
	public void setEndOfRepeatDate(Date endOfRepeatDate) {
		this.endOfRepeatDate = endOfRepeatDate;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	/**
	 * Server sender ut melding til alle deltakere om at appointment'en er slettet. 
	 * Deretter blir appointment-objektet slettet.
	 */
	public void deleteAppointment() {
		delete();
	}
	public void updateDaysAppearing(Set<Day> days){
		this.daysAppearing = days;
		save();
	}
	public void updateDescription(String description){
		this.description = description;
	}
	public Map<Person, Boolean> getParticipants() {
		return participants;
	}
	public void setParticipants(HashMap<Person, Boolean> participants) {
		this.participants = participants;
	}
	public String getRoom_name() {
		return room_name;
	}
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	public Person getCreator() {
		return creator;
	}
	
	public String toString(){
		String base = "%s (%s)";
		DateFormat formatter = new SimpleDateFormat("dd.MM HH:mm");
		String startdate = formatter.format(startTime);
		return String.format(base, title, startdate);
	}
	
}
