package gui;

import java.io.*;
import java.util.*;
import javax.swing.*;

public class TextAreaOutputStream extends OutputStream {

	private JTextArea textArea;
	private int maxLines;
	private LinkedList<Integer> lineLengths;
	private int curLength;
	private byte[] oneByte;
	static private byte[] LINE_SEP = System.getProperty("line.separator","\n").getBytes();
	
	public TextAreaOutputStream(JTextArea textArea) {
	    this(textArea, 1000);
    }
	
	public TextAreaOutputStream(JTextArea textArea, int maxLines) {
	    if (maxLines < 1) { 
	    	throw new IllegalArgumentException("Maximum lines of "+maxLines+" in TextAreaOutputStream constructor is not permitted"); 
    	}
	    this.textArea = textArea;
	    this.maxLines = maxLines;
	    lineLengths = new LinkedList<Integer>();
	    curLength = 0;
	    oneByte = new byte[1];
    }
	
	public synchronized void clear() {
	    lineLengths = new LinkedList<Integer>();
	    curLength = 0;
	    textArea.setText("");
    }
	
	public synchronized int getMaximumLines() { 
		return maxLines; 
	}
	
	public synchronized void setMaximumLines(int val) { 
		maxLines = val;
	}
	
	public void close() {
	    if (textArea != null) {
	        textArea = null;
	        lineLengths = null;
	        oneByte = null;
        }
    }
	
	public void flush() {
    }
	
	public void write(int val) {
	    oneByte[0] = (byte) val;
	    write(oneByte,0,1);
    }
	
	public void write(byte[] ba) {
	    write(ba, 0, ba.length);
    }
	
	public synchronized void write(byte[] ba,int str,int len) {
	    try {
	        curLength += len;
	        if (bytesEndWith(ba, str, len, LINE_SEP)) {
	            lineLengths.addLast(new Integer(curLength));
	            curLength=0;
	            if(lineLengths.size() > maxLines){
	                textArea.replaceRange(null, 0, ((Integer) lineLengths.removeFirst()).intValue());
                }
            }
	        for(int xa=0; xa<10; xa++) {
	            try { 
	            	textArea.append(new String(ba, str, len)); 
	            	textArea.setCaretPosition(textArea.getDocument().getLength());
	            	break; 
            	}
	            // sometimes throws a java.lang.Error: Interrupted attempt to aquire write lock
	            catch(Throwable thr) {    
	                if (xa == 9) { 
	                	thr.printStackTrace(); 
                	} else {
                		Thread.sleep(200);    
            		}
	            }
	        }
	    } catch(Throwable thr) {
	        CharArrayWriter caw = new CharArrayWriter();
	        thr.printStackTrace(new PrintWriter(caw, true));
	        textArea.append(System.getProperty("line.separator","\n"));
	        textArea.append(caw.toString());
        }
    }

	private boolean bytesEndWith(byte[] ba, int str, int len, byte[] ew) {
	    if(len<LINE_SEP.length) { 
	    	return false; 
    	}
	    for(int xa = 0, xb = str + len - LINE_SEP.length; xa < LINE_SEP.length; xa++, xb++) {
	        if(LINE_SEP[xa] != ba[xb]) {
	        	return false; 
        	}
        }
	    return true;
    }
}
