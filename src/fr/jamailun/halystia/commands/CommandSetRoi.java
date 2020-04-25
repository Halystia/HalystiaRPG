package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.royaumes.Royaume;

public class CommandSetRoi implements CommandExecutor, Listener {
	
	private final HalystiaRPG main;
	
	private List<String> royaumes;
	
	public CommandSetRoi(HalystiaRPG main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
		royaumes = new ArrayList<>();
		for(Royaume r : Royaume.values()) {
			if(r != Royaume.NEUTRE)
				royaumes.add(r.toString().toLowerCase());
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
			p.sendMessage("/set-roi <royaume> <joueur>");
			return true;
		}
		
		String rStr = args[0];
		Royaume r = Royaume.NEUTRE;
		try {
			r = Royaume.valueOf(rStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			p.sendMessage(RED + "Royaume inconnu : " + DARK_RED + rStr + RED + ".");
			return true;
		}
		
		String plStr = args[1];
		Player cible = Bukkit.getPlayer(plStr);
		if(cible == null) {
			p.sendMessage(RED + "Joueur inconnu : " + DARK_RED + plStr + RED + ".");
			return true;
		}
		
		main.getDataBase().setRoi(r, cible);
		
		for(Player pl : Bukkit.getWorld(HalystiaRPG.WORLD).getPlayers()) {
			pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 2f, .8f);
			pl.sendMessage(" ");
			pl.sendMessage(" ");
			pl.sendMessage(" ");
			pl.sendMessage(GOLD + "" + BOLD + "Votre attention !");
			pl.sendMessage(" ");
			pl.sendMessage(GOLD + "Aujourd'hui, un nouveau Roi a été désigné pour le " + r.getColor() + r.getName() + GOLD + " !");
			pl.sendMessage(GOLD + "Il s'agit de " + UNDERLINE + "" + BOLD + "" + DARK_RED + cible.getName() + GOLD + " !");
			pl.sendMessage(GOLD + "Longue vie au nouveau Roi !");
			pl.sendMessage(" ");
		}
		
		return true;
	}
	
	@EventHandler
	public void playerCompletion(TabCompleteEvent e) {
		String data[] = e.getBuffer().split(" ");
		if(data[0].equals("/set-roi") && e.getSender().isOp()) {
			if(data.length == 1) {
				e.setCompletions(royaumes);
			}
		}
	}
	
}
