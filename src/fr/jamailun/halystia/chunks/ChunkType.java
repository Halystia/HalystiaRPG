package fr.jamailun.halystia.chunks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.RandomPick;

public class ChunkType {
	
	public final static List<String> tags = Arrays.asList("spawn-limit");
	
	Material icon;
	String name;
	HashMap<String, Integer> possiblesSpawns;
	int limitSpawns = 5;
	/**
	 * Create new ChunkType in config.
	 */
	ChunkType(String name, HashMap<String, Integer> possiblesSpawns, Material icon, ChunkCreator creator) {
		this.name = name;
		this.possiblesSpawns = new HashMap<>(possiblesSpawns);
		FileConfiguration config = creator.getConfig();
		this.icon = icon;
		this.limitSpawns = 5;
		config.set(name+".icon", icon.toString());
		config.set(name+".spawn-limit", 5);
		int i = 0;
		for(String key : possiblesSpawns.keySet()) {
			config.set(name+"."+i+".mobId", key);
			config.set(name+"."+i+".chances", possiblesSpawns.get(key));
			i++;
		}
		creator.saveConfig();
	}
	/**
	 * Get ChunkType FROM config.
	 */
	ChunkType(String name, ChunkCreator creator) {
		this.name = name;
		FileConfiguration config = creator.getConfig();
		try {
			icon = Material.valueOf(config.getString(name+".icon"));
		} catch (Exception e) {
			icon = Material.BARRIER;
			System.err.println("Impossible de lire le material [" + config.getString(name+".icon") + "] dans chunktype, name="+name+".");
		}
		possiblesSpawns = new HashMap<>();
		int i = 0;
		while(config.contains(name+"."+i)) {
			String mob = config.getString(name+"."+i+".mobId");
			int chances = config.getInt(name+"."+i+".chances");
			possiblesSpawns.put(mob, chances);
			i++;
		}
		if( ! config.contains(name+".spawn-limit")) {
			config.set(name+".spawn-limit", 5);
			creator.saveConfig();
		}
		limitSpawns = config.getInt(name+".spawn-limit");
		
	}
	
	void update(ChunkCreator creator) {
		FileConfiguration config = creator.getConfig();
		config.set(name+".icon", icon.toString());
		int i = 0;
		for(String key : possiblesSpawns.keySet()) {
			config.set(name+"."+i+".mobId", key);
			config.set(name+"."+i+".chances", possiblesSpawns.get(key));
			i++;
		}
		creator.saveConfig();
	}
	
	/**
	 * @return null if no mob can be spawned here;
	 */
	public String createMobIdToSpawn() {
		if(possiblesSpawns.isEmpty())
			return null;
		return new RandomPick<>(possiblesSpawns).nextPick();
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getIcone() {
		return new ItemBuilder(icon).setName(ChatColor.BLUE + name).toItemStack();
	}
	
	public HashMap<String, Integer> getSpawnPossibilities() {
		return new HashMap<>(possiblesSpawns);
	}
	
	public int getSpawnLimit() {
		return limitSpawns;
	}
	
	public void changeSpawnLimit(int limit, ChunkCreator creator) {
		this.limitSpawns = limit;
		creator.getConfig().set("spawn-limit", 5);
		creator.saveConfig();
	}
	
}
