package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import no.ntnu.fp.model.Person;

import org.junit.Before;
import org.junit.Test;

import calendar.Appointment;
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
	public void testGetAllRooms() throws ClassNotFoundException, IOException, SQLException{
		Room a = new Room("EL-204", 6, false);
		Room b = new Room("EL-304", 6, false);
		Room c = new Room("random", 150, false);
		List<Room> allRooms = RoomHandler.getAllRooms();
		int numRooms = allRooms.size();
		assertEquals("Skal være tre rom i databasen!", 3, numRooms);
		assertEquals("Samme objektet skal kunne hentes ut!", a, RoomHandler.getRoom(a.getName()));
		assertTrue("Rom a skal ligge i databasen!", allRooms.contains(a));
		assertTrue("Rom b skal ligge i databasen!", allRooms.contains(b));
		assertTrue("Rom c skal ligge i databasen!", allRooms.contains(c));
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
		Date start = new Date(System.currentTimeMillis());
		Date end = new Date(System.currentTimeMillis()+10000000);
		List<Room> roomsAvaiable = RoomHandler.availableRooms(start, end, 6);
		for (Room room: roomsAvaiable) {
			System.out.println(room.getName());
		}
	}

}
