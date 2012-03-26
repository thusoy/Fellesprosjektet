package server;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.DBAccess;
import rmi.RmiStarter;

public class DBStarter extends RmiStarter {

	public DBStarter() {
		super(AppointmentHandler.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
            DBAccess engine = new AppointmentHandler();
            DBAccess engineStub = (DBAccess) UnicastRemoteObject.exportObject(engine, 0);
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("RMI server address: " + addr.getHostAddress());
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(AppointmentHandler.SERVICE_NAME, engineStub);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new DBStarter();
	}

}
