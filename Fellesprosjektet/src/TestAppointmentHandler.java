import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import calendar.Appointment;
import no.ntnu.fp.model.*;
import server.AppointmentHandler;
import server.PersonHandler;

public class TestAppointmentHandler {
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		Date start = new Date(System.currentTimeMillis());
		Date end = new Date(System.currentTimeMillis()+2000);
		Appointment app = new Appointment("Handletur", start, end, false, null);
		app.setDescription("gå i butikken og handle sjokolade mm");
		AppointmentHandler.createAppointment(app);
		System.out.println("Hei");
		
	}
}
