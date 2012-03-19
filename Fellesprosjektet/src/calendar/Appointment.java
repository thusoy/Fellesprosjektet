package calendar;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.RoundTime;

public class Appointment implements Serializable {
	private static final long serialVersionUID = -5442910434292395380L;
	private Long appId = null;
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
			Map<Person, Boolean> participants, boolean recreation) throws IOException {
		this.title = title;
		this.startTime = RoundTime.roundTime(startTime);
		this.endTime = RoundTime.roundTime(endTime);
		this.isPrivate = isPrivate;
		if (participants == null)
			this.participants = new HashMap<Person, Boolean>();
		else
			this.participants = participants;
		this.description = new String();
		if(!recreation){
			AppointmentHandler.createAppointment(this);
		}
	}
	
	public Appointment(String title, Date startTime, Date endTime, boolean isPrivate, 
			Map<Person, Boolean> participants, Person creator, boolean recreation) throws IOException {
		this.title = title;
		this.creator = creator;
		this.startTime = RoundTime.roundTime(startTime);
		this.endTime = RoundTime.roundTime(endTime);
		this.isPrivate = isPrivate;
		if (participants == null)
			this.participants = new HashMap<Person, Boolean>();
		else
			this.participants = participants;
		this.description = new String();
		if(!recreation){
			AppointmentHandler.createAppointment(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((daysAppearing == null) ? 0 : daysAppearing.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((endOfRepeatDate == null) ? 0 : endOfRepeatDate.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + (isPrivate ? 1231 : 1237);
		result = prime * result
				+ ((participants == null) ? 0 : participants.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result
				+ ((room_name == null) ? 0 : room_name.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Appointment other = (Appointment) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (daysAppearing == null) {
			if (other.daysAppearing != null)
				return false;
		} else if (!daysAppearing.equals(other.daysAppearing))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endOfRepeatDate == null) {
			if (other.endOfRepeatDate != null)
				return false;
		} else if (!endOfRepeatDate.equals(other.endOfRepeatDate))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (isPrivate != other.isPrivate)
			return false;
		if (participants == null) {
			if (other.participants != null)
				return false;
		} else if (!participants.equals(other.participants))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (room_name == null) {
			if (other.room_name != null)
				return false;
		} else if (!room_name.equals(other.room_name))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public void acceptInvite(Person user, Boolean answer){
		participants.put(user, answer);
	}
	
	public void updateParticipants(HashMap<Person, Boolean> participants){
		this.participants = participants;
	}
	
	public void setAppId(long appId) {
		this.appId = appId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCreator(Person creator) throws IOException {
		this.creator = creator;
		AppointmentHandler.updateAppointment(this);
	}

	public long getAppId() {
		if (appId == null){
			throw new IllegalStateException("AppId ikke satt enda!");
		}
		return appId;
	}
	public String getTitle() {
		return title;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) throws IOException {
		this.place = place;
		AppointmentHandler.updateAppointment(this);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) throws IOException {
		this.description = description;
		AppointmentHandler.updateAppointment(this);
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) throws IOException {
		this.startTime = startTime;
		AppointmentHandler.updateAppointment(this);
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) throws IOException {
		this.endTime = endTime;
		AppointmentHandler.updateAppointment(this);
	}
	public Set<Day> getDaysAppearing() {
		return daysAppearing;
	}
	public void setDaysAppearing(Set<Day> daysAppearing) throws IOException {
		this.daysAppearing = daysAppearing;
		AppointmentHandler.updateAppointment(this);
	}
	public Date getEndOfRepeatDate() {
		return endOfRepeatDate;
	}
	public void setEndOfRepeatDate(Date endOfRepeatDate) throws IOException {
		this.endOfRepeatDate = endOfRepeatDate;
		AppointmentHandler.updateAppointment(this);
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) throws IOException {
		this.isPrivate = isPrivate;
		AppointmentHandler.updateAppointment(this);
	}
	/**
	 * Server sender ut melding til alle deltakere om at appointment'en er slettet. 
	 * Deretter blir appointment-objektet slettet.
	 * @throws IOException 
	 */
	public void deleteAppointment() throws IOException {
		AppointmentHandler.deleteAppointment(this.appId);
	}
	public void updateDaysAppearing(Set<Day> days) throws IOException{
		this.daysAppearing = days;
		AppointmentHandler.updateAppointment(this);
	}
	public void updateDescription(String description) throws IOException{
		this.description = description;
		AppointmentHandler.updateAppointment(this);
	}
	public Map<Person, Boolean> getParticipants() {
		return participants;
	}
	public void setParticipants(HashMap<Person, Boolean> participants) throws IOException {
		this.participants = participants;
		AppointmentHandler.updateAppointment(this);
	}
	public String getRoom_name() {
		return room_name;
	}
	public void setRoomName(String room_name) throws IOException {
		this.room_name = room_name;
		AppointmentHandler.updateRoomName(this.getAppId(), room_name);
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

	public void setId(long appId) {
		this.appId = appId;
	}

	public static Appointment getAppointment(long appId) throws IOException {
		return AppointmentHandler.getAppointment(appId);
	}

	public static List<Appointment> getAll() throws IOException {
		return AppointmentHandler.getAll();
	}
	
}
