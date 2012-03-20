package calendar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import no.ntnu.fp.model.Person;

import org.junit.Before;
import org.junit.Test;

import server.Execute;
import server.MessageHandler;

public class TestMessage {
	
	@Before
	public void truncateTable() throws IOException{
		String query = "TRUNCATE TABLE Message";
		Execute.executeUpdate(query);
	}
	
	@Test
	public void testCreateMessage() throws IOException {
		Message a = new Message("Testmelding", "Svært viktig melding!");
		Message dbA = MessageHandler.getMessage(a.getId());
		assertEquals("Meldingene som hentes ut fra databasen skal være lik de som ble puttet inn!", a, dbA);
//		Message b = new Message("En melding til!", "ikke fullt så viktig");
	}
	
	@Test
	public void testSendAndReadMessage() throws IOException {
		Person target = new Person("john", "locke", "email", null, "passord");
		List<Message> initial = MessageHandler.getUnreadMessagesForUser(target);
		assertEquals("Personen skal ikke ha noen uleste meldinger til å begynne med!", 0, initial.size());
		Message m = new Message("Testmelding", "Svært viktig melding!");
		m.addReceiver(target);
		List<Message> unreadMessages = MessageHandler.getUnreadMessagesForUser(target);
		assertEquals("personen skal ha fått meldingen!", 1, unreadMessages.size());
		assertEquals("Personen skal ha fått riktig melding!", m, unreadMessages.get(0));
		m.showMessage(target);
		List<Message> newUnreadMessages = MessageHandler.getUnreadMessagesForUser(target);
		assertEquals("Personen skal ikke ha noen uleste meldinger!", 0, newUnreadMessages.size());
	}
	
}
