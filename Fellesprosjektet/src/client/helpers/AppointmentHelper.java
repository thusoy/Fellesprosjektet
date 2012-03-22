package client.helpers;

import static client.helpers.DBHelper.getPersonFromEmail;
import static client.helpers.IO.getString;
import static client.helpers.IO.getValidNum;
import static client.helpers.IO.parseDate;
import static client.helpers.DBHelper.isValidEmail;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.PersonHandler;
import server.RoomHandler;
import calendar.Appointment;
import calendar.Room;

public class AppointmentHelper {

	public static List<Appointment> getWeekAppointments(Person user, int weekNum) throws IOException{
		List<Appointment> appointments = AppointmentHandler.getAllCreated(user.getId(), weekNum);
		List<Appointment> app = AppointmentHandler.getAllInvited(user.getId(), weekNum);	
		List<Appointment> apps = PersonHandler.getFollowAppointments(user.getId(), weekNum);
		appointments.addAll(apps);
		appointments.addAll(app);
		Collections.sort(appointments);		
		return appointments;
	}
	
	public static List<Appointment> getAllAppointments(Person user) throws IOException{
		List<Appointment> created = AppointmentHandler.getAllCreated(user.getId());
		List<Appointment> invited = AppointmentHandler.getAllInvited(user.getId());		
		List<Appointment> follows = PersonHandler.getFollowAppointments(user.getId());
		created.addAll(follows);
		created.addAll(invited);
		Set<Appointment> sortedUnique = new TreeSet<Appointment>();
		sortedUnique.addAll(created);
		List<Appointment> unique = new ArrayList<Appointment>(sortedUnique);
		Collections.sort(unique);
		return unique;
	}
	
	public static void showAppointment(Person user) throws IOException {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");		
		List<Appointment> appointments =  getAllAppointments(user);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hvilken avtale vil du se? ");
		int appointmentNo = scanner.nextInt();
		Appointment app = appointments.get(appointmentNo);
		System.out.printf("Eier av avtalen: %s %s \n", app.getCreator().getFirstname(), app.getCreator().getLastname());
		System.out.println("Tittel: "+app.getTitle());
		System.out.println("Sted: "+app.getPlace());
		System.out.println("Start: "+df.format(app.getStartTime()));
		System.out.println("Slutt: "+df.format(app.getEndTime()));
		System.out.println("Beskrivelse: "+app.getDescription());
		System.out.println("Rom: "+app.getRoomName());
		System.out.println("Privat: "+app.isPrivate());
		System.out.println("Deltakere: ");
		Map<Person, Boolean> participants = app.getParticipants();
		for (Person user: participants.keySet()) {
			Boolean answer = participants.get(user);
			String answerString = null;
			if (answer == null){
				answerString = "har ikke svart";
			}else{
				answerString = answer ? "kommer" : "kommer ikke";
			}
			System.out.printf("%s %s %s.\n", user.getFirstname(), user.getLastname(), answerString);
		}
	}
	
	public static void changeAppointment(Person user) throws IOException {
		List<Appointment> appointments = AppointmentHandler.getAllCreated(user.getId());
		for (int i=0; i<appointments.size(); i++) {
			System.out.printf("%d. %s\n", i+1, appointments.get(i));
		}
		int change = getValidNum(appointments.size());
		Appointment app = appointments.get(change-1);
		changeAppointment(user, app);
	}
	
	public static void changeAppointment(Person user, Appointment app) throws IOException{
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ").isEmpty();
		
		app.setTitle(getString("Skriv inn tittel: "));
		app.setStartTime(startdate);
		app.setEndTime(enddate);
		app.setPrivate(isPrivate);
		app.setParticipants(getParticipants(user));
		app.setDescription(getString("Skriv inn beskrivelse: "));
		setRoomOrPlace(app);
		
		app.save();
		System.out.println("Avtalen er endret.");
	}
	
	public static void deleteAppointment(Person user) throws IOException {
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		for(int i = 0; i < appointments.size(); i++){
			System.out.printf("%d. %s\n", i+1, appointments.get(i));
		}
		int delete = getValidNum(appointments.size());
		long appId = appointments.get(delete-1).getAppId();
		Appointment app = AppointmentHandler.getAppointment(appId);
		deleteAppointment(user, app);
	}
	
	private static void deleteAppointment(Person user, Appointment app) throws IOException{
		if (user.equals(app.getCreator())){
			app.deleteAppointment();
		}else {
			app.deleteAppointmentInvited();
		}
		System.out.println("Avtalen er slettet");
	}
	
	private static List<Appointment> getAllAppointmentsInvolved(long userId) throws IOException {
		List<Appointment> appointments = AppointmentHandler.getAllCreated(userId);
		List<Appointment> app = AppointmentHandler.getAllInvited(userId);
		appointments.addAll(app);
		return appointments;
	}
	
	public static void addNewAppointment(Person user) throws IOException {
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		String title = getString("Skriv inn tittel: ");
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ").isEmpty();
		Map<Person, Boolean> participants = getParticipants(user);
		
		Appointment app = new Appointment(title, startdate, enddate, isPrivate, participants, user);
		setRoomOrPlace(app);
		app.save();
		System.out.println("Ny avtale lagret!");
	}
	
	private static void setRoomOrPlace(Appointment app) throws IOException{
		if (app.getParticipants() != null){
			String reserve = getString("Vil du reservere m¿terom? (ja/nei): ");
			if (reserve.equalsIgnoreCase("ja")){
				String roomName = reserveRoom(app.getStartTime(), app.getEndTime(), app.getParticipants());
				app.setRoomName(roomName);
			} else {
				String place = getString("Skriv inn sted: ");
				app.setPlace(place);
			}
		} else {
			String place = getString("Skriv inn sted: ");
			app.setPlace(place);
		}
	}
	
	private static String reserveRoom(Date startdate, Date enddate, Map<Person, Boolean> participants) throws IOException {
		List<Room> rooms = RoomHandler.availableRooms(startdate, enddate, participants.size());
		System.out.println("Reserver m¿terom: ");
		for (int i = 0; i < rooms.size(); i++) {
			System.out.printf("%d. %s\n", i+1, rooms.get(i).getName());
		}
		int choosenRoom = getValidNum(rooms.size());
		return rooms.get(choosenRoom).getName();
	}
	
	private static Map<Person, Boolean> getParticipants(Person user) throws IOException{
		Map<Person, Boolean> map = new HashMap<Person, Boolean>();
		while (true) {
			String email = getString("Enter email to invite to the event: ");
			if (email.isEmpty()){
				break;
			} else if (isValidEmail(email) && !email.equalsIgnoreCase(user.getEmail())){
				Person p = getPersonFromEmail(email);
				map.put(p, null);
				System.out.printf("%s added to participants!\n", email);
			} else {
				System.out.println("Invalid email!");
			}
		}
		return map;
	}
}
