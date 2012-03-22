package client.helpers;

import static client.helpers.DBHelper.isValidEmail;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class IO {

	public static String getString(String display){
		Scanner scanner = new Scanner(System.in);
		System.out.print(display);
		return scanner.nextLine();
	}
	
	/**
	 * Gets a valid integer from the user, 1 <= num <= upperInclusiveBound.
	 * @param upperInclusiveBound
	 */
	public static int getValidNum(int upperInclusiveBound){
		Scanner scanner = new Scanner(System.in);
		int input;
		while(true){
			try {
				System.out.print("Gj�r et valg: ");
				input = scanner.nextInt();
				if (1 <= input && input <= upperInclusiveBound){
					break;
				}
			} catch (Exception e){ 
				System.out.println("Beklager, det du skrev inn er ikke et gyldig valg. Pr�v igjen.");
			}
		}
		return input;
	}
	
	public static Date parseDate(String format, String inputText){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		while (true) {
			try {
				java.util.Date date = sdf.parse(getString(inputText));
				Date otherDate = new Date(date.getTime());
				return otherDate;
			} catch (ParseException e) {
				System.out.println("Klarte ikke lese datoen din, vennligst pr�v p� nytt.");
			}
		}
	}
	
	public static String getValidEmail(String display) throws IOException{
		String email;
		do {
			email = getString(display);
		} while (!isValidEmail(email));
		return email;
	}
}
