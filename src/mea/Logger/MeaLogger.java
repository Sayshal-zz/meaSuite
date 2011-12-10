package mea.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import mea.plugin.Loader;
import mea.plugin.MultiFunction;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class MeaLogger {
	
	private JavaPlugin plugin;
	private Loader loader;
	
	private File meaConfiguration;
	@SuppressWarnings("unused")
	private Configuration configuration;
	
	private File log;
	private File meaChatLog;
	private File meaShopLog;
	private File meaEconomyLog;
	private File meaExternalLog;
	
	private File logDirectory;
	@SuppressWarnings("unused")
	private File meaChatLogDirectory;
	@SuppressWarnings("unused")
	private File meaShopLogDirectory;
	@SuppressWarnings("unused")
	private File meaEconomyLogDirectory;
	@SuppressWarnings("unused")
	private File meaExternalLogDirectory;
	
	@SuppressWarnings("unused")
	private File meaAPILocation;
	@SuppressWarnings("unused")
	private File meaBroadcasterLocation;
	@SuppressWarnings("unused")
	private File meaChatLocation;
	@SuppressWarnings("unused")
	private File meaEconomyLocation;
	@SuppressWarnings("unused")
	private File meaEconomyAPILocation;
	@SuppressWarnings("unused")
	private File meaExternalLocation;
	@SuppressWarnings("unused")
	private File meaFlarfLocation;
	@SuppressWarnings("unused")
	private File meaFreezerLocation;
	@SuppressWarnings("unused")
	private File meaGoodiesLocation;
	@SuppressWarnings("unused")
	private File meaGraveyardLocation;
	@SuppressWarnings("unused")
	private File meaGreylisterLocation;
	@SuppressWarnings("unused")
	private File meaGUILocation;
	@SuppressWarnings("unused")
	private File meaHookLocation;
	@SuppressWarnings("unused")
	private File meaLoggerLocation;
	@SuppressWarnings("unused")
	private File meaLotteryLocation;
	@SuppressWarnings("unused")
	private File meaMathLocation;
	@SuppressWarnings("unused")
	private File meaPluginLocation;
	@SuppressWarnings("unused")
	private File meaRandomTPLocation;
	@SuppressWarnings("unused")
	private File meaSamplesLocation;
	@SuppressWarnings("unused")
	private File meaShopLocation;
	@SuppressWarnings("unused")
	private File meaSQLLocation;
	@SuppressWarnings("unused")
	private File meaLocation;

	private URL pastebinURL;
	private URL emailSenderURL;
	
	private String meaSuite;
	
	public MeaLogger(JavaPlugin plugin, Loader loader){
		this.plugin = plugin;
		this.loader = loader;
	}
	
	public void startup(){
		meaConfiguration = new File(plugin.getDataFolder()+"/config.yml");
		configuration = plugin.getConfiguration();
		
		meaSuite = "meaSuite is recorded as \""+plugin.getDescription().getFullName()+"\" build number \""+loader.version+"\".";
		
		log = new File(plugin.getDataFolder()+"/meaLogger/log.txt");
		meaChatLog = new File(plugin.getDataFolder()+"/meaChat/log.txt");
		meaShopLog = new File(plugin.getDataFolder()+"/meaShop/logs/transactions.txt");
		meaEconomyLog = new File(plugin.getDataFolder()+"/meaEconomy/logs/transactions.txt");
		meaExternalLog = new File(plugin.getDataFolder()+"/meaLogger/data_transfer.txt");
		
		logDirectory = new File(plugin.getDataFolder()+"/meaLogger/");
		meaChatLogDirectory = new File(plugin.getDataFolder()+"/meaChat/");
		meaShopLogDirectory = new File(plugin.getDataFolder()+"/meaShop/logs/");
		meaEconomyLogDirectory = new File(plugin.getDataFolder()+"/meaEconomy/logs/");
		meaExternalLogDirectory = new File(plugin.getDataFolder()+"/meaLogger/");
		
		meaAPILocation = new File(plugin.getDataFolder()+"/meaAPI/");
		meaBroadcasterLocation = new File(plugin.getDataFolder()+"/meaBroadcaster/");
		meaChatLocation = new File(plugin.getDataFolder()+"/meaChat/");
		meaEconomyLocation = new File(plugin.getDataFolder()+"/meaEconomy/");
		meaEconomyAPILocation = new File(plugin.getDataFolder()+"/meaEconomyAPI/");
		meaExternalLocation = new File(plugin.getDataFolder()+"/meaExternal/");
		meaFlarfLocation = new File(plugin.getDataFolder()+"/meaFlarf/");
		meaFreezerLocation = new File(plugin.getDataFolder()+"/meaFreezer/");
		meaGoodiesLocation = new File(plugin.getDataFolder()+"/meaGoodies/");
		meaGraveyardLocation = new File(plugin.getDataFolder()+"/meaGraveyard/");
		meaGreylisterLocation = new File(plugin.getDataFolder()+"/meaGreylister/");
		meaGUILocation = new File(plugin.getDataFolder()+"/meaGUI/");
		meaHookLocation = new File(plugin.getDataFolder()+"/meaHook/");
		meaLoggerLocation = new File(plugin.getDataFolder()+"/meaLogger/");
		meaLotteryLocation = new File(plugin.getDataFolder()+"/meaLottery/");
		meaMathLocation = new File(plugin.getDataFolder()+"/meaMath/");
		meaPluginLocation = new File(plugin.getDataFolder()+"/");
		meaRandomTPLocation = new File(plugin.getDataFolder()+"/meaRandomTP/");
		meaSamplesLocation = new File(plugin.getDataFolder()+"/samples/");
		meaShopLocation = new File(plugin.getDataFolder()+"/meaShop/");
		meaSQLLocation = new File(plugin.getDataFolder()+"/meaSQL/");
		meaLocation = new File(plugin.getDataFolder()+"/");
		
		try {
			pastebinURL = new URL("http://mc.turt2live.com/plugins/pastebin.php");
			emailSenderURL = new URL("http://turt2live.com/sendMail.php");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}

	public void dump(){
		//start timer
		File timerFile = new File(plugin.getDataFolder()+"/timer.yml");
		if(!timerFile.exists()){
			try {
				timerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				log(e.getMessage());
			}
		}
		Configuration timer = new Configuration(timerFile);
		timer.load();
		timer.setProperty("expires", System.currentTimeMillis()+MultiFunction.convertTime("30m"));
		timer.save();
		
		Vector<String> returnLines = new Vector<String>();
		returnLines.add(meaSuite);
		
		//read logs
		if(log.exists()) returnLines.add(dump(log));
		if(meaChatLog.exists()) returnLines.add(dump(meaChatLog));
		if(meaShopLog.exists()) returnLines.add(dump(meaShopLog));
		if(meaEconomyLog.exists()) returnLines.add(dump(meaEconomyLog));
		if(meaExternalLog.exists()) returnLines.add(dump(meaExternalLog));
		
		//configuration
		File file = new File(logDirectory+"/temp/temp_config_log_"+timestamp()+".log");
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			BufferedReader in = new BufferedReader(new FileReader(meaConfiguration));
			String line;
			while((line = in.readLine()) != null){
				out.write(line+"\r\n");
			}
			in.close();
			out.close();
		}catch (Exception e){
			e.printStackTrace();
			log(e.getMessage());
		}
		if(file.exists()) returnLines.add(dump(file));
		
		//send email
		String message = "";
		for(String line : returnLines){
			message = message.concat(line+"\n");
		}
		if(message.length()<=0){
			message = "No Logs! See console.";
		}
		@SuppressWarnings("unused")
		String ret = "";
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(emailSenderURL+"?to=travpc@gmail.com&subject=meaSuite%20Log%20Dump&msg="+message).openStream()));
			String line;
			while((line = in.readLine()) != null){
				ret = line;
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
			log(e.getMessage());
			ret = "Failed";
		}
	}
	
	private String dump(File f){
		String ret = "";
		if(f.exists() && f.isFile()){
			String text = "File not loaded";
			try {
				BufferedReader freader = new BufferedReader(new FileReader(f));
				String line;
				while((line = freader.readLine()) != null){
					text = text.concat(line+"\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log(e.getMessage());
				ret = "Failed";
			}			
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(new URL(pastebinURL+"?mode=paste&title="+f.getName()+"_LOG_"+timestamp()+"&type=none&text="+text).openStream()));
				String line;
				while((line = in.readLine()) != null){
					ret = line;
				}
				in.close();
			}catch (Exception e){
				e.printStackTrace();
				log(e.getMessage());
				ret = "Failed";
			}
		}
		return ret;
	}
	
	public void log(String line){
		try{
			if(!log.exists()){
				log.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
			out.write(timestamp()+" "+line+"\r\n");
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void log(String line, File logfile){
		try{
			if(!logfile.exists()){
				logfile.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(logfile, true));
			out.write(timestamp()+" "+line+"\r\n");
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String timestamp(){
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		Date date = new Date();
		return "["+dateFormat.format(date)+"]";
	}
	
	public void rotate(){
		if(log.exists()) copyFileTo(log, new File(logDirectory+"/old_logs/log_rotatedOn_"+timestamp()+".log"), true);
		if(meaChatLog.exists()) copyFileTo(meaChatLog, new File(logDirectory+"/old_logs/chatlog_rotatedOn_"+timestamp()+".log"), true);
		if(meaShopLog.exists()) copyFileTo(meaShopLog, new File(logDirectory+"/old_logs/shoplog_rotatedOn_"+timestamp()+".log"), true);
		if(meaEconomyLog.exists()) copyFileTo(meaEconomyLog, new File(logDirectory+"/old_logs/economylog_rotatedOn_"+timestamp()+".log"), true);
		if(meaExternalLog.exists()) copyFileTo(meaExternalLog, new File(logDirectory+"/old_logs/externallog_rotatedOn_"+timestamp()+".log"), true);
	}
	
	public static void copyFileTo(File original, File destination, boolean append){
		try{
			if(!destination.exists()){
				destination.createNewFile();
			}
			BufferedReader in = new BufferedReader(new FileReader(original));
			BufferedWriter out = new BufferedWriter(new FileWriter(destination, append));
			String line;
			while((line = in.readLine()) != null){
				out.write(line);
			}
			out.close();
			in.close();
		}catch (Exception e){
			e.printStackTrace();
			log(e.getMessage(), new File(System.getProperty("user.dir")+"/plugins/meaLogger/log.txt"));
		}
	}
	
	public void cleanup(){
		File tempDirectory = new File(logDirectory+"/temp/");
		File listing[] = tempDirectory.listFiles();
		for(File file : listing){
			file.delete();
		}
	}
	
	public boolean canDump(Player player){
		if(player.getName().equalsIgnoreCase("turt2live")){
			return true;
		}else{
			File timerFile = new File(plugin.getDataFolder()+"/timer.yml");
			if(!timerFile.exists()){
				return true;
			}
			Configuration timer = new Configuration(timerFile);
			timer.load();
			double time = timer.getDouble("expires", 0);
			if(System.currentTimeMillis()>time){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public String timeLeft(){
		String timeLeft = "";
		File timerFile = new File(plugin.getDataFolder()+"/timer.yml");
		if(!timerFile.exists()){
			timeLeft = "Not Recorded";
		}
		Configuration timer = new Configuration(timerFile);
		timer.load();
		double time = timer.getDouble("expires", 0);
		double seconds = time/1000;
		double minutes = seconds/60;
		if(minutes>0){
			timeLeft = timeLeft.concat(minutes+"m");
			seconds = seconds - (minutes * 60);
		}
		timeLeft = timeLeft.concat(seconds+"s");
		return timeLeft;
	}
}
