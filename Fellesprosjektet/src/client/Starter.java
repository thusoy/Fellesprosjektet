package client;

import static ascii.Art.printAsciiArt;
import static client.helpers.AppointmentHelper.addNewAppointment;
import static client.helpers.AppointmentHelper.changeAppointment;
import static client.helpers.AppointmentHelper.deleteAppointment;
import static client.helpers.AppointmentHelper.getWeekAppointments;
import static client.helpers.AppointmentHelper.showAppointment;
import static client.helpers.AuthHelper.authenticateUser;
import static client.helpers.DBHelper.getUserIdFromEmail;
import static client.helpers.IO.getString;
import static client.helpers.IO.getValidEmail;
import static client.helpers.IO.getValidNum;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import no.ntnu.fp.model.Person;
import server.AppointmentHandler;
import server.MessageHandler;
import calendar.Appointment;
import calendar.Day;
import calendar.Message;
import calendar.RejectedMessage;
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
		printAsciiArt("Velkommen!");
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
			System.out.println(String.format(String.format("%%0%dd", 30), 0).replace("0","\n"));
			int numUnansweredMeetings = AppointmentHandler.getAllUnansweredInvites(user.getId()).size();
			int numNewMessages = MessageHandler.getUnreadMessagesForUser(user).size();
			showWeek();
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
			addNewAppointment(user);
			break;
		case DELETE_APPOINTMENT:
			deleteAppointment(user);
			break;
		case CHANGE_APPOINTMENT:
			changeAppointment(user);
			break;
		case SHOW_APPOINTMENT:
			showAppointment(user);
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
			System.out.println("Hva vil du gjøre?");
			m.getAndExecuteUserResponse(this);
		}
	}

	private void showInvites() throws IOException {
		List<Appointment> allInvited = AppointmentHandler.getAllUnansweredInvites(user.getId());
		for(int i = 0; i < allInvited.size(); i++){
			System.out.printf("%d. %s\n", i + 1, allInvited.get(i));
		}
		System.out.print("Hvilken innkalling vil du svare på? ");
		int userNum = getValidNum(allInvited.size());
		answerInvite(allInvited.get(userNum-1));
	}
	

	
	private void answerInvite(Appointment appointment) throws IOException {
		while(true){
			String input = getString("Hva vil du svare? ('ja', 'nei', eller enter for å utsette) ");
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
		getString("Trykk enter for å gå videre.");
	}


	private void getAndShowWeek() throws IOException {
		System.out.print("Skriv inn en uke du vil vise: ");
		int weekNum =  getValidNum(52);
		this.weekNum = weekNum;
	}

	private void followCalendar() throws IOException {
		String email = getValidEmail("Skriv inn e-postadressen til personen du vil følge: ");
		long otherUserId = getUserIdFromEmail(email);
		user.followPerson(otherUserId);
	}

	private void showWeek() throws IOException {
		List<Appointment> appointments = getWeekAppointments(user, weekNum);
		Day previous = null;
		String timeFormat = "HH:mm";
		DateFormat df = new SimpleDateFormat(timeFormat);
		printAsciiArt(String.format("Uke %d", weekNum));
		for(Appointment app: appointments){
			Day thisDay = Day.fromDate(app.getStartTime());
			if (thisDay != previous){
				System.out.println(thisDay);
			}
			if (app.getCreator().equals(user)){
				System.out.printf("\t%s %s\n", df.format(app.getStartTime()), app.getTitle());
			} else {
				System.out.printf("\t%s %s (%s)\n", df.format(app.getStartTime()), app.getTitle(), app.getCreator().fullName());
			}
			previous = thisDay;
		}
		System.out.println("****************************************");
	}
	
}
