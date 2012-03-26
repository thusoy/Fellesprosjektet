package server;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import calendar.Room;
import client.helpers.StoopidSQLException;

public class RoomHandler extends Handler{
	
	public static void createRoom(Room room) throws IOException {
		String name = room.getName();
		int capacity = room.getCapacity();
		
		String query =
				"INSERT INTO Room(name, capacity) VALUES('%s', %d)";
		dbEngine.update(String.format(query, name, capacity));
	}
	
	public static Room getRoom(String name) throws IOException {
		String query = "Select capacity FROM Room WHERE name=?";
		int capacity = dbEngine.getInt(query, name);
		Room room = Room.recreateRoom(name, capacity);
		return room;
	}
	
	public static List<Room> availableRooms(Date start, Date end, int capacity) throws IOException {
		String query = "SELECT name, capacity FROM Room WHERE name NOT IN " + 
					"(SELECT roomName FROM Appointment WHERE appId NOT IN " + 
						"(SELECT appId FROM Appointment WHERE endTime < ? OR startTime > ?) AND " +
					"roomName IS NOT NULL) AND capacity >= ? ORDER BY capacity";
		PreparedStatement ps = dbEngine.getPreparedStatement(query);
		try {
			ps.setTimestamp(1, new Timestamp(start.getTime()));
			ps.setTimestamp(2, new Timestamp(end.getTime()));
			ps.setInt(3, capacity);
			return getRoomsFromPreparedStatement(ps);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	private static List<Room> getRoomsFromPreparedStatement(PreparedStatement ps){
		List<Room> rooms = new ArrayList<Room>();
		try {
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				String name = rs.getString("name");
				int capacity = rs.getInt("capacity");
				Room room = Room.recreateRoom(name, capacity);
				rooms.add(room);
			}
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
		return rooms;
	}
}
