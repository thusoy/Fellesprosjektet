package server;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.RmiStarter;
import calendar.Appointment;

public class DBStarter extends RmiStarter {

	public DBStarter() {
		super(AppointmentHandlerImpl.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			LocateRegistry.createRegistry(1099);
            AppointmentHandler engine = new AppointmentHandlerImpl();
            AppointmentHandler engineStub = (AppointmentHandler) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(AppointmentHandler.SERVICE_NAME, engineStub);
            Appointment.bindToHandler();
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("RMI server running on: " + addr.getHostAddress());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new DBStarter();
	}

}
