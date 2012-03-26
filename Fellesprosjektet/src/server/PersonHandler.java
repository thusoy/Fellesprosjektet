package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import calendar.Appointment;
import calendar.Person;

public interface PersonHandler extends Remote{
	
	String SERVICE_NAME = "PERSON_HANDLER";

	public void createUser(Person person) throws IOException, RemoteException;
	
	public void deleteUser(long personId) throws IOException,RemoteException;
	
	public Person getPerson(long personId) throws IOException, RemoteException;
	
	public String getSalt(long userId) throws RemoteException, IOException;
	
	public String getSalt(String email) throws RemoteException, IOException;
	
	public void followOtherPerson(long userId, long otherUserId) throws RemoteException, IOException;
	
	public List<Appointment> getFollowAppointments(long userId, int weekNum) throws IOException, RemoteException;
	
	public List<Appointment> getFollowAppointments(long userId) throws IOException, RemoteException;

}
