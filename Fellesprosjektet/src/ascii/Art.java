package ascii;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Art {
	private final static int charHeight = 6;
	private final static String fontChars = "abcdefghijklmnopqrstuvwxyz0123456789 !";
	
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
	}
	
	public static void printAsciiArt(String raw){
		raw = raw.toLowerCase();
		List<StringBuilder> outer = new ArrayList<StringBuilder>();
		for(int i = 0; i < charHeight; i++){
			outer.add(new StringBuilder());
		}
		
		for(char c: raw.toCharArray()){
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
	
}