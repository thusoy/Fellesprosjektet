package client;

import static hashtools.Hash.createHash;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import calendar.RejectedMessage;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.Execute;
import server.MessageHandler;
import server.PersonHandler;
import server.RoomHandler;
import calendar.Appointment;
import calendar.Day;
import calendar.Message;
import calendar.Room;

public class Starter {
	
	private Person user;
	private int weekNum;
	
	public static void main(String[] args) {
		try {
			(new Starter()).initAndLogin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Starter(){
		weekNum = getCurrentWeekNum();
	}
	
	private void initAndLogin() throws IOException {
		Person user = null;
		do {
			try {
				user = authenticateUser();
				System.out.println("Auth OK!");
			} catch(RuntimeException e){
				System.out.println("Ugyldig kombinasjon av passord og brukernavn, prøv igjen.");
			}
		} while (user == null);
		this.user = user;
		run();
	}

	private void run() throws IOException{
		CalendarFunction[] allFunc = CalendarFunction.values();
		while(true){
			showWeek();
			int numUnansweredMeetings = AppointmentHandler.getAllUnansweredInvites(user.getId()).size();
			int numNewMessages = MessageHandler.getUnreadMessagesForUser(user).size();
			System.out.println("Hva vil du gjøre?");
			for(int i = 0; i < allFunc.length; i++){
				CalendarFunction cf = allFunc[i];
				if (cf == CalendarFunction.SHOW_INVITES && numUnansweredMeetings > 0){
					System.out.printf("%d. %s (%d)\n", i + 1, cf.description, numUnansweredMeetings);
				} else if (cf == CalendarFunction.SHOW_MESSAGES && numNewMessages > 0){
					System.out.printf("%d. %s (%d)\n", i + 1, cf.description, numNewMessages);
				} else {
					System.out.printf("%d. %s\n", i + 1, cf.description);
				}
			}
			CalendarFunction cf = CalendarFunction.getUserFunction();
			if (cf == CalendarFunction.QUIT){
				break;
			}
			runFunc(cf);
		}
		System.out.println("Ha en fortsatt fin dag!");
	}
	
	private int getCurrentWeekNum() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public void runFunc(CalendarFunction func) throws IOException{
		switch(func){
		case ADD_APPOINTMENT:
			addNewAppointment();
			break;
		case DELETE_APPOINTMENT:
			deleteAppointment();
			break;
		case CHANGE_APPOINTMENT:
			changeAppointment();
			break;
		case SHOW_APPOINTMENT:
			showAppointment();
			break;
		case SHOW_WEEK:
			getAndShowWeek();
			break;
		case SHOW_INVITES:
			showInvites();
			break;
		case FOLLOW_CALENDAR:
			followCalendar();
			break;
		case SHOW_NEXT_WEEK:
			weekNum++;
			break;
		case SHOW_MESSAGES:
			showMessages();
			break;
		case SHOW_PREVIOUS_WEEK:
			weekNum--;
			break;
		}
	}
	
	private void showMessages() throws IOException {
		List<Message> unreadMessages = MessageHandler.getUnreadMessagesForUser(user);
		for(int i = 0; i < unreadMessages.size(); i++){
			Message m = unreadMessages.get(i);
			System.out.printf("%d. %s\n", i+1, m);
		}
		System.out.print("Hvilken melding vil du lese? ");
		Scanner scanner = new Scanner(System.in);
		int userInput = scanner.nextInt();
		Message selected = unreadMessages.get(userInput-1);
		System.out.println(selected.showMessage(user));
		if (selected instanceof RejectedMessage){
			RejectedMessage m = (RejectedMessage) selected;
			System.out.println("Hva vil du gjøre?");
			m.getAndExecuteUserResponse(this);
		}
	}

	private void showInvites() throws IOException {
		List<Appointment> allInvited = AppointmentHandler.getAllUnansweredInvites(user.getId());
		for(int i = 0; i < allInvited.size(); i++){
			System.out.printf("%d. %s\n", i + 1, allInvited.get(i));
		}
		System.out.print("Vil du svare på en innkalling? Tast inn nummeret: ");
		Scanner scanner = new Scanner(System.in);
		int userNum = scanner.nextInt();
		answerInvite(allInvited.get(userNum-1));
	}
	
	private void answerInvite(Appointment appointment) throws IOException {
		System.out.print("Hva vil du svare? ('ja', 'nei', eller enter for å utsette) ");
		Scanner scanner = new Scanner(System.in);
		while(true){
			String input = scanner.nextLine();
			if (input.equalsIgnoreCase("ja")){
				appointment.acceptInvite(user, true);
				System.out.printf("Du har bekreftet avtalen %s.\n", appointment);
				break;
			} else if (input.equalsIgnoreCase("nei")){
				appointment.acceptInvite(user, false);
				System.out.printf("Du har takket nei til avtalen %s.\n", appointment);
				break;
			} else if (input.isEmpty()){
				break;
			}
			System.out.println("Beklager, jeg skjønte ikke svaret ditt. Prøv igjen.");
		}
	}


	private void getAndShowWeek() throws IOException {
		Scanner scn = new Scanner(System.in);
		System.out.print("Skriv inn en uke du vil vise: ");
		int weekNum =  scn.nextInt();
		this.weekNum = weekNum;
	}

	private void followCalendar() throws IOException {
		System.out.print("Skriv inn e-postadressen til personen du vil følge: ");
		String email = getValidEmail();
		long otherUserId = getUserIdFromEmail(email);
		user.followPerson(otherUserId);
	}

	private static String getValidEmail() throws IOException {
		Scanner scn = new Scanner(System.in);
		String email;
		do {
			email = scn.nextLine();
		} while (!isValidEmail(email));
		return email;
	}
	
	private static boolean isValidEmail(String email) throws IOException{
		String query = "SELECT * FROM User WHERE email='%s'";
		ResultSet rs = Execute.getResultSet(String.format(query, email));
		try {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i sQL!");
		}
	}

	private void showWeek() throws IOException {
		List<Appointment> appointments = AppointmentHandler.getWeekAppointments(user.getId(), weekNum);
		List<Appointment> apps = PersonHandler.getFollowAppointments(user.getId(), weekNum);
		for (Appointment ap: apps) System.out.println("hei");
		appointments.addAll(apps);
		Collections.sort(appointments);
		Day previous = null;
		System.out.printf("************* UKE %d ********************\n", weekNum);
		for(Appointment app: appointments){
			Day thisDay = Day.fromDate(app.getStartTime());
			if (thisDay != previous){
				System.out.println("-----" + thisDay + "-----");
			}
			System.out.println(app);
			previous = thisDay;
		}
		System.out.println("****************************************");
	}
	
	private void showAppointment() throws IOException {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");		
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hvilken avtale vil du se? ");
		int appointmentNo = scanner.nextInt();
		Appointment app = appointments.get(appointmentNo);
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
	
	public void changeAppointment(Appointment app) throws IOException{
		Scanner scanner = new Scanner(System.in);
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		System.out.print("Skriv inn tittel: ");
		String title = scanner.nextLine();
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		System.out.print("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ");
		boolean isPrivate = scanner.nextLine().isEmpty();
		Map<Person, Boolean> participants = getParticipants();
		System.out.println("Skriv inn beskrivelse: ");
		String description = scanner.nextLine();
		
		app.setTitle(title);
		app.setStartTime(startdate);
		app.setEndTime(enddate);
		app.setPrivate(isPrivate);
		app.setParticipants(participants);
		app.setDescription(description);
		
		if (participants != null){
			System.out.println("Vil du reservere m¿terom? (ja/nei): ");
			String reserve = scanner.nextLine();
			if (reserve.equalsIgnoreCase("ja")){
				String roomName = reserveRoom(startdate, enddate, participants);
				app.setRoomName(roomName);
			} else {
				System.out.println("Skriv inn sted: ");
				String place = scanner.nextLine();
				app.setPlace(place);
			}
		} else {
			System.out.println("Skriv inn sted: ");
			String place = scanner.nextLine();
			app.setPlace(place);
		}
		app.save();
		System.out.println("Avtalen er endret.");
	}
	
	private void changeAppointment() throws IOException {
		long userId = user.getId();
		List<Appointment> appointments = AppointmentHandler.getAllCreated(userId);
		if (appointments != null){
			for (int i=0; i<appointments.size(); i++) {
				System.out.println(i+". "+appointments.get(i));
			}
			System.out.println("Velg hvilken avtale du vil endre: ");
			Scanner scannerInt = new Scanner(System.in);
			int change = scannerInt.nextInt();
			Appointment app = appointments.get(change);
			changeAppointment(app);
		}
	}

	private void deleteAppointment() throws IOException {
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		System.out.println("Velg hvilken avtale du vil slette: ");
		Scanner scanner = new Scanner(System.in);
		int delete = scanner.nextInt();
		long appId = appointments.get(delete).getAppId();
		Appointment ap = AppointmentHandler.getAppointment(appId);
		deleteAppointment(ap);
	}
	
	public void deleteAppointment(Appointment app) throws IOException{
		if (user.getId() == app.getCreator().getId()){
			app.deleteAppointment();
		}else {
			app.deleteAppointmentInvited();
		}
		System.out.println("Avtalen er slettet");
	}
	
	private List<Appointment> getAllAppointmentsInvolved(long userId) throws IOException {
		List<Appointment> appointments = new ArrayList<Appointment>();
		appointments = AppointmentHandler.getAllCreated(userId);
		for (Appointment app: AppointmentHandler.getAllInvited(userId)) {
			appointments.add(app);
		}
		for (int i=0; i<appointments.size(); i++) {
			System.out.println(i+". "+appointments.get(i));
		}
		
		return appointments;
	}
	private void addNewAppointment() throws IOException {
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		Scanner scanner = new Scanner(System.in);
		System.out.print("Skriv inn tittel: ");
		String title = scanner.nextLine();
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		System.out.print("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter. ");
		boolean isPrivate = scanner.nextLine().isEmpty();
		Map<Person, Boolean> participants = getParticipants();
		
		Appointment ap = new Appointment(title, startdate, enddate, isPrivate, participants, user);
		if (participants != null){
			System.out.println("Vil du reservere m¿terom? (ja/nei): ");
			String reserve = scanner.nextLine();
			if(reserve.equalsIgnoreCase("ja")){
				String roomName = reserveRoom(startdate, enddate, participants);
				ap.setRoomName(roomName);
			}else{
				System.out.println("Skriv inn sted: ");
				String place = scanner.nextLine();
				ap.setPlace(place);
			}
		}else{
			System.out.println("Skriv inn sted: ");
			String place = scanner.nextLine();
			ap.setPlace(place);
		}
		ap.save();
		System.out.println("Ny avtale lagret!");
	}
	
	private String reserveRoom(Date startdate, Date enddate, Map<Person, Boolean> participants) throws IOException {
		List<Room> rooms = RoomHandler.availableRooms(startdate, enddate, participants.size());
		int i = 0;
		System.out.println("Reserver m¿terom: ");
		for (Room room: rooms) {
			System.out.println(i+" "+room.getName());
			i++;
		}
		Scanner scanner = new Scanner(System.in);
		int choosenRoom = scanner.nextInt();
		return rooms.get(choosenRoom).getName();
	}
	
	private Map<Person, Boolean> getParticipants() throws IOException{
		Map<Person, Boolean> map = new HashMap<Person, Boolean>();
		Scanner scanner = new Scanner(System.in);
		String email;
		while (true) {
			System.out.print("Enter email to invite to the event: ");
			email = scanner.nextLine();
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
	
	private Date parseDate(String format, String inputText){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print(inputText);
			try {
				java.util.Date date = sdf.parse(scanner.nextLine());
				Date otherDate = new Date(date.getTime());
				return otherDate;
			} catch (ParseException e) {
				System.out.println("Klarte ikke lese datoen din, vennligst prøv på nytt.");
			}
		}
	}

	private static Person authenticateUser() throws IOException {
		String email = getEmail();
		String password = getPassword();
		Person user = authenticationHelper(email, password);
		return user;
	}
	
	private static Person authenticationHelper(String email, String password) throws IOException{
		String salt = getSalt(email);
		String passwordHash = createHash(password, salt);
		Person user = getUserFromEmailAndPassword(email, passwordHash);
		return user;
	}
	
	/**
	 * Kjøres SERVERSIDE!
	 */
	private static Person getUserFromEmailAndPassword(String email, String passwordHash) throws IOException{
		long id = getUserIdFromEmail(email);
		Person user = PersonHandler.getPerson(id);
		if (user.getPasswordHash().equals(passwordHash)){
			return user;
		} else {
			throw new IllegalArgumentException("Wrong password!");
		}
	}
	
	private static long getUserIdFromEmail(String email) throws IOException{
		String query = "SELECT userId FROM User WHERE email='%s'";
		long id;
		id = Execute.executeGetLong(String.format(query, email));
		return id;
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt;
		salt = Execute.executeGetString(String.format(query, email));
		return salt;
	}
	
	private static Person getPersonFromEmail(String email) throws IOException{
		long id = getUserIdFromEmail(email);
		Person p = PersonHandler.getPerson(id);
		return p;
	}
	
	private static String getEmail(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Skriv inn e-post: ");
		String email = scanner.nextLine();
		return email;
	}
	
	private static String getPassword(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Skriv inn passord: ");
		String password = scanner.nextLine();
		return password;
	}

}
