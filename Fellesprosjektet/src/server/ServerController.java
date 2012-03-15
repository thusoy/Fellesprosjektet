package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import calendarprotocol.ServerState;

public class ServerController extends Thread {
	
	public static final int port = 1337;

	public void run(){
		try {
			initSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void initSocket() throws IOException{
		ServerSocket serverSocket = setUpServerSocket();
		Socket clientSocket = setUpClientSocket(serverSocket);
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		CalendarProtocol protocol = new CalendarProtocol();
		
		String input = null;
		String response = protocol.getResponse(null);
		System.out.println("Server first line: " + response);
		out.println(response);
		
		System.out.println("Server waiting for answer...");
		while (true){
			System.out.println("got message: " + in.readLine());
		}
//		while ( (input = in.readLine()) != null){
//			response = protocol.getResponse(input);
//			out.println(response);
//			System.out.println("Server state: " + protocol.getState());
//			if (protocol.getState() == ServerState.STOP){
//				System.out.println("Server breaking out");
//				break;
//			}
//		}
//		out.close();
//		in.close();
//		clientSocket.close();
//		serverSocket.close();
//		System.out.println("Server session ended.");
	}
	
	private static ServerSocket setUpServerSocket(){
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e){
			System.err.println(String.format("Could not listen on port %d!", port));
			System.exit(1);
		}
		System.out.printf("Server listening on port %d at %s\n", port, serverSocket.getInetAddress());
		return serverSocket;
	}
	
	private static Socket setUpClientSocket(ServerSocket serverSocket){
		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e){
			System.err.println("Failed to accept connection to client!");
			System.exit(1);
		}
		return clientSocket;
	}

}
