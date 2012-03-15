package server;

import calendarprotocol.ServerState;

public class CalendarProtocol {
	private ServerState state;
	private int count;
	
	public CalendarProtocol(){
		state = ServerState.CLOSED;
		count = 0;
	}
	
	public String getResponse(String input){
		String response = null;
		switch (state){
		case CLOSED:
			response = "Hello!";
			state = ServerState.RUNNING;
			break;
		case RUNNING:
			response = "Zup?";
			count++;
			if (count > 5){
				state = ServerState.STOP;
			}
			break;
		case STOP:
			response = "Bye!";
			break;
		}
		return response;
	}
	
	public ServerState getState(){
		return state;
	}
	
}
