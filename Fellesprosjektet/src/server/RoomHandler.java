package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import calendar.Room;

public class RoomHandler {
		
	public static List<Room> getAllRooms() throws ClassNotFoundException, IOException, SQLException{
		
		String queryGetAllRooms =
				"SELECT name FROM Room";
		
		List<Room> roomList = new ArrayList<Room>();
		List<String> RoomNameList= Execute.executeGetStringList(queryGetAllRooms);
		for (String roomName : RoomNameList) {
			Room room = new Room();
			room.setName(roomName);
			
			String queryGetCapacity =
					"Select capacity FROM Room";
			int capacity = Execute.executeGetInt(queryGetCapacity);
			
			room.setCapacity(capacity);
			roomList.add(room);
		}
	return roomList;	 
	}
	
	public static boolean isValid(Date startCandidate, Date endCandidate, int capacity, Room room) {
		for (Date start: room.getIsOccupied().keySet()) {
			Date end = room.getIsOccupied().get(start);
			if (!(startCandidate.before(start) || startCandidate.after(end)) || 
					!(endCandidate.before(start) || endCandidate.after(end))) {
				return false;
			}			
		}		
		return true;
	}
	public static List<Room> availableRooms(Date start, Date end, int capacity) throws ClassNotFoundException, IOException, SQLException{
		List<Room> rooms = new ArrayList<Room>();
		for(Room room: RoomHandler.getAllRooms()) {
			if (RoomHandler.isValid(start, end, capacity, room)) {
				rooms.add(room);
			}
		}
		
		return rooms;
	}
}
