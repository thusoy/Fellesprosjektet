package calendar;

import java.util.Date;
import java.util.LinkedHashMap;

public class Room extends DBObject<Room>{
	private String name;
	private int capacity;
	private LinkedHashMap<Date, Date> isOccupied;
	
	public Room(String name, int capacity){
		this.name = name;
		this.capacity = capacity;
		isOccupied = new LinkedHashMap<Date, Date>();
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
