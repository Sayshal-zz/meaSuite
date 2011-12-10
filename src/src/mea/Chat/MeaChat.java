package mea.Chat;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import mea.Logger.MeaLogger;
import mea.SQL.MeaSQL;
import mea.plugin.MultiFunction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.jibble.pircbot.Colors;

@SuppressWarnings("deprecation")
public class MeaChat {
	
	private JavaPlugin plugin;
	private int port = 4408;
	MeaServerSocket socket;
	
	public MeaChat(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public void startup(MeaIRC irc){
		socket = new MeaServerSocket(plugin, this, irc);
		socket.startServer(port, "meaSuite Chat (mea.sayshal.com)");
		System.out.println("Chat loaded!");
		MeaSQL sql = new MeaSQL(plugin);
		sql.query("CREATE TABLE IF NOT EXISTS `chat_users` (`mc_username` text NOT NULL, `password` text NOT NULL);");
	}
	
	public void initIRC(MeaIRC irc){
		socket.setIRC(irc);
	}

	public boolean authenticated(String username, String password){
		if(socket.isLoggedIn(username)){
			return false;
		}
		password = MultiFunction.encodePassword(password);
		MeaSQL sql = new MeaSQL(plugin);
		ResultSet results = sql.query("SELECT * FROM `chat_users` WHERE `mc_username`='"+username+"' AND `password`='"+password+"'");
		int rows = 1; //reset to 0
		try {
			while(results.next()){
				rows++;
			}
		} catch (SQLException e) {
			return false;
		}
		if(rows==1){
			return true;
		}else{
			return false;
		}
	}
	
	public void addAccount(Player player, String args[]){
		String password = args[0];
		password = MultiFunction.encodePassword(password);
		password = MultiFunction.encodePassword(password); //Double encoded because of SHA1 x-fer from client
		MeaSQL sql = new MeaSQL(plugin);
		ResultSet results = sql.query("SELECT * FROM `chat_users` WHERE `mc_username`='"+player.getName()+"'");
		int rows = 0;
		try {
			while(results.next()){
				rows++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rows>=1){
			sql.query("INSERT INTO `chat_users` (`mc_username`, `password`) VALUES ('"+player.getName()+"', '"+password+"');");
			player.sendMessage(MultiFunction.getPre(plugin)+" Password changed.");
		}else{
			sql.query("INSERT INTO `chat_users` (`mc_username`, `password`) VALUES ('"+player.getName()+"', '"+password+"');");
			player.sendMessage(MultiFunction.getPre(plugin)+" Password set.");
		}
	}
	
	public void message(String message){
		message = Colors.removeFormatting(message);
		message = Colors.removeColors(message);
		socket.broadcast(message);
		MeaLogger.log(message, new File(plugin.getDataFolder()+"/meaChat/log.txt"));
	}
	
	public void kill(){
		socket.kill();
	}
	
	public String getIRCNode(String node){
		Configuration config = plugin.getConfiguration();
		config.load();
		return config.getString("meaChat.irc."+node);
	}
	
	public void disable(){
		Bukkit.getPluginManager().disablePlugin(plugin);
	}
	
	public void toIRC(String message){
		socket.toIRC(message);
	}
}