package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import calendar.Room;

public class RoomHandler {
		
	public List<Room> getAllRooms() throws ClassNotFoundException, IOException, SQLException{
		
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
}
