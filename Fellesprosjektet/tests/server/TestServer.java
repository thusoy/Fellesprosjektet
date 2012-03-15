package server;

import org.junit.Test;

import client.ClientController;

public class TestServer {

	@Test
	public void test() {
		ServerController server = new ServerController();
		server.start();
		
		ClientController client = new ClientController();
		client.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
