package mea.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mea.GUI.sound.MeaMessageSound;

public class MeaPopup {

	private JFrame window;

	private static Rectangle screensize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	public static int spacer = 0;
	public static Point TOP_LEFT = new Point(spacer, spacer);
	public static Point TOP_RIGHT = new Point(screensize.width-250-spacer, spacer);
	public static Point BOTTOM_LEFT = new Point(spacer, screensize.height-75-spacer);
	public static Point BOTTOM_RIGHT = new Point(screensize.width-250-spacer, screensize.height-75-spacer);
	
	private Point location = MeaPopup.BOTTOM_RIGHT;
	
	private JFrame popup;
	private JPanel content;
	
	private String header = "meaChat";
	
	public MeaPopup(String header, String subheader, String message, JFrame window){
		this.window = window;
		if(message.length()>28){
			message = message.substring(0, 28)+"...";
		}
		content = new MeaContentPopup(header, subheader, message);
		this.header = header;
	}
	
	public MeaPopup(String subheader, String message, JFrame window){
		this.window = window;
		if(message.length()>28){
			message = message.substring(0, 28)+"...";
		}
		content = new MeaContentPopup(header, subheader, message);
		this.header = "meaChat";
	}
	
	public void show(){
		popup = new JFrame("meaChat"+System.currentTimeMillis());
		popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		popup.setUndecorated(true);
		popup.setSize(new Dimension(250,75));
		popup.setResizable(false);
		popup.setLocation(location);
		popup.setAlwaysOnTop(true);
		popup.add(content);
		if(!window.hasFocus()) popup.setVisible(true);
		if(!window.hasFocus()){
			MeaMessageSound audio = new MeaMessageSound();
			audio.play();
		}
		popup.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				window.requestFocus();
				despawn();
				e.consume();
			}
			public void mouseEntered(MouseEvent e) {
				e.consume();
			}
			public void mouseExited(MouseEvent e) {
				e.consume();
			}
			public void mousePressed(MouseEvent e) {
				e.consume();
			}
			public void mouseReleased(MouseEvent e) {
				e.consume();
			}
		});
	}
	
	public void forceLocation(Point location){
		this.location = location;
	}
	
	public void despawn(){
		popup.setVisible(false);
		popup.dispose();
	}

@SuppressWarnings("serial")
private class MeaContentPopup extends JPanel{

	private String header = "meachat";
	private String subheader = "New message!";
	private String message = "Hey, how are you?";
	
	public MeaContentPopup(String header, String subheader, String message) {
		this.header = header;
		this.subheader = subheader;
		this.message = message;
	}

	public void paintComponent(Graphics g){
		g.drawImage(new ImageIcon("images/popup.png").getImage(), 0, 0, null);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString(header, 15, 25);
		g.setFont(new Font("Arial", Font.BOLD, 15));
		g.drawString(subheader, 10, 43);
		g.setFont(new Font("Arial", Font.ITALIC, 15));
		g.drawString(message, 10, 63);
	}
}
}
