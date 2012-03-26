package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import calendar.Appointment;
import calendar.Person;

public interface AppointmentHandler extends Remote{
	public static final String SERVICE_NAME = "APPOINTMENT_HANDLER";

	public void createAppointment(Appointment app) throws RemoteException, IOException;
	
	public void answerInvite(long appId, long userId, Boolean answer) throws RemoteException, IOException;
	
	public void updateAppointment(Appointment app) throws IOException, RemoteException;
	
	public void deleteAppointment(long appId) throws IOException, RemoteException;

	public void deleteAppointmentInvited(long appId) throws IOException, RemoteException;
	
	public void updateUserAppointment(long appId, long userId, Boolean bool) throws IOException, RemoteException;

	public void addUserToAppointment(long appId, long userId) throws IOException, RemoteException;
	
	public void deleteUserFromAppointment(long appId, long msgId) throws IOException, RemoteException;
	
	public void updateRoomName(long appId, String roomName) throws IOException, RemoteException;
	
	public void deleteParticipants(long appId, Map<Person, Boolean> participants) throws IOException, RemoteException;
	
	public long getUniqueId() throws IOException, RemoteException;
	
	public List<Appointment> getAllByUser(long userId) throws IOException, RemoteException;
	
	public List<Appointment> getAllInvited(long userId) throws IOException, RemoteException;
	
	public List<Appointment> getAllCreated(long userId, int weekNum) throws IOException, RemoteException;
	
	public List<Appointment> getAllInvitedInWeek(long userId, int weekNum) throws IOException, RemoteException;
	
	public List<Appointment> getAllUnansweredInvites(long userId) throws IOException, RemoteException;
	
	public List<Appointment> getWeekAppointments(long userId, int weekNum) throws IOException, RemoteException;
	
	public Map<Long, Boolean> getParticipants(long appId) throws IOException, RemoteException;
	
	public Appointment getAppointment(long appId) throws IOException, RemoteException;
	


}
