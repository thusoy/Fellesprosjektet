package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

import calendar.Room;

public interface RoomHandler extends Remote{
	
	public static final String SERVICE_NAME = "ROOM_HANDLER";
	
	public void createRoom(Room room) throws IOException, RemoteException;
	
	public Room getRoom(String name) throws IOException, RemoteException;
	
	public List<Room> availableRooms(Date start, Date end, int capacity) throws IOException, RemoteException;

}
