package mea.Chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MeaServerSocket {
	
	private String server = "[Unknown Server]";
	private int port = 4408;
	private MeaChat chat;
	private JavaPlugin plugin;
	private boolean run = true;
	private MeaConnections connections;
	private ServerSocket sock;
	private MeaIRC irc;
	
	public int numguests = 0;
	
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
					if(numguests>0 && parts[1].equalsIgnoreCase("guest")){
						parts[1] = "Guest"+(numguests+1);
						numguests++;
					}else if(numguests == 0 && parts[1].equalsIgnoreCase("guest")){
						numguests++;
					}
					connections.sendToMea("setusername "+parts[1], sock);
					connections.sendToMea("[MOTD] Welcome to "+server, sock);
					connections.broadcastToAll("[mea] Client Connected: "+parts[1]);
				}else{
					connections.sendToMea("noauth", sock);
				}
			}else{
				connections.sendToMea("noauth", sock);
			}
		}else if(line.startsWith("whois")){
			String parts[] = line.split(" ");
			if(parts.length<2){
				connections.sendTo(sock, "Too few arguments! /whois user");
			}else{
				String target = parts[1];
				List<Player> matches = Bukkit.matchPlayer(target);
				if(matches.size()>0){
					connections.sendTo(sock, "Username: "+matches.get(0).getName());
					connections.sendTo(sock, "IP: "+matches.get(0).getAddress().getHostString().replaceAll("\\/", ""));
					connections.sendTo(sock, "Logged into: Minecraft");
				}else{
					if(irc.userOnline(target)){
						connections.sendTo(sock, "Username: "+irc.getUser(target).getNick());
						connections.sendTo(sock, "IP: "+irc.getIP(irc.getUser(target)));
						connections.sendTo(sock, "Logged into: IRC");
					}else{
						if(connections.isLoggedIn(target)){
							
						}
					}
				}
			}
		}else if(line.startsWith("pm")){
			String parts[] = line.split(" ");
			if(parts.length<3){
				connections.sendTo(sock, "Too few arguments! /pm [user] [message]");
			}else{
				String to = parts[1];
				String message = "";
				for(int i=2;i<parts.length;i++){
					message = message.concat(parts[i]+" ");
				}
				List<Player> matches = Bukkit.matchPlayer(to);
				if(matches.size()>0){
					MeaStringFormat format = new MeaStringFormat(plugin);
					matches.get(0).sendMessage(format.formatPM(to, connections.getUsername(sock), message, "mea"));
					connections.sendToMea(format.formatPMFrom(to, connections.getUsername(sock), message, "mea", "MC"), sock);
				}else{
					if(irc.userOnline(to)){
						MeaStringFormat format = new MeaStringFormat(plugin);
						irc.pm(to, format.formatPM(to, connections.getUsername(sock), message, "mea"));
						connections.sendToMea(format.formatPMFrom(to, connections.getUsername(sock), message, "mea", "IRC"), sock);
					}else{
						if(connections.isLoggedIn(to)){
							MeaStringFormat format = new MeaStringFormat(plugin);
							connections.sendToMea(format.formatPM(to, connections.getUsername(sock), message, "mea"), connections.getSocket(to));
							connections.sendToMea(format.formatPMFrom(to, connections.getUsername(sock), message, "mea", "mea"), sock);
						}
					}
				}
			}
		}else if(line.startsWith("meachatonly")){
			String parts[] = line.split(" ");
			if(parts[1].equalsIgnoreCase("<msg>")){
				
			}else if(parts[1].equalsIgnoreCase("<user>")){
				
			}else{
				connections.sendTo(sock, "** Command not in proper format! "+line);
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

	public MeaConnections getChatThreads(){
		return connections;
	}
}
