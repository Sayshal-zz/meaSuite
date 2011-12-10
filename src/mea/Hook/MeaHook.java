package mea.Hook;

import mea.Chat.MeaChat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaHook {
	
	//Config crap
	private JavaPlugin plugin;
	@SuppressWarnings("unused")
	private MeaChat chat;

	//Plugins
	protected boolean Factions = false;
	protected boolean mcMMO = false;
	protected boolean CommandBook = false;
	
	//Hooks
	protected CommandBookHook CommandBook_hook;
	protected FactionsHook Factions_hook;
	protected mcMMOHook mcMMO_hook;
	
	public MeaHook(JavaPlugin plugin, MeaChat chat){
		this.plugin = plugin;
		this.chat = chat;
	}
	
	public void startup(){
		System.out.println("[meaHook] Loading...");
		System.out.println("[meaHook] Loaded!");
	}
	
	public void onJoin(Player player, String source){
		
	}
	
	public void onLeave(Player player, String source, boolean kicked, String kickmessage){
		
	}
	
	public void onMessage(Player player, String source, String message){
		
	}
	
	public String getNode(String node){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaHook."+node);
	}
}
