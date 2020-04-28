package fr.jamailun.halystia.donjons.util;

import static org.bukkit.ChatColor.*;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.shops.Trade;

public class CommandJoinDonjon implements CommandExecutor {

	private HalystiaRPG api;
	
	public CommandJoinDonjon(HalystiaRPG api) {
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

		Player p = null;
		Block cmdB = ((BlockCommandSender)sender).getBlock();
		double distance = 3;
		for(Player pl : cmdB.getWorld().getPlayers()) {
			double dis = cmdB.getLocation().distance(pl.getLocation());
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
			p.sendMessage(RED + "Une erreur est survenue...");
			return true;
		}
		
		DonjonI donjon = null;
		for(DonjonI dj : api.getDonjonManager().getDonjons())
			if(dj.getConfigName().equals(args[0]))
				donjon = dj;
		
		if(donjon == null) {
			p.sendMessage(DARK_RED + "Le donjon [" + args[0] + "] n'est pas valide !");
			return true;
		}
		
		if(donjon.getLevelNeed() > pData.getLevel()) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Il faut être niveau " + GOLD + donjon.getLevelNeed() + RED + " pour pouvoir entrer dans ce donjon !");
			return true;
		}
		
		if(donjon.isPlayerInside(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu es déjà dans ce donjon...");
			return true;
		}
		
		Trade trade = new Trade(null, donjon.getKeyNeed());
		if( ! trade.trade(p, true) ) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la clef du donjon.");
			return true;
		}

		p.playSound(p.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1f, 1f);
		p.getPlayer().teleport(donjon.getEntryInDonjon());
		donjon.playerEnterDonjon(p);
		p.sendMessage(HalystiaRPG.PREFIX + DARK_GREEN + "Tu as rejoint le " + donjon.getDonjonDifficulty().color + donjon.getName().toLowerCase() + DARK_GREEN + ".");
		return true;
	}

}
