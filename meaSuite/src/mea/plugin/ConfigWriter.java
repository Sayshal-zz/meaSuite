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

import java.io.File;

import mea.Shop.MeaShop;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class ConfigWriter {
	
	private JavaPlugin plugin;
	
	public ConfigWriter(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void reload(){
		Configuration config = plugin.getConfiguration();
		config.load();
	}
	
	public void write(){
		File d = plugin.getDataFolder();
		d.mkdirs();
		File f2 = new File(plugin.getDataFolder()+"/config.yml");
		if(!f2.exists()){
			Configuration config = plugin.getConfiguration();
			config.load();
			if(config.getString("meaSuite.author") == null){
				config.setProperty("meaSuite.author", "Travis Ralston : minecraft@turt2live.com");
				config.setProperty("meaSuite.downloadDevVerisons", "false");
				config.setProperty("meaSuite.prename", "meaSuite");
				config.setProperty("meaSuite.colorVariable", "&");
				config.setProperty("meaSuite.SQL.username", "meaCraft");
				config.setProperty("meaSuite.SQL.password", "meaCraft");
				config.setProperty("meaSuite.SQL.host", "localhost");
				config.setProperty("meaSuite.SQL.port", "3306");
				config.setProperty("meaSuite.SQL.database", "meaSuite");
				config.save();
			}
		}
		d = new File(plugin.getDataFolder()+"/meaGreylister/applications");
		d.mkdirs();
		checkForGreylister();
		d = new File(plugin.getDataFolder()+"/meaFreeze/frozen_players");
		d.mkdirs();
		checkForFreeze();
		d = new File(plugin.getDataFolder()+"/meaShop/logs");
		d.mkdirs();
		d = new File(plugin.getDataFolder()+"/meaShop/player_information");
		d.mkdirs();
		checkForShop();
		d = new File(plugin.getDataFolder()+"/meaEconomy/logs");
		d.mkdirs();
		d = new File(plugin.getDataFolder()+"/meaEconomy/player_information");
		d.mkdirs();
		checkForEconomy();
		//d = new File(plugin.getDataFolder()+"/RandomTP");
		//d.mkdirs();
		checkForRandomTP();
		d = new File(plugin.getDataFolder()+"/meaChat/player_information");
		d.mkdirs();
		checkForChat();
		d = new File(plugin.getDataFolder()+"/meaGoodies");
		d.mkdirs();
		checkForGoodies();
		d = new File(plugin.getDataFolder()+"/meaHook");
		d.mkdirs();
		checkForHook();
		d = new File(plugin.getDataFolder()+"/meaLottery");
		d.mkdirs();
		checkForLottery();
		d = new File(plugin.getDataFolder()+"/meaLogger");
		d.mkdirs();
		d = new File(plugin.getDataFolder()+"/meaLogger/temp");
		d.mkdirs();
		d = new File(plugin.getDataFolder()+"/meaLogger/old_logs");
		d.mkdirs();
		checkForLogger();
	}
	
	public void checkForGreylister(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaGreylister.author") == null){
			config.setProperty("meaGreylister.messages.onApplySent", "Your app was sent");
			config.setProperty("meaGreylister.messages.onApplyHave", "Your app was already sent");
			config.setProperty("meaGreylister.messages.onApplyExempt", "You are exempt");
			config.setProperty("meaGreylister.messages.onGuestJoin", "Welcome guest!");
			config.setProperty("meaGreylister.messages.onApplicantJoin", "Welcome applicant!");
			config.setProperty("meaGreylister.messages.onApplyError", "Nice try :)");
			config.setProperty("meaGreylister.messages.onAppError", "Nice try :)");
			config.setProperty("meaGreylister.messages.onConsoleSend", "You must be in-game!");
			config.setProperty("meaGreylister.messages.newApplication", "There is a new application from %PLAYER% (%NEWAPPS% applications)");
			//config.setProperty("meaGreylister.messages.colorVariable", "&");
			config.setProperty("meaGreylister.messages.notEnoughArgs", "You must supply enough arguments: %HELPMENU%");
			config.setProperty("meaGreylister.messages.onEnable", "meaGreylister enabled!");
			config.setProperty("meaGreylister.messages.onDisable", "meaGreylister disabled!");
			config.setProperty("meaGreylister.onAcceptCommads.command1", "perm global addgroup Default %PLAYER%");
			config.setProperty("meaGreylister.onAcceptCommads.command2", "perm global rmgroup Guest %PLAYER%");
			config.setProperty("meaGreylister.onDeclineCommads.command1", "broadcast %PLAYER% fails!");
			config.setProperty("meaGreylister.adminView.var1", "age");
			config.setProperty("meaGreylister.adminView.var2", "rules");
			config.setProperty("meaGreylister.adminView.var3", "message");
			config.setProperty("meaGreylister.adminView.label1", "age");
			config.setProperty("meaGreylister.adminView.label2", "rules");
			config.setProperty("meaGreylister.adminView.label3", "message");
			config.setProperty("meaGreylister.adminView.return1", "Age: %age%");
			config.setProperty("meaGreylister.adminView.return2", "Rules: %rules%");
			config.setProperty("meaGreylister.adminView.return3", "Message: %message%");
			config.setProperty("meaGreylister.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaGreylister.enabled", "true");
			config.save();
		}
	}
	
	public void checkForFreeze(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaFreeze.author") == null){
			//config.setProperty("meaFreeze.messages.colorVariable", "&");
			config.setProperty("meaFreeze.messages.notEnoughArgs", "You must supply enough arguments: %HELPMENU%");
			config.setProperty("meaFreeze.messages.onEnable", "meaFreeze enabled!");
			config.setProperty("meaFreeze.messages.onDisable", "meaFreeze disabled!");
			config.setProperty("meaFreeze.messages.onFreeze", "You are frozen!");
			config.setProperty("meaFreeze.messages.onUnfreeze", "You are free!");
			config.setProperty("meaFreeze.messages.onCodeFreeze", "You are frozen! Type /code %CODE% to unfreeze!");
			config.setProperty("meaFreeze.messages.onFreezeCommand", "nocmd");
			config.setProperty("meaFreeze.messages.onUnfreezeCommand", "nocmd");
			config.setProperty("meaFreeze.messages.notFrozen", "%PLAYER% is not frozen!");
			config.setProperty("meaFreeze.messages.freezeInfo.line1", "Frozen By: %WHOFROZE%");
			config.setProperty("meaFreeze.messages.freezeInfo.line2", "Frozen Until: %FROZENUNTIL%");
			config.setProperty("meaFreeze.messages.freezeInfo.line3", "Code Frozen: %CODEFROZEN%");
			config.setProperty("meaFreeze.messages.freezeInfo.line4", "Frozen On: %FROZENON%");
			config.setProperty("meaFreeze.messages.onFrozenLogin", "You are frozen until %FROZENUNTIL%");
			config.setProperty("meaFreeze.messages.onExempt", "%PLAYER% is exempt!");
			config.setProperty("meaFreeze.timestamp.format", "dd/MMM/YYYY @ hh:mm:ss z");
			config.setProperty("meaFreeze.code.characters", "abcdefghijklmnopqrstuvwxyz0123456789");
			config.setProperty("meaFreeze.code.randomUppercase", "true");
			config.setProperty("meaFreeze.code.length", "8");
			config.setProperty("meaFreeze.settings.lagReduction", "false");
			config.setProperty("meaFreeze.settings.iceDome", "true");
			config.setProperty("meaFreeze.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaFreeze.enabled", "true");
			config.save();
		}
	}
	
	public void checkForShop(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaShop.author") == null){
			config.setProperty("meaShop.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaShop.defaultBuySellStackAmount", "1");
			config.setProperty("meaShop.sellAllConfirmation", "true");
			config.setProperty("meaShop.sellInventoryConfirmation", "false");
			config.setProperty("meaShop.defaultLookupStackAmount", "1");
			//config.setProperty("meaShop.messagesFile", "/meaShop/messages.yml");
			//config.setProperty("meaShop.itemsFile", "/meaShop/items.yml");
			//config.setProperty("meaShop.logFile", "/meaShop/logs/transactions.txt");
			//config.setProperty("meaShop.messagesFile", "/meaShop/messages.yml");
			//config.setProperty("meaShop.discountFile", "/meaShop/discounts.yml");
			//config.setProperty("meaShop.pricesFile", "/meaShop/prices.csv");
			config.setProperty("meaShop.globalMessages", "true");
			config.setProperty("meaShop.giveMostAmount", "true");
			config.setProperty("meaShop.logToFile", "true");
			config.setProperty("meaShop.itemsPerPage", "10");
			config.setProperty("meaShop.discounts", "true");
			config.setProperty("meaShop.backupLogsOnLoad", "true");
			config.setProperty("meaShop.enabled", "true");
			config.save();
		}
		if(!new File(this.plugin.getDataFolder()+"/meaShop/readMeBeforeDeleting.txt").exists()){
			MeaShop shop = new MeaShop(plugin);
			shop.setup();
		}
	}
	
	public void checkForEconomy(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaEconomy.author") == null){
			config.setProperty("meaEconomy.author", "Travis Ralston : minecraft@turt2live.com");
			//config.setProperty("meaEconomy.enabled", "true");
			config.setProperty("meaEconomy.convertIconomy", "false");
			config.setProperty("meaEconomy.iconomy.host", "localhost");
			config.setProperty("meaEconomy.iconomy.port", "3306");
			config.setProperty("meaEconomy.iconomy.user", "meaSuite");
			config.setProperty("meaEconomy.iconomy.pass", "meaSuite");
			config.setProperty("meaEconomy.iconomy.database", "minecraft");
			config.setProperty("meaEconomy.iconomy.table", "iconomy");
			config.setProperty("meaEconomy.logToFile", "true");
			config.setProperty("meaEconomy.defaultBalance", "25.0");
			config.setProperty("meaEconomy.messages.noPerms", "You do not have permission!");
			//config.setProperty("meaEconomy.messages.colorVariable", "&");
			config.setProperty("meaEconomy.messages.balance", "You have %BALANCE%");
			config.setProperty("meaEconomy.messages.otherBalance", "%PLAYER% has %BALANCE%");
			config.setProperty("meaEconomy.messages.onSend", "You gave %AMOUNT% to %PLAYER% making your balance %BALANCE%");
			config.setProperty("meaEconomy.messages.onGive", "You gave %AMOUNT% to %PLAYER%");
			config.setProperty("meaEconomy.messages.onSet", "You set %PLAYER% to have the balance %AMOUNT%");
			config.setProperty("meaEconomy.messages.top", "%RANK%: %PLAYER% has %AMOUNT%");
			config.setProperty("meaEconomy.messages.onGetSet", "%SENDER% set your balance to %BALANCE%");
			config.setProperty("meaEconomy.messages.onGetPay", "You got %AMOUNT% from %SENDER% making your balance %BALANCE%");
			config.setProperty("meaEconomy.messages.onGetGive", "You got %AMOUNT% from %SENDER% making your balance %BALANCE%");
			config.setProperty("meaEconomy.messages.onTax", "You were taxed at %RATE% making your new balance %BALANCE%");
			config.setProperty("meaEconomy.messages.onInterest", "You were given interest at %RATE% making your new balance %BALANCE%");
			config.setProperty("meaEconomy.messages.onLoginBalance", "Hello %PLAYER%! Your balance is %BALANCE%");
			config.setProperty("meaEconomy.interest", 2.0);
			config.setProperty("meaEconomy.tax", 5.0);
			config.setProperty("meaEconomy.doInterest", "true");
			config.setProperty("meaEconomy.doTax", "true");
			config.setProperty("meaEconomy.interestInterval", "1h");
			config.setProperty("meaEconomy.taxInterval", "1h");
			config.setProperty("meaEconomy.taxOnlineOnly", "true");
			config.setProperty("meaEconomy.interestOnlineOnly", "true");
			config.setProperty("meaEconomy.showBalanceOnLogin", "true");
			config.setProperty("meaEconomy.showTop", "10");
			config.setProperty("meaEconomy.dickMode", "true");
			config.save();
		}
	}
	
	public void checkForRandomTP(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaRandomTP.author") == null){
			config.setProperty("meaRandomTP.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaRandomTP.enabled", "true");
			config.setProperty("meaRandomTP.onDisabledError", "We disabled teleports for now, sorry!");
			//config.setProperty("meaRandomTP.colorVariable", "&");
			config.setProperty("meaRandomTP.onTP", "Woosh!");
			config.setProperty("meaRandomTP.onEnable", "RandomTP enabled");
			config.setProperty("meaRandomTP.onDisable", "RandomTP disabled");
			config.setProperty("meaRandomTP.noPerms", "You can't do that!");
			config.setProperty("meaRandomTP.minX", "250");
			config.setProperty("meaRandomTP.minY", "250");
			config.setProperty("meaRandomTP.maxX", "500");
			config.setProperty("meaRandomTP.maxY", "500");
			config.save();
		}
	}
	
	public void checkForChat(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaChat.author") == null){
			config.setProperty("meaChat.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaChat.irc.server", "irc.esper.net");
			config.setProperty("meaChat.irc.channel", "turt2live");
			config.setProperty("meaChat.irc.MinecraftToIRC", false);
			config.setProperty("meaChat.irc.IRCToMinecraft", false);
			config.save();
		}
	}
	
	public void checkForGoodies(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaGoodies.author") == null){
			config.setProperty("meaGoodies.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaGoodies.noCaps", "true");
			config.setProperty("meaGoodies.cancelSuggestionMessage", "true");
			config.setProperty("meaGoodies.messages.onSuggestion", "Thank you for your suggestion.");
			config.setProperty("meaGoodies.messages.onNoSuggestion", "Dumbass.");
			config.setProperty("meaGoodies.timeFormat", "EEE, d MMM yyyy HH:mm:ss Z");
			config.setProperty("meaGoodies.suggestionFormat", "[%TIMESTAMP%] %PLAYER% suggested %SUGGESTION%");
			config.setProperty("meaGoodies.wrapSuggestionInQuotes", "true");
			config.setProperty("meaGoodies.allCapsSuffix", "[Pony Rider]");
			config.save();
		}
	}
	
	public void checkForHook(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaHook.author") == null){
			config.setProperty("meaHook.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaHook.forceMeaEconomy", "false");
			config.save();
		}
	}
	
	public void checkForLottery(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaLottery.author") == null){
			config.setProperty("meaLottery.author", "Travis Ralston : minecraft@turt2live.com");
			config.setProperty("meaLottery.enabled", "true");
			config.setProperty("meaLottery.costOfTicket", 5.0);
			config.setProperty("meaLottery.maxTicketsPerAccount", -1);
			config.setProperty("meaLottery.onlineToWinMode", true);
			config.save();
		}
	}
	
	public void checkForLogger(){
		Configuration config = plugin.getConfiguration();
		config.load();
		if(config.getString("meaLogger.author") == null){
			config.setProperty("meaLogger.author", "Travis Ralston : minecraft@turt2live.com");
			config.save();
		}
	}
	
}
