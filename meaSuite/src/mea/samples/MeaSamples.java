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
package mea.samples;

import java.io.File;
import java.net.URL;

import mea.External.Download;
import mea.External.Unzip;

import org.bukkit.plugin.java.JavaPlugin;

public class MeaSamples {
	
	private JavaPlugin plugin;
	
	public MeaSamples(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unused")
	public void create(){
		try {
			if(!new File(plugin.getDataFolder()+"/samples/readMeBeforeDeleting.txt").exists()){
				File directory = new File(this.plugin.getDataFolder()+"/samples");
				directory.mkdirs();
				Download download = new Download(new URL("http://68.148.10.71/mc/plugins/ext/meaSuite/samples/pack.zip"), this.plugin.getDataFolder()+"/samples/pack.zip");
				Unzip unzip = new Unzip(this.plugin.getDataFolder()+"/samples/pack.zip", this.plugin.getDataFolder()+"/samples/");
				File file = new File(this.plugin.getDataFolder()+"/samples/pack.zip");
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
