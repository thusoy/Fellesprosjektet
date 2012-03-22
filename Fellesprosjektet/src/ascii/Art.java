package ascii;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Art {
	/**
	 * For å bytte font, gå til http://patorjk.com/software/taag/, lim inn
	 * strengen fontChars, legg inn linjeskift mellom alle bokstavene, og copy-paste
	 * resultatet inn i fila font.txt.
	 * 
	 * Nåværende font er 'Star Wars'
	 */
	private final static int charHeight = 6;
	private final static String fontChars = "abcdefghijklmnopqrstuvwxyz0123456789 !";
	private static Map<Character, String> replacements;
	
	private static Map<Character, String[]> chars;

	static {
		try {
			chars = new HashMap<Character, String[]>();
			Scanner scanner = new Scanner(new FileReader("font.txt"));
			for(char c: fontChars.toCharArray()){
				String[] array = new String[charHeight];
				for(int i = 0; i < charHeight; i++){
					array[i] = scanner.nextLine();
				}
				chars.put(c, array);
				scanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		replacements = new HashMap<Character, String>();
		replacements.put('æ', "ae");
		replacements.put('ø', "oe");
		replacements.put('å', "aa");
	}
	
	/**
	 * Printer ut gitt tekststreng til skjermen i ASCII-art. Ignorerer kapitalisering.
	 * @param raw
	 */
	public static void printAsciiArt(String raw){
		String clean = cleanUp(raw);
		List<StringBuilder> outer = new ArrayList<StringBuilder>();
		for(int i = 0; i < charHeight; i++){
			outer.add(new StringBuilder());
		}
		
		for(char c: clean.toCharArray()){
			String[] array = chars.get(c);
			for(int i = 0; i < array.length; i++){
				outer.get(i).append(array[i]);
			}
		}
		for(StringBuilder sb: outer){
			System.out.println(sb);
		}
		System.out.println();
	}
	
	private static String cleanUp(String raw){
		String lower = raw.toLowerCase();
		for(Character c: replacements.keySet()){
			lower = lower.replaceAll(Character.toString(c), replacements.get(c));
		}
		return lower;
	}
	
}