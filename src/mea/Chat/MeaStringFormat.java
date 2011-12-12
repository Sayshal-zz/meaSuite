package mea.Chat;

import mea.plugin.MultiFunction;

import org.bukkit.plugin.java.JavaPlugin;

public class MeaStringFormat {
	
	private JavaPlugin plugin;
	
	public MeaStringFormat(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public String mergeRank(String rank, String format){
		return format.replaceAll("\\^R", rank);
	}
	
	public String addColor(String message){
		return MultiFunction.addColor(message, plugin);
	}
	
	public String forceMeaSuiteMessage(String message){
		return MultiFunction.getPre(plugin)+" "+message;
	}
	
	private String getFormat(String formatName){
		return plugin.getConfig().getString("meaChat.formats."+formatName);
	}
	
	public String formatPM(String to, String from, String message, String source){
		if(source.equalsIgnoreCase("mea")){
			String format = getFormat("meaPM");
			format = format.replaceAll("\\^T", "mea").replaceAll("\\^P", from).replaceAll("\\^M", message);
			return format;
		}
		return null;
	}
	
	public String formatPMFrom(String to, String from, String message, String source, String destination){
		if(source.equalsIgnoreCase("mea")){
			String format = getFormat("meaPM");
			format = format.replaceAll("\\^T", "mea").replaceAll("\\^P", from).replaceAll("\\^M", message);
			format = "TO: "+destination+" >> "+to+" | "+format;
			return format;
		}
		return null;
	}
	
	public String onJoin(String format, String whoJoined, String message, String source, boolean makeItPretty){
		String ret = format;
		ret = format.replaceAll("\\^T", source).replaceAll("\\^P", whoJoined).replaceAll("\\^M", message);
		if(makeItPretty){
			return addColor(ret);
		}
		return ret;
	}

	public String onLeave(String format, String whoLeft, String message, String source, boolean makeItPretty) {
		String ret = format;
		ret = format.replaceAll("\\^T", source).replaceAll("\\^P", whoLeft).replaceAll("\\^M", message);
		if(makeItPretty){
			return addColor(ret);
		}
		return null;
	}

	public String onMessage(String format, String player, String message, String source, boolean makeItPretty) {
		String ret = format;
		ret = format.replaceAll("\\^T", source).replaceAll("\\^P", player).replaceAll("\\^M", message);
		if(makeItPretty){
			return addColor(ret);
		}
		return null;
	}
}
