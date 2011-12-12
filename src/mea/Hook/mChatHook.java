package mea.Hook;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.D3GN.MiracleM4n.mChat.mChatAPI;

public class mChatHook {

	@SuppressWarnings("unused")
	private MeaHook hook;
	private mChatAPI mChat;
	
	@SuppressWarnings("static-access")
	public mChatHook(MeaHook hook){
		this.hook = hook;
		mChat = new net.D3GN.MiracleM4n.mChat.mChat().API;
	}
	
	public String getGroup(Player player){
		return mChat.addColour(mChat.getPrefix(player));
	}
	
	public String getGroup(String player){
		List<Player> matches = Bukkit.matchPlayer(player);
		if(matches.size()>0)
			return mChat.addColour(mChat.getPrefix(matches.get(0)));
		return "Dunno";
	}
}
