/*
 * Created on Oct 27, 2004
 *
 */
package no.ntnu.fp.net;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;

/**
 * Simplest possible test application, client part.
 *
 * @author seb, steinjak
 */
public class TestCoClient {

  /**
   * Empty.
   */
  public TestCoClient() {
  }

  /**
   * Program Entry Point.
   */
  public static void main (String args[]){

    // Set up log
    Log.setLogName("Client");

    // Connection object listening on 4001
    Connection conn = new ConnectionImpl(4001);
    InetAddress addr;  // will hold address of host to connect to
    try {
      // get address of local host and connect
      addr = InetAddress.getLocalHost();
      conn.connect(addr, 5555);
      // send two messages to server
      String firstMsg = "Client: Hello Server! Are you there?";
      conn.send(firstMsg);
      String got = conn.receive();
      assertEquals("Skal ha fått det samme!", "Got this: " + firstMsg, got);
      String secMsg = "Client: Hi again!";
      conn.send(secMsg);
      String got2 = conn.receive();
      assertEquals("Skal ha fått det samme!", "Got this: " + secMsg, got2);
      
      // write a message in the log and close the connection
      Log.writeToLog("Client is now closing the connection!",
		     "TestApplication");
      conn.close();
    }

    catch (ConnectException e){
      Log.writeToLog(e.getMessage(),"TestApplication");
      e.printStackTrace();
    }
    catch (UnknownHostException e){
      Log.writeToLog(e.getMessage(),"TestApplication");
      e.printStackTrace();
    }
    catch (IOException e){
      Log.writeToLog(e.getMessage(),"TestApplication");
      e.printStackTrace();
    }

    System.out.println("CLIENT TEST FINISHED");
    Log.writeToLog("CLIENT TEST FINISHED","TestApplication");
  }

}
