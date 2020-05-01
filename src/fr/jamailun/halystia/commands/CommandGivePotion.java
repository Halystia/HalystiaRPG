package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandGivePotion extends HalystiaCommand {
	
	public CommandGivePotion(HalystiaRPG main) {
		super(main, "give-potion");
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
			p.sendMessage("/give-potion <mana>");
			return true;
		}
		
		int mana = 0;
		try {
			mana = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "L'argument [" + DARK_RED + args[0] + RED + "] doit être le nombre de points de mana dans la potion !");
			return true;
		}
		
		p.getInventory().addItem(main.getPotionManager().generateManaBottle(mana));
		p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Tu as bien reçu une potion de  " + GOLD + mana + GREEN + " points de mana.");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
	
}
