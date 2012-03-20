package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import no.ntnu.fp.model.Person;

import org.junit.Before;
import org.junit.Test;

import calendar.Message;

public class TestMessageHandler {
	
	@Before
	
	
	@Test
	public void testSaveAndFetch() throws IOException {
		Message msg = new Message("Overskrift", "Innhold");
		Message dbMsg = MessageHandler.getMessage(msg.getId());
		assertEquals("Meldingene skal være like", msg, dbMsg);
	}
	
	@Test
	public void testHasBeenRead() throws IOException {
		//Create user
		Person p = new Person("Hans", "Hansen", "hans@hansen.no", "hjemme", "hans");
		//Create message to send
		Message msg = new Message("Overskrift", "Innhold");
		//Send message to user
		MessageHandler.sendMessageToUser(msg.getId(), p.getId());
		//setMessageToHasBeenRead
		MessageHandler.setMessageAsRead(msg.getId(), p.getId());
		assertTrue("hasReadMessage skal v¾re true", MessageHandler.getHasBeenRead(msg.getId(), p.getId()));
	}
}
