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
		String query = "UPDATE UserMessages SET hasBeenRead='true' WHERE userId='%s' AND msgId='%s'";
		executeQuery(String.format(query, user.getId(), this.msgId));
		return content;
	}
}
