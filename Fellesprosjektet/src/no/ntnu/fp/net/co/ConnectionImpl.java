/*
 * Created on Oct 27, 2004
 */
package no.ntnu.fp.net.co;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.cl.ClException;
import no.ntnu.fp.net.cl.ClSocket;
import no.ntnu.fp.net.cl.KtnDatagram;
import no.ntnu.fp.net.cl.KtnDatagram.Flag;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Implementation of the Connection-interface. <br>
 * <br>
 * This class implements the behaviour in the methods specified in the interface
 * {@link Connection} over the unreliable, connectionless network realised in
 * {@link ClSocket}. The base class, {@link AbstractConnection} implements some
 * of the functionality, leaving message passing and error handling to this
 * implementation.
 * 
 * @author Sebjørn Birkeland and Stein Jakob Nordbø
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImpl extends AbstractConnection {

    /** Keeps track of the used ports for each server port. */
    private static Map<Integer, Boolean> usedPorts = Collections.synchronizedMap(new HashMap<Integer, Boolean>());

    /**
     * Initialise initial sequence number and setup state machine.
     * 
     * @param myPort
     *            - the local port to associate with this connection
     */
    public ConnectionImpl(int myPort) {
    	super();
    	this.myAddress = getIPv4Address();
        this.myPort = myPort;
    }

    private String getIPv4Address() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * Establish a connection to a remote location.
     * 
     * @param remoteAddress
     *            - the remote IP-address to connect to
     * @param remotePort
     *            - the remote portnumber to connect to
     * @throws IOException
     *             If there's an I/O error.
     * @throws java.net.SocketTimeoutException
     *             If timeout expires before connection is completed.
     * @throws  
     * @see Connection#connect(InetAddress, int)
     */
    public void connect(InetAddress remoteAddress, int remotePort) throws IOException,
            SocketTimeoutException {
    	ClSocket a2 = new ClSocket();
    	this.remoteAddress = remoteAddress.toString();
    	this.remotePort = remotePort;
    	KtnDatagram datagram = constructInternalPacket(Flag.SYN);
    	
    	try {
			a2.send(datagram);
		} catch (ClException e) {
			e.printStackTrace();
		}
    	this.state = State.SYN_SENT;
    	
    	KtnDatagram answer = receiveAck();
    	
    	KtnDatagram outgoing = constructInternalPacket(Flag.ACK);
    	sendAck(outgoing, false);
    	
    	
    	
    	
    	
    	this.state = State.ESTABLISHED;
    }

    /**
     * Listen for, and accept, incoming connections.
     * 
     * @return A new ConnectionImpl-object representing the new connection.
     * @see Connection#accept()
     */
    public Connection accept() throws IOException, SocketTimeoutException {
    	this.state = State.LISTEN;
    	KtnDatagram packet = receivePacket(true);
    	System.out.println(packet == null);
    	this.remoteAddress = packet.getSrc_addr();
    	this.remotePort = packet.getSrc_port();
//    	Log.writeToLog(packet, "Packet received!", "FroM!");
    	KtnDatagram outgoingPacket = constructInternalPacket(Flag.SYN_ACK);
    	outgoingPacket.setPayload("hellow!");
    	System.out.println("The packet were going to send: " + outgoingPacket.getDest_addr() + outgoingPacket.getDest_port() + outgoingPacket.getSeq_nr());
    	sendAck(outgoingPacket, true);
    	KtnDatagram incoming = receiveAck();
		if (isValid(incoming)){
			return this;
		} else {
			// gjør noe fornuftig
		}
    	return this;
    }
    	
    /**
     * Send a message from the application.
     * 
     * @param msg
     *            - the String to be sent.
     * @throws ConnectException
     *             If no connection exists.
     * @throws IOException
     *             If no ACK was received.
     * @see AbstractConnection#sendDataPacketWithRetransmit(KtnDatagram)
     * @see no.ntnu.fp.net.co.Connection#send(String)
     */
    public void send(String msg) throws ConnectException, IOException {
        throw new NotImplementedException();
    }

    /**
     * Wait for incoming data.
     * 
     * @return The received data's payload as a String.
     * @see Connection#receive()
     * @see AbstractConnection#receivePacket(boolean)
     * @see AbstractConnection#sendAck(KtnDatagram, boolean)
     */
    public String receive() throws ConnectException, IOException {
        throw new NotImplementedException();
    }

    /**
     * Close the connection.
     * 
     * @see Connection#close()
     */
    public void close() throws IOException {
        throw new NotImplementedException();
    }

    /**
     * Test a packet for transmission errors. This function should only called
     * with data or ACK packets in the ESTABLISHED state.
     * 
     * @param packet
     *            Packet to test.
     * @return true if packet is free of errors, false otherwise.
     */
    protected boolean isValid(KtnDatagram packet) {
        throw new NotImplementedException();
    }
}
