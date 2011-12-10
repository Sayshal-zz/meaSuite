package mea.Flarf;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Events {

	private JavaPlugin plugin;
	private File currentgames[];
	
	public Events(JavaPlugin plugin, File[] currentgames){
		this.plugin = plugin;
		this.currentgames = currentgames;
	}
	
	public boolean isFalling(Player player, Location to, Location from, int gameID){
		boolean falling = false;
		
		return falling;
	}
	
	public void playerFell(Player player, Location to, Location from, int gameID, PlayerMoveEvent event){
		
	}
	
	public void playerUsedFishingRod(Player player, PlayerInteractEvent event){
		
	}
	
	public void guestFell(Player player, Location to, Location from, int gameID, PlayerMoveEvent event){
		
	}
}
