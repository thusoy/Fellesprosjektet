package calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import no.ntnu.fp.model.Person;

public class Appointment {
	private int appId;
	private String place;
	private String description;
	private Date startTime;
	private Date endTime;
	private List<Date> daysAppearing;
	private Date endOfRepeatDate;
	private boolean isPrivate;
	private Person creator;
	private HashMap<Person, Boolean> participants;
	private String room_name;
	
	public void inviteUser(Person user){
//	implementer		
	}
	public void acceptInvite(Person user, Boolean answer){
//	implementer		
	}
	public void updateParticipants(HashMap<Person, Boolean> participtants){
//	implementer		
	}
	
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
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
	public List<Date> getDaysAppearing() {
		return daysAppearing;
	}
	public void setDaysAppearing(List<Date> daysAppearing) {
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
	public void deleteAppointment() {
//	implementer
	}
	public void addTimeAppearing(Date time){
//	implementer		
	}
	public void updateDescription(String description){
//	implementer		
	}
	public HashMap<Person, Boolean> getParticipants() {
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
	public void setCreator(Person creator) {
		this.creator = creator;
	}
	
}
