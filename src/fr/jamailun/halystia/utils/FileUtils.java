package fr.jamailun.halystia.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import fr.jamailun.halystia.HalystiaRPG;

public final class FileUtils {
	private FileUtils() {}
	
	public static boolean copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while( (len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			in.close();
			return true;
		} catch (Exception e) {
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage("§c---------------------------------------------------");
			console.sendMessage("§c FileUtils : EXCEPTION");
			console.sendMessage("§c  Fichier : " + file);
			console.sendMessage("§c  Ressource: " + in);
			console.sendMessage("§c---------------------------------------------------");
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean exportResource(String ymlFileName) {
		InputStream in = HalystiaRPG.getInstance().getResource(ymlFileName+".yml");
		File file = new File(HalystiaRPG.PATH + "/"+ymlFileName+".yml");
		if(file.exists())
			file.delete();
		return copy(in, file);
	}
	
	public static boolean exportQuest(String questName) {
		InputStream in = HalystiaRPG.getInstance().getResource("quests/quest-" + questName + ".yml");
		return copy(in, getTheoricalFileForQuest(questName));
	}
	
	public static boolean questExists(String questName) {
		return (getTheoricalFileForQuest(questName).exists());
	}
	
	public static File getTheoricalFileForQuest(String questName) {
		return new File(HalystiaRPG.PATH + "/quests/" + questName + ".yml");
	}
	
}
