//package no.ntnu.fp.net;
//
//import static org.junit.Assert.*;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.SocketTimeoutException;
//
//import no.ntnu.fp.net.co.ConnectionImpl;
//
//import org.junit.Test;
//
//public class TestConnectionImpl {
//	private static final int port = 1337;
//
////	@Test
//	public void testConnection() throws SocketTimeoutException, IOException {
//		System.out.println("Starting server...");
//		ServerImpl server = new ServerImpl(port);
//		System.out.println("Starting client...");
//		ConnectionImpl client = new ConnectionImpl(port);
//		System.out.println("Set server to LISTEN");
//		Thread serverThread = new Thread(new ServerImpl(port));
//		serverThread.start();
//		System.out.println("Connect client...");
//		client.connect(InetAddress.getByName("localhost"), port);
//		System.out.println("Client connected.");
//		client.close();
//		server.close();
//	}
//	
////	@Test
//	public void testSendAndRecieveData() throws SocketTimeoutException, IOException{
//		ServerImpl server = new ServerImpl(port);
//		ConnectionImpl client = new ConnectionImpl(port);
//		server.start();
//		client.connect(InetAddress.getByName("localhost"), port);
//		String sendMessage = "Dette er en personlig melding!";
//		client.send(sendMessage);
//		String input = ServerImpl.getLatestInput();
//		assertEquals("Server skal motta korrekte data!", sendMessage, input);
//		client.close();
//		server.close();
//	}
//}
