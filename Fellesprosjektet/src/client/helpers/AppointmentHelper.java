package client.helpers;

import static client.helpers.DBHelper.getPersonFromEmail;
import static client.helpers.DBHelper.isValidEmail;
import static client.helpers.IO.getString;
import static client.helpers.IO.parseDate;
import static client.helpers.IO.promptChoice;
import static ascii.Art.printAsciiArt;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import server.AppointmentHandler;
import server.PersonHandler;
import server.RoomHandler;
import calendar.Appointment;
import calendar.Person;
import calendar.Room;

public class AppointmentHelper {
	private static final String DATE_FORMAT_STRING = "dd-MM-yyyy HH:mm";
	private static final DateFormat DATE_FORMAT =  new SimpleDateFormat(DATE_FORMAT_STRING);

	public static List<Appointment> getWeekAppointments(Person user, int weekNum) throws IOException{
		List<Appointment> appointments = AppointmentHandler.getAllCreated(user.getId(), weekNum);
		List<Appointment> app = AppointmentHandler.getAllInvitedInWeek(user.getId(), weekNum);	
		List<Appointment> apps = PersonHandler.getFollowAppointments(user.getId(), weekNum);
		appointments.addAll(apps);
		appointments.addAll(app);
		Collections.sort(appointments);		
		return appointments;
	}
	
	public static List<Appointment> getAllAppointments(Person user) throws IOException{
		List<Appointment> created = AppointmentHandler.getAllByUser(user.getId());
		List<Appointment> invited = AppointmentHandler.getAllInvited(user.getId());		
		List<Appointment> follows = PersonHandler.getFollowAppointments(user.getId());
		created.addAll(follows);
		created.addAll(invited);
		Set<Appointment> sortedUnique = new TreeSet<Appointment>();
		sortedUnique.addAll(created);
		List<Appointment> unique = new ArrayList<Appointment>(sortedUnique);
		Collections.sort(unique);
		System.out.println("Størrelse før: " + created.size());
		System.out.println("Størrelse etter: " + unique.size());
		return unique;
	}
	
	public static void showAppointment(Person user) throws IOException, UserAbortException {
		List<Appointment> appointments =  getAllAppointments(user);
		System.out.println("Hvilken avtale vil du se? ");
		int appointmentNo = promptChoice(appointments);
		Appointment app = appointments.get(appointmentNo);
		printAsciiArt(app.getTitle());
		System.out.printf("Eier av avtalen: %s %s\n", app.getCreator().getFirstname(), app.getCreator().getLastname());
		System.out.printf("Sted: %s\n", app.getPlace());
		System.out.printf("Start: %s\n", DATE_FORMAT.format(app.getStartTime()));
		System.out.printf("Slutt: %s\n", DATE_FORMAT.format(app.getEndTime()));
		System.out.printf("Beskrivelse: %s\n", app.getDescription());
		System.out.printf("Rom: %s\n", app.getRoomName());
		System.out.printf("Privat: %s\n", app.isPrivate() ? "ja" : "nei");
		System.out.println("Deltakere: ");
		Map<Person, Boolean> participants = app.getParticipants();
		for (Person p: participants.keySet()) {
			Boolean answer = participants.get(p);
			String answerString = answer == null ? "har ikke svart" : answer ? "kommer" : "kommer ikke";
			System.out.printf("\t%s: %s.\n", p.fullName(), answerString);
		}
	}
	
	public static void changeAppointment(Person user) throws IOException, UserAbortException {
		List<Appointment> appointments = AppointmentHandler.getAllByUser(user.getId());
		System.out.println("Hvilken avtale vil du endre på?");
		int userChoice = promptChoice(appointments);
		Appointment app = appointments.get(userChoice);
		changeAppointment(user, app);
	}
	
