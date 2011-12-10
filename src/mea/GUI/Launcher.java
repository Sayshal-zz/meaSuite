package mea.GUI;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mea.External.Download;

import org.bukkit.util.config.Configuration;
import org.jibble.pircbot.Colors;

@SuppressWarnings("deprecation")
public class Launcher {
	
	static PrintStream printer;
	static BufferedReader reader;
	static Socket sock;
	static String username = "meaChatClient";
	static String password = "meaChatPassword";
	//static String server = "96.52.204.164";  // TODO : TEMP
	private String warningSoILookHereAndResetThisSetting = "";
	static String server = "68.148.10.71";
	
	static Console console = System.console();
	
	protected static int version = 268;
	private static boolean updateBroadcast = false;
	private static MeaChat meaChat;
	
	private static JFrame window;
	
	public static void main(String[] args){
		System.out.println("-------------------------");
		System.out.println("|  meaChat Application  |");
		System.out.println("-------------------------");
		System.out.println("  Version: "+version);
		if(console == null){
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://68.148.10.71/mc/plugins/chat_version.txt").openStream()));
				String line;
				while((line = in.readLine()) != null){
					int v = Integer.parseInt(line);
					if(v>=version){
						JOptionPane.showMessageDialog(null, "Error: Cannot load without console!");
						System.exit(0);
					}
				}
				in.close();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: Exception thrown");
				System.exit(0);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: Exception thrown");
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: Exception thrown");
				System.exit(0);
			}
		}else{
			Configuration config = new Configuration(new File(System.getProperty("user.dir")+"/config.yml"));
			config.load();
			if(config.getInt("version", 0) != version){
				if(config.getString("creds.username") == null || config.getString("creds.username").equalsIgnoreCase(username)){
					config.setProperty("creds.username", username);
					config.setProperty("creds.server", server);
				}
				config.setProperty("getAllMessagesOnLogin", false);
				config.setProperty("version", version);
				config.save();
			}
			String saved_un = config.getString("creds.username");
			String useSave = "n";
			if(!saved_un.equalsIgnoreCase(username)){
				useSave = console.readLine("Username \""+saved_un+"\" saved, use this user? (Y/N, ENTER='Y'): ", "y");
			}
			String un = saved_un;
			if(useSave.equalsIgnoreCase("N")){
				un = console.readLine("meaSuite Username: ");
			}else{
				System.out.println("Using username \""+un+"\"...");
			}
			char pw[] = console.readPassword("meaSuite Password: ");
			username = un;
			password = String.copyValueOf(pw);
			server = config.getString("creds.server");
			config.setProperty("creds.username", username);
			config.setProperty("creds.server", server);
			config.save();
		}
		if(args.length>0){
			if(args[0].equalsIgnoreCase("nogui")){
				consoleMode();
			}else{
				System.out.println("Invalid argument.");
				System.exit(0);
			}
		}else{
			System.out.println("GUI loaded! Please don't close this console!");
			guiMode();
		}
	}
	
	public static void guiMode(){
		//System.out.println("GUI not fully working! Console mode enabled just in case...");
		//consoleMode();
		window = new JFrame("meaChat (Build "+version+") -- "+username);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800,600);
		window.setResizable(true);
		window.setLocationRelativeTo(null);
		meaChat = new MeaChat(username, password, server, console, window, version);
		window.add(meaChat);
		window.setVisible(true);
		
		meaChat.update();
		
		window.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                meaChat.updateSize();
            }
        });
		
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Disconnecting...");
				if(meaChat.printer!=null){
					meaChat.printer.println("disconnect "+username);
				}
				try {
					meaChat.sock.close();
				} catch (IOException e1) {
					//e1.printStackTrace();
					try{
						meaChat.addToChat("** Closing in 5 seconds...");
						System.out.println("** Closing in 5 seconds...");
						Thread.sleep(5000);
						System.exit(0);
					}catch (Exception e11){
						//e11.printStackTrace();
					}
				}
				System.out.println("Disconnected!");
				System.exit(0);
			}
		});
	}
	
	public static void consoleMode(){
		try{
			System.out.println("Connecting to "+server+":4408...");
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
										System.out.println("Connected as \""+username+"\"");
										System.out.println("-------------------------");
										printer.println("setusername "+username);
										log("[SERVER] "+line);
									}else if(line.startsWith("noauth")){
										System.err.println("Not authenticated!");
										System.out.println("-------------------------");
										log("[SERVER] "+line);
										sock.close();
										System.exit(0);
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
											System.out.println(line);
										}
									}
								}
							} catch (IOException e) {
								//e.printStackTrace();
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
								System.out.println("Disconnecting...");
								printer.println("disconnect "+username);
								try {
									sock.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								System.out.println("Disconnected!");
								System.exit(0);
							}else{
								if(line.length()>200){
									System.err.println("Message too long, only sending 200 characters");
									line = line.substring(0, 200);
								}
								if(line.length()>0 && line != null){
									System.out.println("[mea] ["+username+"] "+line);
									printer.println("[mea] ["+username+"] "+line);	
								}else{
									System.err.println("No message!");
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
										System.err.println("** Version "+v+" available (Current Version: "+version+")! Restart to use it!");
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
				System.err.println("[Server] Disconnected by server. (Server Crash?)");
			}else if(e.getMessage().equalsIgnoreCase("connection refused: connect")){
				System.err.println("** Cannot connect to server");
			}else{
				e.printStackTrace();
			}
		}
	}
	
	public static String cleanLine(String line){
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
