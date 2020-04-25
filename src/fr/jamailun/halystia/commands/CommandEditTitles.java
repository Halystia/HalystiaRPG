package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.titles.Title;
import fr.jamailun.halystia.titles.TitlesManager;

public class CommandEditTitles extends HalystiaCommand {

	private static final Set<String> firsts = new HashSet<>(Arrays.asList("create", "remove", "list"));
	
	private final TitlesManager titles;
	public CommandEditTitles(HalystiaRPG main, TitlesManager titles) {
		super(main, "edit-titles");
		this.titles = titles;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		if(args.length == 0) {
			sendHelp(p, label);
			return true;
		}
		if ( args[0].equals("list")) {
			sendList(p);
			return true;
		}
		
		if ( args.length < 2) {
			sendHelp(p, label);
			return true;
		}
		
		if ( ! firsts.contains(args[0]) ) {
			sendHelp(p, label);
			return true;
		}
		// CREATE NEW QUEST
		if(args[0].equals("create")) {
			if ( args.length < 3) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" create <tag> "+DARK_RED+"<display>"+RED+" !");
				return true;
			}
			if(! args[1].matches("[a-zA-Z]+")) {
				p.sendMessage(RED + "Le tag associé ne doit contenir que des lettres.");
				return true;
			}
			String tag = args[1];
			StringBuilder builder = new StringBuilder();
			for(int i = 2; i < args.length; i++) {
				builder.append(args[i]);
				if(i != args.length - 1)
					builder.append(" ");
			}
			
			if ( titles.createTitle(tag, builder.toString()) ) {
				p.sendMessage(GREEN + "Le titre pour le tag ["+tag+"] a été créé avec succès !");
			} else {
				p.sendMessage(RED + "Un titre avec le tag ["+tag+"] existe déjà !");
			}
			return true;
		}

		// AUTRE
		if(args[0].equals("remove")) {
			String tag = args[1];
			if ( titles.removeTitle(tag) ) {
				p.sendMessage(GREEN + "Le titre pour le tag ["+tag+"] a été supprimé avec succès !");
			} else {
				p.sendMessage(RED + "Le titre avec le tag ["+tag+"] n'existe pas !");
			}
			return true;
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 1)
			return firsts.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2 && args[0].equals("remove"))
			return titles.getAllTitles().stream().map(title -> title.getTag()).filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		return new ArrayList<>();
	}// 0=STEPS 1=(QUEST) 2=messages 3=<step> 4=add 5=...
	
	private void sendHelp(Player p, String label) {
		p.sendMessage(AQUA + "/" + label + " create <tag> <display> " + WHITE + ": Créer un nouveau titre.");
		p.sendMessage(AQUA + "/" + label + " remove <tag> " + WHITE + ": Supprime un titre.");
		p.sendMessage(AQUA + "/" + label + " list " + WHITE + ": Liste les titres (tags+display).");
	}
	
	private void sendList(Player p) {
		p.sendMessage(AQUA + "Liste des " + titles.getSize() + " titres :");
		for(Title title : titles.getAllTitles()) {
			StringBuilder builder = new StringBuilder().append("- tag=["+YELLOW+title.getTag()+WHITE+"] -> ["+title.getDisplayName()+WHITE+"]");
			p.sendMessage(builder.toString());
		}
	}
	
}