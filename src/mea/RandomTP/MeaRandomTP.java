package mea.RandomTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import mea.plugin.MultiFunction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MeaRandomTP {
	
	private JavaPlugin plugin;
	private int MIN_X_DISTANCE = 250;
	private int MIN_Y_DISTANCE = 250;
	private int MAX_X_DISTANCE = 2500;
	private int MAX_Y_DISTANCE = 2500;
	public static Player player_public = null;
	
	public MeaRandomTP(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	private boolean isEnabled(){
		return plugin.getConfig().getString("meaRandomTP.enabled").equalsIgnoreCase("true");
	}
	
	public void tp(Player player){
		if(isEnabled()){
			if(player.hasPermission("meaSuite.RandomTP.use")){
				Runnable run = new Runnable(){
					public void run() {
						Player player = player_public;
						World world = player.getWorld();
						Random trand = new Random(System.currentTimeMillis());
						Random rand = new Random((trand.nextInt()*1000)*System.currentTimeMillis());
						Block block = null;
						int ticks = 0;
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter(new File(plugin.getDataFolder()+"/debug.log"), false));
							while(block == null){
								double x = 0.0;
								double y = 0.0;
								MIN_X_DISTANCE = Integer.parseInt(getNode("minX").replaceAll("\\'", ""));
								MIN_Y_DISTANCE = Integer.parseInt(getNode("minY").replaceAll("\\'", ""));
								MAX_X_DISTANCE = Integer.parseInt(getNode("maxX").replaceAll("\\'", ""));
								MAX_Y_DISTANCE = Integer.parseInt(getNode("maxY").replaceAll("\\'", ""));
								x = rand.nextInt(MAX_X_DISTANCE-MIN_X_DISTANCE)+MIN_X_DISTANCE;
								y = rand.nextInt(MAX_Y_DISTANCE-MIN_Y_DISTANCE)+MIN_Y_DISTANCE;
								//out.write(" MX: "+MAX_X_DISTANCE+" MY: "+MAX_Y_DISTANCE+" MiX: "+MIN_X_DISTANCE+" MiY: "+MIN_Y_DISTANCE+" X: "+x+" Y: "+y+"\r\n");
								if(rand.nextInt(100)>=50){
									x = x * -1;
								}
								if(rand.nextInt(100)<=50){
									y = y * -1;
								}
								Location suggested = world.getSpawnLocation();
								suggested.setX(suggested.getX()+x);
								suggested.setZ(suggested.getZ()+y);
								//out.write("LOC: "+suggested.toString()+"\r\n");
								world.loadChunk(suggested.getBlock().getChunk());
								block = world.getBlockAt(new Location(world, suggested.getX(), world.getHighestBlockYAt(suggested), suggested.getZ()));
								//out.write("BLOCK: "+block.toString()+" || "+block.getType().name()+"\r\n");
								if(block != null){
									if(block.getType() == Material.AIR || 
											block.getType() == Material.BEDROCK ||
											block.getType() == Material.WATER ||
											block.getType() == Material.LAVA ||
											block.getType() == Material.WATER_LILY ||
											block.getType() == Material.CACTUS ||
											block.getType() == Material.LEAVES){
										block = null;
									}
								}
								ticks ++;
								if(ticks >= 200){
									block = null;
								}
								if(ticks >= 50 && block != null){
									block = player.getTargetBlock(null, 100);
									if(block != null){
										if(block.getType() == Material.AIR || 
												block.getType() == Material.BEDROCK ||
												block.getType() == Material.WATER ||
												block.getType() == Material.LAVA ||
												block.getType() == Material.WATER_LILY ||
												block.getType() == Material.CACTUS ||
												block.getType() == Material.LEAVES){
											block = null;
										}
									}
									if(block == null){
										Location location = player.getLocation();
										block = world.getHighestBlockAt(location);
									}
									System.out.println("[meaRandomTP] ***** Could not find block after "+ticks+" ticks! *****");
									player.sendMessage(MultiFunction.getPre(plugin)+" "+ChatColor.DARK_RED+"Took too long to TP. Try again!");
									break;
								}
							}
							Location location = block.getLocation();
							location.setY(location.getY()+3);
							player.teleport(location);
							//System.out.println("[meaRandomTP] Location: "+location.toString());
							//System.out.println("[meaRandomTP] Block: "+block.getType().name());
							if(!getNode("onTP").equalsIgnoreCase("nomsg")){
								player.sendMessage(MultiFunction.getPre(plugin)+" "+getNode("onTP"));
							}
							out.close();
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				};
				player_public = player;
				Thread thread = new Thread(run);
				thread.start();
			}else{
				if(!getNode("noPerms").equalsIgnoreCase("nomsg")){
					player.sendMessage(MultiFunction.getPre(plugin)+" "+getNode("noPerms"));
				}
			}
		}else{
			if(!getNode("onDisabledError").equalsIgnoreCase("nomsg")){
				player.sendMessage(MultiFunction.getPre(plugin)+" "+getNode("onDisabledError"));
			}
		}
	}

	private String getNode(String message) {
		return MultiFunction.addColor(plugin.getConfig().getString("meaRandomTP."+message), plugin);
	}
	
	public void enable(Player player){
		if(!getNode("onEnable").equalsIgnoreCase("nomsg")){
			player.sendMessage(getNode("onEnable"));
		}
		plugin.getConfig().set("meaRandomTP.enabled", "true");
	}
	
	public void disable(Player player){
		if(!getNode("onDisable").equalsIgnoreCase("nomsg")){
			player.sendMessage(getNode("onDisable"));
		}
		plugin.getConfig().set("meaRandomTP.enabled", "false");
	}
}
