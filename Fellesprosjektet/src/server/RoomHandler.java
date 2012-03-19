package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import calendar.Room;

public class RoomHandler {
	
	public static void createRoom(Room room) throws IOException {
		String name = room.getName();
		int capacity = room.getCapacity();
		
		String query =
				"INSERT INTO Room(name, capacity) VALUES('%s', %d)";
		try {
			Execute.executeUpdate(String.format(query, name, capacity));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQLFeil");
		}
	}
		
	public static List<Room> getAllRooms() throws IOException{
		
		String queryGetAllRooms =
				"SELECT name FROM Room";
		
		List<Room> roomList = new ArrayList<Room>();
		List<String> RoomNameList;
		try {
			RoomNameList = Execute.executeGetStringList(queryGetAllRooms);
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
		for (String roomName : RoomNameList) {
			String queryGetCapacity =
					"Select capacity FROM Room WHERE name='%s'";
			int capacity;
			try {
				capacity = Execute.executeGetInt(String.format(queryGetCapacity, roomName));
			} catch (SQLException e) {
				throw new RuntimeException("SQLFeil");
			}
			Room room = new Room(roomName, capacity, true);
			
			roomList.add(room);
		}
	return roomList;	 
	}
	public static Room getRoom(String name) throws IOException {
		String query =
				"Select capacity FROM Room WHERE name='%s'";
		int capacity;
		try {
			capacity = Execute.executeGetInt(String.format(query, name));
		} catch (SQLException e) {
			throw new RuntimeException("SQLFeil");
		}
		Room room = new Room(name, capacity, true);
		return room;
	}
	
	public static boolean isValid(Date startCandidate, Date endCandidate, int capacity, String name) throws IOException {
		for (Room rom: RoomHandler.getAllRooms()) {
			if (rom.getName().equals(name))
				if (capacity > rom.getCapacity()) {
					return false;
				}
				for (Date start: rom.getIsOccupied().keySet()) {
					Date end = rom.getIsOccupied().get(start);
					if (!(startCandidate.before(start) || startCandidate.after(end)) || 
							!(endCandidate.before(start) || endCandidate.after(end))) {
						return false;
					}			
				}	
		}			
		return true;
	}
	public static List<Room> availableRooms(Date start, Date end, int capacity) throws IOException {
		List<Room> rooms = new ArrayList<Room>();
		for(Room room: RoomHandler.getAllRooms()) {
			if (RoomHandler.isValid(start, end, capacity, room.getName())) {
				rooms.add(room);
			}
		}
		
		return rooms;
	}
}
