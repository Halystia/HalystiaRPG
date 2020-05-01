package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandPurge extends HalystiaCommand {
	
	public CommandPurge(HalystiaRPG main) {
		super(main, "purge");
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
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
}