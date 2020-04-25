package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;

public class CommandSummonMob extends HalystiaCommand {
	
	private final MobManager mobs;
	
	public CommandSummonMob(HalystiaRPG main, MobManager mobs) {
		super(main, "summon-mob");
		this.mobs = mobs;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Il faut être un joueur !");
			return true;
		}
		Player p = (Player) sender;
		if( ! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(ChatColor.RED + "Il faut être dans le monde RPG !");
			return true;
		}
		if(args.length < 1) {
			p.sendMessage(ChatColor.RED + "Il faut préciser l'id du mob !");
			return true;
		}
		
		if( ! mobs.getAllMobNames().contains(args[0]) ) {
			p.sendMessage(ChatColor.RED + "Mob id ("+args[0]+") inconnu !");
			return true;
		}
		
		mobs.spawnMob(args[0], p.getLocation(), false);
		p.sendMessage(ChatColor.GREEN + "Mob spawné avec succès.");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return mobs.getAllMobNames().stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
}