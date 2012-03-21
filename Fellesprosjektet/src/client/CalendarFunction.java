package client;

import java.util.Scanner;

public enum CalendarFunction {
	ADD_APPOINTMENT ("Legg til avtale"),
	DELETE_APPOINTMENT ("Slett avtale"),
	CHANGE_APPOINTMENT ("Endre avtale"),
	SHOW_WEEK ("Vis uke"),
	FOLLOW_CALENDAR ("Følg annen kalender"),
	SHOW_NEXT_WEEK ("Vis neste uke"),
	SHOW_PREVIOUS_WEEK ("Vis forrige uke"),
	SHOW_INVITES ("Vis møteinnkallinger"),
	SHOW_MESSAGES ("Vis meldinger"),
	QUIT ("Avslutt");
	;
	
	public final String description;
	
	private CalendarFunction(String description){
		this.description = description;
	}
	
	public static CalendarFunction getUserFunction(){
		CalendarFunction[] allFunc = CalendarFunction.values();
		int input;
		Scanner scanner = new Scanner(System.in);
		do {
			input = scanner.nextInt();
		} while (input <= 0 && input > allFunc.length);
		return allFunc[input-1];
	}
}
