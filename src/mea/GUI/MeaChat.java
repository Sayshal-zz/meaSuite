package mea.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mea.External.Download;

import org.jibble.pircbot.Colors;

@SuppressWarnings("serial")
public class MeaChat extends JPanel {
	
	private String username;
	private String password;
	private String server;
	@SuppressWarnings("unused")
	private Console console;
	private JFrame window;
	private int version;

	private Vector<String> who_mea = new Vector<String>();
	private Vector<String> who_MC = new Vector<String>();
	private Vector<String> who_IRC= new Vector<String>();
	
	private MCChatHandler MCPane;
	private IRCChatHandler IRCPane;
	private MeaChatHandler MeaPane;
	private AllChatHandler ChatPane;
	private MeaChatSettingsPane settingsPane;
	private MeaPopup popup;
	private MeaPopup notification;

	private JScrollPane MinecraftScroller;
	private JScrollPane IRCScroller;
	private JScrollPane MeaChatScroller;
	private JScrollPane AllChatScroller;
	
	private JTabbedPane tabs = new JTabbedPane();
	private JTextArea usersOnline;
	private JTextField command;
	
	private boolean updateBroadcast = false;
	public PrintStream printer;
	public BufferedReader reader;
	public Socket sock;

	public MeaChat(String username, String password, String server, Console console, JFrame window, int version) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.console = console;
		this.window = window;
		this.version = version;
		
		this.window.setIconImage(new ImageIcon("images/icon.png").getImage());

		MCPane = new MCChatHandler("[MC]");
		IRCPane = new IRCChatHandler("[IRC]");
		MeaPane = new MeaChatHandler("[mea]");
		ChatPane = new AllChatHandler(null);
		settingsPane = new MeaChatSettingsPane();

		usersOnline = new JTextArea();
		usersOnline.setBorder(BorderFactory.createLineBorder(Color.black));
		
		command = new JTextField(0);
		
		tabs.setPreferredSize(new Dimension(window.getWidth()-220, window.getHeight()-90));
		MCPane.setPreferredSize(tabs.getMaximumSize());
		IRCPane.setPreferredSize(tabs.getMaximumSize());
		MeaPane.setPreferredSize(tabs.getMaximumSize());
		ChatPane.setPreferredSize(tabs.getMaximumSize());
		settingsPane.setPreferredSize(tabs.getMaximumSize());
		usersOnline.setPreferredSize(new Dimension(180, window.getHeight()-90));
		command.setPreferredSize(new Dimension(window.getWidth()-30, 30));
		command.setLocation(new Point(10, window.getHeight()-40));
		
		MCPane.setEditable(false);
		IRCPane.setEditable(false);
		MeaPane.setEditable(false);
		ChatPane.setEditable(false);
		usersOnline.setEditable(false);

		AllChatScroller = new JScrollPane(ChatPane);
		MinecraftScroller = new JScrollPane(MCPane);
		IRCScroller = new JScrollPane(IRCPane);
		MeaChatScroller = new JScrollPane(MeaPane);
		
		tabs.addTab("All Chat", AllChatScroller);
		tabs.addTab("Minecraft", MinecraftScroller);
		tabs.addTab("IRC", IRCScroller);
		tabs.addTab("meaChat", MeaChatScroller);
		tabs.addTab("Settings", settingsPane);
		
		add(tabs);
		add(usersOnline);
		add(command);
		
		addToChat("[MC] This is a minecraft message");
		addToChat("[IRC] This is an IRC message");
		addToChat("[mea] This is a mea message");
		addToChat("[CONSOLE] This is a console message");
		addToChat("======================================");

