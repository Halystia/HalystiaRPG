package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildManager;
import fr.jamailun.halystia.guilds.GuildRank;
import fr.jamailun.halystia.utils.YesNoGUI;

public class GuildCommand extends HalystiaCommand {

	private static final List<String> first = Arrays.asList("create", "disband", "leave", "broadcast", "edit-tag", "gui", "promote", "demote", "invite", "join", "message", "msg");
	
	private final GuildManager guilds;
	
	public GuildCommand(HalystiaRPG main, GuildManager guilds) {
		super(main, "guilds");
		this.guilds = guilds;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( ! (sender instanceof Player) ) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être un joueur pour effectuer cette commande.");
			return true;
		}
		
		Player p = (Player) sender;
		Guild guild = guilds.getGuild(p);
		GuildRank rank = guild == null ? GuildRank.NOT_A_MEMBER : guild.getPlayerRank(p);
		
		if( args.length < 1) {
			openGUI(p, guild, rank);
			return true;
		}
		
		if( ! first.contains(args[0]) ) {
			sendHelp(sender, label, rank);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("gui")) {
			openGUI(p, guild, rank);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("disband")) {
			if(rank != GuildRank.MASTER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Seul le maître de la guilde peut supprimer la guilde !");
				return true;
			}
			confirmDisband(p, guild);
			return true;
		}
		
		if(args.length < 2) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Cette commande nécessite plus d'arguments !");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("create")) {
			if(rank != GuildRank.NOT_A_MEMBER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous avez déjà une guilde !");
				return true;
			}
			
			return true;
		}
		
		if(args[0].equalsIgnoreCase("join")) {
			if(rank != GuildRank.NOT_A_MEMBER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous avez déjà une guilde !");
				return true;
			}
			
			return true;
		}
		
		
		sendHelp(sender, label, rank);
		return true;
	}

	private void openGUI(Player p, Guild guild, GuildRank rank) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length <= 1)
			return first.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
	private void sendHelp(CommandSender sender, String label, GuildRank rank) {
		sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Aide pour les commandes de guilde :");
		if(rank == GuildRank.NOT_A_MEMBER) {
			sender.sendMessage(ChatColor.GREEN + "/" + label + " create <nom>" + ChatColor.WHITE + " : Créer une nouelle guilde (le nom accepte les espaces)");
			sender.sendMessage(ChatColor.GREEN + "/" + label + " join <id>" + ChatColor.WHITE + " : Acceptez l'invitation d'une guilde.");
			return;
		}
		if(rank.getPower() >= GuildRank.MASTER.getPower()) {
			sender.sendMessage(ChatColor.GREEN + "/" + label + " disband" + ChatColor.WHITE + " : Détruire à jamais la guilde.");
		}
		if(rank.getPower() >= GuildRank.RIGHT_ARM.getPower()) {
			sender.sendMessage(ChatColor.GOLD + "/" + label + " promote <joueur>" + ChatColor.WHITE + " : Promouvoir un joueur.");
			sender.sendMessage(ChatColor.GOLD + "/" + label + " demote <joueur>" + ChatColor.WHITE + " : Rétrograde un joueur.");
		}
		if(rank.getPower() >= GuildRank.CAPITAIN.getPower()) {
			sender.sendMessage(ChatColor.DARK_GREEN + "/" + label + " broadcast <message>" + ChatColor.WHITE + " : Envoie une annonce à toute la guilde.");
			sender.sendMessage(ChatColor.DARK_GREEN + "/" + label + " invite <joueur>" + ChatColor.WHITE + " : Inviter un joueur dans notre guilde.");
		}
		sender.sendMessage(ChatColor.GREEN + "/" + label + " message <message>" + ChatColor.WHITE + " : Envoie un message uniquement aux gens de votre guilde.");
		sender.sendMessage(ChatColor.GREEN + "/" + label + " gui" + ChatColor.WHITE + " : Ouvre la GUI de la guilde.");
		sender.sendMessage(ChatColor.GREEN + "/" + label + " leave" + ChatColor.WHITE + " : Quitter définitivement la guilde.");
	}
	
	private void confirmDisband(Player p, Guild guild) {
		new YesNoGUI(ChatColor.DARK_RED + "", main) {
			@Override
			public void onFinish(Response response) {
				if(response == Response.NO)
					return;
				//TODO disband
			}
		}.show(p);
	}
	
}