package calendar;

import static dateutils.DateUtils.stripMsFromTime;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import server.MessageHandler;

public class Message extends DBCommunicator implements Comparable<Message>, Serializable {
	
	private static final long serialVersionUID = 5103325888593276747L;
	protected long msgId;
	protected Date dateSent;
	protected String content;
	protected String title;
	private List<Person> receivers;
	protected static MessageHandler msgHandler;
	
	public static void bindToHandler(){
			msgHandler = (MessageHandler) getHandler(MessageHandler.SERVICE_NAME);
	}
	
	/**
	 * Created as Message object and saved it to the database.
	 * @param title
	 * @param content
	 * @throws IOException
	 */
	public Message (String title, String content) throws IOException{
		bindToHandler();
		this.title=title;
		this.content=content;
		setDateSent(new Date(System.currentTimeMillis()));
		receivers = new ArrayList<Person>();
		msgId = msgHandler.getUniqueId();
		msgHandler.createMessage(this);
	}
	
	protected Message(long id){
		this.msgId = id;
	}
	
	public static Message recreateMessage(long msgId, String title, String content, Date dateSent) throws IOException{
		Message m = new Message(msgId);
		m.dateSent = dateSent;
		m.msgId = msgId;
		m.content = content;
		m.title = title;
		m.receivers = new ArrayList<Person>();
		return m;
	}
	
	public Message showMessage(Person user) throws IOException {
		bindToHandler();
		msgHandler.setMessageAsRead(msgId, user.getId());
		return this;
	}
	
	public long getId() {
		return msgId;
	}

	public Date getDateSent() {
		return dateSent;
	}
	
	public void addReceiver(Person p) throws IOException{
		msgHandler.sendMessageToUser(msgId, p.getId());
		if (receivers == null){
			receivers = new ArrayList<Person>();
		}
		receivers.add(p);
	}
	
	/**
	 * Sets the given receivers as receivers of the message, and sends it out.
	 * @param receivers
	 * @throws IOException
	 */
	public void setReceivers(List<Person> receivers) throws IOException{
		msgHandler.deleteOldReceivers(msgId);
		if (receivers == null){
			this.receivers = new ArrayList<Person>();
		} else {
			this.receivers = receivers;
			msgHandler.sendMessageToAllParticipants(this);
		}
	}
	
	public List<Person> getReceivers(){
		return receivers;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void setId(long msgId) {
		this.msgId = msgId;
	}
	
	public void setDateSent(Date dateSent){
		this.dateSent = stripMsFromTime(dateSent);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((dateSent == null) ? 0 : dateSent.hashCode());
		result = prime * result + (int) (msgId ^ (msgId >>> 32));
		result = prime * result
				+ ((receivers == null) ? 0 : receivers.hashCode());
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
		if (msgId != other.msgId)
			return false;
		if (receivers == null) {
			if (other.receivers != null)
				return false;
		} else if (!receivers.equals(other.receivers))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int compareTo(Message m) {
		return dateSent.compareTo(m.dateSent);
	}
	
	@Override
	public String toString(){
		return title;
	}

}
