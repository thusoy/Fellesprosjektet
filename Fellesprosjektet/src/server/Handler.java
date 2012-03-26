package server;

import java.io.IOException;

import rmi.DBHandler;

public abstract class Handler {
	protected static DBHandler dbEngine;
	public static String SERVICE_NAME;
	
	public Handler(String serviceName){
		SERVICE_NAME = serviceName;
	}
	
	static {
		try {
			dbEngine = new ExecutionEngine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
