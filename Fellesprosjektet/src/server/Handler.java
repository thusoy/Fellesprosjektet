package server;

import java.io.IOException;

import rmi.DBHandler;

public abstract class Handler {
	protected static DBHandler dbEngine;
	
	static {
		try {
			dbEngine = new ExecutionEngine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
