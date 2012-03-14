package calendar;

public enum Day{
	MONDAY("mandag"), TUESDAY("tirsdag"), WEDNESDAY("onsdag"), 
	THURSDAY("torsdag"), FRIDAY("fredag"), SATURDAY("lørdag"), SUNDAY("søndag");
	
	String norwegianRepr;
	
	Day(String norwegian){
		this.norwegianRepr = norwegian;
	}
	
	public Day fromString(String day){
		for(Day d: Day.values()){
			if (d.norwegianRepr.equalsIgnoreCase(day)){
				return d;
			}
		}
		throw new IllegalArgumentException("No such day!");
	}
}
