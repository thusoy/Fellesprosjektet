/*
 * Created on 02.feb.2004
 *
 */
package no.ntnu.fp.net.co;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.admin.Settings;

/**
 * @author Geir Vevle
 * 
 * This is a Chat srver made only as a experiment.
 */
public class ChatServer extends JFrame {

	private static final long serialVersionUID = 8713617956336119490L;

	public static boolean SIMPLE_CONNECTION = false;

    private Connection server;

    private ArrayList<User> users;

    private int listenPort = 4444;

    private JList userlist = new JList();

    private static boolean debug = true;

    //Lagrer info om hver og en bruker
    private class User {
        public String name;
        private ReceiveThread receiveThread;
        public Connection conn;

        public User(String name, Connection conn) {
            this.name = name;
            this.conn = conn;
            receiveThread = new ReceiveThread();
            receiveThread.start();
        }
        
        @Override
        public String toString(){
        	return name;
        }

        private class ReceiveThread extends Thread {

            public void run() {
                while (true) {
                    try {
                    	System.out.println("SERVER ACCEPTING MESSAGES!");
                    	try {
                    		String msg = User.this.conn.receive();
                    		System.out.println("GOT MESSAGE: " + msg);
                    		User.this.recieve(msg);
                    		System.out.println("COMPLETED MESSAGE HANDLING!");
                    	} catch (Exception e){
                    		System.out.println("GOT EXCEPTION!");
                    		e.printStackTrace();
                    		throw e;
                    	}
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    } catch (EOFException e) {
					    DBG("User.run(): Disconnect was requested.");
					    try { conn.close(); }
					    catch (IOException ioe) {
				    	    System.err.println("Chat server: IOException while" +
							   "closing connection: " +
							   ioe.getMessage());
					    }
					    if (!ChatServer.this.users.remove(User.this)){
					    	DBG("User.run(): Unable to remove 'this' from list " +
						    "of users - expect errors!");
					    }
					    ChatServer.this.broadcast("***: " + User.this.name + " disconnected.");
			            ChatServer.this.
						broadcast(ChatServer.this.getUserNames().toString());
			            break;
                    } catch (IOException e) {
                    	DBG("User.run(): Error: " + e.getMessage());
                    } catch (Exception e){
                    }
                }
            }
        }

        private void recieve(String mess) {
        	System.out.println("IN RECEIVE: " + mess);
             if (mess.equals(name + " is closing")) {

                receiveThread = null;
                try {
                    conn.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                ChatServer.this.users.remove(this);
                String users = ChatServer.this.getUserNames().toString();
                ChatServer.this.broadcast(users);
                try {
                    this.finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
			} else if (mess.substring(0, 1).equals("/")) {
	                if (mess.substring(1, 9).equals("newName:")) {
	                    String oldName = name;
	                    name = mess.substring(10, mess.length());
	                    ChatServer.this.broadcast(ChatServer.this.getUserNames()
	                            .toString());
	                    ChatServer.this.broadcast("**: " + oldName + " changed nick to " + name + ".");
	                }
            } else if (mess.length() > 0){
            	System.out.println("BROADCASTING MESSAGE FROM USER!");
                ChatServer.this.broadcast(mess);
            }
        }

        private void send(String mess) {
            try {
                conn.send(mess);
            } catch (ConnectException e) {
	      DBG("User.send(): ConnectException: '" + e.getMessage() +
		  "' while sending message '" + mess + "'");
	    } catch (EOFException exp) {
	      DBG("User.send(): Disconnect requested.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void broadcast(String mess) {
        for (User element: users) {
            element.send(mess);
        }
    }

    public ChatServer(int port) {
        listenPort = port;
        setTitle("Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 300);
        getContentPane().add(userlist);
        setVisible(true);
    }

    public void startServer() {
        users = new ArrayList<User>();

        if (SIMPLE_CONNECTION){
            server = new SimpleConnection(listenPort);
        } else { 
            server = new ConnectionImpl(listenPort);
        }
        Thread listener = new Thread() {

            private Connection newConn;
            private String message = "";

            public void run() {
                while (true) {
                    try {
                        DBG("Server lytter på:" + listenPort);
                        newConn = server.accept();
                        message = newConn.receive();

                        if (message.substring(0, 6).equals("Hello:")) {
                            User newUser;
                            DBG("Fikk inn connection fra: "
                                    + message.substring(6, message.length()));
                            users.add(newUser = new User(message.substring(6,
                                    message.length()), newConn));
                            broadcast("*: " + newUser + " joined.");
                            broadcast(getUserNames().toString());
                        }
                    } catch (SocketTimeoutException e) {
					      DBG("startServer(): Noe gikk galt, forsøk igjen.");
					      e.printStackTrace();
                    } catch (IOException e) {
					      DBG("startServer(): Noe gikk galt, forsøk igjen.");
					      e.printStackTrace();
                    }
                }
            }
        };
        listener.start();
    }

    private ArrayList<String> getUserNames() {
        ArrayList<String> userList = new ArrayList<String>();
        for (int i = 0; i < users.size(); i++) {
            userList.add(((User) users.get(i)).name);
        }
        userlist.setListData(userList.toArray());
        return userList;
    }

    public static void main(String[] args) {
        int port;
        Log.setLogName("Server");
        Settings settings = new Settings();
        port = settings.getServerPort();
        SIMPLE_CONNECTION = settings.useSimpleConnection();
        if (SIMPLE_CONNECTION){
            DBG("Using SimpleConnection");
        }
        ChatServer server = new ChatServer(port);
        server.startServer();
    }

  /** Write debug message to stdout. */
  private static void DBG(String msg) {
    if (debug)
      System.out.println("ChatServer: " + msg);
  }
}
