package client;


public enum CalendarFunction {
	ADD_APPOINTMENT ("Legg til avtale"),
	SHOW_APPOINTMENT ("Vis avtale"),
	CHANGE_APPOINTMENT ("Endre avtale"),
	DELETE_APPOINTMENT ("Slett avtale"),
	SHOW_WEEK ("Vis uke"),
	FOLLOW_CALENDAR ("Følg annen kalender"),
	SHOW_INVITES ("Vis møteinnkallinger"),
	SHOW_MESSAGES ("Vis meldinger"),
	SHOW_NEXT_WEEK ("Vis neste uke"),
	SHOW_PREVIOUS_WEEK ("Vis forrige uke"),
	QUIT ("Avslutt");
	;
	
	public final String description;
	
	private CalendarFunction(String description){
		this.description = description;
	}
	
}
