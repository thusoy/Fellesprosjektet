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
import java.util.Timer;

import no.ntnu.fp.net.admin.Log;
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
 * @author Sebjørn Birkeland and Stein Jakob Nordbø
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImpl extends AbstractConnection {

    /**
     * Initialise sequence number and setup state machine.
     * 
     * @param myPort
     *            - the local port to associate with this connection
     */
    public ConnectionImpl(int myPort) {
    	this.myAddress = getIPv4Address();
        this.myPort = myPort;
        System.out.println("Ny forbindelse kjører nå på " + this.myAddress);
    }

    /**
     * Get the IPv4-address of this machine, or localhost.
     * @return
     */
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
     * @throws IOException
     *             If there's an I/O error.
     * @throws java.net.SocketTimeoutException
     *             If timeout expires before connection is completed.
     * @see Connection#connect(InetAddress, int)
     */
    public void connect(InetAddress remoteAddress, int remotePort) throws IOException,
            SocketTimeoutException {
    	this.remoteAddress = remoteAddress.getHostAddress();
    	this.remotePort = remotePort;
    	KtnDatagram syn = constructInternalPacket(Flag.SYN);
    	this.state = State.SYN_SENT;
    	KtnDatagram synAck = sendUntilAcked(syn);
    	sendAck(synAck);
    	this.state = State.ESTABLISHED;
    }
    
    /**
     * Tries to send the packet until a correct ack is received or the 
     * connection can be considered terminated.
     * @param packetToSend
     * @throws EOFException
     * @throws IOException
     */
    private KtnDatagram sendUntilAcked(KtnDatagram packetToSend) throws EOFException, IOException{
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new SendTimer(new ClSocket(), packetToSend), 0, RETRANSMIT);
    	KtnDatagram response;
    	int tries = 5;
    	while(tries > 0){
    		response = receiveAck();
    		if(isValid(response)){
    			if(response.getAck() == packetToSend.getSeq_nr()){
    				timer.cancel();
    				return response;
    			}
    		}
    	}
    	timer.cancel();
    	throw new IOException("Couldn't receive response from server!");
    }

	/**
     * Listen for, and accept, incoming connections.
     * 
     * @return A new ConnectionImpl-object representing the new connection.
     * @see Connection#accept()
     */
    public Connection accept() throws IOException, SocketTimeoutException {
    	this.state = State.LISTEN;
    	KtnDatagram packet = receiveValidInternalPacket();
    	this.remoteAddress = packet.getSrc_addr();
    	this.remotePort = packet.getSrc_port();
    	Log.writeToLog(packet, "Packet received!", "FroM!");
    	this.state = State.ESTABLISHED;
		sendSynAck(packet);
		return this;
    }
    
    /**
     * Receive a valid non-internal packet.
     * 
     * @throws EOFException
     * @throws IOException
     */
    private KtnDatagram receiveValidPacket() throws EOFException, IOException{
    	return receiveHelper(false);
    }
    
    /**
     * Receive a valid internal packet.
     * @throws EOFException
     * @throws IOException
     */
    private KtnDatagram receiveValidInternalPacket() throws EOFException, IOException{
    	return receiveHelper(true);
    }
    
    /**
     * Helps receive packets and performs validation according to wheter the
     * received packet should be internal or not.
     * @param internal
     * @return
     * @throws EOFException
     * @throws IOException
     */
    private KtnDatagram receiveHelper(boolean internal) throws EOFException, IOException{
    	KtnDatagram packet;
    	int tries = 5;
    	while(tries > 0) {
    		packet = receivePacket(internal);
    		if (isValid(packet)){
    			if (internal){
    				if (packet.getFlag() != Flag.NONE){
    					return packet;
    				}
    			} else {
    				if (packet.getFlag() == null || packet.getFlag() == Flag.NONE){
    					return packet;
    				}
    			}
    		}
    	}
    	throw new IOException("Cant receive correct data from server!");
    }
    
    /**
     * Send ack for the packet.
     */
    private void sendAck(KtnDatagram packet) throws ConnectException, IOException{
    	sendAck(packet, false);
    }
    
    /**
     * Send SynAck for the packet.
     */
    private void sendSynAck(KtnDatagram packet) throws ConnectException, IOException{
    	sendAck(packet, true);
    }
    	
    /**
     * Send a message to over the established connection.
     * 
     * @param msg
     *            - the String to be sent.
     * @throws ConnectException
     *             If no connection exists.
     * @throws IOException
     *             If no ACK was received in 5 tries or wrong ACK was received.
     * @see AbstractConnection#sendDataPacketWithRetransmit(KtnDatagram)
     * @see no.ntnu.fp.net.co.Connection#send(String)
     */
    public void send(String msg) throws ConnectException, IOException {
        if (state != State.ESTABLISHED){
        	throw new IllegalStateException("Forbindelsen må være åpnet for å kunne sende! Var i " + state);
        }
        KtnDatagram packet = constructDataPacket(msg);
        KtnDatagram ack;
        int tries = 5;
        while(tries > 0){
        	ack = sendDataPacketWithRetransmit(packet);
        	tries--;
        	if(isValid(ack)){
        		break;
        	}
        }
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
        	KtnDatagram packet = receiveValidPacket();
    		sendAck(packet);
    		String message = (String) packet.getPayload();
			System.out.println("RECEIVED DATA IN RECEIVE: " + message);
			System.out.println("packet flag: " + packet.getFlag());
			return message;
        } catch (EOFException e){
        	System.out.println("GOT DISCONNECT!");
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
    		clientClose();
    	} else {
    		serverClose();
    	}
    }
    
    /**
     * Close the connection as a client.
     */
    private void clientClose() throws EOFException, IOException{
    	this.state = State.FIN_WAIT_1;
    	KtnDatagram fin = constructInternalPacket(Flag.FIN);
    	sendUntilAcked(fin);
		this.state = State.FIN_WAIT_2;
		receiveAndAckFin();
		this.state = State.TIME_WAIT;
		closeWait();
		this.state = State.CLOSED;
    }
    
    /**
     * Receive a FIN-packet and ack it.
     * @throws IOException
     */
    private void receiveAndAckFin() throws IOException{
    	KtnDatagram packet = receiveValidInternalPacket();
    	if (packet.getFlag() == Flag.FIN){
    		sendAck(packet);
    		return;
    	}
    	receiveAndAckFin();
    }
    
    /**
     * Close the connection as a server.
     * @throws EOFException
     * @throws IOException
     */
    private void serverClose() throws EOFException, IOException{
    	this.state = State.LAST_ACK;
    	KtnDatagram fin = constructInternalPacket(Flag.FIN);
    	sendUntilAcked(fin);
		this.state = State.CLOSED;
    }
    
    /**
     * Wait for 3s before continuing.
     */
    private void closeWait(){
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    }
    
    /**
     * Test a packet for transmission errors. This function should only called
     * with data or ACK packets in the ESTABLISHED state.
     * 
     * Sjekker for både bitfeil, og hvorvidt pakken faktisk er ment for oss,
     * og at det er den pakken vi venter på.
     * 
     * @param packet
     *            Packet to test.
     * @return true if packet is free of errors, false otherwise.
     */
    protected boolean isValid(KtnDatagram packet) {
    	System.out.println("**************** TESTING VALIDITY ******************");
    	if(packet == null){
    		System.out.println("NULL PACKET!");
    		return false;
    	}
    	int ackNr = packet.getAck();
    	if(state == State.ESTABLISHED && ackNr != -1 && ackNr != lastDataPacketSent.getSeq_nr()){
    		System.out.println("MOTTOK FEIL ACK!");
    		return false;
    	}
        if(packet.getChecksum() != packet.calculateChecksum()){
        	System.out.println("************** FANT BITFEIL ******************");
        	return false;
        }
        if(!myAddress.equals(packet.getDest_addr())){
        	System.out.println("************** FANT GHOST *********************");
        	System.out.println("FORVENTET: " + myAddress);
        	System.out.println("MOTTOK TIL: " + packet.getDest_addr());
        	return false;
        }
        KtnDatagram lastPacket = lastValidPacketReceived;
        if(lastPacket != null && lastPacket.getSeq_nr() + 1 != packet.getSeq_nr()){
        	System.out.println("************** FEIL PAKKE ********************");
        	return false;
        }
        System.out.println("VALID PACKET!");
        return true;
    }
}
