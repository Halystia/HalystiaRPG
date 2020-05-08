package fr.jamailun.halystia.storage.common;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import fr.jamailun.halystia.HalystiaRPG;

public abstract class Database {
	protected HalystiaRPG plugin;
	protected Connection connection;
	protected final String dbName;
	// The name of the table we created back in SQLite class.
	public Database(HalystiaRPG instance, String dbName){
		plugin = instance;
		this.dbName = dbName;
	}

	public Connection getSQLConnection() {
		File dataFolder = new File(plugin.getDataFolder(), dbName+".db");
		if (!dataFolder.exists()){
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: "+dbName+".db");
			}
		}
		try {
			if(connection!=null&&!connection.isClosed()){
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return connection;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}
		return null;
	}

	public abstract void load();
	
	public void close(PreparedStatement ps,ResultSet rs){
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			SqlError.close(plugin, ex);
		}
	}
}