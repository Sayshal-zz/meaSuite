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
package mea.api;

import mea.Economy.api.MeaEconomyAPI;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MeaAPI {
	
	private static JavaPlugin plugin;
	
	@SuppressWarnings("static-access")
	public MeaAPI(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public MeaEconomyAPI getEconomy(){
		return new MeaEconomyAPI(plugin);
	}
	
	public MeaEconomyAPI getEconomy(Player player){
		return new MeaEconomyAPI(plugin, player);
	}
}
