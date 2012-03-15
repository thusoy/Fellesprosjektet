package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;

import org.junit.Test;

import calendar.Appointment;
import calendar.Room;

public class TestRoomHandler {
	
	@Test 
	//tester at det i utgangspunktet ikke er rom i databasen
	public void testGetAllRoomsNull() throws ClassNotFoundException, IOException, SQLException{
		assertEquals(RoomHandler.getAllRooms().size(), 0);
		assertTrue(RoomHandler.getAllRooms().contains(null));
}	
	@Test
	//tester at fire rom blir lagt til databasen og legges til lista som returneres av getAllRooms
	public void testGetAllRooms() throws ClassNotFoundException, IOException, SQLException{
		Room testRoom1 = new Room("Vegas", 5);
		Room testRoom2 = new Room("Bamba", 10);
		Room testRoom3 = new Room("Limba", 3);
		Room testRoom4 = new Room("Soru", 6);
		assertEquals(RoomHandler.getAllRooms().size(), 4);
		assertTrue(RoomHandler.getAllRooms().contains(testRoom1));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom2));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom3));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom4));
	}
	
	@Test
	public void testIsValid(){
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		Appointment app1 = new Appointment("Ledermøte", df, slutt, false, null);
		Appointment app2 = new Appointment("Ledermøte", df, slutt, false, null);
		Appointment app3 = new Appointment("Ledermøte", df, slutt, false, null);
		AppointmentHandler.createAppointment(app1);
		AppointmentHandler.createAppointment(app2);
		AppointmentHandler.createAppointment(app3);
		//tester tidspunkt som skal fungere
		asserTrue(RoomHandler.isValid(starttid, slutttid, 4, "Vegas"));
		//tester tidspunkt som starter før møte slutt
		assertTrue(RoomHandler.isValid(startCandidate, endCandidate, 10, "Bamba"));
		//tester tidspunkt som slutter etter møte startet
		assertTrue(RoomHandler.isValid(startCandidate, endCandidate, 2, "Soru"));
		//tester tidspunkt som starter
	}
	
	@Test
	public void testAvailableRooms(){
		
	}

}
