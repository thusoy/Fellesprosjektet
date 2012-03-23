package server;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import calendar.Room;
import client.helpers.StoopidSQLException;

public class RoomHandler {
	
	public static void createRoom(Room room) throws IOException {
		String name = room.getName();
		int capacity = room.getCapacity();
		
		String query =
				"INSERT INTO Room(name, capacity) VALUES('%s', %d)";
		Execute.executeUpdate(String.format(query, name, capacity));
	}
	
	private static List<Room> getAllRooms() throws IOException{
		String query = "SELECT name, capacity FROM Room";
		List<Room> roomList = new ArrayList<Room>();
		PreparedStatement ps = Execute.getPreparedStatement(query);
		try {
			ResultSet rs = ps.executeQuery();
			String name = rs.getString("name");
			int capacity = rs.getInt("capacity");
			roomList.add(Room.recreateRoom(name, capacity));
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
		return roomList;
	}
	
	public static Room getRoom(String name) throws IOException {
		String query = "Select capacity FROM Room WHERE name=?";
		int capacity = Execute.getInt(query, name);
		Room room = Room.recreateRoom(name, capacity);
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
