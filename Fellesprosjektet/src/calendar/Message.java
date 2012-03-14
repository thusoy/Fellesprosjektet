package calendar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import server.MessageHandler;
import no.ntnu.fp.model.Person;

public class Message extends DBObject {
	
	private long msgId;
	private Date dateSent;
	private String content;
	private String title;
	
	public Message (String title, String content, Date dateSent){
		this.title=title;
		this.content=content;
		this.dateSent=dateSent;
	}
	
	public String showMessage(Message msgId, Person user) throws ClassNotFoundException, IOException, SQLException{
		return MessageHandler.getContentMessage(msgId, user) ;
	}
	
	public long getId() {
		return msgId;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
