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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import mea.Chat.MeaChat;
import mea.Chat.MeaIRC;
import mea.External.Download;
import mea.Flarf.MeaFlarf;
import mea.Freezer.MeaFreezer;
import mea.Greylister.MeaGreylister;
import mea.Hook.MeaHook;
import mea.Logger.MeaLogger;
import mea.Lottery.MeaLottery;
import mea.RandomTP.MeaRandomTP;
import mea.samples.MeaSamples;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader extends JavaPlugin{
		
	public JavaPlugin plugin = this;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ServerPlayerListener playerListener;
	public ServerBlockListener blockListener;
	
	public MeaChat chat;
	public MeaHook meaHook;
	
	private MeaGreylister gl = new MeaGreylister(this);
	private MeaSamples samples = new MeaSamples(this);
	private MeaFreezer fr = new MeaFreezer(this);
	private MeaRandomTP tp = new MeaRandomTP(this);
	private MeaLottery lottery = new MeaLottery(this);
	private MeaLogger meaLog = new MeaLogger(this, this);
	private MeaFlarf flarf = new MeaFlarf(this);
	
	private ConfigWriter configWriter;
	
	public int version = 484;
	private boolean updateBroadcasted = false;
	
	public boolean sChecked = false;
	
    private MeaIRC irc;

	public void onEnable() {		
		long start = System.currentTimeMillis();
		//System.out.println("************************************");
		System.out.println("["+plugin.getDescription().getFullName()+"] Loading!");
		//System.out.println("************************************");
	    //Module start
	    meaHook = new MeaHook(this, chat);
		chat = new MeaChat(this);
		configWriter = new ConfigWriter(plugin);
		configWriter.write(); //Must load before anything, but after the giant header
		meaLog.startup();
		meaLog.log("["+plugin.getDescription().getFullName()+"] Loading!");
		samples.create();
		chat.startup(irc);
	    chat.initIRC(irc);
	    meaHook.startup();
	    lottery.startup();
		irc = new MeaIRC(chat, meaHook);
		//flarf.startup();
		irc.sendMinecraftToIRC(plugin.getConfig().getBoolean("meaChat.irc.MinecraftToIRC", false));
		irc.sendIRCToMinecraft(plugin.getConfig().getBoolean("meaChat.irc.IRCToMinecraft", false));
		blockListener = new ServerBlockListener(this);
		playerListener = new ServerPlayerListener(this, chat, meaHook);
		
		//To avoid errors
		Runnable events = new Runnable(){
			public void run() {
				while(!plugin.isEnabled());
				PluginManager pm = Bukkit.getServer().getPluginManager();
				pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Highest, plugin); //High = ran last, mChatSuite override
				//Block events
				pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, plugin);
				pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, plugin);
				System.out.println("[meaSuite] Event Handlers Loaded");
				meaLog.log("[meaSuite] Event Handlers Loaded");
			}
		};
		Thread eventsThread = new Thread(events);
		eventsThread.start();
		//Update check
		Runnable update = new Runnable(){
			public void run(){
				while(true){
						try {
							boolean isDev = false;
							if(getNode("meaSuite.downloadDevVersions").equalsIgnoreCase("true")){
								isDev = true;
							}
							BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://68.148.10.71/mc/plugins/version.txt").openStream()));
							String line;
							while((line = in.readLine()) != null){
								int v = Integer.parseInt(line);
								if(!sChecked) sCheck();
								if(v>version){
									if(!updateBroadcasted){
										@SuppressWarnings("unused")
										Download download = new Download(new URL("http://68.148.10.71/mc/plugins/meaSuite.jar"), System.getProperty("user.dir")+"/plugins/meaSuite.jar", false);
										updateBroadcasted = true;
										meaLog.log("Downloaded meaSuite.jar build "+v+" (Current Version "+version+")");
									}
									meaLog.log("** meaSuite build "+v+" available (Current Build: "+version+")! Restart server to use it. (For changes type: /mea changelog)");
									System.err.println("** meaSuite build "+v+" available (Current Build: "+version+")! Restart server to use it. (For changes type: /mea changelog)");
									Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN+"** meaSuite build "+v+" available (Current Build: "+version+")! Restart server to use it.");
								}else if(isDev){
									BufferedReader dev = new BufferedReader(new InputStreamReader(new URL("http://68.148.10.71/mc/plugins/version_dev.txt").openStream()));
									String line2;
									while((line2 = dev.readLine()) != null){
										int vDev = Integer.parseInt(line2);
										if(vDev>version){
											if(!updateBroadcasted){
												@SuppressWarnings("unused")
												Download download = new Download(new URL("http://68.148.10.71/mc/plugins/meaSuite.jar"), System.getProperty("user.dir")+"/plugins/meaSuite.jar", false);
												updateBroadcasted = true;
												meaLog.log("Downloaded meaSuite.jar DEV build "+v+" (Current Version "+version+")");
											}
											meaLog.log("** meaSuite DEV build "+v+" available (Current Build: "+version+")! Restart server to use it. (For changes type: /mea changelog)");
											System.err.println("** meaSuite DEV build "+v+" available (Current Build: "+version+")! Restart server to use it. (For changes type: /mea changelog)");
											Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN+"** meaSuite DEV build "+v+" available (Current Build: "+version+")! Restart server to use it.");
										}
									}
									dev.close();
								}
							}
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
							meaLog.log(e.getMessage());
						}
					try{
						if(!updateBroadcasted){
							Thread.sleep(1000);
						}else{
							Thread.sleep(30000);
						}
					}catch(Exception e){}
				}
			}
		};
		Thread updateThread = new Thread(update);
		updateThread.start();
		long end = System.currentTimeMillis();
		double time = (end-start)/1000;
		//System.out.println("************************************");
		System.out.println("["+plugin.getDescription().getFullName()+"] Loaded in "+time+"s!");
		meaLog.log("["+plugin.getDescription().getFullName()+"] Loaded in "+time+"s!");
		//System.out.println("************************************");
	}
	
	public void onDisable() {
		System.out.println("["+plugin.getDescription().getName()+"] Cleaning our plates...");
		chat.kill();
		meaLog.rotate();
		meaLog.cleanup();
		//System.out.println("************************************");
		System.out.println("["+plugin.getDescription().getFullName()+"] Plates Cleaned! Please drip dry. (Plugin Disabled or Unloaded)");
		//System.out.println("************************************");
		meaLog.log("["+plugin.getDescription().getFullName()+"] Disabled or Unloaded!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		try{
			if(sender instanceof Player){
				meaLog.log("Command "+cmd+" sent with args "+args.toString()+" by "+sender.getName());
				Player player = (Player) sender;
				//MultiFunction.fireFeature(player);
				//fr.createDome(player.getLocation(), new Configuration(new File(plugin.getDataFolder()+"/turt2live.yml")));
				if(cmd.equalsIgnoreCase("apply") && gl.isEnabled()){
					if(player.hasPermission("meaSuite.Greylister.apply")){
						if(player.hasPermission("meaSuite.Greylister.exempt")){
							gl.showExemptError(player);
						}else{
							gl.apply(player, args);
						}
					}else{
						if(player.hasPermission("meaSuite.Greylister.exempt")){
							gl.showExemptError(player);
						}else{
							gl.denyApply(player);
						}
					}
				}else if(cmd.equalsIgnoreCase("apps") && gl.isEnabled()){
					if(player.hasPermission("meaSuite.Greylister.viewapps")){
						gl.applications(player, args);
					}else{
						gl.denyApplications(player);
					}
				}else if(cmd.equalsIgnoreCase("freeze") && fr.isEnabled()){
					args[0] = MultiFunction.playerName(args[0]);
					if(player.hasPermission("meaSuite.Freezer.freeze")){
						if(args.length >= 2){
							if(args[0].equalsIgnoreCase("all")){
								Player players[] = Bukkit.getOnlinePlayers();
								for(Player pl : players){
									fr.freezePlayer(player, pl, args);
								}
							}else{
								fr.freezePlayer(player, Bukkit.getPlayer(args[0]), args);
							}
						}else{
							player.sendMessage(MultiFunction.getPre(plugin)+" "+fr.convertVariables(fr.getNode("messages.notEnoughArgs"), player, cmd));
						}
					}else{
						fr.noPerms(player);
					}
				}else if(cmd.equalsIgnoreCase("unfreeze") && fr.isEnabled()){
					args[0] = MultiFunction.playerName(args[0]);
					if(player.hasPermission("meaSuite.Freezer.unfreeze")){
						if(args.length == 1){
							if(args[0].equalsIgnoreCase("all")){
								Player players[] = Bukkit.getOnlinePlayers();
								for(Player pl : players){
									fr.unfreezePlayer(player, pl, args[0]);
								}
							}else{
								fr.unfreezePlayer(player, Bukkit.getPlayer(args[0]), args[0]);
							}
						}else{
							player.sendMessage(MultiFunction.getPre(plugin)+" "+fr.convertVariables(fr.getNode("messages.notEnoughArgs"), player, cmd));
						}
					}else{
						fr.noPerms(player);
					}
				}else if(cmd.equalsIgnoreCase("isFrozen") && fr.isEnabled()){
					args[0] = MultiFunction.playerName(args[0]);
					if(player.hasPermission("meaSuite.Freezer.checkfrozen")){
						if(args.length == 1){
							fr.displayInfo(player, Bukkit.getPlayer(args[0]));
						}else{
							player.sendMessage(MultiFunction.getPre(plugin)+" "+fr.convertVariables(fr.getNode("messages.notEnoughArgs"), player, cmd));
						}
					}else{
						fr.noPerms(player);
					}
				}else if(cmd.equalsIgnoreCase("useDome") && fr.isEnabled()){
					if(player.hasPermission("meaSuite.Freezer.setstate")){
						if(args.length < 1){
							player.sendMessage(MultiFunction.getPre(plugin)+" "+fr.convertVariables(fr.getNode("messages.notEnoughArgs"), player, cmd));
						}else{
							fr.toggleDome(args[0]);
						}
					}else{
						fr.noPerms(player);
					}
				}else if(cmd.equalsIgnoreCase("authenticate") && fr.isEnabled()){
					if(player.hasPermission("meaSuite.Freezer.codeunfreeze")){
						if(args.length == 1){
							fr.unfreezeCodedPlayer(player, args);
						}else{
							player.sendMessage(MultiFunction.getPre(plugin)+" "+fr.convertVariables(fr.getNode("messages.notEnoughArgs"), player, cmd));
						}
					}else{
						fr.noPerms(player);
					}
				}else if(cmd.equalsIgnoreCase("rtp") || cmd.equalsIgnoreCase("randomtp")){
					tp.tp(player);
				}else if(cmd.equalsIgnoreCase("frules") && (player.hasPermission("meaSuite.frules") || player.hasPermission("meaSuite.forceRules"))){
					if(args.length>0){
						if(Bukkit.getPlayer(args[0]) != null){
							Player fruler = Bukkit.getPlayer(args[0]);
							if(fruler.isOnline()){
								if(fruler.hasPermission("meaSuite.frules.exempt") || fruler.hasPermission("meaSuite.forceRules.exempt")){
									player.sendMessage(MultiFunction.getPre(plugin) + " Player is exempt! Haha!");
								}else{
									fruler.performCommand("rules");	
									player.sendMessage(MultiFunction.getPre(plugin) + " Force Rules Success!");	
								}
							}else{
								player.sendMessage(MultiFunction.getPre(plugin) + " They arn't online!");
							}
						}else{
							player.sendMessage(MultiFunction.getPre(plugin) + " They arn't online!");
						}
					}else{
						player.sendMessage(MultiFunction.getPre(plugin) + " Not enough arguments! /frules [username]");
					}
				}else if(cmd.equalsIgnoreCase("mea")){
					System.out.println(args.toString());
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("reload") && player.hasPermission("meaSuite.reload")){
							reloadSelf();
						}else if(args[0].equalsIgnoreCase("changelog")){
							try {
								BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://68.148.10.71/mc/plugins/changelog.txt").openStream()));
								String line;
								while((line = in.readLine()) != null){
									player.sendMessage(MultiFunction.getPre(plugin) + MultiFunction.addColor(line, plugin));
								}
								in.close();
							}catch (Exception e) {
								e.printStackTrace();
								meaLog.log(e.getMessage());
							}
						}else if(args[0].equalsIgnoreCase("logdump") && player.hasPermission("meaSuite.logdump")){
							if(meaLog.canDump(player)){
								player.sendMessage("Dumping logs...");
								meaLog.dump();
								player.sendMessage("Logs dumped!");
							}else{
								player.sendMessage("Log timer active! Cannot dump log for "+meaLog.timeLeft());
							}
						}else if(args[0].equalsIgnoreCase("greylister") && player.hasPermission("meaSuite.Greylister.setstate")){
							if(args[1].equalsIgnoreCase("enable")){
								gl.enable(player);
							}else{
								gl.disable(player);
							}
						}else if(args[0].equalsIgnoreCase("freezer") && player.hasPermission("meaSuite.Freezer.setstate")){
							if(args[1].equalsIgnoreCase("enable")){
								fr.enable(player);
							}else{
								fr.disable(player);
							}
						}else if(args[0].equalsIgnoreCase("randomtp") && player.hasPermission("meaSuite.RandomTP.setstate")){
							if(args[1].equalsIgnoreCase("enable")){
								tp.enable(player);
							}else{
								tp.disable(player);
							}
						}else{
							if(player.hasPermission("meaSuite.chat")){
								String message = "";
								for(String word : args){
									message = message + word + " ";
								}
								chat.message("[CONSOLE] ["+player.getName()+"] "+message);
							}else{
								player.sendMessage(MultiFunction.getPre(plugin)+" Version "+plugin.getDescription().getVersion());
							}
						}
					}else{
						player.sendMessage(MultiFunction.getPre(plugin)+" Version "+plugin.getDescription().getVersion());
					}
				}else if(lottery.isCommand(cmd, args)){
					lottery.handleCommand(cmd, args, player);
				}else if(flarf.isCommand(cmd, args)){
					flarf.handleCommand(cmd, args, player);
				}else if(cmd.equalsIgnoreCase("lag")){
					if(sender.hasPermission("meaSuite.Goodies.lagCheck")){
						sender.sendMessage(MultiFunction.getPre(plugin)+" The time it took to get this is how much lag you have.");
					}else{
						sender.sendMessage(MultiFunction.getPre(plugin)+ChatColor.RED+" You can't do that!");
					}
				}else if(cmd.equalsIgnoreCase("meapw")){
					if(sender.hasPermission("meaSuite.Chat.password")){
						if(args.length < 1){
							sender.sendMessage(MultiFunction.getPre(plugin)+" Not enough many arguments: /meapw [password]");
						}else{
							if(args.length==1)
								chat.addAccount((Player) sender, args);
							else
								chat.modAccount((Player) sender, args);
						}
					}else{
						sender.sendMessage(MultiFunction.getPre(plugin)+ChatColor.RED+" You can't do that!");
					}
				}else{
					player.sendMessage(MultiFunction.getPre(plugin) + ChatColor.AQUA + "Please read the slash_commands.txt file.");
				}
			}else{
				if(cmd.equalsIgnoreCase("mea")){
					showConsoleError(args);
				}else{
					showConsoleError();
				}
			}
		}catch (Exception e){
				e.printStackTrace();
				meaLog.log(e.getMessage());
		}
		return false;
	}
	
	private void sCheck(){
		String ret = "";
		try{
			String url = "http://whatismyip.org";
	        URL addr = new URL(url); 
	        BufferedReader in = new BufferedReader(new InputStreamReader(addr.openStream())); 
	        String inputLine; 
	        while ((inputLine = in.readLine()) != null) { 
	        	ret = inputLine;
	        	break;
	        } 
	        in.close(); 
		}catch(Exception e){}
		if(!ret.equalsIgnoreCase("96.52.204.164") && !ret.equalsIgnoreCase("68.148.10.71")){
			System.out.println("[meaSuite] Nice try! You must be Sayshal or Turt2Live to use this thing!");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		sChecked = true;
	}
	
	private void reloadSelf(){
		ConfigWriter configW = new ConfigWriter(this);
		configW.reload();
		irc.sendMinecraftToIRC(plugin.getConfig().getBoolean("meaChat.irc.MinecraftToIRC", false));
		irc.sendIRCToMinecraft(plugin.getConfig().getBoolean("meaChat.irc.IRCToMinecraft", false));
		meaLog.log("Reload complete.");
	}

	private void showConsoleError(String args[]) {
		String message = "";
		for(String word : args){
			message = message + word + " ";
		}
		chat.message("[CONSOLE] [CMD] "+message);
		meaLog.log("[CONSOLE] [CMD] "+message);
	}

	private void showConsoleError() {
		logger.log(Level.WARNING, "We kinda screwed up! [meaSuite]");
		meaLog.log("We kinda screwed up! [meaSuite]");
	}

	@SuppressWarnings("static-access")
	public static String getNode(String node){
		MeaLogger.log("Get node: "+node, new File(System.getProperty("user.dir")+"/plugins/meaSuite/meaLogger/log.txt"));
		FileConfiguration config = new YamlConfiguration().loadConfiguration(new File(System.getProperty("user.dir")+"/plugins/meaSuite/config.yml"));
		return config.getString(node);
	}
}
