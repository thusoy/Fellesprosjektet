package calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class Room extends DBObject<Room>{
	private String name;
	private int capacity;
	private LinkedHashMap<Date, Date> isOccupied;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public static List<Room> availableRooms(Date start, Date end, int capacity){
		List<Room> rooms = new ArrayList<Room>();
		for(Room room: Room.all()) {
			if (room.isValid(start, end, capacity)) {
				rooms.add(room);
			}
		}
		
		return rooms;
	}
	private boolean isValid(Date startCandidate, Date endCandidate, int capacity) {
		for (Date start : isOccupied.keySet()) {
			Date end = isOccupied.get(start);
			if (!(startCandidate.before(start) || startCandidate.after(end)) || 
					!(endCandidate.before(start) || endCandidate.after(end))) {
				return false;
			}			
		}		
		return true;
	}

}
