package client.helpers;

import static client.helpers.DBHelper.isValidEmail;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Scanner;

public class IO {

	public static String getString(String display) throws UserAbortException{
		Scanner scanner = new Scanner(System.in);
		System.out.print(display);
		String userInput = scanner.nextLine();
		if (userInput.equalsIgnoreCase("quit")){
			throw new UserAbortException();
		}
		return userInput;
	}
	
	/**
	 * Gets a valid integer from the user, 1 <= num <= upperInclusiveBound.
	 * @param upperInclusiveBound
	 */
	@Deprecated
	public static int getValidNum(int upperInclusiveBound){
		Scanner scanner = new Scanner(System.in);
		int input;
		while(true){
			try {
				System.out.print("Gjør et valg: ");
				input = scanner.nextInt();
				if (1 <= input && input <= upperInclusiveBound){
					break;
				}
			} catch (Exception e){ 
				System.out.println("Beklager, det du skrev inn er ikke et gyldig valg. Prøv igjen.");
			}
		}
		return input;
	}
	
	public static Date parseDate(String format, String inputText) throws UserAbortException{
		DateFormat sdf = new SimpleDateFormat(format);
		return parseDate(sdf, inputText);
		
	}
	
	public static Date parseDate(DateFormat format, String inputText) throws UserAbortException{
		while (true) {
			try {
				java.util.Date date = format.parse(getString(inputText));
				Date otherDate = new Date(date.getTime());
				return otherDate;
			} catch (ParseException e) {
				System.out.println("Klarte ikke lese datoen din, vennligst prøv på nytt.");
			}
		}
	}
	
	public static String getValidEmail(String display) throws IOException, UserAbortException{
		String email;
		do {
			email = getString(display);
		} while (!isValidEmail(email));
		return email;
	}
	
	/**
	 * Displays all the elements in the Collection alternatives to the user, prompts the user
	 * to select one, and returns the index of the element selected, 0 <= index < alternatives.size().
	 * @param alternatives
	 * @return
	 * @throws UserAbortException
	 */
	public static int promptChoice(Collection<? extends Object> alternatives) throws UserAbortException{
		int index = 1;
		for(Object alt: alternatives){
			System.out.printf("%d. %s\n", index, alt);
			index++;
		}
		String userInput = null;
		Scanner scanner = new Scanner(System.in);
		while (true){
			try {
				userInput = scanner.nextLine();
				if (userInput.isEmpty()){
					throw new UserAbortException();
				}
				int userChoice = Integer.parseInt(userInput);
				if (1 <= userChoice && userChoice <= alternatives.size()){
					return userChoice - 1;
				}
			} catch (NumberFormatException e){
				System.out.println("Ugyldig valg. For å avslutte, trykk på enter.");
			}
		}
	}
}
