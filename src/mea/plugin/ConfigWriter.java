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
import java.io.IOException;

import mea.Logger.MeaLogger;
import mea.Shop.MeaShop;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigWriter {
	
	private JavaPlugin plugin;
	
	public ConfigWriter(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void reload(){
		plugin.reloadConfig();
	}
	
	public void write(){
		try{
			File d = plugin.getDataFolder();
			d.mkdirs();
			File f2 = new File(plugin.getDataFolder()+"/plugin.getConfig().yml");
			if(!f2.exists()){
				f2.createNewFile();
				if(plugin.getConfig().getString("meaSuite.author") == null){
					plugin.getConfig().set("meaSuite.author", "Travis Ralston : minecraft@turt2live.com");
					plugin.getConfig().set("meaSuite.downloadDevVersions", "false");
					plugin.getConfig().set("meaSuite.prename", "meaSuite");
					plugin.getConfig().set("meaSuite.colorVariable", "&");
					plugin.getConfig().set("meaSuite.SQL.username", "meaCraft");
					plugin.getConfig().set("meaSuite.SQL.password", "meaCraft");
					plugin.getConfig().set("meaSuite.SQL.host", "localhost");
					plugin.getConfig().set("meaSuite.SQL.port", "3306");
					plugin.getConfig().set("meaSuite.SQL.database", "meaSuite");
					plugin.saveConfig();
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
		}catch (Exception e){
			e.printStackTrace();
			MeaLogger.log(e.getMessage(), new File(plugin.getDataFolder()+"/meaLogger/log.txt"));
		}
		reload();
	}
	
	public void checkForGreylister() throws IOException{
		if(plugin.getConfig().getString("meaGreylister.author") == null){
			plugin.getConfig().set("meaGreylister.messages.onApplySent", "Your app was sent");
			plugin.getConfig().set("meaGreylister.messages.onApplyHave", "Your app was already sent");
			plugin.getConfig().set("meaGreylister.messages.onApplyExempt", "You are exempt");
			plugin.getConfig().set("meaGreylister.messages.onGuestJoin", "Welcome guest!");
			plugin.getConfig().set("meaGreylister.messages.onApplicantJoin", "Welcome applicant!");
			plugin.getConfig().set("meaGreylister.messages.onApplyError", "Nice try :)");
			plugin.getConfig().set("meaGreylister.messages.onAppError", "Nice try :)");
			plugin.getConfig().set("meaGreylister.messages.onConsoleSend", "You must be in-game!");
			plugin.getConfig().set("meaGreylister.messages.newApplication", "There is a new application from %PLAYER% (%NEWAPPS% applications)");
			//plugin.getConfig().set("meaGreylister.messages.colorVariable", "&");
			plugin.getConfig().set("meaGreylister.messages.notEnoughArgs", "You must supply enough arguments: %HELPMENU%");
			plugin.getConfig().set("meaGreylister.messages.onEnable", "meaGreylister enabled!");
			plugin.getConfig().set("meaGreylister.messages.onDisable", "meaGreylister disabled!");
			plugin.getConfig().set("meaGreylister.onAcceptCommads.command1", "perm global addgroup Default %PLAYER%");
			plugin.getConfig().set("meaGreylister.onAcceptCommads.command2", "perm global rmgroup Guest %PLAYER%");
			plugin.getConfig().set("meaGreylister.onDeclineCommads.command1", "broadcast %PLAYER% fails!");
			plugin.getConfig().set("meaGreylister.adminView.var1", "age");
			plugin.getConfig().set("meaGreylister.adminView.var2", "rules");
			plugin.getConfig().set("meaGreylister.adminView.var3", "message");
			plugin.getConfig().set("meaGreylister.adminView.label1", "age");
			plugin.getConfig().set("meaGreylister.adminView.label2", "rules");
			plugin.getConfig().set("meaGreylister.adminView.label3", "message");
			plugin.getConfig().set("meaGreylister.adminView.return1", "Age: %age%");
			plugin.getConfig().set("meaGreylister.adminView.return2", "Rules: %rules%");
			plugin.getConfig().set("meaGreylister.adminView.return3", "Message: %message%");
			plugin.getConfig().set("meaGreylister.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaGreylister.enabled", "true");
			plugin.saveConfig();
		}
	}
	
	public void checkForFreeze() throws IOException{
		if(plugin.getConfig().getString("meaFreeze.author") == null){
			//plugin.getConfig().set("meaFreeze.messages.colorVariable", "&");
			plugin.getConfig().set("meaFreeze.messages.notEnoughArgs", "You must supply enough arguments: %HELPMENU%");
			plugin.getConfig().set("meaFreeze.messages.onEnable", "meaFreeze enabled!");
			plugin.getConfig().set("meaFreeze.messages.onDisable", "meaFreeze disabled!");
			plugin.getConfig().set("meaFreeze.messages.onFreeze", "You are frozen!");
			plugin.getConfig().set("meaFreeze.messages.onUnfreeze", "You are free!");
			plugin.getConfig().set("meaFreeze.messages.onCodeFreeze", "You are frozen! Type /code %CODE% to unfreeze!");
			plugin.getConfig().set("meaFreeze.messages.onFreezeCommand", "nocmd");
			plugin.getConfig().set("meaFreeze.messages.onUnfreezeCommand", "nocmd");
			plugin.getConfig().set("meaFreeze.messages.notFrozen", "%PLAYER% is not frozen!");
			plugin.getConfig().set("meaFreeze.messages.freezeInfo.line1", "Frozen By: %WHOFROZE%");
			plugin.getConfig().set("meaFreeze.messages.freezeInfo.line2", "Frozen Until: %FROZENUNTIL%");
			plugin.getConfig().set("meaFreeze.messages.freezeInfo.line3", "Code Frozen: %CODEFROZEN%");
			plugin.getConfig().set("meaFreeze.messages.freezeInfo.line4", "Frozen On: %FROZENON%");
			plugin.getConfig().set("meaFreeze.messages.onFrozenLogin", "You are frozen until %FROZENUNTIL%");
			plugin.getConfig().set("meaFreeze.messages.onExempt", "%PLAYER% is exempt!");
			plugin.getConfig().set("meaFreeze.timestamp.format", "dd/MMM/YYYY @ hh:mm:ss z");
			plugin.getConfig().set("meaFreeze.code.characters", "abcdefghijklmnopqrstuvwxyz0123456789");
			plugin.getConfig().set("meaFreeze.code.randomUppercase", "true");
			plugin.getConfig().set("meaFreeze.code.length", "8");
			plugin.getConfig().set("meaFreeze.settings.lagReduction", "false");
			plugin.getConfig().set("meaFreeze.settings.iceDome", "true");
			plugin.getConfig().set("meaFreeze.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaFreeze.enabled", "true");
			plugin.saveConfig();
		}
	}
	
	public void checkForShop() throws IOException{
		if(plugin.getConfig().getString("meaShop.author") == null){
			plugin.getConfig().set("meaShop.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaShop.defaultBuySellStackAmount", "1");
			plugin.getConfig().set("meaShop.sellAllConfirmation", "true");
			plugin.getConfig().set("meaShop.sellInventoryConfirmation", "false");
			plugin.getConfig().set("meaShop.defaultLookupStackAmount", "1");
			//plugin.getConfig().set("meaShop.messagesFile", "/meaShop/messages.yml");
			//plugin.getConfig().set("meaShop.itemsFile", "/meaShop/items.yml");
			//plugin.getConfig().set("meaShop.logFile", "/meaShop/logs/transactions.txt");
			//plugin.getConfig().set("meaShop.messagesFile", "/meaShop/messages.yml");
			//plugin.getConfig().set("meaShop.discountFile", "/meaShop/discounts.yml");
			//plugin.getConfig().set("meaShop.pricesFile", "/meaShop/prices.csv");
			plugin.getConfig().set("meaShop.globalMessages", "true");
			plugin.getConfig().set("meaShop.giveMostAmount", "true");
			plugin.getConfig().set("meaShop.logToFile", "true");
			plugin.getConfig().set("meaShop.itemsPerPage", "10");
			plugin.getConfig().set("meaShop.discounts", "true");
			plugin.getConfig().set("meaShop.backupLogsOnLoad", "true");
			plugin.getConfig().set("meaShop.enabled", "true");
			plugin.saveConfig();
		}
		if(!new File(this.plugin.getDataFolder()+"/meaShop/readMeBeforeDeleting.txt").exists()){
			MeaShop shop = new MeaShop(plugin);
			shop.setup();
		}
	}
	
	public void checkForEconomy() throws IOException{
		if(plugin.getConfig().getString("meaEconomy.author") == null){
			plugin.getConfig().set("meaEconomy.author", "Travis Ralston : minecraft@turt2live.com");
			//plugin.getConfig().set("meaEconomy.enabled", "true");
			plugin.getConfig().set("meaEconomy.convertIconomy", "false");
			plugin.getConfig().set("meaEconomy.iconomy.host", "localhost");
			plugin.getConfig().set("meaEconomy.iconomy.port", "3306");
			plugin.getConfig().set("meaEconomy.iconomy.user", "meaSuite");
			plugin.getConfig().set("meaEconomy.iconomy.pass", "meaSuite");
			plugin.getConfig().set("meaEconomy.iconomy.database", "minecraft");
			plugin.getConfig().set("meaEconomy.iconomy.table", "iconomy");
			plugin.getConfig().set("meaEconomy.logToFile", "true");
			plugin.getConfig().set("meaEconomy.defaultBalance", "25.0");
			plugin.getConfig().set("meaEconomy.messages.noPerms", "You do not have permission!");
			//plugin.getConfig().set("meaEconomy.messages.colorVariable", "&");
			plugin.getConfig().set("meaEconomy.messages.balance", "You have %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.otherBalance", "%PLAYER% has %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onSend", "You gave %AMOUNT% to %PLAYER% making your balance %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onGive", "You gave %AMOUNT% to %PLAYER%");
			plugin.getConfig().set("meaEconomy.messages.onSet", "You set %PLAYER% to have the balance %AMOUNT%");
			plugin.getConfig().set("meaEconomy.messages.top", "%RANK%: %PLAYER% has %AMOUNT%");
			plugin.getConfig().set("meaEconomy.messages.onGetSet", "%SENDER% set your balance to %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onGetPay", "You got %AMOUNT% from %SENDER% making your balance %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onGetGive", "You got %AMOUNT% from %SENDER% making your balance %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onTax", "You were taxed at %RATE% making your new balance %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onInterest", "You were given interest at %RATE% making your new balance %BALANCE%");
			plugin.getConfig().set("meaEconomy.messages.onLoginBalance", "Hello %PLAYER%! Your balance is %BALANCE%");
			plugin.getConfig().set("meaEconomy.interest", 2.0);
			plugin.getConfig().set("meaEconomy.tax", 5.0);
			plugin.getConfig().set("meaEconomy.doInterest", "true");
			plugin.getConfig().set("meaEconomy.doTax", "true");
			plugin.getConfig().set("meaEconomy.interestInterval", "1h");
			plugin.getConfig().set("meaEconomy.taxInterval", "1h");
			plugin.getConfig().set("meaEconomy.taxOnlineOnly", "true");
			plugin.getConfig().set("meaEconomy.interestOnlineOnly", "true");
			plugin.getConfig().set("meaEconomy.showBalanceOnLogin", "true");
			plugin.getConfig().set("meaEconomy.showTop", "10");
			plugin.getConfig().set("meaEconomy.dickMode", "true");
			plugin.saveConfig();
		}
	}
	
	public void checkForRandomTP() throws IOException{
		if(plugin.getConfig().getString("meaRandomTP.author") == null){
			plugin.getConfig().set("meaRandomTP.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaRandomTP.enabled", "true");
			plugin.getConfig().set("meaRandomTP.onDisabledError", "We disabled teleports for now, sorry!");
			//plugin.getConfig().set("meaRandomTP.colorVariable", "&");
			plugin.getConfig().set("meaRandomTP.onTP", "Woosh!");
			plugin.getConfig().set("meaRandomTP.onEnable", "RandomTP enabled");
			plugin.getConfig().set("meaRandomTP.onDisable", "RandomTP disabled");
			plugin.getConfig().set("meaRandomTP.noPerms", "You can't do that!");
			plugin.getConfig().set("meaRandomTP.minX", "250");
			plugin.getConfig().set("meaRandomTP.minY", "250");
			plugin.getConfig().set("meaRandomTP.maxX", "500");
			plugin.getConfig().set("meaRandomTP.maxY", "500");
			plugin.saveConfig();
		}
	}
	
	public void checkForChat() throws IOException{
		if(plugin.getConfig().getString("meaChat.author") == null){
			plugin.getConfig().set("meaChat.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaChat.irc.server", "irc.esper.net");
			plugin.getConfig().set("meaChat.irc.channel", "turt2live");
			plugin.getConfig().set("meaChat.irc.MinecraftToIRC", false);
			plugin.getConfig().set("meaChat.irc.IRCToMinecraft", false);
			plugin.saveConfig();
		}
	}
	
	public void checkForGoodies() throws IOException{
		if(plugin.getConfig().getString("meaGoodies.author") == null){
			plugin.getConfig().set("meaGoodies.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaGoodies.noCaps", "true");
			plugin.getConfig().set("meaGoodies.cancelSuggestionMessage", "true");
			plugin.getConfig().set("meaGoodies.messages.onSuggestion", "Thank you for your suggestion.");
			plugin.getConfig().set("meaGoodies.messages.onNoSuggestion", "Dumbass.");
			plugin.getConfig().set("meaGoodies.timeFormat", "EEE, d MMM yyyy HH:mm:ss Z");
			plugin.getConfig().set("meaGoodies.suggestionFormat", "[%TIMESTAMP%] %PLAYER% suggested %SUGGESTION%");
			plugin.getConfig().set("meaGoodies.wrapSuggestionInQuotes", "true");
			plugin.getConfig().set("meaGoodies.allCapsSuffix", "[Pony Rider]");
			plugin.saveConfig();
		}
	}
	
	@SuppressWarnings("unused")
	public void checkForHook() throws IOException{
		String defaults[][] = {
				{"Guest", "&8"},
				{"Member", "&e"},
				{"Developer", "&a"},
				{"Mod", "&1"},
				{"Admin", "&c"},
				{"Co-Owner", "&6"},
				{"Owner", "&5"}
		};
		if(plugin.getConfig().getString("meaHook.author") == null){
			//plugin.getConfig().set("meaHook.author", "Travis Ralston : minecraft@turt2live.com"); //TODO: TEMP
			plugin.getConfig().set("meaHook.forceMeaEconomy", "false");
			plugin.getConfig().set("meaHook.enableAdmins", "true");
			//plugin.getConfig().set("meaHook.allowClientSideFormatting", "true"); //Force: true
			plugin.getConfig().set("meaHook.formats.irc", "[&9^T&f] ^R <&9^P&f>: &e^M");
			plugin.getConfig().set("meaHook.formats.minecraft", "[&5^T&f] ^R <&5^P&f>: &e^M");
			plugin.getConfig().set("meaHook.formats.meaChat", "[&a^T&f] ^R <&a^P&f>: &e^M");
			plugin.getConfig().set("meaHook.formats.rank", "&f[^RC^R&f]");
			plugin.getConfig().set("meaHook.formats.showRanks", "true"); //If off, remove "double spaces" in frmt
			String main = "meaHook.colors.ranks";
			
			plugin.getConfig().set("meaHook.enableAdmins", "true");
			plugin.saveConfig();
		}
	}
	
	public void checkForLottery() throws IOException{
		if(plugin.getConfig().getString("meaLottery.author") == null){
			plugin.getConfig().set("meaLottery.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.getConfig().set("meaLottery.enabled", "true");
			plugin.getConfig().set("meaLottery.costOfTicket", 5.0);
			plugin.getConfig().set("meaLottery.maxTicketsPerAccount", -1);
			plugin.getConfig().set("meaLottery.onlineToWinMode", true);
			plugin.saveConfig();
		}
	}
	
	public void checkForLogger() throws IOException{
		if(plugin.getConfig().getString("meaLogger.author") == null){
			plugin.getConfig().set("meaLogger.author", "Travis Ralston : minecraft@turt2live.com");
			plugin.saveConfig();
		}
	}
	
}
