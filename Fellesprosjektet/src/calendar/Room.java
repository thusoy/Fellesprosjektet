package calendar;

import java.io.IOException;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import server.RoomHandler;

public class Room implements Comparable<Room>{
	private String name;
	private int capacity;
	private LinkedHashMap<Date, Date> isOccupied;
	
	public Room(String name, int capacity) throws IOException{
		this.name = name;
		this.capacity = capacity;
		isOccupied = new LinkedHashMap<Date, Date>();
		RoomHandler.createRoom(this);
	}
	
	private Room(){
	}
	
	public static Room recreateRoom(String name, int capacity){
		Room room = new Room();
		room.name = name;
		room.capacity = capacity;
		room.isOccupied = new LinkedHashMap<Date, Date>();
		return room;
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

	public Map<Date, Date> getIsOccupied() {
		return isOccupied;
	}
	
	@Override
	public String toString(){
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result
				+ ((isOccupied == null) ? 0 : isOccupied.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (capacity != other.capacity)
			return false;
		if (isOccupied == null) {
			if (other.isOccupied != null)
				return false;
		} else if (!isOccupied.equals(other.isOccupied))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Room other) {
		return Integer.compare(capacity, other.capacity);
	}
	
	
}
