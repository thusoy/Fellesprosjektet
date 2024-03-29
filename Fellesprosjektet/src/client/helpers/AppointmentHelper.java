package client.helpers;

import static ascii.Art.printAsciiArt;
import static client.helpers.DBHelper.getPersonFromEmail;
import static client.helpers.DBHelper.isValidEmail;
import static client.helpers.IO.getString;
import static client.helpers.IO.parseDate;
import static client.helpers.IO.promptChoice;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.AppointmentHandler;
import server.PersonHandler;
import server.RoomHandler;
import calendar.Appointment;
import calendar.DBCommunicator;
import calendar.Message;
import calendar.Person;
import calendar.Room;

public class AppointmentHelper extends DBCommunicator{
	private static final String DATE_FORMAT_STRING = "dd-MM-yyyy HH:mm";
	private static final DateFormat DATE_FORMAT =  new SimpleDateFormat(DATE_FORMAT_STRING);
	private static AppointmentHandler appHandler;
	private static PersonHandler personHandler;
	private static RoomHandler roomHandler;
	
	static {
			appHandler = (AppointmentHandler) getHandler(AppointmentHandler.SERVICE_NAME);
			personHandler = (PersonHandler) getHandler(PersonHandler.SERVICE_NAME);
			roomHandler = (RoomHandler) getHandler(RoomHandler.SERVICE_NAME);
	}

	public static List<Appointment> getWeekAppointments(Person user, int weekNum) throws IOException{
		List<Appointment> appointments = appHandler.getAllCreated(user.getId(), weekNum);
		List<Appointment> app = appHandler.getAllInvitedInWeek(user.getId(), weekNum);	
		List<Appointment> apps = personHandler.getFollowAppointments(user.getId(), weekNum);
		appointments.addAll(apps);
		appointments.addAll(app);
		Collections.sort(appointments);		
		return removeDupesAndSort(appointments);
	}
	
	public static List<Appointment> getAllAppointments(Person user) throws IOException{
		List<Appointment> created = appHandler.getAllByUser(user.getId());
		List<Appointment> invited = appHandler.getAllInvited(user.getId());		
		List<Appointment> follows = personHandler.getFollowAppointments(user.getId());
		created.addAll(follows);
		created.addAll(invited);
		return removeDupesAndSort(created);
	}
	
	private static List<Appointment> removeDupesAndSort(List<Appointment> apps){
		return apps;
//		Set<Appointment> sortedUnique = new TreeSet<Appointment>();
//		sortedUnique.addAll(apps);
//		List<Appointment> unique = new ArrayList<Appointment>(sortedUnique);
//		Collections.sort(unique);
//		return unique;
	}
	
	public static void showAppointment(Person user) throws IOException, UserAbortException {
		List<Appointment> appointments =  getAllAppointments(user);
		System.out.println("Hvilken avtale vil du se? ");
		int appointmentNo = promptChoice(appointments);
		Appointment app = appointments.get(appointmentNo);
		printAsciiArt(app.getTitle());
		System.out.printf("Eier av avtalen: %s\n", app.getCreator().fullName());
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
		getString("Trykk enter for � fortsette");
	}
	
	public static void changeAppointment(Person user) throws IOException, UserAbortException {
		List<Appointment> appointments = appHandler.getAllByUser(user.getId());
		System.out.println("Hvilken avtale vil du endre p�?");
		int userChoice = promptChoice(appointments);
		Appointment app = appointments.get(userChoice);
		changeAppointment(user, app);
	}
	
	public static void changeAppointment(Person user, Appointment app) throws IOException, UserAbortException{
		System.out.printf("Tast inn endringene for %s.\n", app);
		app.setTitle(getString("Skriv inn tittel: "));
		Date startdate = parseDate(DATE_FORMAT, String.format("Startdato (%s): ", DATE_FORMAT_STRING));
		Date enddate = parseDate(DATE_FORMAT, String.format("Sluttdato (%s): ", DATE_FORMAT_STRING));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk p� enter. ").isEmpty();
		app.setStartTime(startdate);
		app.setEndTime(enddate);
		app.setPrivate(isPrivate);
		app.setParticipants(getParticipants(user));
		app.setDescription(getString("Skriv inn beskrivelse: "));
		setRoomOrPlace(app);
		app.save();
		System.out.println("Avtalen er endret.");
		if (!app.getParticipants().isEmpty()) {
			Message msg = new Message("Avtalen "+app.getTitle()+" er endret", "Denne avtalen har blitt endret. ");
			msg.setReceivers(mapKeysToList(app.getParticipants()));
		}
	}
	
