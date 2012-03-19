package no.ntnu.fp.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import server.PersonHandler;

public class TestPerson {

	@Test
	public void testUniquePasswordHashes() throws IOException {
		Person p = new Person("john", "high", "lol", "komtek", "banan", false);
		String johnHash = p.getPasswordHash();
		Person p2 = new Person("john", "high", "lol", "komtek", "banan", false);
		String otherHash = p2.getPasswordHash();
		assertFalse("Hashene skal ikke v¾re like!", johnHash.equals(otherHash));
		p.setPassword("banan");
		String secondHash = p.getPasswordHash();
		assertEquals("Samme passord skal ha samme hash på samme person", secondHash, johnHash);
	}
	
	@Test
	public void testSlowEnough() throws IOException{
		for(int i = 0; i<10; i++){
			long startTime = System.currentTimeMillis();
			new Person("john", "high", "lol", "komtek", "banan", false);
			long durationInMs = System.currentTimeMillis() - startTime;
			String message = String.format("Å opprette en Person må ta minst 100ms! " + 
					"Tok bare %dms!", durationInMs);
			assertTrue(message, durationInMs >= 100);
		}
	}
	
	@Test
	public void testIOWithDb() throws IOException{
		Person p = new Person("john", "high", "lol", "komtek", "banan", false);
		Person dbPerson = PersonHandler.getPerson(p.getId());
		assertEquals("Personene skal være like!", p, dbPerson);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullFirstname() throws IOException{
		new Person(null, "", "", "", "", false);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullLastname() throws IOException{
		new Person("", null, "", "", "", false);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullEmail() throws IOException{
		new Person("", "", null, "", "", false);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullDepartment() throws IOException{
		new Person("", "", "", null, "", false);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNullPassword() throws IOException{
		new Person("", "", "", "", null, false);
	}
}