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
		assertEquals("Det skal v�re 7 forskjellige instanser av Day.", Day.values().length, 7);
		String[] norwegian = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", 
				"L�rdag", "S�ndag"};
		Day[] days = Day.values();
		for(int i = 0; i < days.length; i++){
			assertEquals(norwegian[i].toLowerCase(), days[i].getNorwegian());
		}
	}
	
	@Test
	public void testCorrectFromString() {
		String teststring = "MONDAY, TUESDAY";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Det skal v�re to elementer i days etter � ha testet med strengen " +
				"'MONDAY, TUESDAY'", days.size(), 2);
		assertTrue("Mandag m� ligge i settet som returneres", days.contains(Day.MONDAY));
		assertTrue("Tirsdag m� ligge i settet som returneres", days.contains(Day.TUESDAY));
	}
	
	@Test
	public void testWrongFromString(){
		String teststring = "LUNDI, MERCREDI, ";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med ugyldig input skal settet v�re tomt!", days.size(), 0);
	}
	
	@Test
	public void testWrongFromString2(){
		String teststring = "Dette er en lengre bullshittekst om ingenting!";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med ugyldig input skal settet v�re tomt!", days.size(), 0);
	}
	
	@Test
	public void testEmptyFromString(){
		String teststring = "";
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med tom input skal settet v�re tomt!", days.size(), 0);
	}
	
	@Test
	public void testNullFromString(){
		String teststring = null;
		Set<Day> days = Day.fromString(teststring);
		assertEquals("Med null-input skal settet v�re tomt!", days.size(), 0);
	}
}
