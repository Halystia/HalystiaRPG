package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GuildCommand extends HalystiaCommand {

	private static final Map<String, GuildRank> subCommands = new HashMap<>();
	static {
		subCommands.put("help", GuildRank.NOT_A_MEMBER);
		subCommands.put("create", GuildRank.NOT_A_MEMBER);
		subCommands.put("join", GuildRank.NOT_A_MEMBER);
		subCommands.put("message", GuildRank.MEMBER);
		subCommands.put("members", GuildRank.MEMBER);
		subCommands.put("leave", GuildRank.MEMBER);
		subCommands.put("msg", GuildRank.MEMBER);
		subCommands.put("gui", GuildRank.MEMBER);
		subCommands.put("invite", GuildRank.CAPITAIN);
		subCommands.put("broadcast", GuildRank.CAPITAIN);
		subCommands.put("promote", GuildRank.RIGHT_ARM);
		subCommands.put("demote", GuildRank.RIGHT_ARM);
		subCommands.put("kick", GuildRank.RIGHT_ARM);
		subCommands.put("disband", GuildRank.MASTER);
		subCommands.put("pvp", GuildRank.MASTER);
		subCommands.put("edit-tag", GuildRank.MASTER);
	}
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
		
		if( ! subCommands.containsKey(args[0]) ) {
			sendHelp(sender, label, rank);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("gui")) {
			openGUI(p, guild, rank);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("help")) {
			sendHelp(p, label, rank);
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
			if(rank == GuildRank.MASTER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Impossible de quitter une guilde dont vous êtes le maître !");
				return true;
			}
			confirmLeave(p, guild);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("members")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			sendMembers(p, guild);
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
			if(args[1].length() < 5)
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le nom de la guilde doit faire au moins 5 caractères de long !");
			guilds.createGuild(p, args[1]);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("pvp")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.MASTER.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Seul le maître de guilde peut autoriser ou non le pvp !");
				return true;
			}
			if( ! ( args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("disable")) ) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Les valeurs autorisées sont : 'enable' ou 'disable'.");
				return true;
			}
			boolean now = guild.allowsPVP();
			boolean enable = args[1].equalsIgnoreCase("enable");
			if(now == enable) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le PvP est déjà " + (enable ? "autorisé" : "interdit") + " !");
				return true;
			}
			guild.setPvp(enable);
			guild.sendMessageToMembers(guild.getTag() + ChatColor.YELLOW + "" + ChatColor.BOLD + "Nouvelle règle pour le PvP : " + (enable ? ChatColor.GREEN + "autorisé" : ChatColor.RED + "interdit")+ ChatColor.YELLOW + "" + ChatColor.BOLD +".");
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
			guilds.joinGuild(p, args[1]);
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
			OfflinePlayer target = Arrays.asList(Bukkit.getOfflinePlayers()).stream().filter(t -> t.getName().equals(args[1])).findAny().orElse(null);
			if(target == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le joueur '" + args[1] + "' n'existe pas.");
				return true;
			}
			GuildResult result = guild.promote(target.getUniqueId());
			if(result == GuildResult.PLAYER_NOT_HERE) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce joueur n'est pas dans la guilde...");
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
			guild.sendMessageToMembers(guild.getTag() + ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " a été promu " + ChatColor.GOLD + guild.getPlayerRank(target.getUniqueId()).toString() + ChatColor.GREEN + ".");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("demote")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.RIGHT_ARM.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être au moins bras droit pour rétrograder un joueur dans la guilde !");
				return true;
			}
			OfflinePlayer target = Arrays.asList(Bukkit.getOfflinePlayers()).stream().filter(t -> t.getName().equals(args[1])).findAny().orElse(null);
			if(target == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le joueur '" + args[1] + "' n'existe pas.");
				return true;
			}
			GuildResult result = guild.demote(target.getUniqueId());
			if(result == GuildResult.PLAYER_NOT_HERE) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce joueur n'est pas dans la guilde...");
				return true;
			}
			if(result == GuildResult.MASTER_CANNOT_BE_DEMOTE) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il s'agit du maître de guilde...");
				return true;
			}
			if(result == GuildResult.IS_ALREADY_MEMBER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce joueur a déjà le rôle le plus bas.");
				return true;
			}
			if(result != GuildResult.SUCCESS) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue : " + result + ".");
				return true;
			}
			guild.sendMessageToMembers(guild.getTag() + ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " a été rétrogradé au rôle de " + ChatColor.GOLD + guild.getPlayerRank(target.getUniqueId()).toString() + ChatColor.GREEN + ".");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("kick")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.RIGHT_ARM.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être au moins bras droit pour renvoyer un joueur de la guilde !");
				return true;
			}
			OfflinePlayer target = Arrays.asList(Bukkit.getOfflinePlayers()).stream().filter(t -> t.getName().equals(args[1])).findAny().orElse(null);
			if(target == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le joueur '" + args[1] + "' n'existe pas.");
				return true;
			}
			if(target.getUniqueId().equals(p.getUniqueId())) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous ne pouvez pas vous renvoyer vous même...");
				return true;
			}
			GuildRank targetRank = guild.getPlayerRank(target.getUniqueId());
			if(targetRank == GuildRank.MASTER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le maître de guilde ne peut pas être renvoyé !");
				return true;
			}
			if(targetRank == GuildRank.NOT_A_MEMBER) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce joueur n'est pas dans la guilde !");
				return true;
			}
			confirmKick(p, guild, target);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("edit-tag")) {
			if(guild == null) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut une guilde pour effectuer cette commande.");
				return true;
			}
			if(rank.getPower() < GuildRank.MASTER.getPower()) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être le maître de guilde pour changer le tag.");
				return true;
			}
			GuildResult result = guild.changeTag(args[1]);
			if(result == GuildResult.WRONG_TAG_SIZE) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le tag doit faire exactement " + Guild.TAG_LENGHT + " caractères de long.");
				return true;
			}
			if(result == GuildResult.TAG_ALREADY_EXISTS) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Ce tag existe déjà !");
				return true;
			}
			if(result != GuildResult.SUCCESS) {
				p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue : " + result + ".");
				return true;
			}
			guild.sendMessageToMembers(guild.getTag() + ChatColor.GREEN + "La guilde a un nouveau tag : " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + args[1] + ChatColor.GREEN+" !");
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
			TextComponent debut = new TextComponent(HalystiaRPG.PREFIX + ChatColor.GREEN + "Pour la rejoindre, ");
			TextComponent clickable = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "cliquez ici");
			clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " join " + invite.getToken()));
			clickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( ChatColor.AQUA+"Rejoindre cette guilde !" ).create()));
			TextComponent fin = new TextComponent(ChatColor.GREEN + ".");
			target.spigot().sendMessage(debut, clickable, fin);
			target.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "-----------------------------------------------------------");
			p.sendMessage(guild.getTag() + ChatColor.GREEN + "Demande envoyée à " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " avec succès.");
			return true;
		}
		
		
		sendHelp(sender, label, rank);
		return true;
	}


	private void sendMembers(Player p, Guild guild) {
		p.sendMessage(guild.getTag() + ChatColor.YELLOW + " " + ChatColor.BOLD + "Liste des membres de la guilde " + guild.getGuildName() + ChatColor.YELLOW + " " + ChatColor.BOLD + ":");
		for(String member : guild.getMembersDisplay())
			p.sendMessage(ChatColor.GRAY + " - " + member);
	}

	private void openGUI(Player p, Guild guild, GuildRank rank) {
		// TODO openGUI()
		//p.sendMessage(ChatColor.RED + "Non implémenté pour le moment.");
		sendHelp(p, "guilds", rank);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if( ! (sender instanceof Player))
			return new ArrayList<>();
		Guild guild = guilds.getGuild((Player)sender);
		GuildRank rank = guild == null ? GuildRank.NOT_A_MEMBER : guild.getPlayerRank((Player)sender);
		if(args.length <= 1)
			return subCommands.entrySet().stream()
					.filter(entry -> 
						entry.getKey().startsWith(args[0])
						&& entry.getValue().getPower() <= rank.getPower()
						&& ! ( ((entry.getKey().equals("join") || entry.getKey().equals("create")) && rank != GuildRank.NOT_A_MEMBER) )
						&& ! ( ((entry.getKey().equals("leave")) && rank == GuildRank.MASTER) )
					).map(entry -> entry.getKey())
					.collect(Collectors.toList());
		if(args.length <= 2) {
			if(args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("demote") || args[0].equalsIgnoreCase("kick") ) {
				if(rank.getPower() >= GuildRank.RIGHT_ARM.getPower())
					return guild.getOfflinePlayersNames().stream().filter(n -> !n.equals(sender.getName()) && n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
			}
			if(args[0].equalsIgnoreCase("invite"))
				return Bukkit.getOnlinePlayers().stream().filter(pl -> ! pl.getUniqueId().equals(((Player)sender).getUniqueId()) && pl.getName().toLowerCase().startsWith(args[1].toLowerCase())).map(pl -> pl.getName()).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	private void sendHelp(CommandSender sender, String label, GuildRank rank) {
		sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Aide pour les commandes de guilde :");
		if(rank == GuildRank.NOT_A_MEMBER) {
			sender.sendMessage(ChatColor.GREEN + "/" + label + " create <nom>" + ChatColor.WHITE + " : Créer une nouelle guilde (le nom n'accepte pas les espaces)");
			sender.sendMessage(ChatColor.GREEN + "/" + label + " join <id>" + ChatColor.WHITE + " : Acceptez l'invitation d'une guilde.");
			sender.sendMessage(ChatColor.GREEN + "/" + label + " help" + ChatColor.WHITE + " : Affiche ce menu.");
			return;
		}
		if(rank.getPower() >= GuildRank.MASTER.getPower()) {
			sender.sendMessage(ChatColor.DARK_RED + "/" + label + " disband" + ChatColor.WHITE + " : Détruire à jamais la guilde.");
			sender.sendMessage(ChatColor.BLUE + "/" + label + " edit-tag" + ChatColor.WHITE + " : Modifie le tag de la guilde.");
		}
		if(rank.getPower() >= GuildRank.RIGHT_ARM.getPower()) {
			sender.sendMessage(ChatColor.GOLD + "/" + label + " promote <joueur>" + ChatColor.WHITE + " : Promouvoir un joueur.");
			sender.sendMessage(ChatColor.GOLD + "/" + label + " demote <joueur>" + ChatColor.WHITE + " : Rétrograde un joueur.");
			sender.sendMessage(ChatColor.GOLD + "/" + label + " kick <joueur>" + ChatColor.WHITE + " : Renvoie un joueur de la guilde.");
		}
		if(rank.getPower() >= GuildRank.CAPITAIN.getPower()) {
			sender.sendMessage(ChatColor.DARK_GREEN + "/" + label + " broadcast <message>" + ChatColor.WHITE + " : Envoie une annonce à toute la guilde.");
			sender.sendMessage(ChatColor.DARK_GREEN + "/" + label + " invite <joueur>" + ChatColor.WHITE + " : Inviter un joueur dans notre guilde.");
		}
		sender.sendMessage(ChatColor.GREEN + "/" + label + " message <message>" + ChatColor.WHITE + " : Envoie un message uniquement aux gens de votre guilde.");
		sender.sendMessage(ChatColor.GREEN + "/" + label + " gui" + ChatColor.WHITE + " : Ouvre la GUI de la guilde.");
		if(rank != GuildRank.MASTER)
			sender.sendMessage(ChatColor.GREEN + "/" + label + " leave" + ChatColor.WHITE + " : Quitter définitivement la guilde.");
	}
	
	private void confirmDisband(Player p, Guild guild) {
		new YesNoGUI(ChatColor.DARK_RED + "Dissoudre la guilde " + ChatColor.BOLD + "définitivement" + ChatColor.DARK_RED + " ?", main) {
			@Override
			public void onFinish(Response response) {
				p.closeInventory();
				if(response == Response.NO)
					return;
				guilds.disbandGuild(p, guild);
			}
		}.show(p);
	}
	
	private void confirmLeave(Player p, Guild guild) {
		new YesNoGUI(ChatColor.DARK_RED + "Quitter votre guilde " + ChatColor.DARK_RED + " ?", main) {
			@Override
			public void onFinish(Response response) {
				p.closeInventory();
				if(response == Response.NO)
					return;
				guild.playerLeaves(p.getUniqueId(), false);
			}
		}.show(p);
	}
	
	private void confirmKick(Player p, Guild guild, OfflinePlayer target) {
		new YesNoGUI(ChatColor.DARK_RED + "Renvoyer "+target.getName()+" " + " ?", main) {
			@Override
			public void onFinish(Response response) {
				p.closeInventory();
				if(response == Response.NO)
					return;
				guild.playerLeaves(target.getUniqueId(), true);
			}
		}.show(p);
	}

}