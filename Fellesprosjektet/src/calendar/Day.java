package calendar;

import java.util.Set;
import java.util.TreeSet;

public enum Day{
	
	MONDAY("mandag"), TUESDAY("tirsdag"), WEDNESDAY("onsdag"), 
	THURSDAY("torsdag"), FRIDAY("fredag"), SATURDAY("l�rdag"), SUNDAY("s�ndag");
	
	private String norwegian;
	
	Day(String norwegian){
		this.norwegian = norwegian;
	}
	
	public String getNorwegian(){
		return norwegian;
	}
	
	public static Day fromString(String day){
		for (Day d: Day.values()){
			if (d.name().equalsIgnoreCase(day)){
				return d;
			}
		}
		throw new IllegalArgumentException("Invalid day: " + day);
	}
	
	public static Set<Day> fromSetString(String stringFromSet){
		if (stringFromSet == null || stringFromSet.length() < 2){
			return new TreeSet<Day>();
		}
		String days = stringFromSet.substring(1, stringFromSet.length()-1); 
		Set<Day> output = new TreeSet<Day>();
		String[] dayArray = days.split(", ");
		for(String dayString: dayArray){
			try{
				Day day = Day.fromString(dayString);
				output.add(day);
			} catch (IllegalArgumentException e){}
		}
		return output;
	}
}
