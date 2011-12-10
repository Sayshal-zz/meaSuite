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
package mea.Economy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import mea.Economy.api.MeaEconomyAPI;
import mea.Math.Expression;
import mea.plugin.MultiFunction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaEconomy {
	
	private JavaPlugin plugin;
	private Thread TAX_THREAD;
	private Thread INTEREST_THREAD;
	private boolean TAX_THREAD_GO = true;
	private boolean INTEREST_THREAD_GO = true;
		
	public MeaEconomy(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void startup(){
		reload();
	}
	
	public boolean isCommand(String cmd, String[] args, Player player){
		boolean command = false;
		MeaEconomyAPI api = new MeaEconomyAPI(plugin);
		api.setPlayer(player);
		if(cmd.equalsIgnoreCase("money")){
			if(args.length < 1){
				command = true;
				player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(getNode("messages.balance").replaceAll("%BALANCE%", api.getBalanceAsString()), plugin));
			}else{
				if(args[0].equalsIgnoreCase("pay") && player.hasPermission("meaSuite.Money.send")){
					command = true;
					String to = args[1];
					String amount = args[2];
					api.transfer(Double.parseDouble(amount), player, Bukkit.getPlayer(to));
					log(player, "SENT "+amount+" TO "+to);
					String victimMessage = MultiFunction.addColor(getNode("messages.onGetFromPay").replaceAll("%SENDER%", player.getName()).replaceAll("%AMOUNT%", amount).replaceAll("%BALANCE%", api.getBalanceAsString(Bukkit.getPlayer(to))), plugin);
					if(!victimMessage.equalsIgnoreCase("nomsg")){
						Bukkit.getPlayer(to).sendMessage(MultiFunction.getPre(plugin)+" "+victimMessage);
					}
					String message = MultiFunction.addColor(getNode("messages.onSend").replaceAll("%PLAYER%", to).replaceAll("%AMOUNT%", Wallet.convertBalanceToString(amount)).replaceAll("%BALANCE%", api.getBalanceAsString(player)), plugin);
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin)+" "+message);
					}
				}else if(args[0].equalsIgnoreCase("give") && player.hasPermission("meaSuite.Money.give")){
					command = true;
					String to = args[1];
					String amount = args[2];
					api.giveMoney(Double.parseDouble(amount), Bukkit.getPlayer(to));
					log(player, "GAVE "+amount+" TO "+to);
					String victimMessage = MultiFunction.addColor(getNode("messages.onGetFromGive").replaceAll("%SENDER%", player.getName()).replaceAll("%AMOUNT%", amount).replaceAll("%BALANCE%", api.getBalanceAsString(Bukkit.getPlayer(to))), plugin);
					if(!victimMessage.equalsIgnoreCase("nomsg")){
						Bukkit.getPlayer(to).sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(victimMessage, plugin));
					}
					String message = MultiFunction.addColor(getNode("messages.onGive").replaceAll("%PLAYER%", to).replaceAll("%AMOUNT%", Wallet.convertBalanceToString(amount)), plugin);
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message, plugin));
					}
				}else if(args[0].equalsIgnoreCase("set") && player.hasPermission("meaSuite.Money.set")){
					command = true;
					String to = args[1];
					String amount = args[2];
					api.setBalance(Double.parseDouble(amount), Bukkit.getPlayer(to));
					log(player, "SET BALANCE OF "+to+" TO "+amount);
					String victimMessage = MultiFunction.addColor(getNode("messages.onGetFromSet").replaceAll("%SENDER%", player.getName()).replaceAll("%AMOUNT%", amount).replaceAll("%BALANCE%", api.getBalanceAsString(Bukkit.getPlayer(to))), plugin);
					if(!victimMessage.equalsIgnoreCase("nomsg")){
						Bukkit.getPlayer(to).sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(victimMessage, plugin));
					}
					String message = MultiFunction.addColor(getNode("messages.onSet").replaceAll("%PLAYER%", to).replaceAll("%AMOUNT%", Wallet.convertBalanceToString(amount)), plugin);
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message, plugin));
					}
				}else if(args[0].equalsIgnoreCase("top") && player.hasPermission("meaSuite.Money.top")){
					command = true;
					top(player);
				}else if(args[0].equalsIgnoreCase("reload") && player.hasPermission("meaSuite.Money.reload")){
					reload();
				}else if(player.hasPermission("meaSuite.Money.othermoney")){
					command = true;
					String user = args[0];
					String nodeMessage = MultiFunction.addColor(getNode("messages.otherBalance").replaceAll("%PLAYER%", user).replaceAll("%BALANCE%", api.getBalanceAsString(Bukkit.getPlayer(user))), plugin);
					if(!nodeMessage.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin)+" "+nodeMessage);
					}
					log(player, "REQUESTED BALANCE OF "+user);
				}else{
					command = true;
					String nodeMessage = MultiFunction.addColor(getNode("messages.noPerms"), plugin);
					if(!nodeMessage.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(nodeMessage, plugin));
					}
				}
			}
		}else if(cmd.equalsIgnoreCase("moneytop") && player.hasPermission("meaSuite.Money.top")){
			command = true;
			top(player);
		}else if(cmd.equalsIgnoreCase("moneytop") && !player.hasPermission("meaSuite.Money.top")){
			command = true;
			String nodeMessage = MultiFunction.addColor(getNode("messages.noPerms"), plugin);
			if(!nodeMessage.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(nodeMessage, plugin));
			}
		}
		return command;
	}
	
	public void playerCheck(Player player){
		File file = new File(plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Wallet wallet = new Wallet(plugin, player);
			wallet.setBalance(Double.parseDouble(getNode("defaultBalance")));
		}
	}
	
	public String getNode(String node){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaEconomy."+node);
	}
	
	private void top(Player sender){
		int number = Integer.parseInt(getNode("showTop"));
		Vector<MeaEconomyAPI> people = new Vector<MeaEconomyAPI>();
		File players[] = new File(plugin.getDataFolder()+"/meaEconomy/player_information/").listFiles();
		MeaEconomyAPI previous = new MeaEconomyAPI(plugin);
		for(int i=0;i<number;i++){
			for(File player : players){
				String parts[] = player.getName().split("\\.");
				String playerName = parts[0];
				Player pl = Bukkit.getPlayer(playerName);
				MeaEconomyAPI wallet1 = new MeaEconomyAPI(plugin, pl);
				if(wallet1.getBalance() >= previous.getBalance()){
					previous = wallet1;
				}
			}
			people.add(previous);
		}
		int i = 0;
		for(MeaEconomyAPI player : people){
			String nodeMessage = MultiFunction.addColor(getNode("messages.top").replaceAll("%RANK%", ""+i).replaceAll("%PLAYER%", player.getPlayer().getName()).replaceAll("%AMOUNT%", player.getBalanceAsString()), plugin);
			if(!nodeMessage.equalsIgnoreCase("nomsg")){
				sender.sendMessage(MultiFunction.getPre(plugin)+" "+nodeMessage);
			}
		}
	}
	
	private void log(Player player, String message){
		if(getNode("logToFile").equalsIgnoreCase("true")){
			try{
				DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
				Date date = new Date();
				String timestamp = dateFormat.format(date);
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.plugin.getDataFolder()+"/meaEconomy/logs/transactions.txt"), true));
				out.write("["+timestamp+"] "+player.getName()+": "+message+"\r\n");
				out.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void reload(){
		TAX_THREAD_GO = true;
		INTEREST_THREAD_GO = true;
		Runnable TAX = new Runnable(){
			public void run() {
				while(TAX_THREAD_GO){
					tax();
					//System.out.println("["+plugin.getDescription().getFullName()+"] Taxing players.");
					try {
						Thread.sleep(getTime(getNode("taxInterval")));
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			}
		};
		TAX_THREAD = new Thread(TAX);
		TAX_THREAD.start();
		Runnable INTEREST = new Runnable(){
			public void run() {
				while(INTEREST_THREAD_GO){
					interest();
					//System.out.println("["+plugin.getDescription().getFullName()+"] Giving interest.");
					try {
						Thread.sleep(getTime(getNode("interestInterval")));
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			}
		};
		INTEREST_THREAD = new Thread(INTEREST);
		INTEREST_THREAD.start();
	}
	
	public void kill(){
		TAX_THREAD_GO = false;
		INTEREST_THREAD_GO = false;
		try{
			TAX_THREAD.interrupt();
			INTEREST_THREAD.interrupt();
		}catch (Exception e){
			
		}
	}
	
	private void interest(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(getNode("interestOnlineOnly").equalsIgnoreCase("true")){
			Player online[] = Bukkit.getOnlinePlayers();
			for(Player player : online){
				Wallet wallet = new Wallet(plugin, player);
				wallet.interest(config.getDouble("meaEconomy.interest", 0));
				if(!getNode("messages.onInterest").equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(getNode("messages.onInterest").replaceAll("%RATE%", getNode("interest")+"%").replaceAll("%BALANCE%", wallet.getBalanceAsString()), plugin));
				}
			}
		}else{
			File players[] = new File(plugin.getDataFolder()+"/Economy/player_information/").listFiles();
			for(File file : players){
				String name = file.getName().replaceAll("\\.yml", "");
				Player player = Bukkit.getPlayer(name);
				Wallet wallet = new Wallet(plugin, player);
				wallet.interest(config.getDouble("meaEconomy.interest", 0));
			}
		}
	}
	
	private void tax(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(getNode("taxOnlineOnly").equalsIgnoreCase("true")){
			Player online[] = Bukkit.getOnlinePlayers();
			for(Player player : online){
				Wallet wallet = new Wallet(plugin, player);
				wallet.tax(config.getDouble("meaEconomy.tax", 0));
				if(!getNode("messages.onTax").equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(getNode("messages.onTax").replaceAll("%RATE%", getNode("tax")+"%").replaceAll("%BALANCE%", wallet.getBalanceAsString()), plugin));
				}
			}
		}else{
			File players[] = new File(plugin.getDataFolder()+"/Economy/player_information/").listFiles();
			for(File file : players){
				String name = file.getName().replaceAll("\\.yml", "");
				Player player = Bukkit.getPlayer(name);
				Wallet wallet = new Wallet(plugin, player);
				wallet.tax(config.getDouble("meaEconomy.tax", 0));
			}
		}
	}
	
	private long getTime(String command){
		command = command.replaceAll("y", "*31536000000+");
		command = command.replaceAll("mo", "*2628000000+");
		command = command.replaceAll("w", "*604800000+");
		command = command.replaceAll("d", "*86400000+");
		command = command.replaceAll("h", "*3600000+");
		command = command.replaceAll("m", "*60000+");
		command = command.replaceAll("s", "*1000+");
		command = command.substring(0, command.length()-1);
		double ret = 0.0;
		ret = new Expression(command).resolve();
		long timevalue = (long) ret;
		return timevalue;
	}
}
