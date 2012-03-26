package calendar;

import java.io.IOException;
import java.io.Serializable;

import server.RoomHandler;

public class Room extends DBCommunicator implements Comparable<Room>, Serializable{
	private static final long serialVersionUID = -452046361331320586L;
	private String name;
	private int capacity;
	private static RoomHandler roomHandler;
	
	public static void bindToHandler(){
		roomHandler = (RoomHandler) getHandler(RoomHandler.SERVICE_NAME);
	}
	
	public Room(String name, int capacity) throws IOException{
		if (capacity < 1){
			throw new IllegalArgumentException("Et rom må ha plass til minst én person!");
		}
		this.name = name;
		this.capacity = capacity;
		roomHandler.createRoom(this);
	}
	
	private Room(){
	}
	
	public static Room recreateRoom(String name, int capacity){
		Room room = new Room();
		room.name = name;
		room.capacity = capacity;
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

	@Override
	public String toString(){
		return String.format("%s (%d)", name, capacity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Room other) {
		Integer thisOne = new Integer(capacity);
		return thisOne.compareTo(new Integer(other.capacity));
	}
	
}
