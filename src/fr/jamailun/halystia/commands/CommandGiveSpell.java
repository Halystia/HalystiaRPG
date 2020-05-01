package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.spells.Spell;

public class CommandGiveSpell extends HalystiaCommand {
	
	public CommandGiveSpell(HalystiaRPG main) {
		super(main, "give-spell");
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
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return main.getSpellManager().getAllSpellsName().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
}