package mea.Hook;

import java.io.File;

import mea.Economy.Wallet;
import mea.Economy.api.MeaEconomyAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.Methods;

@SuppressWarnings("deprecation")
public class EconomyHook {
	
	protected boolean MeaEconomy = false;
	
	protected String economyID = "";
	
	private JavaPlugin plugin;
	
	@SuppressWarnings("unused")
	private MeaHook hook;
	@SuppressWarnings("unused")
	private Plugin economy_plugin;
	
	private Method economy_api;
	private MeaEconomyAPI meaEconomy_api;
	
	public EconomyHook(JavaPlugin plugin, MeaHook hook){
		this.plugin = plugin;
		this.hook = hook;
	}
	
	@SuppressWarnings("static-access")
	public boolean findEconomy(){
		boolean economyFound = false;
		PluginManager plugins = Bukkit.getPluginManager();
		Configuration config = new Configuration(new File("bukkit.yml"));
		config.load();
		Methods method = new Methods();
		if(!method.setPreferred(config.getString("economy.preferred"))){
			method.setVersion(plugin.getDescription().getVersion());
			method.setMethod(plugin.getServer().getPluginManager());
		}
		if(method.getMethod() != null){
			economy_api = method.getMethod();
			System.out.println("[meaHook : Economy] Payment method found ("+ method.getMethod().getName() + " version: "+ method.getMethod().getVersion() + ")");
			economy_plugin = (Plugin) method.getMethod().getPlugin();
			economy_api = method.getMethod();
			economyFound = true;
		}else{
			System.out.println("[meaHook : Economy] meaEconomy being used! No other hook found.");
			MeaEconomy = true;
			this.economyID = "meaEconomy";
			economy_plugin = plugins.getPlugin("meaSuite");
			economyFound = true;
		}
		return economyFound;
	}
	
	@SuppressWarnings("static-access")
	public boolean force(String economyID){
		boolean economyFound = true;
		PluginManager plugins = Bukkit.getPluginManager();
		if(economyID.equalsIgnoreCase("meaEconomy")){
			MeaEconomy = true;
			this.economyID = economyID;
			economy_plugin = plugins.getPlugin("meaSuite");
			System.out.println("[meaHook : Economy] meaEconomy being used! [forced]");
			economyFound = true;
		}else{
			Configuration config = new Configuration(new File("bukkit.yml"));
			config.load();
			Methods method = new Methods();
			if(!method.setPreferred(config.getString("economy.preferred"))){
				method.setVersion(plugin.getDescription().getVersion());
				method.setMethod(plugin.getServer().getPluginManager());
			}
			if(method.getMethod() != null){
				economy_api = method.getMethod();
				System.out.println("[meaHook : Economy] Payment method found ("+ method.getMethod().getName() + " version: "+ method.getMethod().getVersion() + ")");
				economy_plugin = (Plugin) method.getMethod().getPlugin();
				economy_api = method.getMethod();
				economyFound = true;
			}else{
				System.out.println("[meaHook : Economy] No economy force found, forcing meaEconomy");
				MeaEconomy = true;
				this.economyID = economyID;
				economy_plugin = plugins.getPlugin("meaSuite");
				economyFound = true;
			}
		}
		return economyFound;
	}

	public void deposit(double amount, Player player){
		if(!MeaEconomy){
			MethodAccount account = economy_api.getAccount(player.getName());
			account.add(amount);
		}else{
			meaEconomy_api.deposit(amount, player);
		}
	}
	
	public boolean withdraw(double amount, Player player){
		if(!MeaEconomy){
			MethodAccount account = economy_api.getAccount(player.getName());
			if(account.hasEnough(amount)){
				account.subtract(amount);
				return true;
			}else{
				return false;
			}
		}else{
			if(meaEconomy_api.getBalance()>=amount){
				meaEconomy_api.withdraw(amount, player);
				return true;
			}else{
				return false;
			}
		}
	}

	@Deprecated
	public void giveMoney(double amount, Player player) {
		deposit(amount, player);
	}
	
	@Deprecated
	public boolean takeMoney(double amount, Player player){
		return withdraw(amount, player);
	}
	
	public boolean transfer(double amount, Player from, Player to){
		if(!MeaEconomy){
			MethodAccount fr_account = economy_api.getAccount(from.getName());
			MethodAccount to_account = economy_api.getAccount(to.getName());
			if(fr_account.hasEnough(amount)){
				fr_account.subtract(amount);
				to_account.add(amount);
				return true;
			}else{
				return false;
			}
		}else{
			if(meaEconomy_api.getBalance(from)>=amount){
				meaEconomy_api.transfer(amount, from, to);
				return true;
			}else{
				return false;
			}
		}
	}

	public void setBalance(double amount, Player player) {
		if(!MeaEconomy){
			MethodAccount account = economy_api.getAccount(player.getName());
			account.set(amount);
		}else{
			meaEconomy_api.setBalance(amount, player);
		}
	}

	public double getBalance(Player player) {
		if(!MeaEconomy){
			MethodAccount account = economy_api.getAccount(player.getName());
			return account.balance();
		}else{
			return meaEconomy_api.getBalance(player);
		}
	}

	public String getBalanceAsString(Player player) {
		if(!MeaEconomy){
			MethodAccount account = economy_api.getAccount(player.getName());
			return Wallet.convertBalanceToString(account.balance());
		}else{
			return meaEconomy_api.getBalanceAsString(player);
		}
	}
}
