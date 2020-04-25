package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.ItemBuilder;

public class CommandIS implements CommandExecutor, Listener {
	
	private List<String> materials;
	
	public CommandIS(HalystiaRPG main) {
		Bukkit.getPluginManager().registerEvents(this, main);
		materials = new ArrayList<>();
		for(Material mat : Material.values()) {
			if(mat != Material.AIR)
				materials.add(mat.toString().toLowerCase());
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length < 2) {
			p.sendMessage("/is <material> <title> ; <lore 1>, <lore 2>, ... , <lore n>");
			return true;
		}
		
		Material mat = Material.AIR;
		try {
			mat = Material.valueOf(args[0].toUpperCase());
		} catch(IllegalArgumentException e) {
			p.sendMessage(HalystiaRPG.PREFIX + "Mauvais matériau : [" + args[0] + "]");
			return true;
		}
		if(mat == Material.AIR) {
			p.sendMessage(HalystiaRPG.PREFIX + "Mauvais matériau : [" + args[0] + "]");
			return true;
		}
		
		StringBuilder str = new StringBuilder(args[1]);
		for(int i = 2; i < args.length; i++)
			str.append(" " + args[i]);
		
		String[] both = str.toString().split(";", 2);
		String title = WHITE + "" + BOLD + ChatColor.translateAlternateColorCodes('&', both[0]);
		
		List<String> lore = new ArrayList<>();
		if(both.length > 1) {
			String[] allLores = both[1].split(",");
			for(int i = 0; i < allLores.length; i++)
				lore.add(GRAY + ChatColor.translateAlternateColorCodes('&', allLores[i]));
		}
		
		ItemBuilder builder = new ItemBuilder(mat).setName(title);
		builder.setLore(lore);
		p.getInventory().addItem(builder.toItemStack());
		p.sendMessage(GREEN+"Objet créé avec succès !");
		
		return true;
	}
	
	@EventHandler
	public void playerCompletion(TabCompleteEvent e) {
		String data[] = e.getBuffer().split(" ");
		if(data[0].equals("/is") && e.getSender().isOp()) {
			if(data.length == 2 && (!e.getBuffer().endsWith(" "))) {
				List<String> goods = new ArrayList<>();
				for(String str : materials)
					if(str.startsWith(data[1]))
						goods.add(str);
				e.setCompletions(goods);
				return;
			}
			e.setCompletions(new ArrayList<>());
		}
	}
	
}
