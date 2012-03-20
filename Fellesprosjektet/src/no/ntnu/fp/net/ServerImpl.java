package no.ntnu.fp.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.fp.net.co.ConnectionImpl;

public class ServerImpl extends ConnectionImpl implements Runnable {
	private int myPort;
	private static List<String> input;

	public ServerImpl(int myPort) {
		super(myPort);
		this.myPort = myPort;
		input = new ArrayList<String>();
	}
	
	public void start() throws SocketTimeoutException, IOException{
		while(true){
			ConnectionImpl newserver = new ConnectionImpl(myPort);
			newserver.accept();
			ServerThread newthread = new ServerThread(newserver);
			newthread.start();
			usedPorts.put(myPort, true);
			myPort++;
		}
	}
	
	public static String getLatestInput(){
		return input.get(input.size()-1);
	}
	
	private static void addInput(String incoming){
		input.add(incoming);
	}

	class ServerThread extends Thread {
		private ConnectionImpl server;
		
		protected ServerThread(ConnectionImpl server){
			this.server = server;
		}
		
		public void run(){
			while(true){
				try {
					String payload = server.receive();
					System.out.println("Server recieved data: " + payload);
					ServerImpl.addInput(payload);
				} catch (ConnectException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void run() {
		try {
			start();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
