package no.ntnu.fp.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPerson {

	@Test
	public void test() {
		Person p = new Person("john", "high", "lol", "komtek", "banan");
		String johnHash = p.getPasswordHash();
		Person p2 = new Person("john", "high", "lol", "komtek", "banan");
		String otherHash = p2.getPasswordHash();
		System.out.println(johnHash);
		System.out.println(otherHash);
		assertFalse("Hashene skal ikke v¾re like!", johnHash.equals(otherHash));
		p.setPasswordHash("banan");
		String secondHash = p.getPasswordHash();
		assertEquals("Samme passord skal ha samme hash på samme person", secondHash, johnHash);
	}
}
