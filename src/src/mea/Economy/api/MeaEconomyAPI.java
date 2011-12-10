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
package mea.Economy.api;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import mea.Economy.Wallet;
import mea.SQL.MeaSQL;
import mea.plugin.MultiFunction;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@SuppressWarnings("deprecation")
public class MeaEconomyAPI {
	
	private JavaPlugin plugin;
	private Player player;
	
	public MeaEconomyAPI(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public MeaEconomyAPI(JavaPlugin plugin, Player player){
		this.player = player;
		this.plugin = plugin;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}
	
	public Wallet getWallet(Player player){
		return new Wallet(plugin, player);
	}
	
	public Wallet getWallet(){
		return new Wallet(plugin, player);
	}
	
	public void deposit(double amount, Player player){
		getWallet(player).deposit(amount);
	}
	
	public void deposit(double amount){
		getWallet().deposit(amount);
	}
	
	public void withdraw(double amount, Player player){
		getWallet(player).withdraw(amount);
	}
	
	public void withdraw(double amount){
		getWallet().withdraw(amount);
	}
	
	public void transfer(double amount, Player from, Player to){
		getWallet(from).withdraw(amount);
		getWallet(to).deposit(amount);
	}
	
	public void transfer(double amount, Player to){
		getWallet().withdraw(amount);
		getWallet(to).deposit(amount);
	}
	
	public double getBalance(Player player){
		return getWallet(player).getBalance();
	}
	
	public double getBalance(){
		if(player == null){
			return 0.0;
		}
		return getWallet().getBalance();
	}
	
	public void tax(double percent, Player player){
		getWallet(player).tax(percent);
	}
	
	public void tax(double percent){
		getWallet().tax(percent);
	}
	
	public void giveMoney(double amount, Player player){
		getWallet(player).giveAmount(amount);
	}
	
	public void giveMoney(double amount){
		getWallet().giveAmount(amount);
	}
	
	public void takeMoney(double amount, Player player){
		getWallet(player).takeMoney(amount);
	}
	
	public void takeMoney(double amount){
		getWallet().takeMoney(amount);
	}
	
	public void setBalance(double balance, Player player){
		getWallet(player).setBalance(balance);
	}
	
	public void setBalance(double balance){
		getWallet().setBalance(balance);
	}
	
	public String getBalanceAsString(){
		return getWallet().getBalanceAsString(getWallet().getBalance());
	}
	
	public String getBalanceAsString(Player player){
		return getWallet(player).getBalanceAsString(getWallet(player).getBalance());
	}
	
	public void onLogin(){
		Configuration config = plugin.getConfiguration();
		config.load();
		String message = config.getString("meaEconomy.messages.onLoginBalance");
		if(!message.equalsIgnoreCase("nomsg")){
			message = MultiFunction.addColor(message.replaceAll("%PLAYER%", player.getName()).replaceAll("%BALANCE%", getBalanceAsString()), plugin);
			player.sendMessage(message);
		}
	}
	
	public void onLogin(Player player){
		setPlayer(player);
		onLogin();
	}
	
	public void convertIconomy(String sql_host, String sql_port, String sql_username, String sql_password, String sql_database, String sql_table){
		MeaSQL sql = new MeaSQL(plugin);
		Connection connection = null;
		Properties properties = new Properties();
		properties.put("user", sql_username);
		properties.put("password", sql_username);
		Statement statement = null;
		try {
			connection = sql.getConnection(sql_host, sql_port, sql_username, sql_password, sql_database);
			statement = sql.getStatement(connection);
			String query = "select * from "+sql_database+"."+sql_table;
			ResultSet results = sql.query(statement, query, connection);
			while(results.next()){
				String player_username = results.getString("username");
				double player_balance = results.getDouble("balance");
				Configuration config = new Configuration(new File(plugin.getDataFolder()+"/meaEconomy/player_information/"+player_username+".yml"));
				config.setProperty("balance", player_balance);
				config.save();
			}
		} catch (SQLException e) {
			e.printStackTrace();
	    } finally {
	    	if (statement != null) { 
    			try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public void iconomyCheck(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaEconomy.convertIconomy").equalsIgnoreCase("true")){
			System.out.println("Converting iConomy!");
			String host = config.getString("meaEconomy.iconomy.host");
			String port = config.getString("meaEconomy.iconomy.port");
			String user = config.getString("meaEconomy.iconomy.user");
			String pass = config.getString("meaEconomy.iconomy.pass");
			String db = config.getString("meaEconomy.iconomy.database");
			String table = config.getString("meaEconomy.iconomy.table");
			convertIconomy(host, port, user, pass, db, table);
			config.setProperty("meaEconomy.convertIconomy", "false");
			config.save();
		}
	}
}
