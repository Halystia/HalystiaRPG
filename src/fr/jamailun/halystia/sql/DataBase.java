package fr.jamailun.halystia.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.ChatColor;

import fr.jamailun.halystia.HalystiaRPG;

public abstract class DataBase {

	private String url ;
	private String user ;
	private String password;
	protected final boolean debug;

	protected HalystiaRPG api;

	protected static Connection conn;

	public DataBase(HalystiaRPG api, String server, String dataBase, String user, String password, boolean debug) {
		this.api = api;
		this.debug = debug;
		enableDataBase(server, dataBase, user, password);
	}

	public void enableDataBase(String server, String dataBase, String user, String password) {
		this.url = "jdbc://" + server + "/" + dataBase;
		this.user = user;
		this.password = password;
		connect();
	}

	protected void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {e1.printStackTrace();}

		try {
			api.getConsole().sendMessage(ChatColor.YELLOW + "[MySQL] connect : url = [" + ChatColor.BLUE + url + ChatColor.YELLOW + "]");
			api.getConsole().sendMessage(ChatColor.YELLOW + "[MySQL] connect : user = [" + ChatColor.BLUE  + user + ChatColor.YELLOW + "]");
			api.getConsole().sendMessage(ChatColor.YELLOW + "[MySQL] connect : password = [" + ChatColor.BLUE  + password + ChatColor.YELLOW + "]");
			api.getConsole().sendMessage(ChatColor.YELLOW + "[MySQL] connect > Connecting...");
			conn = DriverManager.getConnection(url, user, password);
			api.getConsole().sendMessage(ChatColor.GREEN + "[MySQL] connect > Successful connection.");
		} catch (SQLException e) {
			api.getConsole().sendMessage(ChatColor.DARK_RED + "[MySQL] connect > Connection attempt failed.");
			if(debug)
				e.printStackTrace();
		}
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				api.getConsole().sendMessage(ChatColor.YELLOW + "[MySQL] disconnect > Closing the connection.");
				conn.close();
				api.getConsole().sendMessage(ChatColor.GREEN + "[MySQL] disconnect > Successful disconnection.");
			} catch (SQLException e) {
				if(debug)
					e.printStackTrace();
				api.getConsole().sendMessage(ChatColor.DARK_RED + "[MySQL] disconnect > Error during the disconnection.");
			}
		}
	}

	public boolean isConnected() {
		try {
			if(conn == null || conn.isClosed())
				return false;
			else
				return true;
		} catch (SQLException e) {
			if(debug)
				e.printStackTrace();
		}
		return false;
	}

	public Connection getConnection() {
		return conn;
	}

	protected String getBddName() {
		return user;
	}
}
