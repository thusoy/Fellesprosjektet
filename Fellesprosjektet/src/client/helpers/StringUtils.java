package client.helpers;

public class StringUtils {
	public static String center(String input, int width, char fillChar){
		int length = input.length();
		if (length < width){
			int padding = (width - length)/2;
			String blank = repeat(Character.toString(fillChar), padding);
			String output = blank + input + blank; 
			boolean isOdd = width % 2 == 0;
			return isOdd ? output : output.substring(0, width-1);
		} else {
			return input.substring(0, width-3) + "...";
		}
	}
	
	public static String padRight(String input, int width){
		int length = input.length();
		if (length <= width){
			int padding = width - length;
			String blank = repeat(" ", padding);
			String output = input + blank; 
			return output;
		} else {
			return input.substring(0, width-4) + "... ";
		}
	}
	
	public static String padLeft(String input, int width){
		int length = input.length();
		if (length <= width){
			int padding = width - length;
			String blank = repeat(" ", padding);
			String output = blank + input; 
			return output;
		} else {
			return input.substring(0, width-4) + "... ";
		}
	}
	
	public static String repeat(String input, int times){
		if (times <= 0){
			return "";
		}
		return String.format(String.format("%%0%dd", times), 0).replace("0", input);
	}
}
