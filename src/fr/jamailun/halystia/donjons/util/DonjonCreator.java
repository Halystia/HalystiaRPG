package fr.jamailun.halystia.donjons.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;

public class DonjonCreator {

	private DonjonI donjon;
	
	public DonjonCreator(DonjonI donjon) {
		this.donjon = donjon;
	}
	
	public void createEntry(Location loc) {
		loc.getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		
		Block obsi = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ()).getBlock();
		obsi.setType(Material.OBSIDIAN);
		
		Location cmdLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 2, loc.getZ());
		Block cmdBlock = cmdLoc.getWorld().getBlockAt(cmdLoc);
		cmdBlock.setType(Material.COMMAND_BLOCK);
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				CommandBlock cmdBlockData = (CommandBlock) cmdBlock.getState();
				cmdBlockData.setCommand("joindonjon " + donjon.getName().replaceAll(" ", "_"));
				cmdBlockData.update();
			}
		}, 20L);
		 
		List<String> lines = new ArrayList<>();
		lines.add(donjon.getDonjonDifficulty().color + "" + ChatColor.BOLD + donjon.getName());
		lines.add(ChatColor.GRAY + "Difficultée : " + donjon.getDonjonDifficulty().getDisplayName());
		lines.add(ChatColor.GRAY + "Niveau minimum : " + ChatColor.YELLOW + donjon.getLevelNeed());
		
		Location locBase = new Location(loc.getWorld(), loc.getX(), loc.getY() + (lines.size() * 0.6), loc.getZ());
		for(int i = 0; i < lines.size(); i++) {
			Location aLoc = new Location(locBase.getWorld(), locBase.getX(), locBase.getY() - (i * 0.5), locBase.getZ());
			ArmorStand as = (ArmorStand) aLoc.getWorld().spawnEntity(aLoc, EntityType.ARMOR_STAND);
			as.setInvulnerable(true);
			as.setVisible(false);
			as.setGravity(false);
			as.setCustomNameVisible(true);
			as.setCustomName(lines.get(i));
		}
		
	}
	
}
