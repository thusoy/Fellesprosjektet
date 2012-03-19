package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;
import java.sql.Date;

import org.junit.Test;

import calendar.Appointment;
import calendar.Room;

public class TestRoomHandler {
	
//	@Test 
	//tester at det i utgangspunktet ikke er rom i databasen
//	public void testGetAllRoomsNull() throws ClassNotFoundException, IOException, SQLException{
//		assertEquals(RoomHandler.getAllRooms().size(), 0);
//		assertTrue(RoomHandler.getAllRooms().contains(null));
//	}	
	@Test
	//tester at fire rom blir lagt til databasen og legges til lista som returneres av getAllRooms
	public void testGetAllRooms() throws ClassNotFoundException, IOException, SQLException{
		Room testRoom1 = new Room("Vegas", 5, false);
		Room testRoom2 = new Room("Bamba", 10, false);
		Room testRoom3 = new Room("Limba", 3, false);
		Room testRoom4 = new Room("Soru", 6, false);
		assertEquals(RoomHandler.getAllRooms().size(), 4);
		assertTrue(RoomHandler.getAllRooms().contains(testRoom1));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom2));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom3));
		assertTrue(RoomHandler.getAllRooms().contains(testRoom4));
	}
	
	@Test
	public void testIsValid() throws IOException{
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		Date start = new Date(System.currentTimeMillis());
		Date end = new Date(System.currentTimeMillis()+400000);
		Appointment app1 = new Appointment("Ledermøte", start, end, false, null, false);
		Appointment app2 = new Appointment("Ledermøte", start, end, false, null, false);
		Appointment app3 = new Appointment("Ledermøte", start, end, false, null, false);
		AppointmentHandler.createAppointment(app1);
		AppointmentHandler.createAppointment(app2);
		AppointmentHandler.createAppointment(app3);
		//tester tidspunkt som skal fungere
		assertTrue(RoomHandler.isValid(start, end, 4, "Vegas"));
		//tester tidspunkt som starter før møte slutt
		assertTrue(RoomHandler.isValid(start, end, 10, "Bamba"));
		//tester tidspunkt som slutter etter møte startet
		assertTrue(RoomHandler.isValid(start, end, 2, "Soru"));
		//tester tidspunkt som starter
	}
	
	@Test
	public void testAvailableRooms(){
		
	}

}
