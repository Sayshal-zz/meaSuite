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
	
	@SuppressWarnings("unused")
	private String getNode(String path){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaLottery."+path, null);
	}
}
