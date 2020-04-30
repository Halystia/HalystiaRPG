package fr.jamailun.halystia.jobs2.system;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

import fr.jamailun.halystia.HalystiaRPG;

public class CacheMemory {
	
	private ConcurrentHashMap<Location, Material> blocks;
	
	public CacheMemory() {
		blocks = new ConcurrentHashMap<>();
	}
	
	public void addToBlocksCache(Location location, Material type) {
		if(blocks.containsKey(location))
			blocks.remove(location);
		blocks.put(location, type);
	}
	
	public void removeFromBlocksCache(Location location) {
		if(blocks.containsKey(location))
			blocks.remove(location);
	}
	
	public void applyCache() {
		World world = Bukkit.getWorld(HalystiaRPG.WORLD);
		blocks.forEach((location, type) -> {
			final Block b = world.getBlockAt(location);
			b.setType(type);
			if(b.getBlockData() instanceof Ageable) {
				Ageable age = (Ageable) b.getBlockData();
				age.setAge(((Ageable)b.getBlockData()).getMaximumAge());
				b.setBlockData(age);
				b.getState().update();
			}
		});
		Bukkit.getLogger().log(Level.INFO, "Cache has been appliyed on " + blocks.size() + " blocks.");
		blocks.clear();
	}
}