package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawner;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawnerManager;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawnerType;
import fr.jamailun.halystia.enemies.mobs.MobManager;

public class CommandSetSpawner extends HalystiaCommand {
	
	private final MobManager mobs;
	private final MobSpawnerManager spawners;
	private static List<String> first = Arrays.asList("create", "mode", "remove");
	
	public CommandSetSpawner(HalystiaRPG main, MobManager mobs, MobSpawnerManager spawners) {
		super(main, "set-spawner");
		this.mobs = mobs;
		this.spawners = spawners;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Il faut être un joueur !");
			return true;
		}
		Player p = (Player) sender;
		if( ! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		if(args.length < 1) {
			p.sendMessage(RED + "/" + cmd + " <create/mode/remove>");
			return true;
		}
		
		Block targetedBlock = getTargetBlock(p.getPlayer(), 5);
		
		if(args[0].equalsIgnoreCase("create")) {
			
			if(args.length < 2) {
				p.sendMessage(RED + "/" + cmd + " " + args[0] + " [name]");
				return true;
			}
			if( ! mobs.hasMobName(args[1])) {
				p.sendMessage(RED + "Le mob '" + args[1] + "' n'existe pas.");
				return true;
			}
			
			if(targetedBlock.getType() == Material.SPAWNER) {
				p.sendMessage(RED + "Impossible : le bloc ciblé est déjà un MobSpawner.");
				return true;
			}
			
			spawners.createSpawner(targetedBlock, sender, args[1]);
			
			p.sendMessage(GREEN + "Mob spawner créé !");
			
			return true;
		} else if(args[0].equalsIgnoreCase("mode")) {
			if(args.length < 2) {
				p.sendMessage(RED + "/" + cmd + " " + args[0] + " [data]");
				p.sendMessage(YELLOW + "Types existants :");
				for(MobSpawnerType mst : MobSpawnerType.values())
					p.getPlayer().sendMessage(YELLOW + "-" + mst.toString());
				return true;
			}
			
			MobSpawner spawner = spawners.getSpawner(targetedBlock.getLocation()); 
			if(spawner == null) {
				p.sendMessage(RED + "Ce bloc n'est pas un MobSpawner valide (" + targetedBlock.getType() + ").");
				return true;
			}
			
			MobSpawnerType spawnerType = MobSpawnerType.NORMAL;
			try {
				spawnerType = MobSpawnerType.valueOf(args[1]);
			} catch (IllegalArgumentException e) {
				p.sendMessage(RED + "Le type [" + args[1] + "] n'existe pas !");
				p.sendMessage(YELLOW + "Types existants :");
				for(MobSpawnerType mst : MobSpawnerType.values())
					p.getPlayer().sendMessage(YELLOW + "-" + mst.toString());
				return true;
			}
			
			
			if(spawners.changeSpawnerMode(spawner, spawnerType))
				p.sendMessage(GREEN + "Mode du MobSpawner : " + DARK_GREEN + spawnerType.toString());
			else
				p.sendMessage(RED + "Echec...");
			return true;
		} else if(args[0].equalsIgnoreCase("remove")) {
			
			if(spawners.destroySpawner(targetedBlock, sender))
				p.sendMessage(GREEN + "Mob spawner supprimé !");
			else
				p.sendMessage(RED + "Une erreur est survenue.");
			
			return true;
		}

		p.sendMessage(RED + "/" + cmd + " <create/mode/remove>");
		return true;
	}
	
	public static final Block getTargetBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR)
				continue;
			break;
		}
		return lastBlock;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return first.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2)
			if(args[0].equals("create"))
				return mobs.getAllMobNames().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
			else if(args[0].equals("mode"))
				return Arrays.asList(MobSpawnerType.values()).stream().map(m -> m.toString()).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
		return new ArrayList<>();
	}

}
