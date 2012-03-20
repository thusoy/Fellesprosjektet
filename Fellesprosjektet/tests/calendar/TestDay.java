package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class TestDay {

	@Test
	public void testNorwegianNames(){
		assertEquals("Det skal v�re 7 forskjellige instanser av Day.", Day.values().length, 7);
		String[] norwegian = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", 
				"L�rdag", "S�ndag"};
		Day[] days = Day.values();
		for(int i = 0; i < days.length; i++){
			assertEquals(norwegian[i].toLowerCase(), days[i].toString());
		}
	}
	
	@Test
	public void testFromString(){
		String teststring = "MONDAY";
		assertEquals(Day.MONDAY, Day.fromString(teststring));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testFromStringEmpty(){
		String teststring = "";
		Day.fromString(teststring);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testFromStringNull(){
		String teststring = null;
		Day.fromString(teststring);
	}
	
	@Test
	public void testCorrectFromSetString() {
		TreeSet<Day> set = new TreeSet<Day>();
		set.add(Day.MONDAY);
		set.add(Day.TUESDAY);
		String teststring = set.toString();
		Set<Day> days = Day.fromSetString(teststring);
		String feedback = String.format("Det skal v�re to elementer i days etter � ha testet med strengen '%s'", teststring);
		assertEquals(feedback, 2, days.size());
		assertTrue("Mandag m� ligge i settet som returneres", days.contains(Day.MONDAY));
		assertTrue("Tirsdag m� ligge i settet som returneres", days.contains(Day.TUESDAY));
	}
	
	@Test
	public void testWrongFromSetString(){
		String teststring = "LUNDI, MERCREDI, ";
		Set<Day> days = Day.fromSetString(teststring);
		assertEquals("Med ugyldig input skal settet v�re tomt!", 0, days.size());
	}
	
	@Test
	public void testWrongFromSetString2(){
		String teststring = "Dette er en lengre bullshittekst om ingenting!";
		Set<Day> days = Day.fromSetString(teststring);
		assertEquals("Med ugyldig input skal settet v�re tomt!", 0, days.size());
	}
	
	@Test
	public void testEmptyFromSetString(){
		String teststring = "";
		Set<Day> days = Day.fromSetString(teststring);
		assertEquals("Med tom input skal settet v�re tomt!", 0, days.size());
	}
	
	@Test
	public void testNullFromSetString(){
		String teststring = null;
		Set<Day> days = Day.fromSetString(teststring);
		assertEquals("Med null-input skal settet v�re tomt!", 0, days.size());
	}
	
	@Test
	public void testDayFromDate(){
		Day[] allDays = Day.values();
		for(int i = 0; i < 7; i++){
			Date date = new Date(112, 3, 19+i);
			Day day = Day.fromDate(date);
			assertEquals("Skal hente ut riktig dag!", allDays[i], day);
		}
	}
}
