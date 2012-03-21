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
import java.util.Set;
import java.util.TreeSet;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.Execute;
import server.MessageHandler;
import server.PersonHandler;
import server.RoomHandler;
import calendar.Appointment;
import calendar.Day;
import calendar.Message;
import calendar.RejectedMessage;
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
				System.out.println("Ugyldig kombinasjon av passord og brukernavn, pr�v igjen.");
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
			System.out.println("Hva vil du gj�re?");
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
		int userInput = getValidNum(unreadMessages.size());
		Message selected = unreadMessages.get(userInput-1);
		System.out.println(selected.showMessage(user).getContent());
		if (selected instanceof RejectedMessage){
			RejectedMessage m = (RejectedMessage) selected;
			System.out.println("Hva vil du gj�re?");
			m.getAndExecuteUserResponse(this);
		}
	}

	private void showInvites() throws IOException {
		List<Appointment> allInvited = AppointmentHandler.getAllUnansweredInvites(user.getId());
		for(int i = 0; i < allInvited.size(); i++){
			System.out.printf("%d. %s\n", i + 1, allInvited.get(i));
		}
		System.out.print("Hvilken innkalling vil du svare p�? ");
		int userNum = getValidNum(allInvited.size());
		answerInvite(allInvited.get(userNum-1));
	}
	
	/**
	 * Gets a valid integer from the user, 1 <= num <= upperInclusiveBound.
	 * @param upperInclusiveBound
	 */
	private int getValidNum(int upperInclusiveBound){
		Scanner scanner = new Scanner(System.in);
		int input;
		while(true){
			try {
				System.out.print("Gj�r et valg: ");
				input = scanner.nextInt();
				if (1 <= input && input <= upperInclusiveBound){
					break;
				}
			} catch (Exception e){ 
				System.out.println("Beklager, det du skrev inn er ikke et gyldig valg. Pr�v igjen.");
			}
		}
		return input;
	}
	
	private void answerInvite(Appointment appointment) throws IOException {
		while(true){
			String input = getString("Hva vil du svare? ('ja', 'nei', eller enter for � utsette) ");
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
			System.out.println("Beklager, jeg skj�nte ikke svaret ditt. Pr�v igjen.");
		}
		getString("Trykk enter for � g� videre.");
	}


	private void getAndShowWeek() throws IOException {
		System.out.print("Skriv inn en uke du vil vise: ");
		int weekNum =  getValidNum(52);
		this.weekNum = weekNum;
	}

	private void followCalendar() throws IOException {
		System.out.print("Skriv inn e-postadressen til personen du vil f�lge: ");
		String email = getValidEmail();
		long otherUserId = getUserIdFromEmail(email);
		user.followPerson(otherUserId);
	}

	private static String getValidEmail() throws IOException {
		String email;
		do {
			email = getString("Skriv inn e-postadresse: ");
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
		List<Appointment> appointments = getWeekAppointments();
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
	
	private List<Appointment> getWeekAppointments() throws IOException{
		List<Appointment> appointments = AppointmentHandler.getAllCreated(user.getId(), weekNum);
		List<Appointment> app = AppointmentHandler.getAllInvited(user.getId(), weekNum);	
		List<Appointment> apps = PersonHandler.getFollowAppointments(user.getId(), weekNum);
		appointments.addAll(apps);
		appointments.addAll(app);
		Collections.sort(appointments);		
		return appointments;
	}
	
	private List<Appointment> getAllAppointments() throws IOException{
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
	
	private void showAppointment() throws IOException {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");		
		List<Appointment> appointments =  getAllAppointments();
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
	
	private void changeAppointment() throws IOException {
		List<Appointment> appointments = AppointmentHandler.getAllCreated(user.getId());
		for (int i=0; i<appointments.size(); i++) {
			System.out.printf("%d. %s\n", i+1, appointments.get(i));
		}
		int change = getValidNum(appointments.size());
		Appointment app = appointments.get(change-1);
		changeAppointment(app);
	}
	
	public void changeAppointment(Appointment app) throws IOException{
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk p� enter. ").isEmpty();
		
		app.setTitle(getString("Skriv inn tittel: "));
		app.setStartTime(startdate);
		app.setEndTime(enddate);
		app.setPrivate(isPrivate);
		app.setParticipants(getParticipants());
		app.setDescription(getString("Skriv inn beskrivelse: "));
		setRoomOrPlace(app);
		
		app.save();
		System.out.println("Avtalen er endret.");
	}
	
	private void deleteAppointment() throws IOException {
		long userId = user.getId();
		List<Appointment> appointments = getAllAppointmentsInvolved(userId);
		int delete = getValidNum(appointments.size());
		long appId = appointments.get(delete).getAppId();
		Appointment app = AppointmentHandler.getAppointment(appId);
		deleteAppointment(app);
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
		List<Appointment> appointments = AppointmentHandler.getAllCreated(userId);
		List<Appointment> app = AppointmentHandler.getAllInvited(userId);
		appointments.addAll(app);
		return appointments;
	}
	
	private void addNewAppointment() throws IOException {
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		String title = getString("Skriv inn tittel: ");
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		boolean isPrivate = !getString("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk p� enter. ").isEmpty();
		Map<Person, Boolean> participants = getParticipants();
		
		Appointment app = new Appointment(title, startdate, enddate, isPrivate, participants, user);
		setRoomOrPlace(app);
		app.save();
		System.out.println("Ny avtale lagret!");
	}
	
	private void setRoomOrPlace(Appointment app) throws IOException{
		if (app.getParticipants() != null){
			String reserve = getString("Vil du reservere m�terom? (ja/nei): ");
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
	
	private String reserveRoom(Date startdate, Date enddate, Map<Person, Boolean> participants) throws IOException {
		List<Room> rooms = RoomHandler.availableRooms(startdate, enddate, participants.size());
		System.out.println("Reserver m�terom: ");
		for (int i = 0; i < rooms.size(); i++) {
			System.out.printf("%d. %s\n", i+1, rooms.get(i).getName());
		}
		int choosenRoom = getValidNum(rooms.size());
		return rooms.get(choosenRoom).getName();
	}
	
	private Map<Person, Boolean> getParticipants() throws IOException{
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
	
	private Date parseDate(String format, String inputText){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		while (true) {
			try {
				java.util.Date date = sdf.parse(getString(inputText));
				Date otherDate = new Date(date.getTime());
				return otherDate;
			} catch (ParseException e) {
				System.out.println("Klarte ikke lese datoen din, vennligst pr�v p� nytt.");
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
	 * Kj�res SERVERSIDE!
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
		long id = Execute.executeGetLong(String.format(query, email));
		return id;
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt = Execute.executeGetString(String.format(query, email));
		return salt;
	}
	
	private static Person getPersonFromEmail(String email) throws IOException{
		long id = getUserIdFromEmail(email);
		Person p = PersonHandler.getPerson(id);
		return p;
	}
	
	private static String getEmail(){
		return getString("Skriv inn e-post: ");
	}
	
	private static String getPassword(){
		return getString("Skriv inn passord: ");
	}
	
	private static String getString(String display){
		Scanner scanner = new Scanner(System.in);
		System.out.print(display);
		return scanner.nextLine();
	}

}
