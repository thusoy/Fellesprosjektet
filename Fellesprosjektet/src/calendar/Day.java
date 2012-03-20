package calendar;

import java.sql.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.Calendar;

public enum Day{
	
	MONDAY("mandag"), TUESDAY("tirsdag"), WEDNESDAY("onsdag"), 
	THURSDAY("torsdag"), FRIDAY("fredag"), SATURDAY("lørdag"), SUNDAY("søndag");
	
	private String norwegian;
	
	Day(String norwegian){
		this.norwegian = norwegian;
	}
	
	public static Day fromString(String day){
		for (Day d: Day.values()){
			if (d.name().equalsIgnoreCase(day) || d.norwegian.equalsIgnoreCase(day)){
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
	
	public String toString(){
		return norwegian;
	}

	public static Day fromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayNum = cal.get(Calendar.DAY_OF_WEEK);
		return Day.values()[ (dayNum-2)%7 ];
	}
}
