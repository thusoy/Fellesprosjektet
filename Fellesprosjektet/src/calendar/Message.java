package calendar;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Date;

import server.MessageHandler;
import server.RoundTime;
import no.ntnu.fp.model.Person;

public class Message extends DBObject<Message> {
	
	private long msgId;
	private Date dateSent;
	private String content;
	private String title;
	
	public Message (String title, String content, Date dateSent, boolean recreation) throws IOException{
		this.title=title;
		this.content=content;
		this.dateSent= RoundTime.roundTime(dateSent);
		if(!recreation) {
			MessageHandler.createMessage(this);
		}
	}
	
	public String showMessage(long msgId, Person user) throws ClassNotFoundException, IOException, SQLException{
		MessageHandler.hasReadMessage(msgId, user.getId());
		return MessageHandler.getContentMessage(msgId) ;
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
	public void setId(long msgId) {
		this.msgId = msgId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((dateSent == null) ? 0 : dateSent.hashCode());
		result = prime * result + (int) (msgId ^ (msgId >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (dateSent == null) {
			if (other.dateSent != null)
				return false;
		} else if (!dateSent.equals(other.dateSent))
			return false;
		System.out.println("kom hit");
		if (msgId != other.msgId)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
