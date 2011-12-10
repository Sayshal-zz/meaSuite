package mea.Goodies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mea.plugin.MultiFunction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaGoodies {
	
	private JavaPlugin plugin;
	
	public MeaGoodies(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void suggestion(String line, Player player){
	    Configuration config = plugin.getConfiguration();
	    if(line.replaceAll(" ", "").length()<=8){
	    	String message = config.getString("meaGoodies.messages.onNoSuggestion");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
			}
	    }else{
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(plugin.getDataFolder()+"/meaGoodies/suggestions.txt"), true));
				DateFormat dateFormat = new SimpleDateFormat(config.getString("meaGoodies.timeFormat"));
			    Date date = new Date();
			    String timestamp = dateFormat.format(date);
			    if(config.getString("meaGoodies.wrapSuggestionInQuotes").equalsIgnoreCase("true")){
			    	line = "\""+line+"\"";
			    }
			    String message = config.getString("meaGoodies.suggestionFormat").replaceAll("%PLAYER%", player.getName()).replaceAll("%SUGGESTION%", line).replaceAll("%TIMESTAMP%", timestamp);
				out.write(message+"\r\n");
				out.close();
				config.load();
				message = config.getString("meaGoodies.messages.onSuggestion");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
				}
			}catch (Exception e){
				e.printStackTrace();
				player.sendMessage(ChatColor.RED+"Something went wrong. Please type: /modreq #suggest not working");
			}
	    }
	}
	
	public boolean setToLowerCase(){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaGoodies.noCaps").equalsIgnoreCase("true");
	}
	
}
