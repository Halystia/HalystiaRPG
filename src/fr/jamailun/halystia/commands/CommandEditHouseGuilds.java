package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.GuildManager;
import fr.jamailun.halystia.guilds.houses.GuildHouse;
import fr.jamailun.halystia.guilds.houses.HouseSize;

public class CommandEditHouseGuilds extends HalystiaCommand {

	private static final Set<String> firsts = new HashSet<>(Arrays.asList(
		"create", "goto", "delete", "size", "list"
	));
	
	private final GuildManager guilds;
	public CommandEditHouseGuilds(HalystiaRPG main, GuildManager guilds) {
		super(main, "edit-house");
		this.guilds = guilds;
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

		if ( ! firsts.contains(args[0]) ) {
			sendHelp(p, label);
			return true;
		}
		
		if ( args[0].equals("list")) {
			sendList(p);
			return true;
		}
		if(args[0].equals("create")) {
			if(args.length < 3) {
				p.sendMessage(RED + "/"+label + " create <id> <size>");
				return true;
			}
			if(! args[1].matches("[a-zA-Z0-9]+")) {
				p.sendMessage(RED + "Le nom ne doit contenir que des caractères alphanumériques.");
				return true;
			}
			HouseSize size = HouseSize.fromString(args[2]);
			if(size == HouseSize.UNDEFINED) {
				p.sendMessage(GREEN + "Tailles autorisées : SMALL, MEDIUM, LARGE, PALACE.");
				return true;
			}
			if ( guilds.getHousesRegistry().generateHouse(args[1], size, p.getLocation().getChunk()) ) {
				p.sendMessage(GREEN + "Succès : la maison a été créée.");
			} else {
				p.sendMessage(RED + "Une erreur est survenue. Soit l'id existe déjà, soit il y a déjà une maison sur ce chunk.");
			}
			return true;
		}
		
		if ( args[0].equals("goto") ) {
			if(args.length < 2) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" goto <id>");
				return true;
			}
			String id = args[1];
			if ( ! guilds.getHousesRegistry().houseIdExists(id) ) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "L'id '"+DARK_RED+id+RED+"' n'existe pas.");
				return true;
			}
			GuildHouse house = guilds.getHousesRegistry().getHouse(id);
			int x = house.getChunkX() * 16 + (house.getChunkX() >= 0 ? 8 : -8);
			int z = house.getChunkZ() * 16 + (house.getChunkZ() >= 0 ? 8 : -8);
			p.teleport(new Location(Bukkit.getWorld(HalystiaRPG.WORLD), x, 100, z));
			p.playSound(p.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 5f, 1.6f);
			return true;
		}
		
		if(args[0].equals("delete")) {
			if(args.length < 2) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" delete <id>");
				return true;
			}
			String id = args[1];
			if ( ! guilds.getHousesRegistry().houseIdExists(id) ) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "L'id '"+DARK_RED+id+RED+"' n'existe pas.");
				return true;
			}
			guilds.getHousesRegistry().unregisterHouse(id);
			p.sendMessage(HalystiaRPG.PREFIX + DARK_RED + "" + BOLD + "Maison détruite.");
			p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 6f, .7f);
			return true;
		}
		
		if(args[0].equals("size")) {
			if(args.length < 2) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" size <id> [size]");
				return true;
			}
			String id = args[1];
			if ( ! guilds.getHousesRegistry().houseIdExists(id) ) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "L'id '"+DARK_RED+id+RED+"' n'existe pas.");
				return true;
			}
			GuildHouse house = guilds.getHousesRegistry().getHouse(id);
			if(args.length < 3) {
				p.sendMessage(HalystiaRPG.PREFIX + GREEN + "La maison '"+id+"' est de taille " + AQUA + house.getSize() + GREEN + ".");
				return true;
			}
			HouseSize size = HouseSize.fromString(args[2]);
			if(size == HouseSize.UNDEFINED) {
				p.sendMessage(GREEN + "Tailles autorisées : SMALL, MEDIUM, LARGE, PALACE.");
				return true;
			}
			guilds.getHousesRegistry().changeSize(house, size);
			p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Nouvelle taille : " + AQUA + size + GREEN + ".");
			p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 6f, .7f);
			return true;
		}
		
		p.sendMessage(RED + "Commande ["+args[0] +"] autorisée mais non paramétrée...");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 1)
			return firsts.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2 && (! args[0].equals("list") ))
			return guilds.getHousesRegistry().getAllHouses().stream().map(h -> h.getID()).filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		if(args.length <= 3)
			if(args[0].equals("create") || args[0].equals("size"))
				return Arrays.asList(HouseSize.values()).stream().filter(s->s!=HouseSize.UNDEFINED).map(d -> d.toString()).filter(str -> str.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
	private void sendHelp(Player p, String label) {
		p.sendMessage(WHITE + " > Menu d'aide pour les maisons de guilde <");
		p.sendMessage(AQUA + "/" + label + " create <id> <size>" + WHITE + ": Créer une nouvelle maison sur ce chunk.");
		p.sendMessage(AQUA + "/" + label + " delete <id>" + WHITE + ": Supprime la maison où vous vous trouvez.");
		p.sendMessage(AQUA + "/" + label + " size <id> [size] " + WHITE + ": Change (ou lis) la taille de la maison où vous vous trouvez.");
		p.sendMessage(AQUA + "/" + label + " goto <id> " + WHITE + ": Vous téléporte à une maison.");
		p.sendMessage(AQUA + "/" + label + " list " + WHITE + ": Liste les maisons existantes.");
	}

	private void sendList(Player p) {
		Collection<GuildHouse> houses = this.guilds.getHousesRegistry().getAllHouses();
		p.sendMessage(BLUE + "Liste des " + houses.size() + " maisons :");
		for(GuildHouse h : houses) {
			p.sendMessage(BLUE + "["+YELLOW+h.getID()+BLUE+"] ("+h.getSize()+BLUE+") - x=" + h.getChunkX() + ", z=" + h.getChunkZ());
		}
	}
}