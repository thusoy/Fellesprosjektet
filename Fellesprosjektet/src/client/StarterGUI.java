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
import gui.SimpleTextGUI;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import server.AppointmentHandlerImpl;
import server.MessageHandler;
import calendar.Appointment;
import calendar.Message;
import calendar.Person;
import calendar.RejectedMessage;
import client.helpers.InvalidLoginException;
import client.helpers.UserAbortException;
import dateutils.Day;

public class StarterGUI {
	
	private SimpleTextGUI gui;
	private Person user;
	private int weekNum;
	
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
		gui.clear();
		run();
	}
	
	private void run() throws IOException{
		CalendarFunction[] allFunc = CalendarFunction.values();
		while(true){
			showWeek();
			int numUnansweredMeetings = AppointmentHandlerImpl.getAllUnansweredInvites(user.getId()).size();
			int numNewMessages = MessageHandler.getUnreadMessagesForUser(user).size();
			SimpleTextGUI.setInputText("Hva vil du gjøre?");
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
	
	private int getCurrentWeekNum() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.WEEK_OF_YEAR);
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
		List<Message> unreadMessages = MessageHandler.getUnreadMessagesForUser(user);
		System.out.print("Hvilken melding vil du lese? \n");
		int userInput = promptChoice(unreadMessages);
		Message selected = unreadMessages.get(userInput);
		System.out.println(selected.showMessage(user).getContent());
		if (selected instanceof RejectedMessage){
			RejectedMessage m = (RejectedMessage) selected;
			System.out.println("Hva vil du gjøre?");
			m.getAndExecuteUserResponse(user);
		}
	}

	private void showInvites() throws IOException, UserAbortException {
		List<Appointment> allInvited = AppointmentHandlerImpl.getAllUnansweredInvites(user.getId());
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
		List<String>[] days = (List<String>[]) new ArrayList[7];
		for(int i = 0; i < Day.values().length; i++){
			days[i] = new ArrayList<String>();
		}
		Day previous = null;
		String timeFormat = "H:mm";
		DateFormat df = new SimpleDateFormat(timeFormat);
		printAsciiArt(String.format("Uke %d", weekNum));
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
	
	private static void printCalendar(List<String>[] dayEvents){
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
	
	private static String center(String input, int width, char fillChar){
		int length = input.length();
		if (length < width){
			int padding = (width - length)/2;
			String blank = repeat(Character.toString(fillChar), padding);
			String output = blank + input + blank; 
			boolean isOdd = width % 2 == 0;
			return isOdd ? output : output.substring(0, width-1);
		} else {
			return input.substring(0, width-3) + "...";
		}
	}
	
	private static String padRight(String input, int width){
		int length = input.length();
		if (length <= width){
			int padding = width - length;
			String blank = repeat(" ", padding);
			String output = input + blank; 
			return output;
		} else {
			return input.substring(0, width-4) + "... ";
		}
	}
	
	private static String padLeft(String input, int width){
		int length = input.length();
		if (length <= width){
			int padding = width - length;
			String blank = repeat(" ", padding);
			String output = blank + input; 
			return output;
		} else {
			return input.substring(0, width-4) + "... ";
		}
	}
	
	private static String repeat(String input, int times){
		if (times <= 0){
			return "";
		}
		return String.format(String.format("%%0%dd", times), 0).replace("0", input);
	}

}
