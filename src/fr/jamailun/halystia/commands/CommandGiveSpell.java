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
import fr.jamailun.halystia.spells.Spell;

public class CommandGiveSpell implements CommandExecutor, Listener {
	
	private final HalystiaRPG main;
	
	private List<String> spells;
	
	public CommandGiveSpell(HalystiaRPG main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
		spells = new ArrayList<>();
		for(String spell : main.getSpellManager().getAllSpellsName()) {
			spells.add(spell);
		}
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
		
		if(args.length == 0) {
			p.sendMessage("/give-spell <spell>");
			return true;
		}
		
		Spell spell = main.getSpellManager().getSpellOfIdentification(args[0]);
		if(spell == null) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Id inconnu ! ("+args[0]+")");
			return true;
		}
		
		p.getInventory().addItem(main.getSpellManager().generateItem(spell));
		p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Tu as bien reçu le spell ("+spell.getColor()+spell.getName()+GREEN+")");
		
		return true;
	}
	
	@EventHandler
	public void playerCompletion(TabCompleteEvent e) {
		String data[] = e.getBuffer().split(" ");
		if(data[0].equals("/give-spell") && e.getSender().isOp()) {
			if(data.length == 1) {
				e.setCompletions(spells);
				return;
			}
			if(data.length == 2) {
				List<String> goods = new ArrayList<>();
				//spells.stream().findAny().filter(s -> s.startsWith(data[1])).orElse("");
				for(String spell : spells)
					if(spell.startsWith(data[1]))
						goods.add(spell);
				e.setCompletions(goods);
			}
		}
	}
	
}
