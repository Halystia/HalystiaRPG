package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.TabCompleteEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandGivePotion implements CommandExecutor {
	
	private final HalystiaRPG main;
	
	public CommandGivePotion(HalystiaRPG main) {
		this.main = main;
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
	
	@EventHandler
	public void playerCompletion(TabCompleteEvent e) {
		String data[] = e.getBuffer().split(" ");
		if(data[0].equals("/give-spell") && e.getSender().isOp()) {
			e.setCompletions(new ArrayList<>());
		}
	}
	
}
