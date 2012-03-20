package client;

import static hashtools.Hash.createHash;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.Execute;
import server.PersonHandler;
import sun.misc.Perf.GetPerfAction;
import calendar.Appointment;

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
		setUp();
		do {
			try {
				Person user = authenticateUser();
				this.user = user;
				System.out.println("Auth OK!");
				run(user);
			} catch(IllegalArgumentException e){
				System.out.println("Ugyldig kombinasjon av passord og brukernavn, prøv igjen.");
			}
		} while (user == null);
	}

	private void run(Person user) throws IOException{
		showWeek();
		CalendarFunction[] allFunc = CalendarFunction.values();
		System.out.println("Hva vil du gjøre?");
		while(true){
			for(int i = 0; i < allFunc.length; i++){
				CalendarFunction cf = allFunc[i];
				System.out.println(String.format("%d. %s", i + 1, cf.description));
			}
			CalendarFunction cf = CalendarFunction.getUserFunction();
			if (cf == CalendarFunction.QUIT){
				break;
			}
			run(cf);
		}
		System.out.println("Ha en fortsatt fin dag!");
	}
	
	private int getCurrentWeekNum() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public void run(CalendarFunction func) throws IOException{
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
		case SHOW_WEEK:
			getAndShowWeek();
			break;
		case FOLLOW_CALENDAR:
			followCalendar();
			break;
		}
	}
	
	private void getAndShowWeek() throws IOException {
		Scanner scn = new Scanner(System.in);
		int weekNum =  scn.nextInt();
		this.weekNum = weekNum;
		showWeek();
	}

	private void followCalendar() throws IOException {
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
		try {
			return Execute.getResultSet(String.format(query, email)).next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}

	private void showWeek() throws IOException {
		
		List<Appointment> appointments = AppointmentHandler.getWeekAppointments(user.getId(), weekNum);
		for(Appointment app: appointments){
			System.out.println(app);
		}
	}

	private void changeAppointment() {
		
	}

	private void deleteAppointment() {
		
	}

	private void addNewAppointment() {
		String dateTimeFormat = "dd-MM-yyyy HH:mm";
		Scanner scanner = new Scanner(System.in);
		System.out.print("Skriv inn tittel: ");
		String title = scanner.nextLine();
		Date startdate = parseDate(dateTimeFormat, String.format("Startdato (%s): ", dateTimeFormat));
		Date enddate = parseDate(dateTimeFormat, String.format("Sluttdato (%s): ", dateTimeFormat));
		System.out.println("Hvis avtalen er privat, skriv 'ja'. Hvis ikke, trykk på enter.");
		boolean isPrivate = scanner.nextLine().isEmpty();
		Map<Person, Boolean> participants = getParticipants();
//		Appointment app = new Appointment();
	}
	
	private Map<Person, Boolean> getParticipants(){
		Map<Person, Boolean> map = new HashMap<Person, Boolean>();
		String email;
		Scanner scanner = new Scanner(System.in);
		do {
			email = scanner.nextLine();
			Person p = getPersonFromEmail(email);
		}
	}
	
	private Date parseDate(String format, String inputText){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print(inputText);
			try {
				Date date = (Date) sdf.parse(scanner.nextLine());
				return date;
			} catch (ParseException e) {}
		}
	}

	private static void setUp() throws IOException{
		String query = "TRUNCATE TABLE User";
		try {
			Execute.executeUpdate(query);
		} catch (SQLException e){
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL");
		}
		new Person("tarjei", "husøy", "tarjei@roms.no", "komtek", "lol", false);
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
		try {
			id = Execute.executeGetLong(String.format(query, email));
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	private static String getSalt(String email) throws IOException{
		String query = "SELECT salt FROM User WHERE email='%s'";
		String salt;
		try {
			salt = Execute.executeGetString(String.format(query, email));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
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
		return "tarjei@roms.no"; //email;
	}
	
	private static String getPassword(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Skriv inn passord: ");
		String password = scanner.nextLine();
		return "lol"; //password;
	}

}
