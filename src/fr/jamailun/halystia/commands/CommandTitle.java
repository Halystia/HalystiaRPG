package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guis.MainTitlesGUI;

public class CommandTitle extends HalystiaCommand {
	
	public CommandTitle(HalystiaRPG main) {
		super(main, "titles");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		
		new MainTitlesGUI(p);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
}