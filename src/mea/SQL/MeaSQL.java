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
package mea.SQL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.plugin.java.JavaPlugin;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class MeaSQL {

	private JavaPlugin plugin;
	
	public MeaSQL(JavaPlugin plugin){
		this.plugin = plugin;
	}
	
	public Connection getConnection(String sql_host, String sql_port, String sql_username, String sql_password, String sql_database){
		try {
			return (Connection) DriverManager.getConnection("jdbc:mysql://"+sql_host+":"+sql_port+"/"+sql_database, sql_username, sql_password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Statement getStatement(Connection connection){
		try {
			return (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ResultSet query(Statement statement, String query, Connection connection){
		try {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void modifyDataQuery(String query){
		String sql_username = "", sql_password = "", sql_host = "", sql_port = "", sql_database = "";
		sql_username = plugin.getConfig().getString("meaSuite.SQL.username");
		sql_password = plugin.getConfig().getString("meaSuite.SQL.password");
		sql_host = plugin.getConfig().getString("meaSuite.SQL.host");
		sql_database = plugin.getConfig().getString("meaSuite.SQL.database");
		sql_port = plugin.getConfig().getString("meaSuite.SQL.port");
		Connection connection = null;
		Properties properties = new Properties();
		properties.put("user", sql_username);
		properties.put("password", sql_username);
		Statement statement = null;
		try {
			connection = getConnection(sql_host, sql_port, sql_username, sql_password, sql_database);
			statement = getStatement(connection);
			statement.executeUpdate(query);
		} catch (Exception e) {
			if(!e.getMessage().contains("Duplicate entry")){
				e.printStackTrace();
			}
	    } finally {
	    	if(statement != null){
	    		try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public ResultSet query(String query){
		if(query.toLowerCase().startsWith("create") ||
				query.toLowerCase().startsWith("insert") ||
				query.toLowerCase().startsWith("update") ){
			modifyDataQuery(query);
			return null;
		}
		String sql_username = "", sql_password = "", sql_host = "", sql_port = "", sql_database = "";
		sql_username = plugin.getConfig().getString("meaSuite.SQL.username");
		sql_password = plugin.getConfig().getString("meaSuite.SQL.password");
		sql_host = plugin.getConfig().getString("meaSuite.SQL.host");
		sql_database = plugin.getConfig().getString("meaSuite.SQL.database");
		sql_port = plugin.getConfig().getString("meaSuite.SQL.port");
		Connection connection = null;
		Properties properties = new Properties();
		properties.put("user", sql_username);
		properties.put("password", sql_username);
		Statement statement = null;
		try {
			connection = getConnection(sql_host, sql_port, sql_username, sql_password, sql_database);
			statement = getStatement(connection);
			ResultSet results = query(statement, query, connection);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
	    }
		return null;
	}
	
	public void query(File file){
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while((line = in.readLine()) != null){
				query(line.replaceAll("\\\r\\\n", ""));
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
