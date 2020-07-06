package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildInvite;
import fr.jamailun.halystia.guilds.GuildManager;
import fr.jamailun.halystia.guilds.GuildRank;
import fr.jamailun.halystia.guilds.GuildResult;
import fr.jamailun.halystia.utils.YesNoGUI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class GuildCommand extends HalystiaCommand {

	private static final List<String> first = Arrays.asList("create", "disband", "leave", "broadcast", "edit-tag", "gui", "promote", "demote", "invite", "join", "message", "msg");
															//--X--------X----------X----------X---------------------X-                       -----X-------X--------X--------X
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
		
		if(args[0].equalsIgnoreCase("leave")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			confirmLeave(p, guild);
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
			StringBuilder builder = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			guilds.createGuild(p, builder.toString());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("broadcast")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			GuildResult result = guild.broadcast(p, builder.toString());
			if(result == GuildResult.SUCCESS)
				return true;
			if(result == GuildResult.NEED_TO_BE_CAPTAIN) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être capitaine pour cette commande.");
				return true;
			}
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue : " + result + ".");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("msg")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 1; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			guild.internalMessage(p, builder.toString());
			return true;
		}
		
		if(args[0].equalsIgnoreCase("join")) {
			if(rank != GuildRank.NOT_A_MEMBER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous avez déjà une guilde !");
				return true;
			}
			guild.playerJoin(p, args[1]);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("promote")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.RIGHT_ARM.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être au moins bras droit pour promouvoir un joueur dans la guilde !");
				return true;
			}
			Player target = Bukkit.getPlayerExact(args[1]);
			if(target == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le joueur '" + args[1] + "' n'existe pas ou n'est pas connecté.");
				return true;
			}
			GuildResult result = guild.promote(target);
			if(result == GuildResult.PLAYER_NOT_HERE) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il s'agit du maître de guilde...");
				return true;
			}
			if(result == GuildResult.IS_ALREADY_MASTER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il s'agit du maître de guilde...");
				return true;
			}
			if(result == GuildResult.CAN_ONLY_HAVE_ONE_MASTER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il ne peut il y avoir qu'un seul maître de guilde...");
				return true;
			}
			if(result == GuildResult.CAN_ONLY_HAVE_RIGHT_ARM) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il y a déjà un bras droit en place !");
				return true;
			}
			if(result != GuildResult.SUCCESS) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue : " + result + ".");
				return true;
			}
			guild.sendMessageToMembers(guild.getTag() + ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " a été promu " + ChatColor.GOLD + guild.getPlayerRank(target).toString() + ChatColor.GREEN + ".");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("invite")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.CAPITAIN.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être au moins capitaine pour inviter un nouveau joueur dans la guilde !");
				return true;
			}
			Player target = Bukkit.getPlayerExact(args[1]);
			if(target == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le joueur '" + args[1] + "' n'existe pas ou n'est pas connecté.");
				return true;
			}
			GuildInvite invite = guild.generateInvite(p);
			target.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "-----------------------------------------------------------");
			target.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + p.getName() + ChatColor.GREEN + " vous invite dans sa guilde : " + ChatColor.AQUA + guild.getGuildName());
			TextComponent debut = new TextComponent(ChatColor.GREEN + "Pour la rejoindre, ");
			TextComponent clickable = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "cliquez ici");
			clickable.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + label + " join " + invite.getToken()));
			TextComponent fin = new TextComponent(ChatColor.GREEN + ".");
			target.spigot().sendMessage(debut, clickable, fin);
			target.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "-----------------------------------------------------------");
			p.sendMessage(guild.getTag() + ChatColor.GREEN + "Demande envoyée à " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " avec succès.");
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
		new YesNoGUI(ChatColor.DARK_RED + "Dissoudre la guilde " + ChatColor.BOLD + "définitivement" + ChatColor.DARK_RED + " ?", main) {
			@Override
			public void onFinish(Response response) {
				if(response == Response.NO)
					return;
				guilds.disbandGuild(p, guild);
			}
		}.show(p);
	}
	
	private void confirmLeave(Player p, Guild guild) {
		new YesNoGUI(ChatColor.DARK_RED + "Quitter la guilde " + ChatColor.BOLD + "définitivement" + ChatColor.DARK_RED + " ?", main) {
			@Override
			public void onFinish(Response response) {
				if(response == Response.NO)
					return;
				guild.playerLeaves(p, false);
			}
		}.show(p);
	}

}