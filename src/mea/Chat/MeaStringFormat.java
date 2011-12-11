package mea.Chat;

import mea.plugin.MultiFunction;

import org.bukkit.plugin.java.JavaPlugin;

public class MeaStringFormat {
	
	@SuppressWarnings("unused")
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
	
	public String onJoin(String format, String whoJoined, String message, String source, boolean makeItPretty){
		String ret = format;
		ret = format.replaceAll("\\^T", source).replaceAll("\\^P", whoJoined).replaceAll("\\^M", message);
		if(makeItPretty){
			return addColor(ret);
		}
		return ret;
	}
}
