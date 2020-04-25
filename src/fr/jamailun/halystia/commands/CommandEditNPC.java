package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.NpcMode;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.npcs.Texture;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class CommandEditNPC extends HalystiaCommand {

	private static final Set<String> firsts = new HashSet<>(Arrays.asList("create", "remove", "tphere", "rename", "dialog", "goto", "list", "equipment", "reload", "texture", "mode"));
	private static final Set<String> dialogs = new HashSet<>(Arrays.asList("clear", "see", "add", "remove", "set", "insert"));
	//private static final Set<String> slots = new HashSet<>(Arrays.asList("head", "chest", "legs", "feet", "mainhand", "offhand")); 
	private final static String NULL_ITEM = "#none";
	
	private final NpcManager npcs;
	public CommandEditNPC(HalystiaRPG main, NpcManager npcs) {
		super(main, "edit-npc");
		this.npcs = npcs;
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
			npcs.reload();
			p.sendMessage(GREEN+"NPCs reloadés avec succès.");
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
		// CREATE NEW NPC
		if(args[0].equals("create")) {
			if(! args[1].matches("[a-zA-Z0-9]+")) {
				p.sendMessage(ChatColor.GREEN + "Le nom ne doit contenir que des caractères alphanumériques.");
				return true;
			}
			RpgNpc npc = npcs.createNpc(args[1], p.getLocation());
			if ( npc != null ) {
				p.sendMessage(ChatColor.GREEN + "Le npc ["+args[1]+"] a été créé avec succès !");
				npc.spawn();
				npc.changeLocation(p.getLocation());
			} else {
				p.sendMessage(ChatColor.RED + "Le npc ["+args[1]+"] existe déjà !");
			}
			return true;
		}

		RpgNpc npc = npcs.getNpcWithConfigId(args[1]);
		if(npc == null) {
			p.sendMessage(ChatColor.RED + "Le npc ["+args[1]+"] n'existe pas.");
			return true;
		}
		
		// AUTRE
		if(args[0].equals("remove")) {
			if ( npcs.removeNpc(npc) ) {
				npc.deleteData();
				p.sendMessage(ChatColor.GREEN + "Le npc ["+args[1]+"] a été supprimé avec succès !");
			} else
				p.sendMessage(ChatColor.RED + "Le npc ["+args[1]+"] n'a pas pu être supprimé...");
			return true;
		}
		
		if(args[0].equals("goto")) {
			p.teleport(npc.getNPC().getStoredLocation(), TeleportCause.PLUGIN);
			p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 5f, .8f);
			p.sendMessage(GREEN + "Vous avez été téléporté au NPC.");
			return true;
		}
		
		if(args[0].equals("tphere")) {
		///	npc.depopToAllPlayers();
			p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 5f, .8f);
			npc.changeLocation(p.getLocation());
			p.sendMessage(GREEN + "NPC déplacé avec succès.");
		//	npc.showToAllPlayers();
			return true;
		}
		
		if(args[0].equals("texture")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Précisez la texture !");
				return true;
			}
			Texture skin = npcs.getTexture(args[2]);
			if(skin == null) {
				p.sendMessage(RED + "Texture inconnue :" + args[2]);
				return true;
			}
			npc.changeSkin(skin);
			p.sendMessage(GREEN + "Skin changé avec succès !");
			return true;
		}
		
		if(args[0].equals("mode")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Précisez le mode !");
				return true;
			}
			try {
				NpcMode mode = NpcMode.valueOf(args[2]);
				npc.changeMode(mode);
			} catch (IllegalArgumentException e) {
				p.sendMessage(RED + "Mode inconnu :" + args[2]);
				return true;
			}
			p.sendMessage(GREEN + "Mode changé avec succès !");
			return true;
		}
		
		if(args[0].equals("rename")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Précisez le nouveau nom !");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 2; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			
			if(builder.toString().length() > 25) {
				p.sendMessage(RED + "Le nom est trop long ! ("+builder.toString()+")");
				return true;
			}
			p.playSound(p.getLocation(), Sound.ITEM_AXE_STRIP, 5f, .8f);
			npc.rename(builder.toString());
			p.sendMessage(GREEN + "Le nom du npc a bien été changé.");
			return true;
		}
		
		if(args[0].equals("dialog")) {
			if(args.length < 3) {
				sendHelpDialog(p, label, args[1]);
				return true;
			}
			if(args[2].equals("clear")) {
				npc.clearDialog();
				p.sendMessage(GREEN + "Dialogue supprimé avec succès.");
				return true;
			}
			if(args[2].equals("see")) {
				p.sendMessage(BLUE + "< Dialogue >");
				npc.getDialog().forEach(msg -> npc.sendMessage(p, msg));
				p.sendMessage(BLUE + "< ------ >");
				return true;
			}
			
			if(args.length < 4) {
				sendHelpDialog(p, label, args[1]);
				return true;
			}
			
			if(args[2].equals("add")) {
				StringBuilder builder = new StringBuilder();
				for(int i = 3; i < args.length; i++) {
					builder.append(args[i]);
					if(i < args.length - 1)
						builder.append(" ");
				}
				npc.addDialogLine(builder.toString());
				p.sendMessage(GREEN + "Ligne de dialogue ajoutée avec succès.");
				return true;
			}
			
			if(args[2].equals("remove")) {
				int line = -1;
				try {
					line = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de numéro de ligne valide.");
					return true;
				}
				if(npc.removeDialogLine(line))
					p.sendMessage(GREEN + "Ligne de dialogue supprimée avec succès.");
				else
					p.sendMessage(RED + "La ligne de dialogue n'a pas pu être supprimée.");
				return true;
			}
			
			if(args[2].equals("set") || args[2].equals("insert")) {
				if(args.length < 5) {
					p.sendMessage(RED + "/"+label+" " + args[0] + args[1] + " set <line-number> <texte>");
					return true;
				}
				int line = -1;
				try {
					line = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de numéro de ligne valide.");
					return true;
				}
				StringBuilder builder = new StringBuilder();
				for(int i = 4; i < args.length; i++) {
					builder.append(args[i]);
					if(i < args.length - 1)
						builder.append(" ");
				}
				if(args[2].equals("set") ? npc.setDialogLine(builder.toString(), line) : npc.insertDialogLine(builder.toString(), line))
					p.sendMessage(GREEN + "Ligne de dialogue modifée avec succès.");
				else
					p.sendMessage(RED + "Ligne de dialogue n'a pas pu être modifiée.");
				return true;
			}
			
			sendHelpDialog(p, label, args[1]);
			return true;
		}
		
		if(args[0].equals("equipment")) {
			if(args.length < 4) {
				p.sendMessage(RED + "/"+label+" " + args[0] + " " + args[1] + RED + " <slot> <material>");
				return true;
			}
			EquipmentSlot slot = null;
			try {
				slot = EquipmentSlot.valueOf(args[2].toUpperCase());
			} catch (IllegalArgumentException ee) {
				p.sendMessage("Slot inconnu : ["+args[2].toUpperCase()+"].");
				return true;
			}
			Material material = Material.AIR;
			if( ! args[3].equals(NULL_ITEM)) {
				try {
					material = Material.valueOf(args[3].toUpperCase());
				} catch (IllegalArgumentException ee) {
					p.sendMessage("Material inconnu : ["+args[3].toUpperCase()+"].");
					return true;
				}
			}
			npc.setEquipment(slot, new ItemStack(material));
			p.sendMessage(GREEN + "Equipement changé avec succès.");
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
			return npcs.getAllConfigIds().stream().filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		if(args.length <= 3)
			if(args[0].equals("dialog"))
				return dialogs.stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("equipment"))
				return Arrays.asList(EquipmentSlot.values()).stream().map(slot -> slot.toString()).filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("mode"))
				return Arrays.asList(NpcMode.values()).stream().map(slot -> slot.toString()).filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("texture"))
				return npcs.getTextures().stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
		if(args.length <= 4)
			if(args[0].equals("equipment")) {
				List<String> ret = Arrays.asList(Material.values()).stream().map(mat -> mat.toString().toLowerCase()).filter(str -> str.startsWith(args[3])).collect(Collectors.toList());
				if(NULL_ITEM.startsWith(args[3])) ret.add(NULL_ITEM);
				return ret;
			}
		return new ArrayList<>();
	}
	
	private void sendHelp(Player p, String label) {
		p.sendMessage(BLUE + "/" + label + " create <id> " + WHITE + ": Créer un nouveau npc.");
		p.sendMessage(BLUE + "/" + label + " remove <id> " + WHITE + ": Supprime un npc.");
		p.sendMessage(BLUE + "/" + label + " list " + WHITE + ": Liste les npcs existants.");
		p.sendMessage(BLUE + "/" + label + " tphere <id> " + WHITE + ": Téléporte un npc à votre position.");
		p.sendMessage(BLUE + "/" + label + " goto <id> " + WHITE + ": Vous téléporte à un npc.");
		p.sendMessage(BLUE + "/" + label + " rename <id> <nom>" + WHITE + ": Renommme un npc. Couleurs.");
		p.sendMessage(BLUE + "/" + label + " dialog <id> <dialog>" + WHITE + ": Change le dialogue d'un npc. Couleurs et multiligne.");
		p.sendMessage(BLUE + "/" + label + " equipment <id> <slot> <type>" + WHITE + ": Change le dialogue d'un npc. Couleurs et multiligne.");
		p.sendMessage(BLUE + "/" + label + " texture <id> <texture>" + WHITE + ": Change le skin d'un npc.");
	}
	
	private void sendHelpDialog(Player p, String label, String npc) {
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" clear " + WHITE + ": Supprime le dialogue.");
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" see " + WHITE + ": Voir tout le dialogue.");
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" add <text>" + WHITE + ": Rajouter une nouvelle étape de dialoque. Couleurs, multiligne.");
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" remove <n> " + WHITE + ": Supprimer une étape de dialogue.");
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" set <n> <text> " + WHITE + ": Supprimer une étape de dialogue. Couleurs, multiligne.");
		p.sendMessage(BLUE + "/" + label + " dialog "+npc+BLUE+" insert <n> <text> " + WHITE + ": Insère une étape de dialogue. Couleurs, multiligne.");
	}

	private void sendList(Player p) {
		boolean cl = true;
		StringBuilder builder = new StringBuilder();
		for(RpgNpc npc : npcs.getNpcs()) {
			cl =! cl;
			builder.append(cl ? ChatColor.YELLOW : ChatColor.WHITE)
			.append(npc.getConfigId())
			.append(ChatColor.GRAY)
			.append("(").append(npc.getMode().toString()).append(")")
			.append(" ");
		}
		p.sendMessage(ChatColor.BLUE + "Liste des " + npcs.getAllConfigIds().size() + " NPCs :");
		p.sendMessage(builder.toString());
	}
}