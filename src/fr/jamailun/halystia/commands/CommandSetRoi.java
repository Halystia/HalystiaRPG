package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.UNDERLINE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.royaumes.Royaume;

public class CommandSetRoi extends HalystiaCommand {
	
	public CommandSetRoi(HalystiaRPG main) {
		super(main, "set-roi");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length < 2) {
			p.sendMessage("/set-roi <royaume> <joueur>");
			return true;
		}
		
		String rStr = args[0];
		Royaume r = Royaume.NEUTRE;
		try {
			r = Royaume.valueOf(rStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			p.sendMessage(RED + "Royaume inconnu : " + DARK_RED + rStr + RED + ".");
			return true;
		}
		
		String plStr = args[1];
		Player cible = Bukkit.getPlayer(plStr);
		if(cible == null) {
			p.sendMessage(RED + "Joueur inconnu : " + DARK_RED + plStr + RED + ".");
			return true;
		}
		
		main.getDataBase().setRoi(r, cible);
		
		for(Player pl : Bukkit.getWorld(HalystiaRPG.WORLD).getPlayers()) {
			pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 2f, .8f);
			pl.sendMessage(" ");
			pl.sendMessage(" ");
			pl.sendMessage(" ");
			pl.sendMessage(GOLD + "" + BOLD + "Votre attention !");
			pl.sendMessage(" ");
			pl.sendMessage(GOLD + "Aujourd'hui, un nouveau Roi a été désigné pour le " + r.getColor() + r.getName() + GOLD + " !");
			pl.sendMessage(GOLD + "Il s'agit de " + UNDERLINE + "" + BOLD + "" + DARK_RED + cible.getName() + GOLD + " !");
			pl.sendMessage(GOLD + "Longue vie au nouveau Roi !");
			pl.sendMessage(" ");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return Arrays.asList(Royaume.values()).stream().filter(r -> r != Royaume.NEUTRE).map(r -> r.toString().toLowerCase()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2)
			return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
}
