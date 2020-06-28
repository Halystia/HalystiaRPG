package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.custom.fishing.Canne;

public class CommandGiveCanne extends HalystiaCommand {

	public CommandGiveCanne(HalystiaRPG main) {
		super(main, "give-canne");
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
		
		if(args.length == 0) {
			p.sendMessage("/give-canne <nom>");
			return true;
		}
		
		Canne canne;
		try {
			canne = Canne.valueOf(args[0]);
		} catch (IllegalArgumentException e) {
			p.sendMessage(RED + "Nom incorrect.");
			return true;
		}
		
		p.getInventory().addItem(canne.generate());
		p.sendMessage(GREEN + "Succès.");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return Arrays.asList(Canne.values()).stream().map(c -> c.toString()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}

}
