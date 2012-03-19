package calendar;

import java.io.IOException;
import java.sql.Date;
import java.util.LinkedHashMap;

import server.RoomHandler;

public class Room extends DBObject<Room>{
	private String name;
	private int capacity;
	private LinkedHashMap<Date, Date> isOccupied;
	
	public Room(String name, int capacity, boolean recreation) throws IOException{
		this.name = name;
		this.capacity = capacity;
		isOccupied = new LinkedHashMap<Date, Date>();
		if(!recreation) {
			RoomHandler.createRoom(this);
		}
	}
	
	public String getName() {
		return name;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public LinkedHashMap<Date, Date> getIsOccupied() {
		return isOccupied;
	}
}
