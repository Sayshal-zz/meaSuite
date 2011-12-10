package mea.Chat;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;

public class MeaChatThread implements Runnable{
	
	private BufferedReader reader;
	private MeaConnections connections;
	private String username = "meaSuiteUser";
	
	public int id = 0;
	public Socket socket;

	@SuppressWarnings("unused")
	private MeaIRC irc;
	@SuppressWarnings("unused")
	private MeaServerSocket server;
	@SuppressWarnings("unused")
	private MeaChat chat;
	@SuppressWarnings("unused")
	private PrintStream writer;
	
	private boolean running = true;
	
	public MeaChatThread(Socket socket, PrintStream writer, BufferedReader reader, MeaChat chat, MeaServerSocket server, MeaConnections connections, MeaIRC irc){
		this.socket = socket;
		this.writer = writer;
		this.reader = reader;
		this.chat = chat;
		this.server = server;
		this.connections = connections;
		this.irc = irc;
	}
	
	public void close(){
		running = false;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void run(){
		try{
			while(running){
				String line = reader.readLine();
				if(line != null){
					if(line.startsWith("disconnect")){
						String parts[] = line.split(" ");
						String username = "UNKNOWN";
						if(parts.length==2){
							username = parts[1];
						}
						connections.removeConnection(socket, username);
					}else if(line.startsWith("setusername")){
							String parts[] = line.split(" ");
							String username = "UNKNOWN";
							if(parts.length==2){
								username = parts[1];
							}
							setUsername(username);
					}else{
						connections.broadcastToAll(line);
					}
				}
			}
		}catch (Exception e){
			if(e.getMessage().equalsIgnoreCase("connection reset")){
				connections.removeConnection(socket, username);
			}else{
				e.printStackTrace();
			}
		}
	}
}
