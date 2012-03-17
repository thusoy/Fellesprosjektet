package server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Date;

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
}
