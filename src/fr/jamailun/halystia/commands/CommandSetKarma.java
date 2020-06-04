package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;

public class CommandSetKarma extends HalystiaCommand {
	
	public CommandSetKarma(HalystiaRPG main) {
		super(main, "set-karma");
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
			sender.sendMessage(HalystiaRPG.PREFIX + GREEN + "(" + YELLOW + args[0] + GREEN + ") a " + data.getNiceKarma() + GREEN + " pts de karma.");
			return true;
		}
		
		int karma = -1;
		try {
			karma = Integer.parseInt(args[1]);
		} catch (IllegalArgumentException e) {
			sender.sendMessage(HalystiaRPG.PREFIX + RED + "Nombre incorrect : " + DARK_RED + args[1] + RED + ".");
			return true;
		}
		
		data.forceKarma(karma);

		sender.sendMessage(HalystiaRPG.PREFIX + GREEN+"Succès ! Le joueur " + YELLOW + args[0] + GREEN + " a désormais "+data.getNiceKarma()+GREEN+" pts de karma.");
		cible.sendMessage(HalystiaRPG.PREFIX + GREEN+"Un administrateur a fixé votre karma a "+data.getNiceKarma()+GREEN+" points.");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
}
