package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.RmiStarter;
import calendar.Appointment;
import calendar.Person;

public class DBStarter extends RmiStarter {

	public DBStarter() {
		super(AppointmentHandlerImpl.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			LocateRegistry.createRegistry(1099);
			Registry registry = LocateRegistry.getRegistry();
            AppointmentHandler appEngine= new AppointmentHandlerImpl();
            AppointmentHandler appEngineStub = (AppointmentHandler) UnicastRemoteObject.exportObject(appEngine, 0);
            registry.rebind(AppointmentHandler.SERVICE_NAME, appEngineStub);
            AppointmentHandlerImpl.init();
            Appointment.bindToHandler();
            
            PersonHandler personEngine = new PersonHandlerImpl();
            PersonHandler personEngineStub = (PersonHandler) UnicastRemoteObject.exportObject(personEngine, 0);
            registry.rebind(PersonHandler.SERVICE_NAME, personEngineStub);
            PersonHandlerImpl.init();
            Person.bindToHandler();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new DBStarter();
		try {
			InetAddress addr = InetAddress.getLocalHost();
			System.out.println("RMI server running on: " + addr.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
