package fr.jamailun.halystia.chunks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.enemies.tags.MetaTag.Type;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.PlayerUtils;
import fr.jamailun.halystia.utils.RandomPick;

public class ChunkType {
	
	public final static List<String> tags = Arrays.asList("spawn-limit");
	
	public static final List<MetaTag> metaDatas = Arrays.asList(
			 new MetaTag("collectable", Type.BOOLEAN)	// if players can collect ressources.
			,new MetaTag("title", Type.STRING)			// title displayed
			,new MetaTag("subtitle", Type.STRING)			// title displayed
	);
		
	public static MetaTag getTag(String name) {
		for(MetaTag tag : metaDatas)
			if(tag.getName().equalsIgnoreCase(name))
				return tag;
		return null;
	}
		
	public static String getAllTags() {
		StringBuilder b = new StringBuilder();
		metaDatas.forEach(g -> b.append(g.getType() == Type.BOOLEAN ? ChatColor.AQUA : ChatColor.YELLOW).append(g.getName() + " "));
		return b.toString();
	}
	
	protected Material icon;
	protected String name;
	protected HashMap<String, Integer> possiblesSpawns;
	private String title, subtitle;
	
	private boolean collectable;
	/**
	 * Create new ChunkType in config.
	 */
	ChunkType(String name, HashMap<String, Integer> possiblesSpawns, Material icon, ChunkCreator creator) {
		this.name = name;
		this.possiblesSpawns = new HashMap<>(possiblesSpawns);
		FileConfiguration config = creator.getConfig();
		this.icon = icon;
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
		if( config.contains(name+".collectable") ) {
			collectable = config.getString(name+".collectable") == "true" || config.getString(name+".collectable") == "1";
		}
		if( config.contains(name+".title") ) {
			title = config.getString(name+".title");
		}
		if( config.contains(name+".title") ) {
			subtitle = config.getString(name+".subtitle");
		}
		
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
		return new ItemBuilder(icon).setName(ChatColor.BLUE + name).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
	}
	
	public HashMap<String, Integer> getSpawnPossibilities() {
		return new HashMap<>(possiblesSpawns);
	}
	
	public boolean isHarvestable() {
		return collectable;
	}
	
	public boolean haveTitle() {
		return title != null;
	}
	
	public boolean haveSubTitle() {
		return subtitle != null;
	}
	
	public void sendTitleToPlayer(Player p) {
		new PlayerUtils(p).sendTitle(10, 40, 10, haveTitle() ? title : "", haveSubTitle() ? subtitle : "");
	}
}