	public static void changeAppointment(Person user, Appointment app) throws IOException, UserAbortException{
		System.out.printf("Tast inn endringene for %s.\n", app);
		app.setTitle(getString("Skriv inn tittel: "));
		Date startdate = parseDate(DATE_FORMAT, String.format("Startdato (%s): ", DATE_FORMAT_STRING));
		Date enddate = parseDate(DATE_FORMAT, String.format("Sluttdato (%s): ", DATE_FORMAT_STRING));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ").isEmpty();
		app.setStartTime(startdate);
		app.setEndTime(enddate);
		app.setPrivate(isPrivate);
		app.setParticipants(getParticipants(user));
		app.setDescription(getString("Skriv inn beskrivelse: "));
		setRoomOrPlace(app);
		
		app.save();
		System.out.println("Avtalen er endret.");
	}
	
	public static void deleteAppointment(Person user) throws IOException, UserAbortException {
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		int choice = promptChoice(appointments);
		long appId = appointments.get(choice).getAppId();
		Appointment app = AppointmentHandler.getAppointment(appId);
		deleteAppointmentHelper(user, app);
	}
	
	public static void deleteAppointment(Person user, Appointment app) throws IOException{
		deleteAppointmentHelper(user, app);
	}
	
	private static void deleteAppointmentHelper(Person user, Appointment app) throws IOException{
		if (user.equals(app.getCreator())){
			app.delete();
		}else {
			app.deleteAppointmentInvited();
		}
		System.out.println("Avtalen er slettet");
	}
	
	/**
	 * Gets all the appointments a user either has created, or participates in.
	 * @param userId
	 * @throws IOException
	 */
	private static List<Appointment> getAllAppointmentsInvolved(long userId) throws IOException {
		List<Appointment> ownApps = AppointmentHandler.getAllByUser(userId);
		List<Appointment> participatesInApps = AppointmentHandler.getAllInvited(userId);
		ownApps.addAll(participatesInApps);
		return ownApps;
	}
	
	public static void addNewAppointment(Person user) throws IOException, UserAbortException {
		String title = getString("Skriv inn tittel: ");
		Date startdate = parseDate(DATE_FORMAT, String.format("Startdato (%s): ", DATE_FORMAT_STRING));
		Date enddate = parseDate(DATE_FORMAT, String.format("Sluttdato (%s): ", DATE_FORMAT_STRING));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ").isEmpty();
		Map<Person, Boolean> participants = getParticipants(user);
		
		Appointment app = new Appointment(title, startdate, enddate, isPrivate, participants, user);
		setRoomOrPlace(app);
		app.save();
		System.out.println("Ny avtale lagret!");
	}
	
	private static void setRoomOrPlace(Appointment app) throws IOException, UserAbortException{
		if (app.getParticipants() != null){
			String reserve = getString("Vil du reservere møterom? (ja/nei): ");
			if (reserve.equalsIgnoreCase("ja")){
				reserveRoom(app);
			} else {
				String place = getString("Skriv inn sted: ");
				app.setPlace(place);
			}
		} else {
			String place = getString("Skriv inn sted: ");
			app.setPlace(place);
		}
	}
	
	private static void reserveRoom(Appointment app) throws IOException, UserAbortException {
		List<Room> rooms = RoomHandler.availableRooms(app.getStartTime(), app.getEndTime(), app.getParticipants().size());
		System.out.println("Reserver møterom: ");
		int chosenRoom = promptChoice(rooms);
		app.setRoomName(rooms.get(chosenRoom).getName());
	}
	
	private static Map<Person, Boolean> getParticipants(Person user) throws IOException, UserAbortException{
		Map<Person, Boolean> map = new HashMap<Person, Boolean>();
		while (true) {
			String email = getString("Skriv inn e-posten til personen du vil følge (eller trykk enter hvis du er ferdig): ");
			if (email.isEmpty()){
				break;
			} else if (isValidEmail(email) && !email.equalsIgnoreCase(user.getEmail())){
				Person p = getPersonFromEmail(email);
				map.put(p, null);
				System.out.printf("%s ble invitert!\n", email);
			} else {
				System.out.println("Ugyldig e-postadresse, prøv igjen.");
			}
		}
		return map;
	}
}
