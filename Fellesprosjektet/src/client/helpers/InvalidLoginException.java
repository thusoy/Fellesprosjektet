package client.helpers;

public class InvalidLoginException extends Exception{

	private static final long serialVersionUID = -6308285173760562483L;

	public InvalidLoginException(){
		super("Ugyldig kombinasjon av brukernavn/passord, pr�v igjen!");
	}
}
