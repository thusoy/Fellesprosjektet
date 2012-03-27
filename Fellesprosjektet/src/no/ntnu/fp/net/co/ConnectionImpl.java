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
import java.util.zip.CRC32;

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
        System.out.println("Ny forbindelse kjører nå på " + this.myAddress);
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
    	isValid(answer);
    	
    	
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
    	KtnDatagram outgoingPacket = constructInternalPacket(Flag.SYN_ACK);
    	this.state = State.ESTABLISHED;
    	System.out.println("The packet we're going to send: " + outgoingPacket);
    	KtnDatagram incomingAck = sendDataPacketWithRetransmit(outgoingPacket);
//    	while( (incomingAck = receiveAck()) == null){
//    		try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//    		System.out.println("Trying to receive packet again...");
//    	}
		if (isValid(incomingAck)){
			sendSynAck(incomingAck);
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
        	throw new IllegalStateException("Forbindelsen må være åpnet for å kunne sende!");
        }
        KtnDatagram packet = constructDataPacket(msg);
        sendDataPacketWithRetransmit(packet);
        KtnDatagram ack = receiveAck();
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
        
    }

    /**
     * Close the connection.
     * 
     * @see Connection#close()
     */
    public void close() throws IOException {
    	KtnDatagram finDatagram = constructInternalPacket(Flag.FIN);
        
        KtnDatagram ack = sendDataPacketWithRetransmit(finDatagram);
        isValid(ack);
        this.state = State.FIN_WAIT_1;      
        KtnDatagram answer1 = receiveAck();
        this.state = State.FIN_WAIT_2;
        KtnDatagram answer2 = null;
        try {
        	answer2 = receiveAck();
        } catch (EOFException e) {
        	sendSynAck(answer2);
        }
        this.state = State.TIME_WAIT;
        
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
        long actualChecksum = calculateChecksum(packet);
        return actualChecksum == packetChecksum; 
    }

	private static long calculateChecksum(KtnDatagram packet) {
		CRC32 crc = new CRC32();
		System.out.println("calculated: " + packet.calculateChecksum());
		System.out.println("bytes: " + packet.getPayloadAsBytes());
		byte[] bytes = packet.getPayloadAsBytes();
		if (bytes != null){
			crc.update(packet.getPayloadAsBytes());
			return crc.getValue();
		} else {
			return packet.calculateChecksum();
		}
	}      
}
