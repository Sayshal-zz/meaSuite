package mea.GUI;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MeaColoredChat {
	
	public static void setColored(String message, JTextPane pane){
		Color c = Color.red;
		if(message.startsWith(" [mea]")){
			c = Color.blue;
		}else if(message.startsWith(" [MC]")){
			c = Color.green;
		}else if(message.startsWith(" [IRC]")){
			c = Color.cyan;
		}
	    SimpleAttributeSet attrs = new SimpleAttributeSet();  
	    StyledDocument doc = pane.getStyledDocument();  	   
	    StyleConstants.setForeground(attrs, c);     
	    try {
			doc.insertString(doc.getLength(), message, attrs);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}
