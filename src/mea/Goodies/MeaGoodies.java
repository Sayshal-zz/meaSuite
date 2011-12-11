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

public class MeaGoodies {
	
	private JavaPlugin plugin;
	
	public MeaGoodies(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void suggestion(String line, Player player){
	    if(line.replaceAll(" ", "").length()<=8){
	    	String message = plugin.getConfig().getString("meaGoodies.messages.onNoSuggestion");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
			}
	    }else{
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(plugin.getDataFolder()+"/meaGoodies/suggestions.txt"), true));
				DateFormat dateFormat = new SimpleDateFormat(plugin.getConfig().getString("meaGoodies.timeFormat"));
			    Date date = new Date();
			    String timestamp = dateFormat.format(date);
			    if(plugin.getConfig().getString("meaGoodies.wrapSuggestionInQuotes").equalsIgnoreCase("true")){
			    	line = "\""+line+"\"";
			    }
			    String message = plugin.getConfig().getString("meaGoodies.suggestionFormat").replaceAll("%PLAYER%", player.getName()).replaceAll("%SUGGESTION%", line).replaceAll("%TIMESTAMP%", timestamp);
				out.write(message+"\r\n");
				out.close();
				plugin.reloadConfig();
				message = plugin.getConfig().getString("meaGoodies.messages.onSuggestion");
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
		return plugin.getConfig().getString("meaGoodies.noCaps").equalsIgnoreCase("true");
	}
	
}
