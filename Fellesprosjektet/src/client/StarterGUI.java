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
import static client.helpers.IO.promptChoice;
import static client.helpers.StringUtils.center;
import static client.helpers.StringUtils.padLeft;
import static client.helpers.StringUtils.padRight;
import static client.helpers.StringUtils.repeat;
import static dateutils.DateUtils.getCurrentWeekNum;
import gui.SimpleTextGUI;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import server.AppointmentHandler;
import server.MessageHandler;
import calendar.Appointment;
import calendar.DBCommunicator;
import calendar.Message;
import calendar.Person;
import calendar.RejectedMessage;
import client.helpers.InvalidLoginException;
import client.helpers.UserAbortException;
import dateutils.Day;

public class StarterGUI extends DBCommunicator{
	
	private SimpleTextGUI gui;
	private Person user;
	private int weekNum;
	private static AppointmentHandler appHandler;
	private static MessageHandler msgHandler;
	
	static {
		appHandler = (AppointmentHandler) getHandler(AppointmentHandler.SERVICE_NAME);
		msgHandler = (MessageHandler) getHandler(MessageHandler.SERVICE_NAME);
//		try {
//			new Person("trine", "myklebust", "trine@gmail.com", "indøk", "trine");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		try {
			(new StarterGUI()).initAndLogin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private StarterGUI(){
		weekNum = getCurrentWeekNum();
	}
	
	private void initAndLogin() throws IOException {
		gui = new SimpleTextGUI();
		printAsciiArt("Login");
		Person user = null;
		while (user == null) {
			try {
				user = authenticateUser();
			} catch (InvalidLoginException e) {
				System.out.println(e.getMessage());
			} catch (UserAbortException e) {
				System.out.println("Ha en fortsatt fin dag!");
				System.exit(0);
			}
		}
		this.user = user;
		run();
	}
	
	private void run() throws IOException{
		gui.clear();
		CalendarFunction[] allFunc = CalendarFunction.values();
		while(true){
			showWeek();
			int numUnansweredMeetings = appHandler.getAllUnansweredInvites(user.getId()).size();
			int numNewMessages = msgHandler.getUnreadMessagesForUser(user).size();
			System.out.println("Hva vil du gjøre?");
			List<String> choices = new ArrayList<String>();
			for(CalendarFunction cf: allFunc){
				String description = cf.description;
				if (cf == CalendarFunction.SHOW_INVITES && numUnansweredMeetings > 0){
					description += String.format("(%d)", numUnansweredMeetings);
				} else if (cf == CalendarFunction.SHOW_MESSAGES && numNewMessages > 0){
					description += String.format("(%d)", numNewMessages);
				}
				choices.add(description);
			}
			try {
				SimpleTextGUI.setInputText("Gjør et valg: ");
				CalendarFunction cf = allFunc[promptChoice(choices)];
				if (cf == CalendarFunction.QUIT){
					gui.clear();
					System.out.println("Ha en fortsatt fin dag!");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
				runFunc(cf);
			} catch (UserAbortException e) {
			}
			gui.clear();
		}
	}
	
	public void runFunc(CalendarFunction func) throws IOException, UserAbortException{
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
			showWeek();
			break;
		case SHOW_MESSAGES:
			showMessages();
			break;
		case SHOW_PREVIOUS_WEEK:
			weekNum--;
			showWeek();
			break;
		}
	}
	
	private void showMessages() throws IOException, UserAbortException {
		List<Message> unreadMessages = msgHandler.getUnreadMessagesForUser(user);
		if (unreadMessages.size() > 0){
			System.out.println("Hvilken melding vil du lese?");
			int userInput = promptChoice(unreadMessages);
			Message selected = unreadMessages.get(userInput);
			System.out.println(selected.showMessage(user).getContent());
			
			if (selected instanceof RejectedMessage){
				RejectedMessage m = (RejectedMessage) selected;
				System.out.println("Hva vil du gjøre?");
				m.getAndExecuteUserResponse(user);
			}
			getString("Trykk enter for å gå videre.");
		} else {
			System.out.println("Ingen nye meldinger!");
		}
	}

	private void showInvites() throws IOException, UserAbortException {
		List<Appointment> allInvited = appHandler.getAllUnansweredInvites(user.getId());
		System.out.print("Hvilken innkalling vil du svare på? ");
		int userNum = promptChoice(allInvited);
		answerInvite(allInvited.get(userNum));
	}
	
	private void answerInvite(Appointment appointment) throws IOException, UserAbortException {
		while(true){
			String input = getString("Hva vil du svare? ('ja', 'nei', eller enter for å utsette) ");
			if (input.equalsIgnoreCase("ja")){
				appointment.answerInvite(user, true);
				System.out.printf("Du har bekreftet avtalen %s.\n", appointment);
				break;
			} else if (input.equalsIgnoreCase("nei")){
				appointment.answerInvite(user, false);
				System.out.printf("Du har takket nei til avtalen %s.\n", appointment);
				break;
			} else if (input.isEmpty()){
				break;
			}
			System.out.println("Beklager, jeg skjønte ikke svaret ditt. Prøv igjen.");
		}
		getString("Trykk enter for å gå videre.");
	}
	
	private void getAndShowWeek() throws IOException, UserAbortException {
		System.out.print("Skriv inn en uke du vil vise: ");
		int weekNum =  getValidNum(52);
		this.weekNum = weekNum;
		showWeek();
	}

	private void followCalendar() throws IOException, UserAbortException {
		String email = getValidEmail("Skriv inn e-postadressen til personen du vil følge: ");
		long otherUserId = getUserIdFromEmail(email);
		user.followPerson(otherUserId);
	}

	private void showWeek() throws IOException {
		List<Appointment> appointments = getWeekAppointments(user, weekNum);
		List<String>[] dayEvents = splitAppointmentsByDay(appointments);
		printCalendar(dayEvents);
	}
	
	/**
	 * Splits the appointments given into seven lists, one for each day.
	 * @param appointments
	 */
	private List<String>[] splitAppointmentsByDay(List<Appointment> appointments){
		@SuppressWarnings("unchecked")
		List<String>[] days = (List<String>[]) new ArrayList[7];
		for(int i = 0; i < Day.values().length; i++){
			days[i] = new ArrayList<String>();
		}
		Day previous = null;
		String timeFormat = "H:mm";
		DateFormat df = new SimpleDateFormat(timeFormat);
		int index = 0;
		for(Appointment app: appointments){
			Day thisDay = Day.fromDate(app.getStartTime());
			if (thisDay != previous){
				index = thisDay.dayInWeek() - 1;
			}
			days[index].add(String.format("%s - %s", padLeft(df.format(app.getStartTime()), 5), app.getTitle()));
			previous = thisDay;
		}
		return days;
	}
	
	private void printCalendar(List<String>[] dayEvents){
		printAsciiArt(String.format("Uke %d", weekNum));
		int columnWidth = 25;
		boolean foundMore = true;
		StringBuilder first = new StringBuilder();
		for(Day d: Day.values()){
			first.append(center(d.toString(), columnWidth, '_') + "|");
		}
		System.out.println(first);
		while(foundMore){
			foundMore = false;
			StringBuilder line = new StringBuilder();
			for(List<String> list: dayEvents){
				if (list.size() > 0){
					line.append(padRight(list.get(0), columnWidth));
					list.remove(0);
					foundMore = true;
				} else {
					line.append(repeat(" ", columnWidth));
				}
			}
			System.out.println(line.toString());
		}
	}
	
}
