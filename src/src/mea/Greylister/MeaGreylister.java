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
package mea.Greylister;

import java.io.File;

import mea.plugin.MultiFunction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaGreylister{
	
	public JavaPlugin plugin;
	
	public MeaGreylister(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public boolean isEnabled(){
		Configuration config = plugin.getConfiguration();
		return config.getString("meaGreylister.enabled").equalsIgnoreCase("TRUE");
	}
	
	public void enable(Player player){
		Configuration config = plugin.getConfiguration();
		config.load();
		config.setProperty("meaGreylister.enabled", "true");
		if(player != null){
			player.sendMessage(convertVariables(getMessage("onEnable"), player, "apply"));
		}
		config.save();
	}
	
	public void disable(Player player){
		Configuration config = plugin.getConfiguration();
		config.load();
		config.setProperty("meaGreylister.enabled", "false");
		if(player != null){
			player.sendMessage(convertVariables(getMessage("onDisable"), player, "apply"));
		}
		config.save();
	}

	public void apply(Player player, String[] args) {
		File application = new File(plugin.getDataFolder()+"/meaGreylister/applications/"+player.getName()+".yml");
		if(application.exists()){
			String message = convertVariables(getMessage("onApplyHave"), player, "apply");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(message);
			}
		}else{
			if(args.length+1 < getRequiredApplyCommand()){
				String message = convertVariables(getMessage("notEnoughArgs"), player, "apply");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(message);
				}
			}else{
				Configuration config = new Configuration(application);
				for(int x=0;x<args.length;x++){
					int i = x + 1;
					if(getAdminVar("var"+(i+1)) == null){
						config.setProperty(getAdminVar("var"+i), MultiFunction.merge(args, i-1));
						break;
					}else{
						config.setProperty(getAdminVar("var"+i), args[x]);
					}
				}
				config.save();
				alertNewApplication(player);
				String message = convertVariables(getMessage("onApplySent"), player, "apply");
				if(!message.equalsIgnoreCase("nomsg")){
					player.sendMessage(message);
				}
			}
		}
	}

	public void showExemptError(Player player) {
		String message = convertVariables(getMessage("onApplyExempt"), player, "apply");
		if(!message.equalsIgnoreCase("nomsg")){
			player.sendMessage(message);
		}
	}

	public void denyApply(Player player) {
		String message = convertVariables(getMessage("onApplyError"), player, "apply");
		if(!message.equalsIgnoreCase("nomsg")){
			player.sendMessage(message);
		}
	}

	public void applications(Player player, String[] args) {
		if(args.length < 1){
			String message = convertVariables(getMessage("notEnoughArgs"), player, "apps");
			if(!message.equalsIgnoreCase("nomsg")){
				player.sendMessage(message);
			}
		}else{
			if(args[0].equalsIgnoreCase("list")){
				File list[] = new File(plugin.getDataFolder()+"/meaGreylister/applications/").listFiles();
				int notResponded = 0;
				for(File file: list){
					Configuration config = new Configuration(file);
					config.load();
					if(config.getString("result") == null){
						String name = file.getName().replaceAll("\\.yml", "");
						player.sendMessage(MultiFunction.getPre(plugin)+" "+name);
						notResponded ++;
					}
				}
				player.sendMessage(MultiFunction.getPre(plugin)+" "+notResponded+" application(s).");
			}else{
				if(args.length < 2){
					String message = convertVariables(getMessage("notEnoughArgs"), player, "apps");
					if(!message.equalsIgnoreCase("nomsg")){
						player.sendMessage(message);
					}
				}else{
					if(args[0].equalsIgnoreCase("accept")){
						args[1] = MultiFunction.playerName(args[1]);
						if(args[1].equalsIgnoreCase("global")){
							File list[] = new File(plugin.getDataFolder()+"/meaGreylister/applications/").listFiles();
							for(File file: list){
								Configuration config = new Configuration(file);
								config.load();
								if(config.getString("result") == null){
									String name = file.getName().replaceAll("\\.yml", "");
									acceptApplication(name, player);
								}
							}
						}else{
							acceptApplication(args[1], player);
						}
					}else if(args[0].equalsIgnoreCase("decline")){
						args[1] = MultiFunction.playerName(args[1]);
						if(args[1].equalsIgnoreCase("global")){
							File list[] = new File(plugin.getDataFolder()+"/meaGreylister/applications/").listFiles();
							for(File file: list){
								Configuration config = new Configuration(file);
								config.load();
								if(config.getString("result") == null){
									String name = file.getName().replaceAll("\\.yml", "");
									declineApplication(name, player);
								}
							}
						}else{
							declineApplication(args[1], player);
						}
					}else if(args[0].equalsIgnoreCase("view")){
						args[1] = MultiFunction.playerName(args[1]);
						if(args[1].equalsIgnoreCase("global")){
							String message = convertVariables(getMessage("notEnoughArgs"), player, "apps");
							if(!message.equalsIgnoreCase("nomsg")){
								player.sendMessage(message);
							}
						}else{
							viewApplication(args[1], player);
						}
					}else{
						String message = convertVariables(getMessage("notEnoughArgs"), player, "apps");
						if(!message.equalsIgnoreCase("nomsg")){
							player.sendMessage(message);
						}
					}
				}
			}
		}
	}
	
	private void acceptApplication(String player, Player sender){
		File application = new File(plugin.getDataFolder()+"/meaGreylister/applications/"+player+".yml");
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getAcceptVar("command"+i) != null){
				System.out.println(this.convertVariables(getAcceptVar("command"+i), Bukkit.getPlayer(player), "apps"));
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.getCommandVar(getAcceptVar("command"+i), Bukkit.getPlayer(player), "apps"));
			}else{
				hasCommand = false;
			}
			i++;
		}
		Configuration config = new Configuration(application);
		config.load();
		config.setProperty("result", "accepted by "+sender.getName());
		config.save();
	}
	
	private void declineApplication(String player, Player sender){
		File application = new File(plugin.getDataFolder()+"/meaGreylister/applications/"+player+".yml");
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getDeclineVar("command"+i) != null){
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.getCommandVar(getDeclineVar("command"+i), Bukkit.getPlayer(player), "apps"));
			}else{
				hasCommand = false;
			}
			i++;
		}
		Configuration config = new Configuration(application);
		config.load();
		config.setProperty("result", "declined by "+sender.getName());
		config.save();
	}
	
	private void viewApplication(String player, Player sender){
		File application = new File(plugin.getDataFolder()+"/meaGreylister/applications/"+player+".yml");
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getAdminVar("return"+i) != null){
				sender.sendMessage(""+convertVariables(getAdminVar("return"+i), Bukkit.getPlayer(player), "apps"));
			}else{
				hasCommand = false;
			}
			i++;
		}
		Configuration config = new Configuration(application);
		config.load();
		if(config.getString("result") == null){
			sender.sendMessage(MultiFunction.getPre(plugin)+" Status: Not accepted or declined");
		}else{
			sender.sendMessage(MultiFunction.getPre(plugin)+" Status: "+config.getString("result"));
		}
	}

	public void denyApplications(Player player) {
		String message = convertVariables(getMessage("onAppError"), player, "apps");
		if(!message.equalsIgnoreCase("nomsg")){
			player.sendMessage(message);
		}
	}
	
	private void alertNewApplication(Player player){
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		for(Player mod : players){
			if(mod.hasPermission("meaSuite.Greylister.viewapps")){
				String message = convertVariables(getMessage("newApplication"), player, "apply");
				if(!message.equalsIgnoreCase("nomsg")){
					mod.sendMessage(message);
				}
			}
		}
	}
	
	private int getRequiredApplyCommand(){
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getAdminVar("var"+i) != null){
				i++;
			}else{
				hasCommand = false;
			}
		}
		return i;
	}
	
	public String getNewApplications(){
		String newApps = "0";
		File list[] = new File(plugin.getDataFolder()+"/meaGreylister/applications/").listFiles();
		int notResponded = 0;
		for(File file: list){
			Configuration config = new Configuration(file);
			config.load();
			if(config.getString("result") == null){
				notResponded ++;
			}
		}
		newApps = String.valueOf(notResponded);
		return newApps;
	}

	public String convertVariables(String message, Player player, String command) {
		message = message.replaceAll("%PLAYER%", player.getName());
		message = message.replaceAll("%HELPMENU%", getHelpMenuFor(command));
		message = message.replaceAll("%NEWAPPS%", getNewApplications());
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getAdminVar("var"+i) != null){
				message = message.replaceAll("%"+getAdminVar("var"+i)+"%", getApplicationVar(player, getAdminVar("var"+i)));
				i++;
			}else{
				hasCommand = false;
			}
		}
		message = MultiFunction.addColor(message, plugin);
		return MultiFunction.getPre(plugin)+" "+message;
	}

	public String getCommandVar(String message, Player player, String command) {
		message = message.replaceAll("%PLAYER%", player.getName());
		message = message.replaceAll("%HELPMENU%", getHelpMenuFor(command));
		message = message.replaceAll("%NEWAPPS%", new File(plugin.getDataFolder()+"/meaGreylister/applications").listFiles().length+"");
		boolean hasCommand = true;
		int i = 1;
		while(hasCommand){
			if(getAdminVar("var"+i) != null){
				message = message.replaceAll("%"+getAdminVar("var"+i)+"%", getApplicationVar(player, getAdminVar("var"+i)));
				i++;
			}else{
				hasCommand = false;
			}
		}
		return message;
	}
	
	private String getHelpMenuFor(String command) {
		String help = "No help found :(";
		if(command.equalsIgnoreCase("apply")){
			help = "/apply ";
			boolean hasCommand = true;
			int i = 1;
			while(hasCommand){
				if(getAdminVar("label"+i) != null){
					help = help.concat("["+getAdminVar("label"+i)+"] ");
					i++;
				}else{
					hasCommand = false;
				}
			}
		}else if(command.equalsIgnoreCase("apps")){
			help = "/apps [view | list | accept | decline] [PLAYERNAME | global]";
		}
		return help;
	}

	private String getApplicationVar(Player player, String adminVar) {
		String var = "[Err: Var Not Recorded]";
		File application = new File(plugin.getDataFolder()+"/meaGreylister/applications/"+player.getName()+".yml");
		if(application.exists()){
			Configuration config = new Configuration(application);
			config.load();
			var = config.getString(adminVar);
		}
		return var;
	}

	private String getAdminVar(String variable) {
		Configuration config = plugin.getConfiguration();
		config.load();
		String mess = "";
		mess = config.getString("meaGreylister.adminView."+variable);
		return mess;
	}

	private String getAcceptVar(String variable) {
		Configuration config = plugin.getConfiguration();
		config.load();
		String mess = "";
		mess = config.getString("meaGreylister.onAcceptCommads."+variable);
		return mess;
	}

	private String getDeclineVar(String variable) {
		Configuration config = plugin.getConfiguration();
		config.load();
		String mess = "";
		mess = config.getString("meaGreylister.onDeclineCommads."+variable);
		return mess;
	}

	public String getMessage(String configHeader){
		Configuration config = plugin.getConfiguration();
		config.load();
		String mess = "";
		mess = config.getString("meaGreylister.messages."+configHeader);
		return mess;
	}
}
