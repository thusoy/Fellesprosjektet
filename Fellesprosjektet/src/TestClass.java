import java.util.Set;
import java.util.TreeSet;

import calendar.Day;

public class TestClass {
	
	public static void main(String[] args) {
		Set<Day> days = new TreeSet<Day>();
		days.add(Day.MONDAY);
		days.add(Day.FRIDAY);
		String output = days.toString();
		output = output.substring(1, output.length()-1);
		System.out.println(output);
		
		String[] parts = output.split(", ");
		Set<Day> finalSet = new TreeSet<Day>();
		for(String part: parts){
			
			System.out.println(part);
		}
		Day.
		
		
		
//		Person john = new Person("john", "john@company.com", new Date(637282800L));
//		Calendar cal = new Calendar(john);
//		Date start = new Date(System.currentTimeMillis());
//		Date end = new Date(System.currentTimeMillis() + 2700);
//		Appointment app = new Appointment("tannlege", start, end, false, null);
//		cal.addAppointment(app);
//		cal.save();
	}
}