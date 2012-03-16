package no.ntnu.fp.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import server.PersonHandler;

public class TestPerson {

	@Test
	public void test() throws IOException {
		Person p = new Person("john", "high", "lol", "komtek", "banan", false);
		String johnHash = p.getPasswordHash();
		Person p2 = new Person("john", "high", "lol", "komtek", "banan", false);
		String otherHash = p2.getPasswordHash();
		System.out.println(johnHash);
		System.out.println(otherHash);
		assertFalse("Hashene skal ikke v¾re like!", johnHash.equals(otherHash));
		p.setPasswordHash("banan");
		String secondHash = p.getPasswordHash();
		assertEquals("Samme passord skal ha samme hash på samme person", secondHash, johnHash);
		assertEquals("Peronene skal ikke v¾re like", p, PersonHandler.getPerson(p.getId()));
	}
}