		loadListeners();
	}

	public void loadListeners(){		
		command.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				String line = command.getText();
				boolean isCommand = false;
				if(line.startsWith("/dc") || line.startsWith("/disconnect")){
					addToChat("Disconnecting...");
					printer.println("disconnect "+username);
					try {
						sock.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					addToChat("Disconnected!");
					System.exit(0);
					isCommand = true;
				}else if(line.startsWith("/help") ||
						line.startsWith("/h") ||
						line.startsWith("/?")){
					addToChat("[HELP] /dc OR /disconnect   - Disconnect from the chat server");
					addToChat("[HELP] /whois [username]    - Provides WHO IS data on a username");
					addToChat("[HELP] /pm [username]         - Private message a username");
					addToChat("[HELP] /p [message]              - Send a message to meaChat users only");
					addToChat("[HELP] /p                                   - Auto-Send (until disabled) messages to meaChat users");
					isCommand = true;
				}else if(line.startsWith("/whois")){
					String parts[] = line.split(" ");
					if(parts.length>1){
						printer.println("whois "+parts[1]);
					}else{
						addToChat("No username selected! /whois [username]");
					}
					isCommand = true;
				}else if(line.startsWith("/pm")){
					String parts[] = line.split(" ");
					if(parts.length>1){
						printer.println("whois "+parts[1]);
					}else{
						addToChat("No username selected! /whois [username]");
					}
					isCommand = true;
				}else if(line.startsWith("/p")){
					String parts[] = line.split(" ");
					if(parts.length>1){
						String message = "";
						for(String word : line.split(" ")){
							if(!word.equalsIgnoreCase("/p")){
								message = message.concat(word+" ");
							}
						}
						printer.println("meachatonly "+message);
					}else{
						addToChat("No username selected! /whois [username]");
					}
					isCommand = true;
				}else if(line.startsWith("/help") ||
						line.startsWith("/h") ||
						line.startsWith("/?")){
					addToChat(" /dc OR /disconnect   - Disconnect from the chat server");
					addToChat(" /whois [username]    - Provides WHO IS data on a username");
					addToChat(" /pm [username]       - Private message a username");
					addToChat(" /p [message]         - Use only meaChat");
					addToChat(" /p                   - Use only meaChat (toggle on/off)");
					isCommand = true;
				}
				if(!isCommand){
					if(line.length()>200){
						addToChat("Message too long, only sending 200 characters");
						line = line.substring(0, 200);
					}
					if(line.length()>0 && line != null){
						addToChat("[mea] ["+username+"] "+line);
						printer.println("[mea] ["+username+"] "+line);	
					}else{
						ChatPane.append("No message!\n");
					}
				}
				command.setText("");
			}
		});
		
		window.addWindowFocusListener(new WindowFocusListener(){
			public void windowGainedFocus(WindowEvent e) {
				if(popup != null) popup.despawn();
			}
			
			public void windowLostFocus(WindowEvent e) {
				//Do nothing
			}
		});
	}
	
	public void addToChat(String message){
		if(ChatPane.isTag(message)){
			ChatPane.append(" "+message+"\n");
		}
		if(MCPane.isTag(message)){
			MCPane.append(" "+message+"\n");
			if(popup != null){ popup.despawn(); }
			String parts[] = message.split(" ");
			String username = parts[1];
			String tag = parts[0].replaceAll("\\[", "").replaceAll("\\]", "");
			message = "";
			for(int i=2;i<parts.length;i++){
				message = message.concat(parts[i]+" ");
			}
			popup = new MeaPopup(username+" New "+tag+" msg!", message, window);
			if(!window.hasFocus()) popup.show();
		}
		if(IRCPane.isTag(message)){
			IRCPane.append(" "+message+"\n");
			if(popup != null){ popup.despawn(); }
			String parts[] = message.split(" ");
			String username = parts[1];
			String tag = parts[0].replaceAll("\\[", "").replaceAll("\\]", "");
			message = "";
			for(int i=2;i<parts.length;i++){
				message = message.concat(parts[i]+" ");
			}
			popup = new MeaPopup(username+" New "+tag+" msg!", message, window);
			if(!window.hasFocus()) popup.show();
		}
		if(MeaPane.isTag(message)){
			MeaPane.append(" "+message+"\n");
			if(popup != null){ popup.despawn(); }
			String parts[] = message.split(" ");
			String username = parts[1];
			String tag = parts[0].replaceAll("\\[", "").replaceAll("\\]", "");
			message = "";
			for(int i=2;i<parts.length;i++){
				message = message.concat(parts[i]+" ");
			}
			popup = new MeaPopup(username+" New "+tag+" msg!", message, window);
			if(!window.hasFocus()) popup.show();
		}
		if(message.startsWith("[MOTD]") ||
				message.startsWith("[WHOIS]") ||
				message.startsWith("[PM]") ||
				message.startsWith("[HELP]") ||
				message.startsWith("[NOTICE]")){
			//ChatPane.append(" "+message+"\n");  //Cause it has tag=null
			MCPane.append(" "+message+"\n");
			IRCPane.append(" "+message+"\n");
			MeaPane.append(" "+message+"\n");
		}
		if(ChatPane.getText().length()>1){
			ChatPane.setCaretPosition(ChatPane.getStyledDocument().getLength());
		}
		if(MCPane.getText().length()>1){
			MCPane.setCaretPosition(MCPane.getStyledDocument().getLength());
		}
		if(IRCPane.getText().length()>1){
			IRCPane.setCaretPosition(IRCPane.getStyledDocument().getLength());
		}
		if(MeaPane.getText().length()>1){
			MeaPane.setCaretPosition(MeaPane.getStyledDocument().getLength());
		}
		AllChatScroller.setAutoscrolls(true);
		MinecraftScroller.setAutoscrolls(true);
		IRCScroller.setAutoscrolls(true);
		MeaChatScroller.setAutoscrolls(true);
	}

	public void addUserToList(String source, String username){
		if(source.equalsIgnoreCase("mea")){
			who_mea.add(username);
		}else if(source.equalsIgnoreCase("MC")){
			who_MC.add(username);
		}else if(source.equalsIgnoreCase("IRC")){
			who_IRC.add(username);
		}
		updateWho();
	}

	public void removeUserFromList(String source, String username){
		if(source.equalsIgnoreCase("mea")){
			who_mea.remove(username);
		}else if(source.equalsIgnoreCase("MC")){
			who_MC.remove(username);
		}else if(source.equalsIgnoreCase("IRC")){
			who_IRC.remove(username);
		}
		updateWho();
	}
	
	public void updateWho(){
		String users = "";
		int online = 0;
		int usercounter = 0;
		for(String user : who_MC){
			if(user.equalsIgnoreCase(username)){
				user = "[YOU] "+user;
			}
			users = users.concat("   [MC] "+user+"\n");
			online++;
			usercounter++;
		}
		if(usercounter>0){
			users = users.concat("\n");
			usercounter = 0;
		}
		for(String user : who_IRC){
			if(user.equalsIgnoreCase(username)){
				user = "[YOU] "+user;
			}
			users = users.concat("   [IRC] "+user+"\n");
			online++;
			usercounter++;
		}
		if(usercounter>0){
			users = users.concat("\n");
			usercounter = 0;
		}
		for(String user : who_mea){
			if(user.equalsIgnoreCase(username)){
				user = "[YOU] "+user;
			}
			users = users.concat("   [mea] "+user+"\n");
			online++;
		}
		usersOnline.setText(" Users Online ("+online+"): \n");
		usersOnline.append(users);
	}
	
	public void updateSize(){
		tabs.setPreferredSize(new Dimension(window.getWidth()-200, window.getHeight()-90));
		MCPane.setPreferredSize(tabs.getMaximumSize());
		IRCPane.setPreferredSize(tabs.getMaximumSize());
		MeaPane.setPreferredSize(tabs.getMaximumSize());
		ChatPane.setPreferredSize(tabs.getMaximumSize());
		usersOnline.setPreferredSize(new Dimension(160, window.getHeight()-90));
		window.repaint();
	}
	
	public void update(){
		start();
	}
	
	private void start(){
		try{
			addToChat("Connecting to "+server+":4408...");
			sock = new Socket(server, 4408);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			printer = new PrintStream(sock.getOutputStream());
			printer.println("auth "+username+" "+password);	
			Runnable read = new Runnable(){
				public void run() {
					while(!sock.isClosed()){
						String line;
						if(reader != null){
							try {
								line = reader.readLine();
								if(line != null){
									if(line.startsWith("setusername")){
										String parts[] = line.split(" ");
										username = parts[1];
										addToChat("Connected as \""+username+"\"");
										addToChat("-------------------------");
										printer.println("setusername "+username);
										log("[SERVER] "+line);
									}else if(line.startsWith("noauth")){
										addToChat("Not authenticated!");
										addToChat("-------------------------");
										log("[SERVER] "+line);
										sock.close();
										System.exit(0);
									}else if(line.startsWith("adduser")){
										String parts[] = line.split(" ");
										String source = parts[1];
										String username = parts[2];
										addUserToList(source, username);
									}else if(line.startsWith("Disconnected by server")){
										System.out.println(line);
										log("[SERVER] "+line);
										sock.close();
										System.exit(0);
									}else{
										log(line);
										line = cleanLine(line);
										if(!line.startsWith("[mea] ["+username+"]") &&
												!line.startsWith("[mea] Client Connected: "+username)&&
												!line.startsWith("[IRC] [D3n3r0]")){
											addToChat(line);
											if(line.startsWith("[MOTD]")){
												who_mea.add(username);
												updateWho();
											}
										}
									}
								}
							} catch (IOException e) {
								//e.printStackTrace();
								if(e.getMessage().equalsIgnoreCase("Connection timed out: connect")){
									addToChat("Cannot connect");
									System.out.println("Cannot connect");
								}
								try {
									sock.close();
								} catch (IOException e1) {
									//e1.printStackTrace();
									System.exit(0);
								}
							}
						}
					}
				}
			};
			Runnable write = new Runnable(){
				public void run() {
					while(!sock.isClosed()){
						Scanner in = new Scanner(System.in);
						while(true){
							String line = in.nextLine();
							if(line.startsWith("/disconnect") ||
									line.startsWith("/dc")){
								addToChat("Disconnecting...");
								printer.println("disconnect "+username);
								try {
									sock.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								addToChat("Disconnected!");
								System.exit(0);
							}else{
								if(line.length()>200){
									addToChat("Message too long, only sending 200 characters");
									line = line.substring(0, 200);
								}
								if(line.length()>0 && line != null){
									addToChat("[mea] ["+username+"] "+line);
									printer.println("[mea] ["+username+"] "+line);	
								}else{
									ChatPane.append("No message!\n");
								}
							}
						}
					}
				}
			};
			Runnable sanity = new Runnable(){
				public void run() {
					while(!sock.isClosed());
					System.exit(0);
				}
			};
			Runnable update = new Runnable(){
				public void run(){
					while(true){
							try {
								BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://68.148.10.71/mc/plugins/chat_version.txt").openStream()));
								String line;
								while((line = in.readLine()) != null){
									int v = Integer.parseInt(line);	
									if(v>version){
										if(!updateBroadcast){
											@SuppressWarnings("unused")
											Download download = new Download(new URL("http://68.148.10.71/mc/plugins/meaChatGUI.jar"), System.getProperty("user.dir")+"/meaChatGUI.jar", false);
											updateBroadcast = true;
										}
										addToChat("** Version "+v+" available (Current Version: "+version+")! Restart to use it!");
										if(notification != null){ 
											notification.despawn();
										}
										notification = new MeaPopup("Update Ready! "+version, "Restart to apply the update.", window);
										notification.forceLocation(MeaPopup.TOP_RIGHT);
										notification.show();
									}
								}
								in.close();
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						try{
							if(!updateBroadcast){
								Thread.sleep(1000);
							}else{
								Thread.sleep(30000);
							}
						}catch(Exception e){}
					}
				}
			};
			Thread writerThread = new Thread(write);
			writerThread.start();
			Thread readerThread = new Thread(read);
			readerThread.start();
			Thread sanityThread = new Thread(sanity);
			sanityThread.start();
			Thread updateThread = new Thread(update);
			updateThread.start();
		}catch (Exception e){
			if(e.getMessage().equalsIgnoreCase("connection reset")){
				addToChat("[Server] Disconnected by server. (Server Crash?)");
			}else if(e.getMessage().equalsIgnoreCase("connection refused: connect")){
				addToChat("** Cannot connect to server");
			}else if(e.getMessage().equalsIgnoreCase("Connection timed out: connect")){
				addToChat("** Cannot connect to server");
			}else{
				e.printStackTrace();
			}
		}
	}
	
	public static String cleanLine(String line){
		line = Colors.removeFormatting(line);
		return Colors.removeColors(line);
	}
	
	public static void log(String line){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir")+"/log.txt"), true));
			out.write(line+"\r\n");
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
