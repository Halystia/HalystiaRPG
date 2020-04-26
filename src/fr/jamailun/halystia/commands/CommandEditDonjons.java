package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.YELLOW;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.Donjon;
import fr.jamailun.halystia.donjons.DonjonDifficulty;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.donjons.DonjonManager;

public class CommandEditDonjons extends HalystiaCommand {

	private static final Set<String> firsts = new HashSet<>(Arrays.asList("create", "remove", "list", "rename", "tp", "xp", "level", "difficulty", "set-entry", "reload"));
	
	private final DonjonManager donjons;
	public CommandEditDonjons(HalystiaRPG main, DonjonManager donjons) {
		super(main, "edit-donjons");
		this.donjons = donjons;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
			sendHelp(p, label);
			return true;
		}
		if ( args[0].equals("list")) {
			sendList(p);
			return true;
		}
		if ( args[0].equals("reload")) {
			donjons.reloadData();
			p.sendMessage(GREEN+"Donjons reloadés avec succès.");
			return true;
		}
		if ( args.length < 2) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" <command> <id> [arguments]");
			return true;
		}
		if ( ! firsts.contains(args[0]) ) {
			sendHelp(p, label);
			return true;
		}
		// CREATE NEW DJ
		if(args[0].equals("create")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Précise la difficultée du donjon !");
				return true;
			}
			if(! args[1].matches("[a-zA-Z0-9]+")) {
				p.sendMessage(RED + "Le nom ne doit contenir que des caractères alphanumériques.");
				return true;
			}
			DonjonDifficulty diff = DonjonDifficulty.FACILE;
			try {
				diff = DonjonDifficulty.valueOf(args[2]);
			} catch (IllegalArgumentException e) {
				p.sendMessage(RED + "Difficultée invalide.");
				return true;
			}
			if ( donjons.createDonjon(args[1], p.getLocation(), diff) ) {
				p.sendMessage(GREEN + "Le donjon ["+args[1]+"] a été créé avec succès !");
			} else {
				p.sendMessage(RED + "Le donjon ["+args[1]+"] existe déjà !");
			}
			return true;
		}

		Donjon donjon = donjons.getLegacyWithConfigName(args[1]);
		if(donjon == null) {
			p.sendMessage(RED + "Le donjon ["+args[1]+"] n'existe pas.");
			return true;
		}
		
		// AUTRE
		if(args[0].equals("remove")) {
			if ( donjons.removeDonjon(donjon.getConfigName()) ) {
				p.sendMessage(GREEN + "Le donjon ["+args[1]+"] a été supprimé avec succès !");
			} else
				p.sendMessage(RED + "Le donjon ["+args[1]+"] n'a pas pu être supprimé...");
			return true;
		}
		
		if(args[0].equals("tp")) {
			p.teleport(donjon.getEntryInDonjon(), TeleportCause.PLUGIN);
			p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 5f, .8f);
			p.sendMessage(GREEN + "Vous avez été téléporté au donjon.");
			return true;
		}
		
		if(args[0].equals("set-entry")) {
			p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 5f, .8f);
			donjon.changeEntryLocation(p.getLocation());
			p.sendMessage(GREEN + "Entrée du donjon déplacé avec succès.");
			return true;
		}
		
		if(args[0].equals("xp")) {
			if(args.length < 3) {
				p.sendMessage(BLUE + "Le donjon " + donjon.getConfigName() + " donne " + donjon.getExpReward() + "xp.");
				return true;
			}
			int xp = -1;
			try {
				xp = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(RED + "Format du nombre invalide.");
				return true;
			}
			donjon.changeExpReward(xp);
			p.sendMessage(GREEN + "Xp de récompense changé avec succès !");
			return true;
		}
		
		if(args[0].equals("level")) {
			if(args.length < 3) {
				p.sendMessage(BLUE + "Le donjon " + donjon.getConfigName() + " est de niveau " + donjon.getLevelNeed() + ".");
				return true;
			}
			int level = -1;
			try {
				level = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(RED + "Format du nombre invalide.");
				return true;
			}
			donjon.changeLevelNeeded(level);
			p.sendMessage(GREEN + "Niveau requis changé avec succès !");
			return true;
		}
		
		if(args[0].equals("rename")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Il faut préciser le nouveau nom.");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 2; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			donjon.changeDonjonName(builder.toString());
			p.sendMessage(GREEN + "Nom changé avec succès.");
			return true;
		}
		
		if(args[0].equals("difficulty")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Il faut préciser la difficultée.");
				return true;
			}
			DonjonDifficulty diff = DonjonDifficulty.FACILE;
			try {
				diff = DonjonDifficulty.valueOf(args[2]);
			} catch (IllegalArgumentException e) {
				p.sendMessage(RED + "Difficultée invalide.");
				return true;
			}
			donjon.changeDonjonDifficulty(diff);
			p.sendMessage(GREEN + "Difficultée changeé avec succès. Attention, pensez à refaire les portes.");
			return true;
		}
		p.sendMessage(RED + "Commande ["+args[0] +"] autorisée mais non paramétrée...");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 1)
			return firsts.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2)
			return donjons.getDonjons().stream().map(di -> di.getConfigName()).filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		if(args.length <= 3)
			if(args[0].equals("difficulty") || args[0].equals("create"))
				return Arrays.asList(DonjonDifficulty.values()).stream().map(d -> d.toString()).filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
	private void sendHelp(Player p, String label) {
		p.sendMessage(BLUE + "/" + label + " create <id> <difficulty>" + WHITE + ": Créer un nouveau donjon à votre position actuelle.");
		p.sendMessage(BLUE + "/" + label + " remove <id> " + WHITE + ": Supprime un donjon.");
		p.sendMessage(BLUE + "/" + label + " list " + WHITE + ": Liste les donjons existants.");
		p.sendMessage(BLUE + "/" + label + " rename <id> <nom> " + WHITE + ": Change le nom du donjon.");
		p.sendMessage(BLUE + "/" + label + " tp <id> " + WHITE + ": Vous téléporte à l'entrée du donjon.");
		p.sendMessage(BLUE + "/" + label + " set-entry <id> " + WHITE + ": Déplace l'entrée du donjon à votre position actuelle.");
		p.sendMessage(BLUE + "/" + label + " xp <id> [xp]" + WHITE + ": Change l'exp donnée en récompense à la fin du dj.");
		p.sendMessage(BLUE + "/" + label + " level <id> [level]" + WHITE + ": Change le nivuea requis pour entrer dans le dj.");
		p.sendMessage(BLUE + "/" + label + " difficulty <id> <slot> <type>" + WHITE + ": Change la difficultée affichée d'un donjon.");
		p.sendMessage(BLUE + "/" + label + " reload" + WHITE + ": Recharge les fichiers des donjons.");
	}

	private void sendList(Player p) {
		p.sendMessage(BLUE + "Liste des " + donjons.getDonjons().size() + " donjons :");
		for(DonjonI dj : donjons.getDonjons()) {
			p.sendMessage(BLUE + "["+YELLOW+dj.getConfigName()+BLUE+"] ("+dj.getDonjonDifficulty().color + dj.getName()+BLUE+") -" + dj.getDonjonDifficulty().getDisplayName() + BLUE + " - lvl="+YELLOW+dj.getLevelNeed()+BLUE+", xp="+YELLOW+dj.getExpReward()+BLUE+".");
		}
	}
}