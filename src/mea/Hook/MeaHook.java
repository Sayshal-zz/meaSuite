package mea.Hook;

import mea.Chat.MeaChat;
import mea.Chat.MeaStringFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MeaHook {
	
	//Config crap
	private JavaPlugin plugin;
	@SuppressWarnings("unused")
	private MeaChat chat;
	private MeaStringFormat message_format;

	//Plugins
	protected boolean Factions = false;
	protected boolean mcMMO = false;
	protected boolean CommandBook = false;
	protected boolean mChat = false;
	
	//Hooks
	protected CommandBookHook CommandBook_hook;
	protected FactionsHook Factions_hook;
	protected mcMMOHook mcMMO_hook;
	protected mChatHook mChat_hook;
	
	public MeaHook(JavaPlugin plugin, MeaChat chat){
		this.plugin = plugin;
		this.chat = chat;
	}
	
	public void startup(){
		System.out.println("[meaHook] Loading...");
		//Note : Sayshal outsmarted me here >.>
		if(pluginExists("mcMMO")){
			mcMMO_hook = new mcMMOHook(this);
			mcMMO = true;
		}
		if(pluginExists("Factions")){
			Factions_hook = new FactionsHook(this);
			Factions = true;
		}
		if(pluginExists("CommandBook")){
			CommandBook_hook = new CommandBookHook(this);
			CommandBook = true;
		}
		if(pluginExists("mChat")){
			mChat_hook = new mChatHook(this);
			mChat = true;
		}
		message_format = new MeaStringFormat(plugin);
		System.out.println("[meaHook] Loaded!");
	}
	
	public void onJoin(String player, String source){
		String format = "^T ^P (Error: SOURCE ERR)";
		String sMessage = "No Where";
		if(source.equalsIgnoreCase("mc")){
			format = getNode("formats.minecraft");
			sMessage = "Minecraft";
		}else if(source.equalsIgnoreCase("irc")){
			format = getNode("formats.irc");
			sMessage = "the IRC";
		}else if(source.equalsIgnoreCase("mea")){
			format = getNode("formats.meaChat");
			sMessage = "meaChat";
		}
		if(getNode("formats.showRanks").equalsIgnoreCase("true") && mChat && source.equalsIgnoreCase("mc")){
			format = message_format.mergeRank(mChat_hook.getGroup(player), format);
		}else{
			format.replaceAll("  ", " ");
		}
		//String message = message_format.onJoin(format, player, " * Joined "+sMessage, source);
	}
	
	public void onLeave(Player player, String source, boolean kicked, String kickmessage){
		
	}
	
	public void onLeave(String player, String source, boolean kicked, String kickmessage){
		
	}
	
	public void onMessage(Player player, String source, String message){
		
	}
	
	public void onMessage(String player, String source, String message){
		
	}
	
	private boolean pluginExists(String name){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		return (pm.getPlugin(name)!=null)?true:false;
		//(if call) ? isTrue : isFalse;
	}
	
	public String getNode(String node){
		return plugin.getConfig().getString("meaHook."+node);
	}
}
