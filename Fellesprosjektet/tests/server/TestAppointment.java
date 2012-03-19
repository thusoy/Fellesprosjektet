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

public class TestAppointment {
	
	@Before
	public void emptyDb() throws IOException, SQLException{
		String query = "TRUNCATE TABLE Appointment";
		System.out.println("Truncating Appointment-table.");
		Execute.executeUpdate(query);
	}
	
	@Test
	public void testSaveAndFetch() throws IOException {	
		Person p = new Person();
		Appointment app = new Appointment("tannlege", 
				new Date(System.currentTimeMillis()), 
				new Date(System.currentTimeMillis()+2700), false, null, false);
		Appointment dbApp = Appointment.getAppointment(app.getAppId());
		assertEquals("Objektene skal være like!", app, dbApp);
	}
	
	@Test
	public void testGetAll() throws IOException{
		Person john = new Person("john", "high", "lol", "komtek", "banan", false);
		Date date = new Date(System.currentTimeMillis());
		Appointment a1 = new Appointment("tannlege", date, date, false, null, john, false);
		Appointment a2 = new Appointment("trening", date, date, false, null, john, false);
		List<Appointment> all = Appointment.getAll();
		assertEquals("Skal være to objekter i databasen", 2, all.size());
		System.out.println(all.get(0).equals(a1));
		System.out.println(all.get(1).equals(a1));
		assertTrue("Begge objektene skal være lagt i databasen", all.contains(a1));
		assertTrue("Begge objektene skal være lagt i databasen", all.contains(a2));
	}
	
	@Test
	public void testGetInterval(){
		int dayInMs = 24*3600*1000;
		long now = System.currentTimeMillis();
		Date first = new Date(now);
		Date second = new Date(now + dayInMs);
		Date third = new Date(now + 3*dayInMs);
		
		
	}
	
}