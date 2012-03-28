package no.ntnu.fp.net;

import static org.junit.Assert.assertEquals;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;
import no.ntnu.fp.net.co.Settings;

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestConnectionImpl {
	private Server server;
	private Client client;

	@Test
	public void testConnection() throws SocketTimeoutException, IOException, SAXException {
		Settings.setErrors();
		for(int i = 0; i < 5; i++){
			assertEquals("Ny runde skal gå fint!", i, i);
			server = new Server();
			client = new Client();
			Thread serverThread = new Thread(server);
			serverThread.start();
			Thread clientThread = new Thread(client);
			clientThread.start();
			while(client.lastSent == null || server.lastReceived == null){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			assertEquals("Pakke sent skal være lik pakke mottatt!", client.lastSent, server.lastReceived);
			clientThread.stop();
			serverThread.stop();
		}
	}
	
	abstract class AbstractClient implements Runnable {
		protected Connection conn;
		public String lastReceived;
		public String lastSent;

		public AbstractClient(int port){
			conn = new ConnectionImpl(port);
		}
		
		public void stop() throws IOException{
			conn.close();
		}
		
		@Override
		public abstract void run();
		
	}
	
	class Server extends AbstractClient {
		
		public Server(){
			super(5555);
		}
		
		@Override
		public void run() {
			try {
				while(true){
					conn.accept();
					String msg = conn.receive();
					lastReceived = msg;
					System.out.println("Last received: " + lastReceived + " *******************************************************");
					assertEquals(client.lastSent, lastReceived);
				}
			} catch (EOFException e){
				try {
					conn.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class Client extends AbstractClient {
		
		public Client(){
			super(4001);
		}
		
		@Override
		public void run() {
			try {
				conn.connect(InetAddress.getLocalHost(), 5555);
				String first = "First hello!";
				lastSent = first;
				conn.send(first);
				conn.close();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
