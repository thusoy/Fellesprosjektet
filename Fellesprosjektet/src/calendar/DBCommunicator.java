package calendar;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public abstract class DBCommunicator {
	private static Registry registry;
	
	static {
		try {
			registry = LocateRegistry.getRegistry("78.91.44.182");
		} catch (RemoteException e) {
			System.err.println("Klarte ikke finne RMI-registeret!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static Remote getHandler(String serviceName){
		try {
			return registry.lookup(serviceName);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		System.out.println("RMI Feil!");
		System.exit(1);
		return null;
	}
}
