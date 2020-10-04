package fr.jamailun.halystia.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandGiveWater extends HalystiaCommand {
	
	public CommandGiveWater(HalystiaRPG main) {
		super(main, "givewater");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( (sender instanceof Player)) {
			((Player) sender).getInventory().addItem(main.getJobManager().getItemManager().getWithKey("water").generate());
			return true;
		}
		
		if( (sender instanceof BlockCommandSender)) {
			Block block = ((BlockCommandSender) sender).getBlock();
			Player cible = null;
			double dist = 10;
			for(Player pl : block.getWorld().getPlayers()) {
				double distance = block.getLocation().distance(pl.getLocation());
				if(distance < dist) {
					dist = distance;
					cible = pl;
				}
			}
			if(cible != null)
				cible.getInventory().addItem(main.getJobManager().getItemManager().getWithKey("water").generate());
			return true;
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
}