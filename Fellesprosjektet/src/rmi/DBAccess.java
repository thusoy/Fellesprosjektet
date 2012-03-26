package rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import calendar.Appointment;

public interface DBAccess extends Remote{
	public static final String SERCIVE_NAME = "DBEngine";
	
	public List<Appointment> rmiTest() throws RemoteException;
	
	public void createAppointment(Appointment app) throws RemoteException, IOException;

}
