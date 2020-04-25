package fr.jamailun.halystia.enemies;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface Enemy {
	
	public String getCustomName();
	
	public List<ItemStack> getLoots();
	
	public int getXp();
	
}