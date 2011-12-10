package mea.Hook;

import mea.Chat.MeaChat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
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
		System.out.println("[meaHook] Loaded!");
	}
	
	public void onJoin(String player, String source){
		/* TODO:
		 * 		Configurable fmt
		 * 		Fuck it... adding now
		 */
		String format = "^T ^P (Error: SOURCE ERR)";
		if(source.equalsIgnoreCase("mc")){
			format = "[MC] [^P]: ^m";
		}else if(source.equalsIgnoreCase("irc")){
			format = "[IRC] [^P]: ^m";
		}else if(source.equalsIgnoreCase("mea")){
			format = "[mea] [^P]: ^m";
		}
	}
	
	public void onLeave(Player player, String source, boolean kicked, String kickmessage){
		
	}
	
	public void onMessage(Player player, String source, String message){
		
	}
	
	private boolean pluginExists(String name){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		return (pm.getPlugin(name)!=null)?true:false;
		//(if call) ? isTrue : isFalse;
	}
	
	public String getNode(String node){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaHook."+node);
	}
}
