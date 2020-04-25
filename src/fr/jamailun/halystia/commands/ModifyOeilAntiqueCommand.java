package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.RED;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

public class ModifyOeilAntiqueCommand implements CommandExecutor {
	
	private final HalystiaRPG main;
	
	public ModifyOeilAntiqueCommand(HalystiaRPG main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player) sender;
		
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		
		Location location = p.getLocation();
		if(cmd.getName().equals("set-oeil-antique")) {
			main.getSuperMobManager().addMob("oeil", location, p);
		} else { //remove
			main.getSuperMobManager().removeMob("oeil", location, p);
		}
		return true;
	}

}
