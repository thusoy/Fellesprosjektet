package server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;

import no.ntnu.fp.model.Person;

import org.junit.Test;

import calendar.Appointment;

public class TestServer {

	@Test
	public void test() throws IOException {	
		Person p = new Person();
		Appointment app = new Appointment("tannlege", 
				new Date(System.currentTimeMillis()), 
				new Date(System.currentTimeMillis()+2700), false, null, false);
		assertTrue("Objektene skal være like!", app == AppointmentHandler.getAppointment(app.getAppId()));
	}
}
