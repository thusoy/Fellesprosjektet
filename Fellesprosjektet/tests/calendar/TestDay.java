package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import calendar.Day;

public class TestDay {

	@Test
	public void testNorwegianNames(){
		assertEquals("Det skal være 7 forskjellige instanser av Day.", Day.values().length, 7);
		String[] norwegian = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", 
				"Lørdag", "Søndag"};
		Day[] days = Day.values();
		for(int i = 0; i < days.length; i++){
			assertEquals(norwegian[i].toLowerCase(), days[i].getNorwegian());
		}
	}
	
	@Test
	public void testCorrectFromString() {
		String teststring = "MONDAY, TUESDAY";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Det skal være to elementer i days etter å ha testet med strengen " +
				"'MONDAY, TUESDAY'", days.size(), 2);
		assertTrue("Mandag må ligge i settet som returneres", days.contains(Day.MONDAY));
		assertTrue("Tirsdag må ligge i settet som returneres", days.contains(Day.TUESDAY));
	}
	
	@Test
	public void testWrongFromString(){
		String teststring = "LUNDI, MERCREDI, ";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med ugyldig input skal settet være tomt!", days.size(), 0);
	}
	
	@Test
	public void testWrongFromString2(){
		String teststring = "Dette er en lengre bullshittekst om ingenting!";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med ugyldig input skal settet være tomt!", days.size(), 0);
	}
	
	@Test
	public void testEmptyFromString(){
		String teststring = "";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med tom input skal settet være tomt!", days.size(), 0);
	}
	
	@Test
	public void testNullFromString(){
		String teststring = null;
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med null-input skal settet være tomt!", days.size(), 0);
	}
}
