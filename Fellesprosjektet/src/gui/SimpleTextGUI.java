package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SimpleTextGUI implements KeyListener {
	private JTextField inputField;
	private static String lastInput = null;
	private JTextArea textArea;
	private static JLabel inputLabel;
	
	public SimpleTextGUI(){
		initJFrame();
	}
	private void initJFrame(){
        JFrame frame = new JFrame();
        frame.add(new JLabel("Kalender"), BorderLayout.NORTH);
        frame.add(getOutputWindow(), BorderLayout.CENTER);
        frame.add(getInputWindow(), BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JScrollPane getOutputWindow(){
		textArea = new JTextArea(null, 35, 178);
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        TextAreaOutputStream taos = new TextAreaOutputStream(textArea, 60);
        PrintStream ps = new PrintStream( taos );
        System.setOut(ps);
//        System.setErr(ps);
		return new JScrollPane(textArea);
	}
	
	private JPanel getInputWindow(){
		JPanel inFieldPane = new JPanel();
        inFieldPane.setLayout(new GridLayout(2,2));
        inputLabel = new JLabel("");
        inFieldPane.add(inputLabel);
        inputField = new JTextField(30);
        inputField.addKeyListener(this);
        inFieldPane.add(inputField);
        inputField.requestFocusInWindow();
        return inFieldPane;
	}
	
	private void clearInput(){
		inputField.setText("");
	}
	
	public void clear(){
		textArea.setText("");
	}
	
	public static void setInputText(String inputText){
		inputLabel.setText(inputText);
	}
	
	public static String getInput(){
		while(true){
			if(lastInput == null){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				String output = lastInput;
				lastInput = null;
				return output;
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (keyChar == KeyEvent.VK_ENTER){
			lastInput = inputField.getText();
			clearInput();
		}
			
	}
}
