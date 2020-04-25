package fr.jamailun.halystia.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Used to stock data in yaml format.
 * @author jamailun
 */
public abstract class FileDataRPG {
	
	protected final File file;
	protected FileConfiguration config;
	
	public FileDataRPG(String path, String name) {
		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs();
		file = new File(path + "/" + name + ".yml");
		if(!file.exists())
			try {file.createNewFile();} catch (IOException e) {e.printStackTrace();}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	protected final void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected final void delete() {
		config = null;
		file.delete();
	}
}
