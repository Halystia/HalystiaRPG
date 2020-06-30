package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;

/**
 * This command is made for builder, who would like to teleport to some npcs.
 * @author jamailun
 */
public class CommandNpcTeleport extends HalystiaCommand {

	private NpcManager npcs;
	public CommandNpcTeleport(HalystiaRPG main, NpcManager npcs) {
		super(main, "tpnpc");
		this.npcs = npcs;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être un joueur pour se téléporter à un npc.");
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Précise l'ID du NPC auquel tu souhaites te téléporter.");
			return true;
		}
		RpgNpc npc = npcs.getNpcWithConfigId(args[0]);
		if(npc == null) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Le npc d'id '"+args[0]+"' n'existe pas.");
			return true;
		}
		try {
			((Player)sender).teleport(npc.getNPC().getEntity());
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Succès : vous avez été téléporté à " + npc.getNPC().getName() + ChatColor.GREEN + ".");
		}catch (NullPointerException e) {
			sender.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue... Ce NPC disfoncionne.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1)
			return npcs.getAllConfigIds().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
}