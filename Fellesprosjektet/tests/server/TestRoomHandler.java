package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.sql.Date;

import org.junit.Test;

import calendar.Appointment;
import calendar.Room;

public class TestRoomHandler {
		
	@Test
	public void testGetAllRooms() throws ClassNotFoundException, IOException, SQLException{
		for (Room room: RoomHandler.getAllRooms()) {
			//System.out.println(room.getName());
			//System.out.println(room.getCapacity());
			//assertTrue(RoomHandler.getAllRooms().contains(room));
		}
	}
	
	@Test
	public void testIsValid() throws IOException{
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		Date start = new Date(System.currentTimeMillis());
		Date end = new Date(System.currentTimeMillis()+10000000);
		Appointment app1 = new Appointment("Ledermøte", start, end, false, null, false);
		Appointment app2 = new Appointment("Ledermøte", start, end, false, null, false);
		Appointment app3 = new Appointment("Ledermøte", start, end, false, null, false);
		app1.setRoomName("Vegas");
		app2.setRoomName("Bamba");
		app3.setRoomName("Limba");
		AppointmentHandler.updateAppointment(app1);
		AppointmentHandler.updateAppointment(app2);
		AppointmentHandler.updateAppointment(app3);
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
