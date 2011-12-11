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
package mea.plugin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import mea.Math.Expression;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiFunction {

	public static String merge(String array[], int startAt){
		String message = "";
		for(int i=startAt;i<array.length;i++){
			message = message.concat(array[i]+" ");
		}
		return message;
	}
	
	public static String addColor(String message, JavaPlugin plugin){
		String colorSeperator = plugin.getConfig().getString("meaSuite.colorVariable");
		message = message.replaceAll(colorSeperator+"0", ChatColor.getByCode(0x0).toString());
		message = message.replaceAll(colorSeperator+"1", ChatColor.getByCode(0x1).toString());
		message = message.replaceAll(colorSeperator+"2", ChatColor.getByCode(0x2).toString());
		message = message.replaceAll(colorSeperator+"3", ChatColor.getByCode(0x3).toString());
		message = message.replaceAll(colorSeperator+"4", ChatColor.getByCode(0x4).toString());
		message = message.replaceAll(colorSeperator+"5", ChatColor.getByCode(0x5).toString());
		message = message.replaceAll(colorSeperator+"6", ChatColor.getByCode(0x6).toString());
		message = message.replaceAll(colorSeperator+"7", ChatColor.getByCode(0x7).toString());
		message = message.replaceAll(colorSeperator+"8", ChatColor.getByCode(0x8).toString());
		message = message.replaceAll(colorSeperator+"9", ChatColor.getByCode(0x9).toString());
		message = message.replaceAll(colorSeperator+"a", ChatColor.getByCode(0xA).toString());
		message = message.replaceAll(colorSeperator+"b", ChatColor.getByCode(0xB).toString());
		message = message.replaceAll(colorSeperator+"c", ChatColor.getByCode(0xC).toString());
		message = message.replaceAll(colorSeperator+"d", ChatColor.getByCode(0xD).toString());
		message = message.replaceAll(colorSeperator+"e", ChatColor.getByCode(0xE).toString());
		message = message.replaceAll(colorSeperator+"f", ChatColor.getByCode(0xF).toString());
		message = message.replaceAll(colorSeperator+"A", ChatColor.getByCode(0xA).toString());
		message = message.replaceAll(colorSeperator+"B", ChatColor.getByCode(0xB).toString());
		message = message.replaceAll(colorSeperator+"C", ChatColor.getByCode(0xC).toString());
		message = message.replaceAll(colorSeperator+"D", ChatColor.getByCode(0xD).toString());
		message = message.replaceAll(colorSeperator+"E", ChatColor.getByCode(0xE).toString());
		message = message.replaceAll(colorSeperator+"F", ChatColor.getByCode(0xF).toString());
		return message;
	}
	
	public static void fireFeature(Player player){
		Location location = player.getLocation();
		location.setY(location.getY()+0.5);
		for(int i=0;i<2;i++){
			LivingEntity entity = player.getWorld().spawnCreature(location, CreatureType.SHEEP);
			Sheep sheep = (Sheep) entity;
			Random rand = new Random(System.currentTimeMillis());
			DyeColor colors[] = DyeColor.values();
			int index = rand.nextInt(colors.length);
			sheep.setColor(colors[index]);
		}
	}
	
	public static String playerName(String name){
		if(!name.equalsIgnoreCase("global") && !name.equalsIgnoreCase("all")){
			List<Player> names = Bukkit.matchPlayer(name);
			if(names.size()>0){
				name = names.get(0).getName();
			}
		}
		return name;
	}
	
	public static String getPre(JavaPlugin plugin){
		String name = addColor(plugin.getConfig().getString("meaSuite.prename"), plugin);
		return name;
	}
	
	public static boolean isLetter(String letter){
		boolean isLetter = false;
		if(letter.equalsIgnoreCase("a") || 
				letter.equalsIgnoreCase("b") || 
				letter.equalsIgnoreCase("c") || 
				letter.equalsIgnoreCase("d") || 
				letter.equalsIgnoreCase("e") || 
				letter.equalsIgnoreCase("f") || 
				letter.equalsIgnoreCase("g") || 
				letter.equalsIgnoreCase("h") || 
				letter.equalsIgnoreCase("i") || 
				letter.equalsIgnoreCase("j") || 
				letter.equalsIgnoreCase("k") || 
				letter.equalsIgnoreCase("l") || 
				letter.equalsIgnoreCase("m") || 
				letter.equalsIgnoreCase("n") || 
				letter.equalsIgnoreCase("o") || 
				letter.equalsIgnoreCase("p") || 
				letter.equalsIgnoreCase("q") || 
				letter.equalsIgnoreCase("r") || 
				letter.equalsIgnoreCase("s") || 
				letter.equalsIgnoreCase("t") || 
				letter.equalsIgnoreCase("u") || 
				letter.equalsIgnoreCase("v") || 
				letter.equalsIgnoreCase("w") || 
				letter.equalsIgnoreCase("x") || 
				letter.equalsIgnoreCase("y") || 
				letter.equalsIgnoreCase("z")){
			isLetter = true;
		}
		return isLetter;
	}
	
	public static String encodePassword(String input){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		    byte[] sha1hash = new byte[40];
		    md.update(input.getBytes("iso-8859-1"), 0, input.length());
		    sha1hash = md.digest();
		    StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < sha1hash.length; i++) { 
	            int halfbyte = (sha1hash[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = sha1hash[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        input = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	public static double convertTime(String command){
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
}
