package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.TreeSet;

import calendar.Appointment;
import calendar.Day;

public class ServerTest {
	
	private static String serializeObject(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os;
		String result = null;
		try {
			os = new ObjectOutputStream(baos);
			os.writeObject(obj);
			os.close();
			result = new String(baos.toByteArray());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return result;
	}
	
	private static Object ObjectFromSerialization(String ser){
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(ser.getBytes("ISO-8259-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ObjectInputStream is;
		Object out = null;
		try {
			is = new ObjectInputStream(bais);
			out = (Appointment) is.readObject();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return out;
	}
	
	public static void main(String[] args){
//		Set<Day> days = new TreeSet<Day>();
//		days.add(Day.MONDAY);
//		days.add(Day.TUESDAY);
//		days.add(Day.FRIDAY);
//		String orig = days.toString();
//		String db = orig.substring(1, orig.length()-1);
//		System.out.println(db);
//		Set<Day> output = Day.fromString(db);
//		System.out.println(output);
//		System.out.println(days);
//		System.out.println(output.equals(days));
		
//		Date start = new Date();
//		Date end = new Date(System.currentTimeMillis() + 2700);
//		Appointment app = new Appointment("tannlege", start, end, false, null);
//		System.out.println(app);
//		String ser = serializeObject(app);
//		System.out.println(ser);
//		Appointment app2 = (Appointment) ObjectFromSerialization(ser);
//		System.out.println(app2);
//		Appointment incomingObject = null;
		
		
		
//		try {
//			incomingObject = (Appointment) AwesomeXml.getObjectFromXML(xml);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		System.out.println(incomingObject.getAppId());
//		System.out.println(incomingObject == app);
	}
}
