/* meaSuite is copyright 2011/2012 of Turt2Live Programming and Sayshal Productions
 * 
 * Modifications of the code, or any use of the code must be preauthorized by Travis
 * Ralston (Original author) before any modifications can be used. If any code is 
 * authorized for use, this header must retain it's original state. The authors (Travis
 * Ralston and Tyler Heuman) can request your code at any time. Upon code request you have
 * 24 hours to present code before we will ask you to not use our code.
 * 
 * Contact information:
 * Travis Ralston
 * email: minecraft@turt2live.com
 * 
 * Tyler Heuman
 * email: contact@sayshal.com
 */
package mea.Flarf;

import java.io.File;

import mea.plugin.MultiFunction;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaFlarf {

	private JavaPlugin plugin;
	private File currentgames[];
	private Configuration plugin_configuration;
	private Configuration flarf_configuration;
	private Configuration messages_configuration;
	private Configuration stadiumlisting;
	
	public Events event;
	
	/* Hey tinkerbell: 
	 * 	Build parts of this if you can, I will add them.
	 * 
	 * 	Also add the WorldEdit hook to mea.Hooks, thanks
	 * 
	 */
	
	public MeaFlarf(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void startup(){
		currentgames = new File(plugin.getDataFolder()+"/meaFlarf/games/").listFiles();
		event = new Events(this.plugin, currentgames);
		plugin_configuration = plugin.getConfiguration();
		flarf_configuration = new Configuration(new File(plugin.getDataFolder()+"/meaFlarf/config.yml"));
		messages_configuration = new Configuration(new File(plugin.getDataFolder()+"/meaFlarf/messages.yml"));
		stadiumlisting = new Configuration(new File(plugin.getDataFolder()+"/meaFlarf/stadiums.yml"));
		plugin_configuration.load();
		flarf_configuration.load();
		messages_configuration.load();
		stadiumlisting.load();
	}
	
	public int newGame(){
		int gameID = 0;
		
		return gameID;
	}
	
	public int startGame(Player players[], Player guests[], int stadiumID, int mode, boolean override){
		int gameID = 0;
		/* TODO: Commands:
		 *		/flarf start [detect | PLAYERNAMES] [detect | GUESTNAMES] <mode : 0> <override : false>
		 *		
		 *		NOTE:
		 *		Can be used by MeaHook with null
		 */
		return gameID;
	}
	
	public void closeGame(int gameID){
		/* TODO: Commands:
		 *		/flarf close [gameID]
		 */
	}
	
	public int getGameID(Player player){
		return getGameID(player.getLocation());
	}
	
	public int getGameID(Location location){
		/* TODO: NOTE:
		 *		Used globally and in API/MeaHook
		 *		Can also use (player) mode
		 */
		int gameID = 0;
		
		return gameID;
	}
	
	public void createStadium(Player player, Location location, String name, String configfile, boolean importRequired, String schematic){
		/* TODO: Commands:
		 *		/flarf create [Schematic] [Stadium Name] <Import Schematic>
		 */
		File file = new File(plugin.getDataFolder()+"/meaFlarf/schematics/"+((schematic.endsWith("schematic"))?schematic:schematic+".schematic"));
		if(!file.exists()){
			player.sendMessage(getMessage("stadiumDoesntExist"));
			
		}
	}
	
	public void removeStadium(int stadiumID){
		/* TODO: Commands:
		 *		/flarf remove [name | id | location]
		 */
	}
	
	public String getMessage(String node){
		String message = messages_configuration.getString(node);
		message = MultiFunction.getPre(plugin).concat(" "+message);
		return message;
	}
	
	public void reload(){
		/* TODO: Commands:
		 *		/flarf reload
		 */
		plugin_configuration.load();
		flarf_configuration.load();
		messages_configuration.load();
		stadiumlisting.load();
	}
	
	public void handleCommand(String command, String args[], Player player){
		/* TODO: Commands:
		 *		/flarf create [Schematic] [Stadium Name] <Import Schematic>
		 */
	}
	
	public boolean isCommand(String command, String args[]){
		boolean isCommand = false;
		/* TODO:
		 *		do this
		 */
		return isCommand;
	}
}
