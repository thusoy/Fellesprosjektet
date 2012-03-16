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
		System.out.println(johnHash);
		System.out.println(otherHash);
		assertFalse("Hashene skal ikke v¾re like!", johnHash.equals(otherHash));
		p.setPassword("banan");
		String secondHash = p.getPasswordHash();
		System.out.println(secondHash);
		assertEquals("Samme passord skal ha samme hash på samme person", secondHash, johnHash);
	}
	
	@Test
	public void testSlowEnough() throws IOException{
		long startTime = System.currentTimeMillis();
		Person p = new Person("john", "high", "lol", "komtek", "banan", false);
		long durationInMs = System.currentTimeMillis() - startTime;
		System.out.println("tid: " + durationInMs);
		assertTrue("Å opprette en Person må ta minst 100ms!", durationInMs >= 100);
	}
	
	@Test
	public void testIOWithDb() throws IOException{
		Person p = new Person("john", "high", "lol", "komtek", "banan", false);
		Person dbPerson = PersonHandler.getPerson(p.getId());
		assertEquals("Personene skal være like!", p, dbPerson);
	}
}