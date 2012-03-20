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
		String[] tablesToTruncate = {"Appointment", "User"};
		String query = "TRUNCATE TABLE %s";
		for(String table: tablesToTruncate){
			Execute.executeUpdate(String.format(query, table));
		}
	}
	
	@Test
	public void testSaveAndFetch() throws IOException {	
		Date start = new Date(System.currentTimeMillis()); 
		Date end = new Date(System.currentTimeMillis()+2700);
		Person creator = new Person("john", "locke", "lol@post", null, "");
		Appointment app = new Appointment("tannlege", start, end, false, null, creator);
		Appointment dbApp = Appointment.getAppointment(app.getAppId());
		System.out.println("sammenligner *********************************");
		assertEquals("Objektene skal være like!", app, dbApp);
		System.out.println("ferdig! **************************************");
	}
	
	@Test
	public void testGetAll() throws IOException{
		Person john = new Person("john", "high", "lol", "komtek", "banan");
		Date date = new Date(System.currentTimeMillis());
		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
		Appointment a2 = new Appointment("trening", date, date, false, null, john);
		List<Appointment> all = Appointment.getAll();
		assertEquals("Skal være to objekter i databasen", 2, all.size());
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
	@Test
	public void testUpdateAppointment() throws IOException {
		Person john = new Person("john", "high", "lol", "komtek", "banan");
		Date date = new Date(System.currentTimeMillis());
		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
		a1.setTitle("handletur");
		AppointmentHandler.updateAppointment(a1);
		assertTrue("Tittel skal v¾re lik", a1.getTitle().equals("handletur"));
	}
	
}