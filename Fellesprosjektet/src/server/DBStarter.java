package server;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.DBHandler;
import rmi.RmiStarter;

public class DBStarter extends RmiStarter {

	public DBStarter() {
		super(AppointmentHandler.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
            DBHandler engine = new ExecutionEngine();
            DBHandler engineStub = (DBHandler) UnicastRemoteObject.exportObject(engine, 0);
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(DBHandler.SERVICE_NAME, engineStub);
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
