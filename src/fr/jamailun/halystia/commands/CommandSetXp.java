package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;

public class CommandSetXp extends HalystiaCommand {
	
	public CommandSetXp(HalystiaRPG main) {
		super(main, "set-xp");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		
		if(args.length < 1) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" <joueur> [exp]");
			return true;
		}
		
		Player cible = Bukkit.getPlayerExact(args[0]);
		if(cible == null) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "Joueur inconnu ou non connecté : (" + DARK_RED + args[0] + RED + ").");
			return true;
		}
		
		PlayerData data = main.getClasseManager().getPlayerData(cible);
		if(data == null) {
			sender.sendMessage(HalystiaRPG.PREFIX + DARK_RED + "Une erreur interne est survenue. Réessayez.");
			return true;
		}
		
		if(args.length == 1) {
			sender.sendMessage(HalystiaRPG.PREFIX + GREEN + "(" + YELLOW + args[0] + GREEN + " est niveau " + GOLD + data.getLevel() + GREEN + ", et possède " + YELLOW + data.getExpAmount() + GREEN + "xp.");
			return true;
		}
		
		int xp = -1;
		try {
			if(args[1].toUpperCase().endsWith("L")) {
				String levelS = args[1].toUpperCase().replace("L", "");
				int level = Integer.parseInt(levelS);
				if(level > PlayerData.LEVEL_MAX)
					level = PlayerData.LEVEL_MAX;
				if(level < 0)
					level = 0;
				xp = data.getExpForLevel(level);
			} else {
				xp = Integer.parseInt(args[1]);
			}
		} catch (IllegalArgumentException e) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "Nombre incorrect : " + DARK_RED + args[1] + RED + ".");
			return true;
		}
		
		if(xp < 0) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED+"L'xp ne peut pas être négatif !");
			return true;
		}
		
		data.forceXp(xp);
		
		sender.sendMessage(HalystiaRPG.PREFIX + GREEN+"Succès ! Le joueur " + YELLOW + args[0] + GREEN + " est désormais niveau "+GOLD+data.getLevel()+GREEN+".");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
}
