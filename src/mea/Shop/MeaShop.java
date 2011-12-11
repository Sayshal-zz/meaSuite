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
package mea.Shop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import mea.Economy.Wallet;
import mea.External.Download;
import mea.External.Unzip;
import mea.SQL.MeaSQL;
import mea.plugin.Loader;
import mea.plugin.MultiFunction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaShop {
	
	private JavaPlugin plugin;
	private HashMap<Player, String> confirms = new HashMap<Player, String>();
	
	public MeaShop(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public boolean isCommand(String cmd, String[] args, Player sender){
		boolean command = false;
		if(isEnabled()){
			if((cmd.equalsIgnoreCase("buy") || cmd.equalsIgnoreCase("sbuy")) && args.length >= 1 && hasPermission("User.Buy", sender)){
				command = true;
				buy(args, sender, 0);
			}else if((cmd.equalsIgnoreCase("sell") || cmd.equalsIgnoreCase("ssell")) && args.length >= 1 && hasPermission("User.Sell", sender)){
				command = true;
				sell(args, sender, 0);
			}else if(cmd.equalsIgnoreCase("sellall") && args.length >= 1 && hasPermission("User.Sellall", sender)){
				command = true;
				sell_all(args, sender, 0, false);
			}else if(cmd.equalsIgnoreCase("sellinv") && hasPermission("User.Sellinv", sender)){
				command = true;
				sell_inventory(sender, false);
			}else if((cmd.equalsIgnoreCase("slookup") || cmd.equalsIgnoreCase("scheck")) && args.length >= 2 && hasPermission("User.Check", sender)){
				command = true;
				find_item(args[0], sender);
			}else if(cmd.equalsIgnoreCase("shop") && hasPermission("User.Shop", sender)){
				command = true;
				if(args.length > 0){
					if(args[0].equalsIgnoreCase("buy") && args.length >= 2 && hasPermission("User.Buy", sender)){
						buy(args, sender, 1);
					}else if(args[0].equalsIgnoreCase("sell") && args.length >= 2 && hasPermission("User.Sell", sender)){
						sell(args, sender, 1);
					}else if(args[0].equalsIgnoreCase("sellall") && args.length >= 2 && hasPermission("User.Sellall", sender)){
						sell_all(args, sender, 1, false);
					}else if(args[0].equalsIgnoreCase("sellinv") && hasPermission("User.Sellinv", sender)){
						sell_inventory(sender, false);
					}else if(args[0].equalsIgnoreCase("lookup") && args.length == 2 && hasPermission("User.Check", sender)){
						find_item(args[1], sender);
					}else if(args[0].equalsIgnoreCase("check") && args.length == 2 && hasPermission("User.Check", sender)){
						find_item(args[1], sender);
					}else if(args[0].equalsIgnoreCase("page") && hasPermission("User.Page", sender) && args.length == 2){
						help(sender, args[1]);
					}else if(args[0].equalsIgnoreCase("help") && hasPermission("User.Help", sender)){
						help(sender, 1);
					}else if(args[0].equalsIgnoreCase("admin") && args.length >= 1){
						if(args[1].equalsIgnoreCase("reload") && hasPermission("Admin.Reload", sender)){
							reload();
						}else if(args[1].equalsIgnoreCase("add") && hasPermission("Admin.Edit", sender)){
							add_item(args, sender);
						}else if(args[1].equalsIgnoreCase("hide") && hasPermission("Admin.Edit", sender)){
							remove_item(args, sender, 2);
						}else if(args[1].equalsIgnoreCase("edit") && hasPermission("Admin.Edit", sender)){
							edit_item(args, sender);
						}else{
							help(sender, 1);
						}
					}else if(args[0].equalsIgnoreCase("hide") && args.length >= 1 && hasPermission("Admin.Hide", sender)){
						remove_item(args, sender, 1);
					}else if(args[0].equalsIgnoreCase("reload") && hasPermission("Admin.Reload", sender)){
						reload();
					}else{
						help(sender, 1);
					}
				}else{
					help(sender, 1);
				}
			}else if(cmd.equalsIgnoreCase("shopyes")){
				String action = confirms.get(sender);
				if(action.equalsIgnoreCase("inv")){
					sell_inventory(sender, true);
				}else if(action.startsWith("all")){
					String parts[] = action.split(" ");
					String newParts = "";
					for(int i=1;i<parts.length;i++){
						newParts = newParts.concat(parts[i]+" ");
					}
					parts = newParts.split(" ");
					confirms.remove(sender);
					sell_all(parts, sender, 0, true);
				}
			}else if(cmd.equalsIgnoreCase("shopno")){
				confirms.remove(sender);
				String message = getMessage("onDeny");
				if(!message.equalsIgnoreCase("nomsg")){
					sender.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
				}
			}
		}
		return command;
	}
	
	public void buy(String args[], Player player, int startIndex){
		Wallet wallet = new Wallet(plugin, player);
		String itemName = args[startIndex];
		if(itemCanBeBought(args[startIndex])){
			int amount = 0;
			if(args[startIndex+2] != null){
				amount = Integer.parseInt(getNode("defaultBuySellStackAmount"));
			}
			String itemParts[] = itemName.split("\\:");
			int ID = Integer.parseInt(getItemID(itemParts[0]));
			ItemStack item = new ItemStack(Material.getMaterial(ID));
			if(itemParts.length>1){
				item.setDurability(getDamageValue(itemName));
			}
			if(amount <= 0){
				amount = Integer.parseInt(getNode("defaultBuySellStackAmount"));
			}
			item.setAmount(amount);
			if(player.getInventory().firstEmpty() <= -1){
				String message = getMessage("fullInventory");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message, plugin));
				}
			}else{
				double value = getBuyValue(itemName)*amount;
				value = applyDiscount(player, value, -1);
				if(wallet.getBalance()>=value){
					player.getInventory().setItem(player.getInventory().firstEmpty(), item);
					if(getNode("globalMessages").equalsIgnoreCase("true")){
						String message = getMessage("globalMessageBuy");
						if(!message.equalsIgnoreCase("nomsg")){
							Bukkit.getServer().broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%PLAYERNAME%", player.getName()).replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)+""), plugin));
						}
					}
					wallet.withdraw(value);
					String message = getMessage("onPurchase");
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)), plugin));
					}
					log(player.getName()+" PURCHASE "+getItemName(itemName)+"x"+amount+" FOR "+Wallet.convertBalanceToString(value));
				}else{
					if(getNode("giveMostAmount").equalsIgnoreCase("true")){
						boolean cannotAfford = true;
						int i = 1;
						while(cannotAfford){
							value = getBuyValue(itemName)*(amount-i);
							value = applyDiscount(player, value, -1);
							if(value<=wallet.getBalance()){
								cannotAfford = false;
							}
							i++;
						}
						player.getInventory().setItem(player.getInventory().firstEmpty(), item);
						if(getNode("globalMessages").equalsIgnoreCase("true")){
							String message = getMessage("globalMessageBuy");
							if(!message.equalsIgnoreCase("nomsg")){
								Bukkit.getServer().broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%PLAYERNAME%", player.getName()).replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)+""), plugin));
							}
						}
						wallet.withdraw(value);
						String message = getMessage("onPurchase");
						if(!message.equalsIgnoreCase("nomsg")){
							player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)), plugin));
						}
						message = getMessage("notEnoughMoney");
						if(!message.equalsIgnoreCase("nomsg")){
							String missing = Wallet.convertBalanceToString(value - wallet.getBalance());
							player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message.replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", this.getItemName(itemName)).replaceAll("%MISSING%", missing), plugin));
						}	
						log(player.getName()+" PURCHASE "+getItemName(itemName)+"x"+amount+" FOR "+Wallet.convertBalanceToString(value));
					}else{
						String message = getMessage("notEnoughMoney");
						if(!message.equalsIgnoreCase("nomsg")){
							String missing = Wallet.convertBalanceToString(value - wallet.getBalance());
							player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message.replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", this.getItemName(itemName)).replaceAll("%MISSING%", missing), plugin));
						}							
					}
				}
			}
		}else{
			String message = getMessage("illegalItem");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message.replaceAll("%ITEM%", itemName).replaceAll("%ACTION%", "buy"), plugin));
			}	
		}
		player.updateInventory();
	}
	
	public void sell(String args[], Player player, int startIndex){
		Wallet wallet = new Wallet(plugin, player);
		String itemName = args[startIndex];
		if(itemCanBeSold(args[startIndex])){
			int amount = 0;
			if(args[startIndex+1] != null){
				amount = Integer.parseInt(getNode("defaultBuySellStackAmount"));
			}
			String itemParts[] = itemName.split("\\:");
			int ID = Integer.parseInt(getItemID(itemParts[0]));
			ItemStack item = new ItemStack(Material.getMaterial(ID));
			if(itemParts.length>1){
				item.setDurability(getDamageValue(itemName));
			}
			if(amount <= 0){
				amount = Integer.parseInt(getNode("defaultBuySellStackAmount"));
			}
			item.setAmount(amount);
			int haveItem = 0;
			for(ItemStack invItem : player.getInventory().getContents()){
				if(invItem.getDurability() == item.getDurability()){
					if(invItem.getTypeId() == item.getTypeId()){
						haveItem = invItem.getAmount() + haveItem;
					}
				}
			}
			if(haveItem>amount){
				String message = getMessage("sellTooMuch");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message, plugin));
				}
			}else{
				double value = getSellValue(itemName)*amount;
				value = applyDiscount(player, value, 1);
				if(getNode("globalMessages").equalsIgnoreCase("true")){
					String message = getMessage("globalMessageSell");
					if(!message.equalsIgnoreCase("nomsg")){
						Bukkit.getServer().broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%PLAYERNAME%", player.getName()).replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)+""), plugin));
					}
				}
				int remaining = amount;
				int i = 0;
				for(ItemStack invItem : player.getInventory().getContents()){
					if(invItem.getDurability() == item.getDurability()){
						if(invItem.getTypeId() == item.getTypeId()){
							if(remaining>0){
								if(remaining-invItem.getAmount()>-1){
									remaining = remaining-invItem.getAmount();
									player.getInventory().remove(i);
								}else{
									invItem.setAmount(invItem.getAmount()-remaining);
									remaining = 0;
								}
							}else{
								break;
							}
						}
					}
					i++;
				}
				wallet.deposit(value);
				String message = getMessage("onSell");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", amount+"").replaceAll("%ITEM%", getItemName(itemName)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(value)), plugin));
				}
				log(player.getName()+" SOLD "+getItemName(itemName)+"x"+amount+" FOR "+Wallet.convertBalanceToString(value));
			}
		}else{
			String message = getMessage("illegalItem");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message.replaceAll("%ITEM%", itemName).replaceAll("%ACTION%", "sell"), plugin));
			}	
		}
		player.updateInventory();
	}
	
	public void sell_all(String args[], Player player, int startIndex, boolean confirmed){
		if(getNode("sellAllConfirmation").equalsIgnoreCase("true") && !confirmed){
			confirms.put(player, "all "+args[startIndex]);
			String message = getMessage("confirm");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
			}
		}else{
			int ID = Integer.parseInt(getItemID(args[startIndex]));
			short damage = getDamageValue(args[startIndex]);
			double total = 0;
			double number = 0;
			String name = getItemName(args[startIndex]);
			if(itemCanBeSold(args[startIndex])){
				int i = 0;
				for(ItemStack invItem : player.getInventory().getContents()){
					if(invItem.getTypeId() == ID){
						if(invItem.getDurability() == damage){
							number = number + invItem.getAmount();
							total = invItem.getAmount()*getSellValue(args[startIndex]);
							player.getInventory().remove(i);
						}
					}
					i++;
				}
				Wallet wallet = new Wallet(plugin, player);
				wallet.deposit(total);
				String message = getMessage("onSell");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", number+"").replaceAll("%ITEM%", getItemName(name)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(total)), plugin));
				}
				log(player.getName()+" SOLD "+getItemName(name)+"x"+number+" FOR "+Wallet.convertBalanceToString(total));
			}else{
				String message = getMessage("illegalItem");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(message.replaceAll("%ITEM%", name).replaceAll("%ACTION%", "sell"), plugin));
				}	
				message = getMessage("globalMessageSell");
				if(!message.equalsIgnoreCase("nomsg")){
					Bukkit.getServer().broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%PLAYERNAME%", player.getName()).replaceAll("%AMOUNT%", number+"").replaceAll("%ITEM%", getItemName(name)).replaceAll("%TOTAL%", Wallet.convertBalanceToString(total)+""), plugin));
				}
			}
			player.updateInventory();
		}
	}
	
	public void sell_inventory(Player player, boolean confirmed){
		if(getNode("sellInventoryConfirmation").equalsIgnoreCase("true") && !confirmed){
			confirms.put(player, "inv");
			String message = getMessage("confirm");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
			}
		}else{
			double total = 0.0;
			int i = 0;
			for(ItemStack item : player.getInventory().getContents()){
				if(itemCanBeSold(item.getTypeId()+":"+item.getDurability())){
					total = total + (getSellValue(item.getTypeId()+":"+item.getDurability())*item.getAmount());
					player.getInventory().remove(i);
				}
				i++;
			}
			Wallet wallet = new Wallet(plugin, player);
			wallet.deposit(total);
			String message = getMessage("onSellInventory");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", Wallet.convertBalanceToString(total)), plugin));
			}
			message = getMessage("onSellInventoryCommand");
			if(!message.equalsIgnoreCase("nomsg")){
				Bukkit.getServer().broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message.replaceAll("%AMOUNT%", Wallet.convertBalanceToString(total)).replaceAll("%PLAYER%", player.getName()), plugin));
			}
			log(player.getName()+" SOLD INVENTORY AND EARNED "+Wallet.convertBalanceToString(total));
			player.updateInventory();
		}
	}
	
	public void find_item(String itemname, Player player){
		String parts[] = itemname.split("\\:");
		String name = parts[0];
		MeaSQL sql = new MeaSQL(plugin);
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"') LIMIT 0,"+getNode("itemsPerPage"));
		try {
			while(results.next()){
				String subdata = results.getString(6);
				String item_name = results.getString(1);
				String buy = (results.getBoolean(5))?(Wallet.convertBalanceToString(results.getDouble(4))):"Cannot be bought";
				String sell = (results.getBoolean(6))?(Wallet.convertBalanceToString(results.getDouble(3))):"Cannot be bought";
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						String message = getMessage("lookupFormat").replaceAll("%NAME%", item_name).replaceAll("%BUY%", buy).replaceAll("%SELL%", sell);
						if(!message.equalsIgnoreCase("nomsg")){
							player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
						}
					}
				}else{
					String message = getMessage("lookupFormat").replaceAll("%NAME%", item_name).replaceAll("%BUY%", buy).replaceAll("%SELL%", sell);
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void help(Player player, int page){
		Vector<String> commands = new Vector<String>();
		if(player.hasPermission("meaSuite.Shop.User.Help")){
			commands.add(ChatColor.DARK_GREEN+" /shop help <page>       "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Show help menu with page numbers");
		}
		if(player.hasPermission("meaSuite.Shop.User.Buy")){
			commands.add(ChatColor.DARK_GREEN+" /shop buy <item> [Amt]  "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Buy an item with optional amount");
		}
		if(player.hasPermission("meaSuite.Shop.User.Sell")){
			commands.add(ChatColor.DARK_GREEN+" /shop sell <item> [Amt]  "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Sell an item with an optional amount");
		}
		if(player.hasPermission("meaSuite.Shop.User.Sellall")){
			commands.add(ChatColor.DARK_GREEN+" /shop sellall <item>      "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Sell all of an item");
		}
		if(player.hasPermission("meaSuite.Shop.User.Sellinv")){
			commands.add(ChatColor.DARK_GREEN+" /shop sellinv              "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Sell entire inventory");
		}
		if(player.hasPermission("meaSuite.Shop.User.Check")){
			commands.add(ChatColor.DARK_GREEN+" /shop lookup <item>      "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Lookup an item");
		}
		if(player.hasPermission("meaSuite.Shop.Admin.Edit")){
			commands.add(ChatColor.DARK_GREEN+" /shop admin edit <item> <buy> <sell>  "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Edit an item");
			commands.add(ChatColor.DARK_GREEN+" /shop admin add <item> <buy> <sell>  "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Add an item");
		}
		if(player.hasPermission("meaSuite.Shop.Admin.Hide")){
			commands.add(ChatColor.DARK_GREEN+" /shop admin remove <item>              "+ChatColor.DARK_RED+"| "+ChatColor.DARK_GREEN+"Remove/hide an item");
		}
		int maxPerPage = Integer.parseInt(getNode("itemsPerPage"));
		page = page-1;
		int pages = (int) ((Math.ceil((commands.size()/maxPerPage))>0)?(Math.ceil((commands.size()/maxPerPage))):1);
		String dashes = ChatColor.GOLD+"-----------------";
		player.sendMessage(dashes+ChatColor.getByCode(0xc)+"["+ChatColor.getByCode(0xa)+"meaCraft Store "+ChatColor.getByCode(0xa)+(page+1)+"/"+pages+ChatColor.getByCode(0xc)+"]"+dashes);
		for(int i=(page*maxPerPage);i<commands.size();i++){
			player.sendMessage(MultiFunction.addColor(commands.get(i), plugin));
		}
		player.sendMessage(dashes+ChatColor.getByCode(0xc)+"["+ChatColor.getByCode(0xa)+"meaCraft Store "+ChatColor.getByCode(0xa)+(page+1)+"/"+pages+ChatColor.getByCode(0xc)+"]"+dashes);
	}
	
	public void help(Player player, String page){
		help(player, Integer.parseInt(page));
	}
	
	public void add_item(String args[], Player player){
		edit_item(args, player);
	}
	
	public void remove_item(String args[], Player player, int startIndex){
		String item = args[0];
		String buy = "0.0";
		String sell = "0.0";
		MeaSQL sql = new MeaSQL(plugin);
		String subdata = "";
		String parts[] = item.split("\\:");
		item = parts[0];
		String sub = "0";
		if(parts.length>1){
			subdata = " AND `subdata`LIKE'%"+parts[1]+"%'";
			sub = parts[1];
		}
		sql.query("UPDATE `items` SET `buy_price`="+buy+", `sell_price`="+sell+" WHERE ((`item_name`LIKE'%"+item+"%') OR (`aliases`LIKE'%"+item+"%') OR (`item_id`='"+item+"'))"+subdata+" LIMIT 1");
		String message = getMessage("onAdminHide").replaceAll("%ITEM%", getItemName(item+":"+sub));
		if(!message.equalsIgnoreCase("nomsg")){
			Bukkit.broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
		}
	}
	
	public void edit_item(String args[], Player player){
		String item = args[0];
		String buy = args[1];
		String sell = args[2];
		MeaSQL sql = new MeaSQL(plugin);
		String subdata = "";
		String parts[] = item.split("\\:");
		item = parts[0];
		String sub = "0";
		if(parts.length>1){
			subdata = " AND `subdata`LIKE'%"+parts[1]+"%'";
			sub = parts[1];
		}
		sql.query("UPDATE `items` SET `buy_price`="+buy+", `sell_price`="+sell+" WHERE ((`item_name`LIKE'%"+item+"%') OR (`aliases`LIKE'%"+item+"%') OR (`item_id`='"+item+"'))"+subdata+" LIMIT 1");
		String message = getMessage("onAdminEdit").replaceAll("%ITEM%", getItemName(item+":"+sub)).replaceAll("%BUY%", Wallet.convertBalanceToString(buy)).replaceAll("%SELL%", Wallet.convertBalanceToString(sell));
		if(!message.equalsIgnoreCase("nomsg")){
			Bukkit.broadcastMessage(MultiFunction.getPre(plugin) + " " + MultiFunction.addColor(message, plugin));
		}
	}
	
	public double applyDiscount(Player player, double value, int buySell){
		//Buy discount = -1
		//Sell discount = 1
		MeaSQL sql = new MeaSQL(plugin);
		ResultSet results = sql.query("SELECT * FROM `discounts`");
		try{
			while(results.next()){
				String discount = "meaSuite.Discounts."+results.getString(0);
				int percentOff = results.getInt(2);
				if(player.hasPermission(discount)){
					if(buySell == -1){
						double reduction = value*(percentOff/100);
						value = value - reduction;
						break;
					}else if(buySell == 1){
						double reduction = value*(percentOff/100);
						value = value + reduction;
						break;
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	public short getDamageValue(String name){
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				String subdata = results.getString(6);
				int damage_value = results.getInt(7);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						return (short) damage_value;
					}
				}else{
					return (short) damage_value;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean itemCanBeBought(String name){
		boolean buyable = false;
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				boolean buyable_item = results.getBoolean(4);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						buyable = buyable_item;
						break;
					}
				}else{
					buyable = buyable_item;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return buyable;
	}
	
	public boolean itemCanBeSold(String name){
		boolean sellable = false;
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				boolean sellable_item = results.getBoolean(5);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						sellable = sellable_item;
						break;
					}
				}else{
					sellable = sellable_item;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sellable;
	}
	
	public double getBuyValue(String name){
		double value = -1;
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				double buy_price = results.getDouble(3);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						value = buy_price;
						break;
					}
				}else{
					value = buy_price;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public double getSellValue(String name){
		double value = -1;
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				double sell_price = results.getDouble(2);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						value = sell_price;
						break;
					}
				}else{
					value = sell_price;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String getItemName(String name){
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				String item_name = results.getString(1);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						name = item_name;
						break;
					}
				}else{
					name = item_name;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public String getItemID(String name){
		String ID = "0";
		MeaSQL sql = new MeaSQL(plugin);
		String parts[] = name.split("\\:");
		name = parts[0];
		ResultSet results = sql.query("SELECT * FROM `items` WHERE (`item_name`LIKE'%"+name+"%') OR (`aliases`LIKE'%"+name+"%') OR (`item_id`='"+name+"')");
		try {
			while(results.next()){
				int id = results.getInt(0);
				String subdata = results.getString(6);
				if(parts.length>1){
					if(subdata.contains(parts[1])){
						ID = id+"";
						break;
					}
				}else{
					ID = id+"";
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ID;
	}
	
	public void reload(){
		loadDiscounts();
	}
	
	@SuppressWarnings("unused")
	public void setup(){
		try {
			File directory = new File(this.plugin.getDataFolder()+"/meaShop");
			directory.mkdirs();
			if(!new File(directory+"/readMeBeforeDeleting.txt").exists()){
				Download download = new Download(new URL("http://68.148.10.71/mc/plugins/ext/meaSuite/shop/pack.zip"), this.plugin.getDataFolder()+"/meaShop/pack.zip");
				Unzip unzip = new Unzip(this.plugin.getDataFolder()+"/meaShop/pack.zip", this.plugin.getDataFolder()+"/meaShop/");
				File file = new File(this.plugin.getDataFolder()+"/meaShop/pack.zip");
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		MeaSQL sql = new MeaSQL(plugin);
		sql.query(new File(plugin.getDataFolder()+"/meaShop/setup.sql"));
		sql.query(new File(plugin.getDataFolder()+"/meaShop/inserts.sql"));
		loadDiscounts();
	}
	
	private void loadDiscounts(){
		PluginManager pm = plugin.getServer().getPluginManager();
		MeaSQL sql = new MeaSQL(plugin);
		ResultSet results = sql.query("SELECT * FROM `"+Loader.getNode("meaSuite.SQL.database")+"`.`discounts`");
		try{
			System.out.println(results.getFetchSize());
			if(!results.isClosed()){
				while(results.next()){
					try{
						pm.addPermission(new Permission("meaSuite.Discounts."+results.getString(1)));
					}catch (Exception e){}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private String getNode(String node) {
		return plugin.getConfig().getString("meaShop."+node);
	}
	
	public String getMessage(String node){
		Configuration config = new Configuration(new File(plugin.getDataFolder()+"/meaShop/messages.yml"));
		config.load();
		return config.getString(node);
	}
	
	public boolean hasPermission(String permission, Player player){
		return player.hasPermission("meaSuite.Shop."+permission);
	}
	
	public void enable(Player player){
		plugin.getConfig().set("meaShop.enabled", "true");
		plugin.saveConfig();
		if(player != null){
			player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(getMessage("onEnable"), plugin));
		}
	}
	
	public void disable(Player player){
		plugin.getConfig().set("meaShop.enabled", "false");
		plugin.saveConfig();
		if(player != null){
			player.sendMessage(MultiFunction.getPre(plugin)+" "+MultiFunction.addColor(getMessage("onDisable"), plugin));
		}
	}
	
	public boolean isEnabled(){
		return plugin.getConfig().getString("meaShop.enabled").equalsIgnoreCase("true");
	}
	
	public void log(String message){
		if(getNode("logToFile").equalsIgnoreCase("true")){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(plugin.getDataFolder()+"/meaShop/logs/transactions.txt"), true));
				DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
				Date date = new Date();
				String timestamp = dateFormat.format(date);
				out.write("["+timestamp+"] "+message+"\r\n");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
