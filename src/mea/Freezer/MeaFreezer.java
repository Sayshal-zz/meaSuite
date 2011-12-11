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
package mea.Freezer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import mea.Math.Expression;
import mea.plugin.MultiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaFreezer {

	private JavaPlugin plugin;
	private Material DOME_MATERIAL = Material.GLASS;
	
	public MeaFreezer(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void noPerms(Player player){
		player.sendMessage("You can't do that!!");
	}
	
	public void freezePlayer(Player sender, Player player, String args[]){
		String playername = args[0];
		if(!player.hasPermission("meaSuite.Freezer.exempt")){
			Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+playername+".yml"));
			boolean domes = getNode("settings.iceDome").equalsIgnoreCase("true");
			boolean domed = false;
			if(sender.hasPermission("meaSuite.Freezer.domefreeze") && domes){
				domed = true;
			}
			System.out.println(getNode("settings.iceDome").equalsIgnoreCase("true"));
			System.out.println(sender.hasPermission("meaSuite.Freezer.domefreeze"));
			String rawFrozenLength = args[1];
			String code = "none";
			boolean codeFrozen = false;
			String frozenBy = sender.getName();
			if(args.length > 2 && sender.hasPermission("meaSuite.Freezer.codefreeze")){
				code = args[2];
				if(code.equalsIgnoreCase("code")){
					code = getCode();
				}
				codeFrozen = true;
			}
			frozen.setProperty("frozenOn", getDate());
			frozen.setProperty("codeFrozen", codeFrozen+"");
			frozen.setProperty("code", code);
			frozen.setProperty("frozenUntil", getFrozenUntil(getBackgroundExpire(rawFrozenLength)));
			frozen.setProperty("whoFroze", frozenBy);
			frozen.setProperty("frozenLength", rawFrozenLength);
			frozen.setProperty("dome", domed+"");
			frozen.setProperty("millisUntilExpire", getBackgroundExpire(rawFrozenLength));
			frozen.setProperty("lastCheck", System.currentTimeMillis());
			frozen.setProperty("frozen", "true");
			if(domed){
				createDome(player.getLocation(), frozen);
			}
			frozen.save();
			String command = convertVariables(getNode("messages.onFreezeCommand"), player, "freeze");
			String onFreeze = convertVariables(getNode("messages.onFreeze"), player, "freeze");
			if(codeFrozen){
				onFreeze  = convertVariables(getNode("messages.onCodeFreeze"), player, "freeze");
			}
			if(!onFreeze.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin)+" "+onFreeze);
			}
			if(!command.equalsIgnoreCase("nocmd")){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
			MultiFunction.fireFeature(player);
		}else{
			sender.sendMessage(MultiFunction.getPre(plugin)+" "+convertVariables(getNode("messages.onExempt"), player, "freeze"));
		}
	}
	
	public void unfreezePlayer(Player sender, Player player, String args){
		String playername = args;
		Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+playername+".yml"));
		frozen.load();
		String rawFrozenLength = "0s";
		frozen.setProperty("frozenUntil", getFrozenUntil(getBackgroundExpire(rawFrozenLength)));
		frozen.setProperty("frozenLength", rawFrozenLength);
		frozen.setProperty("frozen", "false");
		frozen.save();
		String command = convertVariables(getNode("messages.onUnfreezeCommand"), player, "unfreeze");
		String onFreeze = convertVariables(getNode("messages.onUnfreeze"), player, "unfreeze");
		if(!onFreeze.equalsIgnoreCase("nomsg")){
			player.sendMessage(MultiFunction.getPre(plugin)+" "+onFreeze);
		}
		if(!command.equalsIgnoreCase("nocmd")){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		removeDome(player);
		frozen.save();
	}
	
	public void unfreezeCodedPlayer(Player sender, String args[]){
		Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+sender.getName()+".yml"));
		frozen.load();
		if(frozen.getBoolean("codeFrozen", false)){
			if(frozen.getString("code").equals(args[0])){
				unfreezePlayer(sender, sender, sender.getName());
			}
		}else{
			sender.sendMessage(MultiFunction.getPre(plugin)+" You are not code frozen!");
		}
	}
	
	public void toggleDome(String enabled){
		if(enabled.equalsIgnoreCase("enable")){
			setNode("settings.iceDome", "true");
		}else{
			setNode("settings.iceDome", "false");
		}
	}
	
	public void refresh(Player player){
		String playername = player.getName();
		Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+playername+".yml"));
		frozen.load();
		if(frozen.getString("frozenLength") != null){
			frozen.setProperty("frozenUntil", getFrozenUntil(getBackgroundExpire(frozen.getString("frozenLength"))));
			frozen.setProperty("millisUntilExpire", checkTime(frozen));
			frozen.setProperty("lastCheck", System.currentTimeMillis());
		}
		frozen.save();
	}
	
	private double checkTime(Configuration frozen) {
		double time = 0;
		long lc = Long.parseLong(frozen.getString("lastCheck"));
		time = System.currentTimeMillis() - lc;
		time = Double.parseDouble(frozen.getString("millisUntilExpire")) - time;
		return time;
	}

	public boolean isFrozen(Player player){
		String playername = player.getName();
		Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+playername+".yml"));
		frozen.load();
		boolean frozenBefore = false;
		if(frozen.getString("millisUntilExpire") != null){
			frozenBefore = (Double.parseDouble(frozen.getString("millisUntilExpire")) <= 0) ? true : false;
		}
		refresh(player);
		boolean frozenAfter = true;
		if(frozen.getString("millisUntilExpire") != null){
			frozenAfter = (Double.parseDouble(frozen.getString("millisUntilExpire")) <= 0) ? false : true;
		}
		if(frozenBefore && !frozenAfter && frozen.getString("frozen").equalsIgnoreCase("true")){
			unfreezePlayer(player, player, player.getName());
		}
		if(frozen.getString("millisUntilExpire") != null){
			return (frozen.getString("frozen").equalsIgnoreCase("true")) ? true : false;
		}else{
			return false;
		}
	}
	
	public void displayInfo(Player sender, Player player){
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getNode("messages.freezeInfo.line"+i) != null){
				sender.sendMessage(MultiFunction.getPre(plugin)+" "+convertVariables(getNode("messages.freezeInfo.line"+i), player, "isFrozen"));
			}else{
				hasCommand = false;
			}
			i++;
		}
	}
	
	public String getCode(){
		String characters[] = getNode("code.characters").split("");
		int length = Integer.parseInt(getNode("code.length"));
		boolean uppercase = Boolean.parseBoolean(getNode("code.randomUppercase"));
		String code = "";
		for(int i=0;i<=length;i++){
			Random rand = new Random(System.currentTimeMillis());
			String character = characters[rand.nextInt(characters.length)-1];
			if(uppercase){
				if(rand.nextInt(100) > 50){
					character = character.toUpperCase();
				}
			}
			code = code.concat(character);
		}
		return code;
	}
	
	public void sendCommand(String command){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	private String getDate(){
		DateFormat dateFormat = new SimpleDateFormat(getNode("timestamp.format"));
		Date date = new Date();
		String timestamp = dateFormat.format(date);
		return timestamp;
	}
	
	private double getBackgroundExpire(String command){
		command = command.replaceAll("y", "*31536000000+");
		command = command.replaceAll("mo", "*2628000000+");
		command = command.replaceAll("w", "*604800000+");
		command = command.replaceAll("d", "*86400000+");
		command = command.replaceAll("h", "*3600000+");
		command = command.replaceAll("m", "*60000+");
		command = command.replaceAll("s", "*1000+");
		command = command.substring(0, command.length()-1);
		Double ret = 0.0;
		ret = new Expression(command).resolve();
		//System.out.println(ret/1000);
		return ret;
	}
	
	private String getFrozenUntil(double d){
		d = d + System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat(getNode("timestamp.format"));
		Date date = new Date((long) d);
		String timestamp = dateFormat.format(date);
		return timestamp;
	}

	public String getNode(String node){
		return plugin.getConfig().getString("meaFreeze."+node);
	}
	

	public void setNode(String node, String value){
		plugin.getConfig().set("meaFreeze."+node, value);
		plugin.saveConfig();
	}
	
	public void createDome(Location location, Configuration config){
		try{
			File f = new File(plugin.getDataFolder()+"/dome.csv");
			if(!f.exists()){
				DomeCreator dome = new DomeCreator();
				dome.writeCSV(f.toString());
			}
			BufferedReader in = new BufferedReader(new FileReader(plugin.getDataFolder()+"/dome.csv"));
			String line = in.readLine();
			in.close();
			String parts[] = line.split("\\.\\.");
			int i = 0;
			for(String part : parts){
				part = part.replaceAll("\\(", "");
				part = part.replaceAll("\\)", "");
				String array[] = part.split("\\,");
				double headAmount = 0.5;
				double x = Double.parseDouble(array[0]);
				double y = Double.parseDouble(array[1])-headAmount;
				double z = Double.parseDouble(array[2]);
				Location block = new Location(location.getWorld(), location.getX()+x, location.getY()+y, location.getZ()+z);
				if(location.getWorld().getBlockAt(block).getType() == Material.AIR){
					block.getBlock().setType(DOME_MATERIAL);
					config.setProperty("dome."+i+".x", block.getX());
					config.setProperty("dome."+i+".y", block.getY());
					config.setProperty("dome."+i+".z", block.getZ());
					//config.setProperty("dome."+i+".world", block.getWorld().getName()+"");
					config.save();
				}
				i++;
			}
			config.setProperty("dome.max", i+"");
			config.save();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void removeDome(Player player){
		Configuration frozen = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+player.getName()+".yml"));
		frozen.load();
		if(frozen.getString("dome.max") != null){
			int lastBlock = Integer.parseInt(frozen.getString("dome.max"));
			for(int i=0;i<lastBlock;i++){
				double x = frozen.getDouble("dome."+i+".x", 0);
				double y = frozen.getDouble("dome."+i+".y", 0);
				double z = frozen.getDouble("dome."+i+".z", 0);
				Location block = new Location(player.getWorld(), x, y, z);
				if(player.getWorld().getBlockAt(block).getType() == DOME_MATERIAL){
					block.getWorld().getBlockAt(block).setType(Material.AIR);
				}
			}
		}
	}
	
	public boolean isEnabled(){
		return plugin.getConfig().getString("meaFreeze.enabled").equalsIgnoreCase("TRUE");
	}
	
	public void enable(Player player){
		plugin.getConfig().set("meaFreeze.enabled", "true");
		if(player != null){
			player.sendMessage(convertVariables(getNode("onEnable"), player, "freeze"));
		}
		plugin.saveConfig();
	}

	public void disable(Player player){
		plugin.getConfig().set("meaFreeze.enabled", "false");
		if(player != null){
			player.sendMessage(convertVariables(getNode("onDisable"), player, "freeze"));
		}
		plugin.saveConfig();
	}
	
	public String getProperty(Player player, String node){
		Configuration config = new Configuration(new File(plugin.getDataFolder()+"/meaFreeze/frozen_players/"+player.getName()+".yml"));
		config.load();
		return config.getString(node);
	}

	public String convertVariables(String message, Player player, String command) {
		message = message.replaceAll("%PLAYER%", player.getName());
		message = message.replaceAll("%HELPMENU%", getHelpMenuFor(command));
		message = message.replaceAll("%FROZENON%", getProperty(player, "frozenOn"));
		message = message.replaceAll("%CODEFROZEN%", getProperty(player, "codeFrozen"));
		message = message.replaceAll("%CODE%", getProperty(player, "code"));
		message = message.replaceAll("%FROZENUNTIL%", getProperty(player, "frozenUntil"));
		message = message.replaceAll("%WHOFROZE%", getProperty(player, "whoFroze"));
		message = message.replaceAll("%FROZENLENGTH%", getProperty(player, "frozenLength"));
		message = message.replaceAll("%DOME%", getProperty(player, "dome"));
		message = MultiFunction.addColor(message, plugin);
		return message;
	}

	private String getHelpMenuFor(String command) {
		String help = "No help found :(";
		if(command.equalsIgnoreCase("freeze")){
			help = "/freeze [PLAYERNAME | all] [Time: #s#m#h#d#w#mo#y] <Optional: Code>";
		}else if(command.equalsIgnoreCase("unfreeze")){
			help = "/unfreeze [PLAYERNAME | all]";
		}else if(command.equalsIgnoreCase("isFrozen")){
			help = "/isFrozen [PLAYERNAME]";
		}else if(command.equalsIgnoreCase("useDome")){
			help = "/useDome [enable | disable]";
		}else if(command.equalsIgnoreCase("authenticate")){
			help = "/authenticate [CODE]";
		}
		return help;
	}
}
