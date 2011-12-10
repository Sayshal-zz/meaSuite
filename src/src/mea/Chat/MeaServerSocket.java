package mea.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.plugin.java.JavaPlugin;

public class MeaServerSocket {
	
	private String server = "[Unknown Server]";
	private int port = 4408;
	private MeaChat chat;
	@SuppressWarnings("unused")
	private JavaPlugin plugin;
	private boolean run = true;
	private MeaConnections connections;
	private ServerSocket sock;
	private MeaIRC irc;
	
	public MeaServerSocket(JavaPlugin plugin, MeaChat chat, MeaIRC irc){
		this.irc = irc;
		this.plugin = plugin;
		this.chat = chat;
		connections = new MeaConnections(plugin, chat, this, irc);
	}
	
	public void startServer(int port, String serverName){
		this.port = port;
		server = serverName;
		createSocket();
	}
	
	public boolean isLoggedIn(String username){
		return connections.isLoggedIn(username);
	}
	
	private void createSocket(){
		Runnable runnable = new Runnable(){
			public void run(){
				try{
					sock = new ServerSocket(port);
					Socket sockAcc;
					while(run){
						sockAcc = sock.accept();
						BufferedReader reader = new BufferedReader(new InputStreamReader(sockAcc.getInputStream()));
						if(sockAcc.isClosed()){
							sockAcc = null;
							continue;
						}else{
							String line = reader.readLine();
							if(line == null){
								sockAcc.close();
								sockAcc = null;
								continue;
							}else{
								connections.addConnection(sockAcc);
								handle(line, sockAcc);
							}
						}
						sockAcc = null;
					}
					//sock.close();
				}catch (Exception e){
					//e.printStackTrace();
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	public void handle(String line, Socket sock){
		if(line.startsWith("auth")){
			String parts[] = line.split(" ");
			if(parts.length == 3){
				if(chat.authenticated(parts[1], parts[2])){
					connections.sendToMea("setusername "+parts[1], sock);
					connections.sendToMea("[MOTD] Welcome to "+server, sock);
					connections.broadcastToAll("[mea] Client Connected: "+parts[1]);
				}else{
					connections.sendToMea("noauth", sock);
				}
			}else{
				connections.sendToMea("noauth", sock);
			}
		}else{
			broadcast(line);
		}
	}
	
	public void broadcast(String message){
		if(message.startsWith("[IRC]")){
			connections.sendToMea(message);
			if(irc != null){
				if(irc.IRCToMinecraft()){
					connections.sendToMinecraft(message);
				}
			}
		}else if(message.startsWith("[MC]")){
			if(irc != null){
				if(irc.MinecraftToIRC()){
					connections.sendToIRC(message);
				}
			}
			connections.sendToMea(message);
		}else if(message.startsWith("[mea]")){
			connections.sendToIRC(message);
			connections.sendToMinecraft(message);
		}else if(message.startsWith("[CONSOLE]")){
			connections.sendToMea(message);
		}
	}
	
	public void kill(){
		connections.disconnectAll();
		connections.killIRC();
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setIRC(MeaIRC irc) {
		this.irc = irc;
		connections.setIRC(irc);		
	}
	
	public void toIRC(String message){
		connections.sendToIRC(message);
	}
}
