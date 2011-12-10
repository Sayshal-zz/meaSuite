package mea.Chat;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

public class MeaIRC extends PircBot{
	
	private MeaChat chat;
	private boolean doMC = false;
	private boolean doIRC = false;
	
	public MeaIRC(MeaChat chat) {
		this.chat = chat;
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
			chat.message("[IRC] ["+sender+"] "+message);
		}
	}
	
	public void onJoin(String channel, String sender, String login, String hostname, String message){
		if(login!=this.getName()){
			chat.message("[IRC] [meaBot] * User joined: "+login);
		}
	}
	
	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason){
		if(recipientNick!=getName()){
			chat.message("[IRC] [meaBot] * User kicked: "+recipientNick+" for \""+reason+"\"");
		}
	}
	
	public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason){
		if(sourceNick!=getName()){
			chat.message("[IRC] [meaBot] * User quit: "+sourceNick+" ("+reason+")");
		}
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
		chat.message("[IRC] [meaBot] * Shutting down bot...");
		disconnect();
	}
	
	public void sendMessage(String message) {
		String channels[] = getChannels();
		for(String channel : channels){
			//System.out.println(channel);
			sendMessage(channel, message);
		}
	}
	
}
