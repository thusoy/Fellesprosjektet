/*
 * Created on Oct 27, 2004
 */
package no.ntnu.fp.net.co;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.cl.ClException;
import no.ntnu.fp.net.cl.ClSocket;
import no.ntnu.fp.net.cl.KtnDatagram;
import no.ntnu.fp.net.cl.KtnDatagram.Flag;

/**
 * Implementation of the Connection-interface. <br>
 * <br>
 * This class implements the behaviour in the methods specified in the interface
 * {@link Connection} over the unreliable, connectionless network realised in
 * {@link ClSocket}. The base class, {@link AbstractConnection} implements some
 * of the functionality, leaving message passing and error handling to this
 * implementation.
 * 
 * @author Sebj�rn Birkeland and Stein Jakob Nordb�
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImpl extends AbstractConnection {

    /** Keeps track of the used ports for each server port. */
    protected static Map<Integer, Boolean> usedPorts = Collections.synchronizedMap(new HashMap<Integer, Boolean>());

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
        System.out.println("Ny forbindelse kj�rer n� p� " + this.myAddress);
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
    	this.remoteAddress = remoteAddress.getHostAddress();
    	this.remotePort = remotePort;
    	KtnDatagram datagram = constructInternalPacket(Flag.SYN);
    	
    	try {
			a2.send(datagram);
		} catch (ClException e) {
			e.printStackTrace();
		}
    	this.state = State.SYN_SENT;
    	
    	KtnDatagram answer;
    	while ( (answer = receiveAck()) == null){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		System.out.println("Still waiting for ack...");
    	}
    	System.out.println("answer: " + answer);

    	sendAck(answer);
    	
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
    	KtnDatagram packet;
    	while( (packet = receivePacket(true)) == null){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	this.remoteAddress = packet.getSrc_addr();
    	this.remotePort = packet.getSrc_port();
    	Log.writeToLog(packet, "Packet received!", "FroM!");
    	this.state = State.ESTABLISHED;
//    	while( (incomingAck = receiveAck()) == null){
//    		try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//    		System.out.println("Trying to receive packet again...");
//    	}
		if (isValid(packet)){
			sendSynAck(packet);
			return this;
		} else {
			System.out.println("invalid packet!");
		}
    	return this;
    }
    
    private void sendAck(KtnDatagram packet) throws ConnectException, IOException{
    	sendAck(packet, false);
    }
    
    private void sendSynAck(KtnDatagram packet) throws ConnectException, IOException{
    	sendAck(packet, true);
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
        if (state != State.ESTABLISHED){
        	throw new IllegalStateException("Forbindelsen m� v�re �pnet for � kunne sende!");
        }
        KtnDatagram packet = constructDataPacket(msg);
        sendDataPacketWithRetransmit(packet);
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
        try{
        	KtnDatagram packet = receivePacket(false);
        	System.out.println("Server got packet: " + packet);
        	System.out.println("packet flag: " + packet.getFlag());
        	if (isValid(packet)){
        		System.out.println("Valid packet received!");
        		sendAck(packet);
        	} else {
        		System.out.println("invalid packet received!");
        	}
        	
        	return packet.getPayloadAsBytes().toString();
        } catch (EOFException e){
        	KtnDatagram fin = disconnectRequest;
        	sendAck(fin);
        	this.state = State.CLOSE_WAIT;
        	throw e;
        }
    }

	/**
     * Close the connection.
     * 
     * @see Connection#close()
     */
    public void close() throws IOException {
    	boolean client = state == State.ESTABLISHED;
    	if (client){
    		KtnDatagram packet = sendFinAndReturnAck(client);
    		this.state = State.FIN_WAIT_2;
    		System.out.println("RUNNING RECEIVE FIN!");
    		KtnDatagram fin = receiveFin();
    		sendAck(fin);
    		System.out.println("FINACK SENT!");
    		this.state = State.TIME_WAIT;
    		try {
    			Thread.sleep(3000);
    		} catch (InterruptedException e1) {
    			e1.printStackTrace();
    		}
    		this.state = State.CLOSED;
    	} else {
    		KtnDatagram ack = sendFinAndReturnAck(client);
    		this.state = State.CLOSED;
    	}
    }
    
    private KtnDatagram receiveFin() throws IOException{
    	KtnDatagram packet;
		while(true){
			packet = receivePacket(true);
			if (packet.getFlag() == Flag.FIN){
				return packet;
			}
		}
    }
    
    private KtnDatagram sendFinAndReturnAck(boolean isClient) throws EOFException, IOException{
    	KtnDatagram finDatagram = constructInternalPacket(Flag.FIN);
    	
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new SendTimer(new ClSocket(), finDatagram), 0, RETRANSMIT);
    	if (isClient){
    		this.state = State.FIN_WAIT_1;
    	} else {
    		this.state = State.LAST_ACK;
    	}
    	KtnDatagram ack = receiveAck();
    	timer.cancel();
    	
    	return ack;
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
        long packetChecksum = packet.getChecksum();
        long actualChecksum = packet.calculateChecksum();
        return actualChecksum == packetChecksum; 
    }

}
