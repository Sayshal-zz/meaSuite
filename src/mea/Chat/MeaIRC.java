package mea.Chat;

import java.io.IOException;

import mea.Hook.MeaHook;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class MeaIRC extends PircBot{
	
	private MeaChat chat;
	private MeaHook hook;
	private boolean doMC = false;
	private boolean doIRC = false;
	
	public MeaIRC(MeaChat chat, MeaHook hook) {
		this.chat = chat;
		this.hook = hook;
		setName("meaBot");
		setAutoNickChange(true);
		boolean connected = true;
		try {
			System.out.println("[meaIRC] Connecting to "+chat.getIRCNode("server")+" channel "+chat.getIRCNode("channel"));
			connect(chat.getIRCNode("server"));
		} catch (NickAlreadyInUseException e) {
			System.out.println("[meaIRC] Cannot connect to IRC: Username in use \"meaBot\"");
			connected = false;
			chat.disable();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
		joinChannel("#"+chat.getIRCNode("channel"));
		if(connected){
			System.out.println("[meaIRC] Connected as 'meaBot'!");
		}
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message){
		if(sender!=getName()){
			if(message.length()>200){
				message = message.substring(0, 200);
				chat.toIRC("[mea] [meaBot:IRC] * Sorry "+sender+"! Message was too long, shortened to 200 characters.");
			}
			hook.onMessage(sender, "IRC", message);
		}
	}
	
	public void onJoin(String channel, String sender, String login, String hostname, String message){
		hook.onJoin(sender, "IRC");
	}
	
	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason){
		hook.onLeave(recipientNick, "IRC", true, reason);
		if (recipientNick.equalsIgnoreCase(getNick())) {
		    joinChannel(channel);
		}
	}
	
	public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason){
		hook.onLeave(sourceNick, "IRC", false, reason);
	}
	
	public boolean MinecraftToIRC(){
		return doMC;
	}
	
	public void sendMinecraftToIRC(boolean value){
		doMC = value;
	}
	
	public boolean IRCToMinecraft(){
		return doIRC;
	}
	
	public void sendIRCToMinecraft(boolean value){
		doIRC = value;
	}

	public void kill(){
		hook.onMessage("meaBot", "IRC", "* Shutting down bot...");
		disconnect();
	}
	
	public void sendMessage(String message) {
		String channels[] = getChannels();
		for(String channel : channels){
			//System.out.println(channel);
			sendMessage(channel, message);
		}
	}
	
	public boolean userOnline(String username){
		User users[] = getUsers(this.getChannels()[0]);
		if(users.length>0){
			for(User user : users){
				if(user.getNick().toLowerCase().startsWith(username.toLowerCase()) || user.getNick().equalsIgnoreCase(username)){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}
	
	public User getUser(String username){
		User users[] = getUsers(this.getChannels()[0]);
		if(users.length>0){
			for(User user : users){
				if(user.getNick().toLowerCase().startsWith(username.toLowerCase()) || user.getNick().equalsIgnoreCase(username)){
					return user;
				}
			}
			return null;
		}else{
			return null;
		}
	}
	
	public boolean pm(String username, String message){
		User user = getUser(username);
		if(user != null){
			this.sendMessage(user.getNick(), message);
			return true;
		}
		return false;
	}
	
	public String getIP(User user){
		String IP = "unknown";
		//TODO: Find a work around
		return IP;
	}
}
