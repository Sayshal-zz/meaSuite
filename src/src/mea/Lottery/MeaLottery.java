package mea.Lottery;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaLottery {
	
	private JavaPlugin plugin;
	
	public MeaLottery(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void startup(){
		
	}
	
	public boolean isCommand(String cmd, String args[]){
		boolean command = false;
		if(cmd.equalsIgnoreCase("lottery")){
			command = true;
		}
		return command;
	}
	
	public void handleCommand(String cmd, String args[], Player player){
		
	}
	
	public void purchaseTicket(Player player, int amount){
		
	}
	
	public void purchaseTicket(Player player){
		purchaseTicket(player, 1);
	}
	
	public void rollForWinner(Player commandsender){
		
	}
	
	private Object getNode(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getProperty("meaLottery."+path);
	}
	
	private double getNodeAsDouble(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getDouble("meaLottery."+path, 0);
	}
	
	private int getNodeAsInt(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getInt("meaLottery."+path, 0);
	}
	
	private String getNodeAsString(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaLottery."+path, null);
	}
	
	private boolean getNodeAsBoolean(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getBoolean("meaLottery."+path, false);
	}
}
