package calendar;

import java.util.Date;
import no.ntnu.fp.model.Person;

public class Message extends DBObject {
	
	private int msgId;
	private Date dateSent;
	private String content;
	private String title;
	
	public Message (String title, String content, Date dateSent){
		this.title = title;
		this.content = content;
		this.dateSent = dateSent;
	}
	
	public String showMessage(Person user){
		return getMsg(this, user);
	}
}
