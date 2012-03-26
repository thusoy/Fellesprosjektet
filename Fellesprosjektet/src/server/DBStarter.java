package server;

import java.net.InetAddress;
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
            AppointmentHandler appEngine= new AppointmentHandlerImpl();
            AppointmentHandler appEngineStub = (AppointmentHandler) UnicastRemoteObject.exportObject(appEngine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(AppointmentHandler.SERVICE_NAME, appEngineStub);
            Appointment.bindToHandler();
            
            PersonHandler personEngine = new PersonHandler();
            PersonHandler personEngineStub = (PersonHandler) UnicastRemoteObject.exportObject(personEngine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(PersonHandler.SERVICE_NAME, personEngineStub);
            Person.bindToHandler();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new DBStarter();
		InetAddress addr = InetAddress.getLocalHost();
		System.out.println("RMI server running on: " + addr.getHostAddress());
	}

}
