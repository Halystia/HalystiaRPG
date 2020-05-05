package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.chunks.ChunkType;

public class CommandSetChunk extends HalystiaCommand {
	
	private static List<Player> editers;
	
	private static List<String> mode = Arrays.asList("set", "toggle-vision", "reset");
	
	public CommandSetChunk(HalystiaRPG main) {
		super(main, "set-chunk");
		editers = new ArrayList<>();
	}
	
	public static boolean isObservingChunkValues(Player p) {
		return editers.contains(p);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		if( ! HalystiaRPG.isInRpgWorld(p)) {
			sender.sendMessage(RED + "Il faut être dans le bon monde !");
			return true;
		}
		
		if(args.length == 0) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "/set-chunk <§bset §e<nom> [rayon (convertit les chunks dans un carré de demi-coté égal à 'rayon')] §c| §btoggle-vision§c>");
			return true;
		}
		
		if(args[0].equals("toggle-vision")) {
			togglePlayer(p);
			return true;
		}
		
		if(args[0].equals("reset")) {
			main.getChunkManager().deleteValueOfChunk(p.getLocation().getChunk());
			p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Sucès. Chunk en (" + p.getLocation().getChunk().getX() + ";" + p.getLocation().getChunk().getZ() + ") reset.");
			return true;
		}
		
		if(args.length < 2) {
			p.sendMessage(HalystiaRPG.PREFIX + "§c/set-chunk set §e<nom> §a[rayon §8(convertit les chunks dans un carré de demi-coté égal à 'rayon')§a]");
			return true;
		}
		
		String chunkName = args[1];
		ChunkType type = main.getChunkCreator().getChunkType(chunkName);
		if(type == null) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Chunk value inconnue : ["+DARK_RED+chunkName+RED+"]");
			return true;
		}
		
		Chunk c = p.getLocation().getChunk();
		int rayon = 0;
		if(args.length == 3) {
			try {
				rayon = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				p.sendMessage(HalystiaRPG.PREFIX + RED+"Le rayon n'est un nombre entier valide : ["+DARK_RED+args[2]+RED+"].");
				return true;
			}
		}
		
		for(int x = c.getX() - rayon; x <= c.getX() + rayon; x++) {
			for(int z = c.getZ() - rayon; z <= c.getZ() + rayon; z++) {
				Chunk cc = p.getWorld().getChunkAt(x, z);
				final ChunkType old = main.getChunkManager().getValueOfChunk(cc);
				main.getChunkManager().setValueOfChunk(cc, type);
				if(old == null) {
					p.sendMessage(HalystiaRPG.PREFIX + GREEN+"Chunk en ["+cc.getX()+","+cc.getZ()+"] a la valeur " +BLUE+type.getName()+GREEN+".");
				} else {
					p.sendMessage(HalystiaRPG.PREFIX + YELLOW+"Chunk en ["+cc.getX()+","+cc.getZ()+"] a la valeur " +BLUE+type.getName()+YELLOW+", son ancienne était " + RED + old.getName() + YELLOW + ".");
				}
			}
		}
		
		return true;
	}
	
	public static void togglePlayer(Player p) {
		if( ! editers.contains(p)) {
			editers.add(p);
			p.sendMessage(HalystiaRPG.PREFIX + GREEN+"Vous avez "+YELLOW+"activé"+GREEN+" la vision des valeurs de chunk.");
			sendChunkReport(p);
			return;
		}
		editers.remove(p);
		p.sendMessage(HalystiaRPG.PREFIX + GREEN+"Vous avez "+YELLOW+"désactivé"+GREEN+" la vision des valeurs de chunk.");
	}
	
	public static void sendChunkReport(Player p) {
		Chunk c = p.getLocation().getChunk();
		ChunkType type = HalystiaRPG.getInstance().getChunkManager().getValueOfChunk(c);
		String value = (type == null ? GRAY+"(Aucune)" : type.getName());
		p.sendMessage(HalystiaRPG.PREFIX + YELLOW + "[Chunk] ("+BLUE+c.getX()+YELLOW+","+GREEN+c.getZ()+YELLOW+") : [" + RED + value + YELLOW + "]");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return mode.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2 && args[0].equals("set"))
			return main.getChunkCreator().getChunkTypeList().stream().map(ch -> ch.getName()).filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
}
