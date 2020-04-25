package fr.jamailun.halystia.donjons.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.Donjon;
import fr.jamailun.halystia.players.PlayerData;

public class CommandJoinDonjon implements CommandExecutor {

	private HalystiaRPG api;
	
	public CommandJoinDonjon(HalystiaRPG api) {
		this.api = api;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String cmd, String[] args) {
		if(!(sender instanceof BlockCommandSender)) {
			sender.sendMessage(ChatColor.RED + "Commande réservée aux commands blocs !");
			return true;
		}
		if(args.length < 1)
			return false;

		Player p = null;
		Block cmdB = ((BlockCommandSender)sender).getBlock();
		double distance = 5;
		for(Player pl : cmdB.getWorld().getPlayers()) {
			double dis = 5.1;
			Location loc = cmdB.getLocation();
			if(pl.getWorld() != loc.getWorld())
				continue;
			dis = Math.sqrt(Math.pow(loc.getX() - pl.getPlayer().getLocation().getX(), 2) + Math.pow(loc.getY() - pl.getPlayer().getLocation().getY(), 2)+ Math.pow(loc.getZ() - pl.getPlayer().getLocation().getZ(), 2));
			if(dis < distance) {
				distance = dis;
				p = pl;
			}
		}
		
		if(p == null) {
			sender.sendMessage("Personne n'a été trouvé...");
			return true;
		}
		
		PlayerData pData = api.getClasseManager().getPlayerData(p);
		if(pData == null) {
			p.sendMessage("§cUne erreur est survenue...");
			return true;
		}
		
		Donjon donjon = null;
		for(Donjon dj : api.getDonjonManager().getDonjons())
			if(dj.getName().replaceAll(" ", "_").equals(args[0]))
				donjon = dj;
		
		if(donjon == null) {
			p.sendMessage(ChatColor.DARK_RED + "Le donjon [" + args[0] + "] n'est pas valide !");
			return true;
		}
		
		if(donjon.getLevelNeed() > pData.getLevel()) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut être niveau " + ChatColor.GOLD + donjon.getLevelNeed() + ChatColor.RED + " pour pouvoir entrer dans ce donjon !");
			return true;
		}

		p.playSound(p.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1f, 1f);
		p.getPlayer().teleport(donjon.getEntryInDonjon());
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.DARK_GREEN + "Tu as rejoint le " + donjon.getDonjonDifficulty().color + donjon.getName().toLowerCase() + ChatColor.DARK_GREEN + ".");
		return true;
	}

}
