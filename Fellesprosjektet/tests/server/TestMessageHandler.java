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
		Message dbMsg = MessageHandler.getMessage(msg.getId());
		assertEquals("Meldingene skal være like", msg, dbMsg);
	}
	
	@Test
	public void testHasBeenRead() throws IOException {
		Message msg = new Message("Overskrift", "Innhold");
		assertEquals("skal ikke være noen uleste meldinger", 0, MessageHandler.getUnreadMessagesForUser(dummy).size());
		msg.addReceiver(dummy);
		assertEquals("skal være 1 ulest melding!", 1, MessageHandler.getUnreadMessagesForUser(dummy).size());
		assertEquals("skal fortsatt være 1 ulest melding!", 1, MessageHandler.getUnreadMessagesForUser(dummy).size());
		assertFalse("meldingen skal ikke være lest", MessageHandler.getHasBeenRead(msg.getId(), dummy.getId()));
		msg.showMessage(dummy);
		assertTrue("meldingen skal være lest", MessageHandler.getHasBeenRead(msg.getId(), dummy.getId()));
		assertEquals("skal ikke være noen uleste meldinger", 0, MessageHandler.getUnreadMessagesForUser(dummy).size());
	}
	
	@Test
	public void testSendAndReadMessage() throws IOException {
		List<Message> initial = MessageHandler.getUnreadMessagesForUser(dummy);
		assertEquals("Personen skal ikke ha noen uleste meldinger til å begynne med!", 0, initial.size());
		Message m = new Message("Testmelding", "Svært viktig melding!");
		m.addReceiver(dummy);
		List<Message> unreadMessages = MessageHandler.getUnreadMessagesForUser(dummy);
		assertEquals("personen skal ha fått meldingen!", 1, unreadMessages.size());
		System.out.println("comparing******************");
		assertEquals("Personen skal ha fått riktig melding!", m, unreadMessages.get(0));
		m.showMessage(dummy);
		List<Message> newUnreadMessages = MessageHandler.getUnreadMessagesForUser(dummy);
		assertEquals("Personen skal ikke ha noen uleste meldinger!", 0, newUnreadMessages.size());
	}
}
