package server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class AwesomeXml {
	
	public static Object getObjectFromXML(String xml) throws UnsupportedEncodingException{
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		XMLDecoder xmldec = new XMLDecoder(bais);
		Object obj = xmldec.readObject();
		xmldec.close();
		return obj;
	}
	
	public static String ObjectToXml(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlenc = new XMLEncoder(baos);
		xmlenc.writeObject(obj);
		xmlenc.close();
		String output = new String(baos.toByteArray());
		return output;
	}
}
