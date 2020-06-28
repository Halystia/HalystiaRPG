package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLACK;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandPing extends HalystiaCommand {
	
	public CommandPing(HalystiaRPG main) {
		super(main, "ping");
	}

	private final static String BASE_MSG = GRAY + "Ping : ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "You have to be a Player !");
			return true;
		}
		
		Player p = (Player) sender;
		
		int ping = getPing(p);
		String s = "";
		if(ping == -1) {
			s = DARK_RED + "Erreur.";
			return true;
		}
		
		if(ping < 5)
			s = BASE_MSG + GREEN + "" + BOLD + ping + GRAY + " (Ouais bon tu joues en local quoi xd)";
		else if(ping < 30)
			s = BASE_MSG + GREEN + "" + BOLD + ping + GRAY + " (Ping excellent !)";
		else if(ping < 60)
			s = BASE_MSG + BLUE + "" + BOLD + ping + GRAY + " (Ping bon)";
		else if(ping < 90)
			s = BASE_MSG + YELLOW + "" + BOLD + ping + GRAY + " (Ping moyen)";
		else if(ping < 150)
			s = BASE_MSG + RED + "" + BOLD + ping + GRAY + " (Ping très moyen !)";
		else if(ping < 300)
			s = BASE_MSG + DARK_RED + "" + BOLD + ping + GRAY + " (Ping nul: tu ne peux pas jouer...)";
		else if(ping < 500)
			s = BASE_MSG + DARK_GRAY + "" + BOLD + ping + GRAY + " (Ping ATROCEMENT mauvais !)";
		else
			s = BASE_MSG + BLACK + "" + BOLD + ping + GRAY + " (Va t'acheter une co bro.)";
		p.sendMessage(GRAY + "-------------------PING--------------------");
		p.sendMessage(DARK_GRAY + "Pong !   " + s);
		p.sendMessage(GRAY + "-------------------------------------------");
		
		return true;
	}
	
	public int getPing(Player p) {
		try{
			Object obj = getCraftBukkitClass("entity.CraftPlayer").cast(p);
			obj = obj.getClass().getDeclaredMethod("getHandle").invoke(obj);
			return obj.getClass().getDeclaredField("ping").getInt(obj);
		} catch(NoSuchMethodException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchFieldException  e){
			e.printStackTrace();
		}
		return -1;
	}
	
	private Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
		String base = Bukkit.getServer().getClass().getPackage().getName();
		return Class.forName(base + "." + name);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
	
}