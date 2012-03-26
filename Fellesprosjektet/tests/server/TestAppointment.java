package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;


import org.junit.Before;
import org.junit.Test;

import calendar.Appointment;
import calendar.Person;

public class TestAppointment {
	
//	@Before
//	public void emptyDb() throws IOException, SQLException{
//		String[] tablesToTruncate = {"UserMessages"};
//		String query = "TRUNCATE TABLE %s";
//		for(String table: tablesToTruncate){
//			Execute.update(String.format(query, table));
//		}
//	}
	
	@Test
	public void testSaveAndFetch() throws IOException {	
		Date start = new Date(System.currentTimeMillis()); 
		Date end = new Date(System.currentTimeMillis()+2700);
		Person creator = new Person("john", "locke", Long.toString(System.currentTimeMillis()), null, "");
		Appointment app = new Appointment("tannlege", start, end, false, null, creator);
		Appointment dbApp = Appointment.getAppointment(app.getId());
		assertEquals("Objektene skal være like!", app, dbApp);
		Appointment dbApp2 = Appointment.getAppointment(app.getId());
		assertEquals("Objektene skal fortsatt være like!", app, dbApp2);
	}
	
//	@Test
//	public void testGetAll() throws IOException{
//		Person john = new Person("john", "high", "lol", "komtek", "banan");
//		Date date = new Date(System.currentTimeMillis());
//		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
//		Appointment a2 = new Appointment("trening", date, date, false, null, john);
//		List<Appointment> all = Appointment.getAll();
//		assertEquals("Skal være to objekter i databasen", 2, all.size());
//		assertTrue("Begge objektene skal være lagt i databasen", all.contains(a1));
//		assertTrue("Begge objektene skal være lagt i databasen", all.contains(a2));
//	}
	
//	@Test
//	public void testGetInterval(){
//		int dayInMs = 24*3600*1000;
//		long now = System.currentTimeMillis();
//		Date first = new Date(now);
//		Date second = new Date(now + dayInMs);
//		Date third = new Date(now + 3*dayInMs);
//	}
	
	@Test
	public void testUpdateAppointment() throws IOException {
		Person john = new Person("john", "high", "lol@1.no", "komtek", "banan");
		Date date = new Date(System.currentTimeMillis());
		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
		a1.setTitle("handletur");
		AppointmentHandlerImpl.updateAppointment(a1);
		assertTrue("Tittel skal v¾re lik", a1.getTitle().equals("handletur"));
	}
	
//	@Test
//	public void testAcceptInviteAppointment() throws IOException {
//		Person john = new Person("john", "high", "lol@3.no", "komtek", "banan");
//		Date date = new Date(System.currentTimeMillis());
//		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
//		AppointmentHandlerImpl.addUserToAppointment(a1.getId(), john.getId());
//		AppointmentHandlerImpl.updateUserAppointment(a1.getId(), john.getId(), true);
//		assertTrue("John kommmer pŒ m¿tet", AppointmentHandlerImpl.getInviteStatusOnUser(a1.getId(), john.getId()));
//	}
	
//	@Test
//	public void testDeleteAppointment() throws IOException {
//		Person john = new Person("john", "high", "lol@4.no", "komtek", "banan");
//		Date date = new Date(System.currentTimeMillis());
//		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
//		AppointmentHandlerImpl.addUserToAppointment(a1.getId(), john.getId());
//		AppointmentHandlerImpl.updateUserAppointment(a1.getId(), john.getId(), true);
//		assertTrue("John kommmer pŒ m¿tet", AppointmentHandlerImpl.getInviteStatusOnUser(a1.getId(), john.getId()));
//		a1.delete();
//	}
	
	@Test
	public void testInviteAppointment() throws IOException {
		Person john = new Person("john", "high", "lol@2.no", "komtek", "banan");
		Person jo = new Person("jo", "high", "lol@22.no", "komtek", "banan");
		Date date = new Date(System.currentTimeMillis());
		Appointment a1 = new Appointment("tannlege", date, date, false, null, john);
		HashMap<Person, Boolean> participants = new HashMap<Person, Boolean>();
		participants.put(jo, null);
		a1.setParticipants(participants);
		a1.save();
		MessageHandlerImpl.sendMessageUserHasDenied(a1.getId(), jo.getId());
	}
}
