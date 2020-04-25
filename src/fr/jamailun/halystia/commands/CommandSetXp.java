package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandSetXp implements CommandExecutor, Listener {
	
	private final HalystiaRPG main;
	
	public CommandSetXp(HalystiaRPG main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		
		if(args.length < 2) {
			p.sendMessage("/set-xp <joueur> <exp>");
			return true;
		}
		
		String plStr = args[0];
		Player cible = Bukkit.getPlayer(plStr);
		if(cible == null) {
			p.sendMessage(RED + "Joueur inconnu : " + DARK_RED + plStr + RED + ".");
			return true;
		}
		
		int xp = -1;
		try {
			xp = Integer.parseInt(args[1]);
			if(xp < 0)
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			p.sendMessage(RED + "Nombre incorrect : " + DARK_RED + args[1] + RED + ".");
			return true;
		}
		
		main.getClasseManager().getPlayerData(cible).forceXp(xp);
		p.sendMessage(GREEN+"Succès !");
		
		return true;
	}
	
	@EventHandler
	public void playerCompletion(TabCompleteEvent e) {
		String data[] = e.getBuffer().split(" ");
		if(data[0].equals("/set-xp") && e.getSender().isOp()) {
			if(data.length <= 2 && ((!e.getBuffer().endsWith(" ")) && data.length > 1)) {
				List<String> strs = new ArrayList<>();
				Bukkit.getOnlinePlayers().forEach(pl -> {strs.add(pl.getName());});
				e.setCompletions(strs);
			} else {
				e.setCompletions(new ArrayList<>());
			}
		}
	}
	
}
