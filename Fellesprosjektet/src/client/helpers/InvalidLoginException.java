package client.helpers;

public class InvalidLoginException extends Exception{

	private static final long serialVersionUID = -6308285173760562483L;

	public InvalidLoginException(String msg){
		super(msg);
	}
}
