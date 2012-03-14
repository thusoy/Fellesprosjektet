package server;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import calendar.Appointment;

public class TestAppointmentHandler {
	
	@Test
	public void testSave() throws ClassNotFoundException, IOException, SQLException{
		Date start = new Date();
		Date end = new Date(System.currentTimeMillis()+2000);
		Appointment app = new Appointment("Handletur", start, end, false, null);
		app.setDescription("gå i butikken og handle sjokolade mm");
		app.save();
		app.getAppId();
	}
}
