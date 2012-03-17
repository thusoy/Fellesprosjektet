package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;

import server.PersonHandler;

import no.ntnu.fp.model.Person;

import org.junit.Test;

import calendar.Message;

public class TestMessageHandler {
	
	@Test
	public void testSaveAndFetch() throws IOException {
		Date date = new Date(System.currentTimeMillis()); 
		Message msg = new Message("Overskrift", "Innhold", date, false);
		Message dbMsg = MessageHandler.getMessage(msg.getId());
		assertEquals("Meldingene skal v¾re like", msg, dbMsg);
		
	}
	
	@Test
	public void testHasBeenRead() throws IOException {
		//Create user
		Person p = new Person("Hans", "Hansen", "hans@hansen.no", "hjemme", "hans", false);
		//Create message to send
		Date date = new Date(System.currentTimeMillis()); 
		Message msg = new Message("Overskrift", "Innhold", date, false);
		//Send message to user
		MessageHandler.sendMessageToUser(msg, p);
		//setMessageToHasBeenRead
		MessageHandler.hasReadMessage(msg.getId(), p.getId());
		assertTrue("hasReadMessage skal v¾re true", MessageHandler.getHasBeenRead(msg.getId(), p.getId()));
	}
}