	private static <T, S> List<T> mapKeysToList(Map<T, S> map){
		List<T> list = new ArrayList<T>();
		for(T t: map.keySet()){
			list.add(t);
		}
		return list;
	}
	
	public static void deleteAppointment(Person user) throws IOException, UserAbortException {
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		int choice = promptChoice(appointments);
		long appId = appointments.get(choice).getId();
		Appointment app = appHandler.getAppointment(appId);
		deleteAppointmentHelper(user, app);
	}
	
	public static void deleteAppointment(Person user, Appointment app) throws IOException{
		deleteAppointmentHelper(user, app);
	}
	
	private static void deleteAppointmentHelper(Person user, Appointment app) throws IOException{
		if (user.equals(app.getCreator())){
			app.delete();
		}else {
			app.deleteAppointmentInvited(user.getId());
		}
		System.out.println("Avtalen er slettet");
	}
	
	/**
	 * Gets all the appointments a user either has created, or participates in.
	 * @param userId
	 * @throws IOException
	 */
	private static List<Appointment> getAllAppointmentsInvolved(long userId) throws IOException {
		List<Appointment> ownApps = appHandler.getAllByUser(userId);
		List<Appointment> participatesInApps = appHandler.getAllInvited(userId);
		ownApps.addAll(participatesInApps);
		return removeDupesAndSort(ownApps);
	}
	
	public static void addNewAppointment(Person user) throws IOException, UserAbortException {
		String title = getString("Skriv inn tittel: ");
		Date startdate = parseDate(DATE_FORMAT, String.format("Startdato (%s): ", DATE_FORMAT_STRING));
		Date enddate = parseDate(DATE_FORMAT, String.format("Sluttdato (%s): ", DATE_FORMAT_STRING));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk p� enter. ").isEmpty();
		Map<Person, Boolean> participants = getParticipants(user);
		
		Appointment app = new Appointment(title, startdate, enddate, isPrivate, participants, user);
		setRoomOrPlace(app);
		app.save();
		System.out.println("Ny avtale lagret!");
	}
	
	private static void setRoomOrPlace(Appointment app) throws IOException, UserAbortException{
		if (app.getParticipants().size() > 0){
			String reserve = getString("Vil du reservere m�terom? (ja/nei): ");
			if (reserve.equalsIgnoreCase("ja")){
				reserveRoom(app);
			} else {
				String place = getString("Skriv inn sted: ");
				app.setPlace(place);
			}
		} else {
			String place = getString("Skriv inn sted: ");
			System.out.println("sted: " + place);
			app.setPlace(place);
		}
	}
	
	private static void reserveRoom(Appointment app) throws IOException, UserAbortException {
		int numParticipants = app.getParticipants().size() + 1; //remember the creator too!
		List<Room> rooms = roomHandler.availableRooms(app.getStartTime(), app.getEndTime(), numParticipants);
		System.out.println("Reserver m�terom: ");
		int chosenRoom = promptChoice(rooms);
		app.setRoomName(rooms.get(chosenRoom).getName());
	}
	
	private static Map<Person, Boolean> getParticipants(Person user) throws IOException, UserAbortException{
		Map<Person, Boolean> map = new HashMap<Person, Boolean>();
		while (true) {
			String email = getString("Skriv inn e-posten til personen du vil invitere (eller trykk enter hvis du er ferdig): ");
			if (email.isEmpty()){
				break;
				//TODO Fjern utkommentering av adding av egen epost
			} else if (isValidEmail(email) ){//&& !email.equalsIgnoreCase(user.getEmail())){
				Person p = getPersonFromEmail(email);
				map.put(p, null);
				System.out.printf("%s ble invitert!\n", email);
			} else {
				System.out.println("Ugyldig e-postadresse, pr�v igjen.");
			}
		}
		return map;
	}
}
