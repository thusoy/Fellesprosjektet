package no.ntnu.fp.net.co;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Settings {
	private static final String PATH = "settings.xml";
	
	// Hide the constructor to signalize that the class should not be instantiated.
	private Settings(){};
	
	public static void setErrorFree() throws SAXException, IOException{
		setErrors(false);
		setLoss(0);
		setDelay(0);
		setGhost(0);
		setPayload(0);
		setHeader(0);
		setOnlyData(true);
		setSimpleConnection(false);
	}
	
	public static void setErrors(boolean errors) throws SAXException, IOException{
		setField("errors", Boolean.toString(errors));
	}
	
	public static void setLoss(double loss) throws SAXException, IOException{
		validRatio(loss, "Pakketapet");
		setField("loss", Double.toString(loss));
	}
	
	public static void setDelay(double delay) throws SAXException, IOException{
		validRatio(delay, "Forsinkelsesratioen");
		setField("delay", Double.toString(delay));
	}
	
	public static void setGhost(double ghost) throws SAXException, IOException{
		validRatio(ghost, "Ghost-package-ratioen");
		setField("ghost", Double.toString(ghost));
	}
	
	public static void setPayload(double payload) throws SAXException, IOException{
		validRatio(payload, "Payload feilraten");
		setField("payload", Double.toString(payload));
	}
	
	public static void setHeader(double header) throws SAXException, IOException{
		validRatio(header, "Header feilraten");
		setField("header", Double.toString(header));
	}
	
	public static void setOnlyData(boolean onlyData) throws SAXException, IOException{
		setField("onlydata", Boolean.toString(onlyData));
	}
	
	public static void setServerAddress(String serverAddress) throws SAXException, IOException{
		setField("serverAddress", serverAddress);
	}
	
	public static void setServerPort(int port) throws SAXException, IOException{
		setField("serverport", Integer.toString(port));
	}
	
	public static void setSimpleConnection(boolean simpleConnection) throws SAXException, IOException{
		setField("simpleConnection", Boolean.toString(simpleConnection));
	}
	
	private static void validRatio(double value, String fieldName){
		if (value < 0 || value > 1){
			String errorBase = "%s kan ikke være større enn 1 eller mindre enn 0!";
			String errorString = String.format(errorBase, fieldName);
			throw new IllegalArgumentException(errorString);
		}
	}
	
	private static void setField(String fieldname, String value) throws SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document testDoc = builder.parse(new File(PATH));
			Element root = testDoc.getDocumentElement();
			Node oldValue = testDoc.getElementsByTagName(fieldname).item(0);
			root.removeChild(oldValue);
			Element node = testDoc.createElement(fieldname);
			node.setTextContent(value);
			root.appendChild(node);
			printDoc(testDoc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	private static void printDoc(Document doc) throws FileNotFoundException, TransformerException{
		DOMSource source = new DOMSource(doc);
		PrintStream ps = new PrintStream(PATH);
		StreamResult result = new StreamResult(ps);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(source, result);
	}

	public static void setErrors() throws SAXException, IOException {
		setErrors(true);
		setLoss(0.8);
		setDelay(0.7);
		setHeader(0.8);
		setPayload(0.8);
	}
}
