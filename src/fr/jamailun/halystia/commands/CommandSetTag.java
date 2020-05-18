package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.sql.temporary.DataHandler;

public class CommandSetTag extends HalystiaCommand {
	
	private final DataHandler bdd;
	
	public CommandSetTag(HalystiaRPG main, DataHandler bdd) {
		super(main, "set-tag");
		this.bdd = bdd;
	}
	
	private final static List<String> cmds = Arrays.asList("add", "clear", "list", "remove");
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(args.length < 2) {
			sender.sendMessage("/set-tag <add/remove/list/clear> <player> [tag]");
			return true;
		}
		
		String plStr = args[1];
		Player cible = Bukkit.getPlayer(plStr);
		if(cible == null) {
			sender.sendMessage(RED + "Joueur inconnu/non connecté : " + DARK_RED + plStr + RED + ".");
			return true;
		}
		
		if(args[0].equals("list")) {
			List<String> tags = bdd.getTagsOfPlayer(cible);
			if(tags.isEmpty())
				sender.sendMessage(YELLOW+"Ce joueur n'a aucun tag.");
			else {
				sender.sendMessage(GREEN+"Liste des " + tags.size() + " tags de " + plStr+GREEN+" :");
				tags.forEach(tag -> sender.sendMessage(WHITE+"+["+GREEN+tag+WHITE+"]"));
			}
			return true;
		}
		
		if(args[0].equals("clear")) {
			for(String tag : bdd.getTagsOfPlayer(cible))
				bdd.removeTagFromPlayer(cible, tag);
			sender.sendMessage(GREEN+"Clear des tags de "+plStr+" terminé.");
			return true;
		}
		
		if(args.length < 3) {
			sender.sendMessage(RED+"Il faut préciser le tag !");
			return true;
		}
		
		final String tag = args[2].toLowerCase();
		
		if(args[0].equals("remove")) {
			bdd.removeTagFromPlayer(cible, tag);
			sender.sendMessage(GREEN+"Retrait du tag ["+tag+"] de "+plStr+" terminé.");
			return true;
		}
		
		if(args[0].equals("add")) {
			bdd.addTagToPlayer(cible, tag);
			sender.sendMessage(GREEN+"Ajout du tag ["+tag+"] à "+plStr+" terminé.");
			return true;
		}

		sender.sendMessage("/set-tag <player> <add/remove/list/clear> [tag]");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(args.length <= 1)
			return cmds.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2)
			return null;
		if(args.length <= 3 && !args[0].equals("clear") && !args[0].equals("list")) {
			Player cible = Bukkit.getPlayer(args[1]);
			if(cible == null)
				return new ArrayList<>();
			return bdd.getTagsOfPlayer(cible).stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
}
