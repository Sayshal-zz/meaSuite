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
import java.util.Vector;

import mea.Chat.MeaChat;
import mea.Economy.api.MeaEconomyAPI;
import mea.Freezer.MeaFreezer;
import mea.Goodies.MeaGoodies;
import mea.Hook.MeaHook;
import mea.Greylister.MeaGreylister;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPlayerListener extends PlayerListener{

	private JavaPlugin plugin;
	private int CAPS_BREAK = 3;
	private MeaChat chat;
	private MeaHook hook;
	
	public ServerPlayerListener(JavaPlugin loader, MeaChat chat, MeaHook hook) {
		this.plugin = loader;
		this.chat = chat;
		this.hook = hook;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		if(plugin.isEnabled()){
			hook.onJoin(event.getPlayer().getName(), "MC");
			//Removed : MeaHook now handles this
			//chat.message("[MC] ["+event.getPlayer().getName()+"] * Joined Minecraft");
			MeaGreylister gl = new MeaGreylister(plugin);
			if(event.getPlayer().hasPermission("meagl.apply")){
				if(!event.getPlayer().hasPermission("meagl.exempt")){
					if(!new File(plugin.getDataFolder()+"/applications/"+event.getPlayer().getName()+".yml").exists()){
						event.getPlayer().sendMessage(gl.convertVariables(gl.getMessage("onGuestJoin"), event.getPlayer(), "apply"));
					}else{
						event.getPlayer().sendMessage(gl.convertVariables(gl.getMessage("onApplicantJoin"), event.getPlayer(), "apply"));
					}
				}
			}
			MeaFreezer fr = new MeaFreezer(plugin);
			if(fr.isFrozen(event.getPlayer())){
				event.getPlayer().sendMessage(fr.convertVariables(fr.getNode("messages.onFrozenLogin"), event.getPlayer(), "freeze"));
			}
			MeaEconomyAPI economy = new MeaEconomyAPI(plugin);
			economy.onLogin(event.getPlayer());
		}
	}

	public void onPlayerMove(PlayerMoveEvent event){
		if(plugin.isEnabled()){
			MeaFreezer fr = new MeaFreezer(plugin);
			fr.refresh(event.getPlayer());
			if(fr.isFrozen(event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().teleport(event.getFrom());
			}
		}
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		if(plugin.isEnabled()){
			chat.message("[MC] ["+event.getPlayer().getName()+"] * Left Minecraft");
		}
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		if(plugin.isEnabled()){
			chat.message("[MC] ["+event.getPlayer().getName()+"] * Was Kicked ("+event.getReason()+")");
		}
	}
	
	public void onPlayerChat(PlayerChatEvent event){
		if(plugin.isEnabled()){
			chat.message("[MC] ["+event.getPlayer().getName()+"] "+event.getMessage());
			MeaGoodies goodies = new MeaGoodies(plugin);
			if(event.getPlayer().getName().equalsIgnoreCase("turt2live")){
				event.setMessage(ChatColor.GOLD+event.getMessage());
			}
			/*if(event.getPlayer().getName().equalsIgnoreCase("sayshal")){
				event.setMessage(ChatColor.AQUA+event.getMessage());
			}*/
			if(event.getMessage().toLowerCase().startsWith("#suggest") && event.getPlayer().hasPermission("meaSuite.Goodies.canSuggest")){
				String line = "";
				String parts[] = event.getMessage().toLowerCase().split("#suggest ");
				for(int i=1;i<parts.length;i++){
					line = line + parts[i];
				}
				goodies.suggestion(line, event.getPlayer());
				plugin.reloadConfig();
				event.setCancelled(event.getPlayer().hasPermission("meaSuite.Goodies.noShowSuggest"));
			}else{
				if(goodies.setToLowerCase() && !event.getPlayer().hasPermission("meaSuite.noCapsExempt")){
					//String messageBefore = event.getMessage();
					String parts[] = event.getMessage().split(" ");
					Vector<String> newMessage = new Vector<String>();
					int caps = 0;
					for(String word : parts){
						if(word.length()>2){
							String wordParts[] = word.split("");
							for(String character : wordParts){
								if(character.equals(character.toUpperCase())){
									if(MultiFunction.isLetter(character)){
										caps++;
									}
								}
								if(caps>=CAPS_BREAK+1){
									break;
								}
							}
						}
						newMessage.add(word);
					}
					String message = "";
					for(String word : newMessage){
						message = message.concat(word+" ");
					}
					if(caps>=CAPS_BREAK+1){
						message = message.toLowerCase();
						String moreParts[] = message.split("");
						moreParts[0] = moreParts[0].toUpperCase();
						message = "";
						for(String part : moreParts){
							message = message.concat(part);
						}
					}
					if(caps>=CAPS_BREAK+1){
						message = message.concat(MultiFunction.addColor(plugin.getConfig().getString("meaGoodies.allCapsSuffix"), plugin));
					}
					//message = message.replaceAll("(?i)turt", "Turt2Live");
					//message = message.replaceAll("(?i)travis", "[Turt2Live is EPIC]");
					//message = message.replaceAll("(?i)sayshal", "[Sayshal is EPIC]");
					//message = message.replaceAll("(?i)tyler", "[Sayshal is EPIC]");
					event.setMessage(message);
				}
			}
		}
	}
}
