package client.helpers;

import static client.helpers.DBHelper.isValidEmail;
import gui.SimpleTextGUI;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Scanner;

public class IO {
	private static final boolean useGUI = false;

	public static String getString(String display) throws UserAbortException{
		String userInput;
		if (useGUI){
			SimpleTextGUI.setInputText(display);
			userInput = SimpleTextGUI.getInput();
		} else {
			System.out.print(display);
			Scanner scanner = new Scanner(System.in);
			userInput = scanner.nextLine();
		}
		if (userInput.equalsIgnoreCase("quit")){
			throw new UserAbortException();
		}
		if (useGUI){
			SimpleTextGUI.setInputText("");
		}
		return userInput;
	}
	
	/**
	 * Gets a valid integer from the user, 1 <= num <= upperInclusiveBound.
	 * @param upperInclusiveBound
	 * @throws UserAbortException 
	 */
	public static int getValidNum(int upperInclusiveBound) throws UserAbortException{
		String userInput;
		while(true){
			try {
				if (useGUI){
					userInput = SimpleTextGUI.getInput();
				} else {
					Scanner scanner = new Scanner(System.in);
					userInput = scanner.nextLine();
				}
				if (userInput.isEmpty()){
					throw new UserAbortException();
				}
				int userChoice = Integer.parseInt(userInput);
				if (1 <= userChoice && userChoice <= upperInclusiveBound){
					return userChoice;
				}
			} catch (NumberFormatException e){
				System.out.println("Ugyldig valg. For å avslutte, trykk på enter.");
			}
		}
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
		while (true){
			try {
				if (useGUI){
					userInput = SimpleTextGUI.getInput();
				} else {
					Scanner scanner = new Scanner(System.in);
					userInput = scanner.nextLine();
				}				
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
