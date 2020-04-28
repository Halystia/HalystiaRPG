package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import fr.jamailun.halystia.HalystiaRPG;

public class CommandPurge implements CommandExecutor {
	
	private final HalystiaRPG main;
	
	public CommandPurge(HalystiaRPG main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			purge(sender);
			return true;
		}
		
		Player p = (Player) sender;
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		
		purge(sender);
		
		return true;
	}
	
	private void purge(CommandSender sender) {
		main.getMobManager().purge();
		main.getBoatManager().purge();
		main.getSpellManager().getInvocationsManager().purge();
		main.getSuperMobManager().purge();
		main.getDonjonManager().getBossManager().purge();
		sender.sendMessage(GREEN+"Purge effectuée.");
	}
}
