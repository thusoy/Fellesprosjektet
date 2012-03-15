package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import server.CalendarProtocol;
import server.ServerController;
import calendarprotocol.ServerState;

public class ClientController extends Thread {

	public static final String hostAddress = "localhost";
	public static int serverPort;
	
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	public ClientController(){
		serverPort = ServerController.port;
	}
	
	public void run(){
		System.out.println("Client running..");
		setUpConnection();
		try {
			System.out.println("Connection established, listening...");
			connect();
		} catch (IOException e) {
			System.err.println("Something failed in the communication with the server!");
			e.printStackTrace();
		}
	}
	
	private void connect() throws IOException{
		CalendarProtocol protocol = new CalendarProtocol();
		String response = protocol.getResponse(null);
		String input;
		System.out.println("waiting for server opening..");
		out.println(response);
		String[] responses = {"hey", "zup", "orly", "omg", "kthxbye!"};
		int index = 0;
		out.flush();
		out.println(responses[index]);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			input = br.readLine();
			out.println(input);
		}
		
//		while( (input = in.readLine()) != null){
//			System.out.println("Got message from server: " + input);
//			response = protocol.getResponse(input);
//			System.out.println("My reponse: " + response);
//			out.println(responses[index]);
//			System.out.println("Client state: " + protocol.getState());
//			if (protocol.getState() == ServerState.STOP){
//				System.out.println("breaking out.");
//				break;
//			}
//			System.out.println("waiting for next line...");
//		}
//		System.out.println("Session done!");
	}
	
	private void setUpConnection(){

		try {
			InetAddress address = InetAddress.getByName(hostAddress);
			socket = new Socket(address, serverPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host!");
			System.exit(1);
		} catch (IOException e){
			System.err.println("Failed to connect to host!");
			System.exit(1);
		}
	}
	
}