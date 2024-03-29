package calendar;

import static client.helpers.AppointmentHelper.changeAppointment;
import static client.helpers.AppointmentHelper.deleteAppointment;
import static client.helpers.IO.promptChoice;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;

import server.AppointmentHandler;
import client.helpers.UserAbortException;

public class RejectedMessage extends Message {
	
	private static AppointmentHandler appHandler;
	
	public static void bindToHandler(){
		if (appHandler == null){
			appHandler = (AppointmentHandler) getHandler(AppointmentHandler.SERVICE_NAME);
		}
	}
	
	private static final long serialVersionUID = -8817341040992007302L;
	private Appointment app;
	private Person rejectingUser;
	
	public RejectedMessage(String title, String content, Appointment app, Person rejectingUser) throws IOException {
		super(0);
		if (!app.getParticipants().containsKey(rejectingUser)){
			throw new IllegalArgumentException("Brukeren som avslo m� v�re blant de inviterte til avtalen!");
		}
		bindToHandler();
		this.app = app;
		this.rejectingUser = rejectingUser;
		this.title = title;
		this.content = content;
		this.msgId = msgHandler.getUniqueId();
		setDateSent(new Date(System.currentTimeMillis()));
		msgHandler.createMessage(this);
	}
	
	private RejectedMessage(long id){
		super(id);
	}
	
	public static RejectedMessage recreateRejectedMessage(long msgId, String title, String content, 
			Date dateSent, Appointment app, Person rejectingUser) throws IOException{
		System.out.println(app.getParticipants());
		System.out.println(rejectingUser);
		RejectedMessage m = new RejectedMessage(msgId);
		m.setDateSent(dateSent);
		m.msgId = msgId;
		m.content = content;
		m.title = title;
		m.app = app; 
		m.rejectingUser = rejectingUser;
		return m;
	}
	
	public Appointment getApp(){
		return app;
	}
	
	public Person getRejectingUser(){
		return rejectingUser;
	}
	
	public void getAndExecuteUserResponse(Person user) throws IOException, UserAbortException{
		String[] options = {"Endre avtale", "Slett avtale", "Slett deltager"};
		int userInput = promptChoice(Arrays.asList(options));
		switch(userInput){
		case 0:
			changeAppointment(user, app);
			break;
		case 1:
			deleteAppointment(user, app);
			break;
		case 2:
			appHandler.deleteUserFromAppointment(app.getId(), rejectingUser.getId());
			System.out.printf("%s fjernet fra avtalen!\n", rejectingUser);
			break;
		}
		
	}

}
