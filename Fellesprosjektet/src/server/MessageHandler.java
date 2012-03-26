package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import calendar.Message;
import calendar.Person;

public interface MessageHandler extends Remote{
	
	public static final String SERVICE_NAME = "MESSAGE_HANDLER";
	
	public void createMessage(Message msg) throws IOException, RemoteException;
	
	public void deleteOldReceivers(long msgId) throws RemoteException, IOException;
	
	public void sendMessageToUser(long msgId, long userId) throws IOException, RemoteException;
	
	public void sendMessageToAllParticipants(Message msg) throws IOException, RemoteException;
	
	public void setMessageAsRead(long msgId, long userId) throws IOException, RemoteException;
	
	public Message getMessage(long msgId) throws IOException, RemoteException;
	
	public List<Person> getReceiversOfMessage(long msgId) throws IOException, RemoteException;
	
	public List<Message> getUnreadMessagesForUser(Person p) throws IOException, RemoteException;
	
	public boolean getHasBeenRead(long msgId, long userId) throws IOException, RemoteException;
	
	public void sendMessageAppointmentInvite(long appId) throws IOException, RemoteException;
	
	public void sendMessageUpdateInfo(long appId) throws IOException, RemoteException;
	
	public void sendMessageUserHasDenied(long appId, long userId) throws IOException, RemoteException;
	
	public long getUniqueId() throws IOException, RemoteException;
}
