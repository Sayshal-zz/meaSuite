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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class Wallet {
	
	private JavaPlugin plugin;
	private Player player;
	
	public Wallet(JavaPlugin plugin, Player player){
		this.player = player;
		this.plugin = plugin;
	}

	public void deposit(double amount) {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		double balance = Double.parseDouble(config.getString("balance"));
		balance = balance + amount;
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, balance, "DEPOSIT");
	}

	public void withdraw(double amount) {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		double balance = Double.parseDouble(config.getString("balance"));
		balance = balance - amount;
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, balance, "WITHDRAW");
	}

	public double getBalance() {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		double balance = Double.parseDouble(config.getString("balance"));
		return balance;
	}

	public void giveAmount(double amount) {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		double balance = Double.parseDouble(config.getString("balance"));
		balance = balance + amount;
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, balance, "GIVE AMOUNT");
	}

	public void tax(double percent) {
		double balance = getBalance();
		double value = 0.0;
		if(percent > 0){
			double abs = Math.abs(percent);
			value = getBalance()*((abs>1)?abs/100:abs);
			balance = balance - value;
		}else{
			logTransaction(player, getBalance(), "ERROR: NO BALANCE CHANGE: TAX % = 0");
		}
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, getBalance(), "TAXED AT "+percent+"% ("+getBalanceAsString(value)+")");
	}
	
	public void interest(double percent){
		//System.out.println(percent);
		double balance = getBalance();
		double value = 0.0;
		if(percent > 0){
			double abs = Math.abs(percent);
			value = getBalance()*((abs>1)?abs/100:abs);
			balance = balance + value;
		}else{
			logTransaction(player, getBalance(), "ERROR: NO BALANCE CHANGE: INTEREST % = 0");
		}
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, getBalance(), "INTEREST "+percent+"% ("+getBalanceAsString(value)+")");
	}

	public void takeMoney(double amount) {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		double balance = Double.parseDouble(config.getString("balance"));
		balance = balance - amount;
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, balance, "TAKE AMOUNT");
		
	}

	public void setBalance(double balance) {
		Configuration config = new Configuration(new File(this.plugin.getDataFolder()+"/meaEconomy/player_information/"+player.getName()+".yml"));
		config.load();
		config.setProperty("balance", balance+"");
		config.save();
		logTransaction(player, getBalance(), "BALANCE SET");
	}
	
	public void logTransaction(Player player, double newBalance, String type){
		if(plugin.getConfig().getString("meaEconomy.logToFile").equalsIgnoreCase("true")){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.plugin.getDataFolder()+"/meaEconomy/logs/transactions.txt"), true));
				out.write("["+getTimestamp()+"] Player "+player.getName()+" now has "+getBalanceAsString(newBalance)+" because of "+type+"\r\n");
				out.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String getTimestamp(){
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	    Date date = new Date();
	    return dateFormat.format(date);
	}
	
	public String getBalanceAsString(double balance){
		String newbalance = ""+balance;
		String parts[] = newbalance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			newbalance = newbalance + "0";
		}
		newbalance = roundDecimal(newbalance);
		return "\\$"+newbalance;
	}
	
	public String getBalanceAsString(){
		double balance = getBalance();
		String newbalance = ""+balance;
		String parts[] = newbalance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			newbalance = newbalance + "0";
		}
		newbalance = roundDecimal(newbalance);
		return "\\$"+newbalance;
	}
	
	public static String convertBalanceToString(double balance){
		String newbalance = ""+balance;
		String parts[] = newbalance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			newbalance = newbalance + "0";
		}
		newbalance = roundDecimal(newbalance);
		return "\\$"+newbalance;
	}
	
	public static String convertBalanceToString(String balance){
		String newbalance = balance;
		String parts[] = newbalance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			newbalance = newbalance + "0";
		}
		newbalance = roundDecimal(newbalance);
		return "\\$"+newbalance;
	}
	
	public static String roundDecimal(String input){
		String output = "";
		String parts[] = input.split("\\.");
		if(parts.length>1){
			if(parts[1].length()>2){
				parts[1] = parts[1].substring(0,2);
			}
			output = parts[0]+"."+parts[1];
		}else{
			output = input;
		}
		return output;
	}
}
