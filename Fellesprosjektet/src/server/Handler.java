package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import rmi.DBHandler;

public abstract class Handler {
	protected static DBHandler dbEngine;
	
	static {
		try {
			Registry registry = LocateRegistry.getRegistry("129.241.126.61");
			dbEngine = (DBHandler) registry.lookup(DBHandler.SERVICE_NAME);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
}
