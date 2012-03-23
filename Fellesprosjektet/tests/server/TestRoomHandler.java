package server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import calendar.Appointment;
import calendar.Person;
import calendar.Room;

public class TestRoomHandler {
		
	
	@Before
	public void truncateRooms() throws IOException, SQLException{
		String query = "TRUNCATE TABLE Room";
		Execute.executeUpdate(query);
		String query2 = "TRUNCATE TABLE User";
		Execute.executeUpdate(query2);
	}
	
	@Test
	public void testIsValid() throws IOException{
		Date start = new Date(System.currentTimeMillis());
		Date end = new Date(System.currentTimeMillis()+10000000);
		Person creator = new Person("john", "lol", "email", null, "passord");
		Appointment app1 = new Appointment("Ledermote", start, end, false, null, creator);
		Appointment app2 = new Appointment("Ledermote", start, end, false, null, creator);
		Appointment app3 = new Appointment("Ledermote", start, end, false, null, creator);
		app1.setRoomName("Vegas");
		app2.setRoomName("Bamba");
		app3.setRoomName("Limba");
		//tester tidspunkt som skal fungere
		assertTrue(RoomHandler.isValid(start, end, 4, "Vegas"));
		//tester tidspunkt som starter før møte slutt
		assertTrue(RoomHandler.isValid(start, end, 10, "Bamba"));
		//tester tidspunkt som slutter etter møte startet
		assertTrue(RoomHandler.isValid(start, end, 2, "Soru"));
		//tester tidspunkt som starter
	}
	
	@Test
	public void testAvailableRooms() throws IOException{
		Date start = new Date(System.currentTimeMillis()-20000000);
		Date end = new Date(System.currentTimeMillis()-10000000);
		List<Room> roomsAvaiable = RoomHandler.availableRooms(start, end, 6);
		
		for (Room room: roomsAvaiable) {
			System.out.println(room.getName());
		}
	}

}
