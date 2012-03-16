package server;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;

import no.ntnu.fp.model.Person;

import org.junit.Test;

import calendar.Appointment;

public class TestServer {

	@Test
	public void testSaveAndFetch() throws IOException {	
		Person p = new Person();
		Appointment app = new Appointment("tannlege", 
				new Date(System.currentTimeMillis()), 
				new Date(System.currentTimeMillis()+2700), false, null, false);
		Appointment dbApp = AppointmentHandler.getAppointment(app.getAppId());
		assertEquals("Beskrivelsene skal v�re like!", app.getDescription(), dbApp.getDescription());
		assertEquals("Starttidspunktene skal v�re like!", app.getStartTime().getTime(), dbApp.getStartTime().getTime());
		assertEquals("Sluttidspunktene skal v�re like!", app.getEndTime().getTime(), dbApp.getEndTime().getTime());
		assertEquals("Objektene skal v�re like!", app, dbApp);
	}
}
