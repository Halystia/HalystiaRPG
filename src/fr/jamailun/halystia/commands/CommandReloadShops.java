package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandReloadShops implements CommandExecutor {
	
	private final HalystiaRPG main;
	
	public CommandReloadShops(HalystiaRPG main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		main.getShopManager().reloadAll();
		
		sender.sendMessage(HalystiaRPG.PREFIX + GREEN + "Tous les shops ont été reload avec succès !");
		
		return true;
	}
	
}
