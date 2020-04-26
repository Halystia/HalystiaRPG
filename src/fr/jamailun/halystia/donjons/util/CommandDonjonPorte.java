package fr.jamailun.halystia.donjons.util;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.animations.PorteDonjon;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.ItemBuilder;

public class CommandDonjonPorte implements CommandExecutor {

	private HalystiaRPG api;
	
	public CommandDonjonPorte(HalystiaRPG api) {
		this.api = api;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String cmd, String[] args) {
		if(!(sender instanceof BlockCommandSender)) {
			sender.sendMessage(ChatColor.RED + "Commande réservée aux commands blocs !");
			return true;
		}
		
		if(args.length < 4)
			return false;
		
		
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
		
		int how = 0;
		try {
			how = Integer.parseInt(args[1]);
		} catch(Exception e1) {
			sender.sendMessage(ChatColor.RED + "ERREUR §lparse§c CMD BLOCK"); //cost
			return true;
		}
		
		if(args.length < 5) {
			sender.sendMessage(ChatColor.RED + "MANQUE DES ARGUMENTS");
			return false;
		}
		
		int x=0; int y=0; int z=0;
		try {
			x = Integer.parseInt(args[2]);
			y = Integer.parseInt(args[3]);
			z = Integer.parseInt(args[4]);
		} catch(Exception e1) {
			sender.sendMessage(ChatColor.RED + "Erreur de parsing"); //x y z
			return false;
		}
		
		sender.sendMessage("ok !");
		
		Location locPorte = new Location(p.getWorld(), x, y, z);
		Block blockPorte = locPorte.getBlock();
		
		Trade trade = new Trade(null, Classe.NONE, null, Arrays.asList(new ItemBuilder(EnemyMob.DONJON_KEY).setAmount(how).toItemStack()), 0);
		
		if(trade.trade(p, true)) {
			for(Player pl : p.getPlayer().getWorld().getPlayers())
				pl.playSound(p.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.3F);
			final PorteDonjon pDj = new PorteDonjon(blockPorte, api);
			removeBouton((BlockCommandSender) sender);
			pDj.open();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(api, new Runnable() {
				public void run() {
					pDj.close();
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(api, new Runnable() {
						public void run() {
							replaceBouton((BlockCommandSender) sender);
						}
					},70L);
				}
			},300L);
			return true;
		}
		p.sendMessage(ChatColor.RED + "Tu n'as pas les " + ChatColor.DARK_RED + how + ChatColor.RED + " âmes nécessaires !");	
		return false;
	}

	private void removeBouton(BlockCommandSender sender) {
		Block b = sender.getBlock();
		Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
		double y = loc.getY();
		y = y + 2;
		loc.setY(y);
		loc.getBlock().setType(Material.AIR);
		for(Player pl : loc.getWorld().getPlayers())
			pl.spawnParticle(Particle.FLAME, loc, 50);
	}

	private void replaceBouton(BlockCommandSender sender) {
		Block b = sender.getBlock();
		Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
		double y = loc.getY();
		y = y + 2;
		loc.setY(y);
		loc.getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		loc.setX(loc.getX() + 0.5);
		loc.setZ(loc.getZ() + 0.5);
		loc.setY(loc.getY() + 1);
		for(Player pl : loc.getWorld().getPlayers()) {
			pl.spawnParticle(Particle.FLAME, loc, 50);
			pl.playSound(loc, Sound.BLOCK_PISTON_CONTRACT, 1.1F, 0.3F);
		}
	}

}
