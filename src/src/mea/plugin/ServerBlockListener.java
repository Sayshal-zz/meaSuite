package mea.plugin;

import mea.Freezer.MeaFreezer;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerBlockListener extends BlockListener{
	
	private JavaPlugin plugin;
	
	public ServerBlockListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer() != null){
			Player player = event.getPlayer();
			MeaFreezer freezer = new MeaFreezer(plugin);
			if(freezer.isFrozen(player)){
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer() != null){
			Player player = event.getPlayer();
			MeaFreezer freezer = new MeaFreezer(plugin);
			if(freezer.isFrozen(player)){
				event.setCancelled(true);
			}
		}
	}
}
