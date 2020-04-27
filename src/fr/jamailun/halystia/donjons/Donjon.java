package fr.jamailun.halystia.donjons;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.Reloadable;

public class Donjon extends FileDataRPG implements DonjonI, Reloadable {

	private final String configName;
	private String name;
	private Location entry, exit, boss;
	private int xpReward, levelNeeded;
	private DonjonDifficulty difficulty;
	
	private final Set<UUID> inside;
	
	public Donjon(String path, String name) {
		super(path, name);
		this.configName = name;
		inside = new HashSet<>();
		reloadData();
	}
	
	public void reloadData() {
		preloadData();
		
		name = config.getString("name");
		entry = config.getLocation("entry");
		exit = config.getLocation("exit");
		boss = config.getLocation("boss");
		xpReward = config.getInt("reward");
		levelNeeded = config.getInt("level");
		
		try {
			difficulty = DonjonDifficulty.valueOf(config.getString("difficulty"));
		} catch (IllegalArgumentException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"La difficulté du donjon ("+configName+") n'est pas valide.");
			difficulty = DonjonDifficulty.FACILE;
		}
	}
	
	private void preloadData() {
		if( ! config.contains("name"))
			config.set("name", "Donjon "+configName);
		if( ! config.contains("entry"))
			config.set("entry", new Location(Bukkit.getWorld(HalystiaRPG.WORLD), 0, 0, 0));
		if( ! config.contains("exit"))
			config.set("exit", new Location(Bukkit.getWorld(HalystiaRPG.WORLD), 0, 0, 0));
		if( ! config.contains("boss"))
			config.set("boss", new Location(Bukkit.getWorld(HalystiaRPG.WORLD), 0, 0, 0));
		if( ! config.contains("reward"))
			config.set("reward", 500);
		if( ! config.contains("level"))
			config.set("level", 10);
		if( ! config.contains("difficulty"))
			config.set("difficulty", DonjonDifficulty.FACILE.toString());
		save();
	}
	
	public void changeEntryLocation(Location loc) {
		entry = loc.clone();
		synchronized (file) {
			config.set("entry", loc);
			save();
		}
	}
	
	public void changeExitLocation(Location loc) {
		exit = loc.clone();
		synchronized (file) {
			config.set("exit", loc);
			save();
		}
	}
	
	public void changeBossLocation(Location loc) {
		boss = loc.clone();
		synchronized (file) {
			config.set("boss", loc);
			save();
		}
	}
	
	public void changeDonjonName(String name) {
		this.name = name;
		synchronized (file) {
			config.set("name", name);
			save();
		}
	}
	
	public void changeDonjonDifficulty(DonjonDifficulty difficulty) {
		this.difficulty = difficulty;
		synchronized (file) {
			config.set("difficulty", difficulty.toString());
			save();
		}
	}
	
	public void changeExpReward(int xpReward) {
		this.xpReward = xpReward;
		synchronized (file) {
			config.set("reward", xpReward);
			save();
		}
	}
	
	public void changeLevelNeeded(int levelNeeded) {
		this.levelNeeded = levelNeeded;
		synchronized (file) {
			config.set("level", levelNeeded);
			save();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Location getEntryInDonjon() {
		return entry.clone();
	}

	@Override
	public String getWorldName() {
		return entry.getWorld().getName();
	}

	@Override
	public ItemStack getKeyNeed() {
		return new ItemBuilder(Material.TRIPWIRE_HOOK).setName(ChatColor.GOLD+""+ChatColor.BOLD + "Clef du " + getDonjonDifficulty().color + "" + ChatColor.BOLD + getName()).shine().toItemStack();
	}

	@Override
	public int getExpReward() {
		return xpReward;
	}

	@Override
	public int getLevelNeed() {
		return levelNeeded;
	}

	@Override
	public DonjonDifficulty getDonjonDifficulty() {
		return difficulty;
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public void destroy() {
		entry = null;
		super.delete();
	}
	
	public boolean equals(Object o) {
		if(o instanceof Donjon)
			return ((Donjon)o).configName.equals(configName);
		return false;
	}

	@Override
	public Location getExitOfDonjon() {
		return exit.clone();
	}

	@Override
	public Set<UUID> getJoinedPlayers() {
		return new HashSet<>(inside);
	}

	@Override
	public boolean isPlayerInside(Player p) {
		return inside.contains(p.getUniqueId());
	}

	@Override
	public boolean playerEnterDonjon(Player p) {
		if(isPlayerInside(p))
			return false;
		inside.add(p.getUniqueId());
		return true;
	}

	@Override
	public boolean forcePlayerExit(Player p) {
		if( ! isPlayerInside(p))
			return false;
		inside.remove(p.getUniqueId());
		return true;
	}

	@Override
	public Location getBossLocation() {
		return boss;
	}
	
}