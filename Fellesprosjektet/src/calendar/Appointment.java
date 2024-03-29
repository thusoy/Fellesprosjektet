package calendar;

import static dateutils.DateUtils.stripMsFromTime;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import server.AppointmentHandler;

public class Appointment extends DBCommunicator implements Serializable, Comparable<Appointment> {
	private static final long serialVersionUID = -5442910434292395380L;
	private Long appId = null;
	private String place;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;
	private boolean isPrivate;
	private Person creator;
	private Map<Person, Boolean> participants;
	private String roomName;
	private static AppointmentHandler appHandler;
	
	public static void bindToHandler(){
		if (appHandler == null){
			appHandler = (AppointmentHandler) getHandler(AppointmentHandler.SERVICE_NAME);
		}
	}
	
	/**
	 * Creates an appointment and saves the object to the database.
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @param isPrivate
	 * @param participants
	 * @param creator
	 * @throws IOException
	 */
	public Appointment(String title, Date startTime, Date endTime, boolean isPrivate, 
			Map<Person, Boolean> participants, Person creator) throws IOException {
		bindToHandler();
		this.title = title;
		this.creator = creator;
		setStartTime(startTime);
		setEndTime(endTime);
		this.isPrivate = isPrivate;
		this.participants = participants != null ? participants : new HashMap<Person, Boolean>();
		this.description = new String();
		appId = appHandler.getUniqueId();
		appHandler.createAppointment(this);
	}
	
	private Appointment(long appId){
		this.appId = appId;
	}
	
	public static Appointment recreateAppointment(long appId, String title, Date startTime, Date endTime, boolean isPrivate, 
			Map<Person, Boolean> participants, Person creator) throws IOException{
		Appointment app = new Appointment(appId);
		app.setTitle(title);
		app.setStartTime(startTime);
		app.setEndTime(endTime);
		app.setPrivate(isPrivate);
		app.setParticipants(participants);
		app.creator = creator;
		return app;
	}

	public void answerInvite(Person user, Boolean answer) throws IOException{
		bindToHandler();
		participants.put(user, answer);
		appHandler.answerInvite(appId, user.getId(), answer);
	}
	
	public void save() throws IOException{
		bindToHandler();
		appHandler.deleteParticipants(appId, participants);
		appHandler.updateAppointment(this);
	}
	
	public void setAppId(long appId) {
		this.appId = appId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
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
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) throws IOException {
		this.description = description;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) throws IOException {
		this.startTime = stripMsFromTime(startTime);
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) throws IOException {
		this.endTime = stripMsFromTime(endTime);
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(boolean isPrivate) throws IOException {
		this.isPrivate = isPrivate;
	}
	
	/**
	 * Server sender ut melding til alle deltakere om at appointment'en er slettet. 
	 * Deretter blir appointment-objektet slettet.
	 * @throws IOException 
	 */
	public void delete() throws IOException {
		bindToHandler();
		appHandler.deleteAppointment(this.appId);
	}
	public void deleteAppointmentInvited(long userId) throws IOException {
		bindToHandler();
		appHandler.deleteAppointmentInvited(this.appId, userId);
	}
	
	public Map<Person, Boolean> getParticipants() {
		return participants;
	}
	
	public void setParticipants(Map<Person, Boolean> participants) throws IOException {
		if (participants == null){
			this.participants = new HashMap<Person, Boolean>();
		} else {
			this.participants = participants;
		}
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void setRoomName(String roomName) throws IOException {
		this.roomName = roomName;
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

	public static Appointment getAppointment(long appId) throws IOException {
		bindToHandler();
		return appHandler.getAppointment(appId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + (isPrivate ? 1231 : 1237);
		result = prime * result
				+ ((participants == null) ? 0 : participants.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result
				+ ((roomName == null) ? 0 : roomName.hashCode());
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (roomName == null) {
			if (other.roomName != null)
				return false;
		} else if (!roomName.equals(other.roomName))
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

	@Override
	public int compareTo(Appointment a) {
		return startTime.compareTo(a.startTime);
	}

}
