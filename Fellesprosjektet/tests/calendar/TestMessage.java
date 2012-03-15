package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import server.MessageHandler;
import calendar.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import no.ntnu.fp.model.Person;
import org.junit.Test;

public class TestMessage {
	
	@Test
	public void testCreateMessage() throws ClassNotFoundException, IOException, SQLException{
		Message testMessage = new Message("Test 1", "Dette er en test for å opprette en melding!", new Date());
		MessageHandler.createMessage(testMessage);
		Person user = new Person("Anne", "Olsen", "anne_olsen@gmail.com", "Teknisk", "Banan");
		long msgId = testMessage.getId();
		testMessage.showMessage(msgId, user);
		
		assertTrue(testMessage.getTitle().equals("Test 1"));
		assertTrue(testMessage.getContent().equals("Dette er en test for å opprette en melding!"));
	}
	@Test
	public void testSetTitle(){
		Message testMessage2 = new Message("Test 2", "BlaBlaBla!", new Date());
		testMessage2.setTitle("New Title");
		assertEquals(testMessage2.getTitle(), "New Title");
	}

}
