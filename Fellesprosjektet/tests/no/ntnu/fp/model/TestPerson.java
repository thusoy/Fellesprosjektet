package no.ntnu.fp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import calendar.Person;

public class TestPerson {
	
//	@Before
//	public void truncateUser() throws IOException, SQLException{
//		String query = "TRUNCATE TABLE User";
//		Execute.update(query);
//	}
	
	@Test
	public void testUniquePasswordHashes() throws IOException {
		Person p = new Person("john", "high", "lol", "komtek", "banan");
		String johnHash = p.getPasswordHash();
		Person p2 = new Person("john", "high", "lol2", "komtek", "banan");
		String otherHash = p2.getPasswordHash();
		assertFalse("Hashene skal ikke v�re like!", johnHash.equals(otherHash));
		p.setPassword("banan");
		String secondHash = p.getPasswordHash();
		assertEquals("Samme passord skal ha samme hash p� samme person", secondHash, johnHash);
	}
	
	@Test
	public void testSlowEnough() throws IOException{
		for(int i = 0; i<10; i++){
			long startTime = System.currentTimeMillis();
			new Person("john", "high", "lol" + i, "komtek", "banan");
			long durationInMs = System.currentTimeMillis() - startTime;
			String message = String.format("� opprette en Person m� ta minst 100ms! " + 
					"Tok bare %dms!", durationInMs);
			assertTrue(message, durationInMs >= 100);
		}
	}
	
//	@Test
//	public void testIOWithDb() throws IOException{
//		Person p = new Person("john", "high", "lol", "komtek", "banan");
//		Person dbPerson = PersonHandlerImpl.getPerson(p.getId());
//		assertEquals("Personene skal v�re like!", p, dbPerson);
//	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullFirstname() throws IOException{
		new Person(null, "", "", "", "");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullLastname() throws IOException{
		new Person("", null, "", "", "");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullEmail() throws IOException{
		new Person("", "", null, "", "");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullPassword() throws IOException{
		new Person("", "", "", "", null);
	}
}