package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import calendar.Message;
import calendar.Person;

public class TestMessageHandler {
	private Person dummy;
	
	@Before
	public void initDummy() throws IOException{
		dummy = new Person("john", "locke", "email4", null, "passord");
	}
	
	@After
	public void deleteDummy() throws IOException{
		dummy.delete();
	}
	
	@Test
	public void testSaveAndFetch() throws IOException {
		Message msg = new Message("Overskrift", "Innhold");
		Message dbMsg = MessageHandlerImpl.getMessage(msg.getId());
		assertEquals("Meldingene skal v�re like", msg, dbMsg);
	}
	
	@Test
	public void testHasBeenRead() throws IOException {
		Message msg = new Message("Overskrift", "Innhold");
		assertEquals("skal ikke v�re noen uleste meldinger", 0, MessageHandlerImpl.getUnreadMessagesForUser(dummy).size());
		msg.addReceiver(dummy);
		assertEquals("skal v�re 1 ulest melding!", 1, MessageHandlerImpl.getUnreadMessagesForUser(dummy).size());
		assertEquals("skal fortsatt v�re 1 ulest melding!", 1, MessageHandlerImpl.getUnreadMessagesForUser(dummy).size());
		assertFalse("meldingen skal ikke v�re lest", MessageHandlerImpl.getHasBeenRead(msg.getId(), dummy.getId()));
		msg.showMessage(dummy);
		assertTrue("meldingen skal v�re lest", MessageHandlerImpl.getHasBeenRead(msg.getId(), dummy.getId()));
		assertEquals("skal ikke v�re noen uleste meldinger", 0, MessageHandlerImpl.getUnreadMessagesForUser(dummy).size());
	}
	
	@Test
	public void testSendAndReadMessage() throws IOException {
		List<Message> initial = MessageHandlerImpl.getUnreadMessagesForUser(dummy);
		assertEquals("Personen skal ikke ha noen uleste meldinger til � begynne med!", 0, initial.size());
		Message m = new Message("Testmelding", "Sv�rt viktig melding!");
		m.addReceiver(dummy);
		List<Message> unreadMessages = MessageHandlerImpl.getUnreadMessagesForUser(dummy);
		assertEquals("personen skal ha f�tt meldingen!", 1, unreadMessages.size());
		System.out.println("comparing******************");
		assertEquals("Personen skal ha f�tt riktig melding!", m, unreadMessages.get(0));
		m.showMessage(dummy);
		List<Message> newUnreadMessages = MessageHandlerImpl.getUnreadMessagesForUser(dummy);
		assertEquals("Personen skal ikke ha noen uleste meldinger!", 0, newUnreadMessages.size());
	}
}
