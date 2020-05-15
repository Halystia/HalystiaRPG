package fr.jamailun.halystia.donjons.util;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.Donjon;
import fr.jamailun.halystia.donjons.DonjonI;

public class CommandBossDonjon implements CommandExecutor {

	private HalystiaRPG api;
	
	public CommandBossDonjon(HalystiaRPG api) {
		this.api = api;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String cmd, String[] args) {
		if(!(sender instanceof BlockCommandSender)) {
			sender.sendMessage(RED + "Commande réservée aux commands blocs !");
			return true;
		}
		if(args.length < 1)
			return false;
		
		DonjonI donjon = null;
		for(DonjonI dj : api.getDonjonManager().getDonjons())
			if(dj.getConfigName().equals(args[0]))
				donjon = dj;
		
		Player p = null;
		Block cmdBlock = ((BlockCommandSender)sender).getBlock();
		double distance = 3;
		for(Player pl : cmdBlock.getWorld().getPlayers()) {
			double dis = cmdBlock.getLocation().distance(pl.getLocation());
			if(dis < distance) {
				distance = dis;
				p = pl;
			}
		}
		
		if(p == null) {
			sender.sendMessage("Personne n'a été trouvé...");
			return true;
		}
		
		if(donjon == null) {
			sender.sendMessage(DARK_RED + "Le donjon [" + args[0] + "] n'est pas valide !");
			return true;
		}

		Donjon.removeKeysFromPlayer(p);
		donjon.trySpawnBoss(p);
		return true;
	}

}
