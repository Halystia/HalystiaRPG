package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandAmes extends HalystiaCommand {

	public CommandAmes(HalystiaRPG main) {
		super(main, "full-ames");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ( args.length > 0 ) {
			Player cible = Bukkit.getPlayerExact(args[0]);
			if(cible == null) {
				sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Joueur ivalide : '"+args[0]+"'.");
				return true;
			}
			fullSouls(cible);
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Les âmes de "+args[0]+" ont été reset.");
			cible.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Tes âmes ont été reset par "+sender.getName()+".");
			return true;
		}
		if( ! (sender instanceof Player)) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Tu n'es pas un joueur !");
			return true;
		}
		fullSouls((Player)sender);
		sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Tes âmes ont été reset.");
		return true;
	}

	private void fullSouls(Player cible) {
		int todo = 3 - main.getDataBase().getHowManySouls(cible);
		for(int i = 1; i<= todo; i++)
			main.getDataBase().refreshSoul(cible);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
			if(args.length <= 1)
				return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}

}