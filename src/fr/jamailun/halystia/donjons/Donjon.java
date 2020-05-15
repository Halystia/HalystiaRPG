package fr.jamailun.halystia.donjons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.boss.Boss;
import fr.jamailun.halystia.enemies.boss.BossManager;
import fr.jamailun.halystia.enemies.boss.model.NemasiaBoss;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.Reloadable;

public class Donjon extends FileDataRPG implements DonjonI, Reloadable {

	private final String configName;
	private String name;
	private Location entry, exit, bossLocation, bossEntry;
	private int xpReward, levelNeeded;
	private DonjonDifficulty difficulty;
	
	private Boss boss;
	
	private final Map<UUID, Integer> inside;
	private final Map<UUID, Boolean> insideBoss;
	
	private final BossManager bosses;
	public Donjon(String path, String name, BossManager bosses) {
		super(path, name);
		this.configName = name;
		this.bosses = bosses;
		inside = new HashMap<>();
		insideBoss = new HashMap<>();
		reloadData();
	}
	
	public void reloadData() {
		preloadData();
		
		name = config.getString("name");
		entry = config.getLocation("entry");
		exit = config.getLocation("exit");
		bossLocation = config.getLocation("boss");
		bossEntry = config.getLocation("bossroom");
		xpReward = config.getInt("reward");
		levelNeeded = config.getInt("level");
		
		try {
			difficulty = DonjonDifficulty.valueOf(config.getString("difficulty"));
		} catch (IllegalArgumentException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"La difficulté du donjon ("+configName+") n'est pas valide.");
			difficulty = DonjonDifficulty.FACILE;
		}
		
		try {
			Class<?> bossClasse = Class.forName(config.getString("boss-class"));
			if ( ! bossClasse.getSuperclass().equals(Boss.class) )
				throw new ClassNotFoundException("The boss of donjon ("+configName+") is not correct !");
			boss = (Boss) bossClasse.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			//e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "boss not found : " + e.getMessage());
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
		if( ! config.contains("bossroom"))
			config.set("bossroom", new Location(Bukkit.getWorld(HalystiaRPG.WORLD), 0, 0, 0));
		if( ! config.contains("reward"))
			config.set("reward", 500);
		if( ! config.contains("level"))
			config.set("level", 10);
		if( ! config.contains("difficulty"))
			config.set("difficulty", DonjonDifficulty.FACILE.toString());
		if( ! config.contains("boss-class"))
			config.set("boss-class", NemasiaBoss.class.getName());
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
		bossLocation = loc.clone();
		synchronized (file) {
			config.set("boss", loc);
			save();
		}
	}
	
	public void changeBossRoomLocation(Location loc) {
		bossEntry = loc.clone();
		synchronized (file) {
			config.set("bossroom", loc);
			save();
		}
	}
	
	public boolean changeBossType(String bossClasseString) {
		try {
			Class<?> bossClasse = Class.forName(bossClasseString);
			if ( ! bossClasse.getSuperclass().equals(Boss.class) )
				throw new ClassNotFoundException("The boss of donjon ("+configName+") is not correct !");
			boss = (Boss) bossClasse.newInstance();
			
			synchronized (file) {
				config.set("boss-class", bossClasseString);
				save();
			}
			return true;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			//e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "boss not found : " + e.getMessage());
		}
		return false;
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
		return new HashSet<>(inside.keySet());
	}

	@Override
	public boolean isPlayerInside(Player p) {
		return inside.containsKey(p.getUniqueId());
	}

	@Override
	public boolean playerEnterDonjon(Player p) {
		if(isPlayerInside(p))
			return false;
		inside.put(p.getUniqueId(), 3);
		insideBoss.put(p.getUniqueId(), false);
		return true;
	}

	@Override
	public boolean forcePlayerExit(Player p, boolean silent) {
		if( ! isPlayerInside(p))
			return false;
		inside.remove(p.getUniqueId());
		insideBoss.remove(p.getUniqueId());
		if(!silent)
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous quittez le donjon.");
		return true;
	}

	@Override
	public Location getBossLocation() {
		return bossLocation;
	}

	@Override
	public boolean trySpawnBoss(Player player) {
		if(boss == null)
			return false;
		insideBoss.put(player.getUniqueId(), true);
		if(boss.exists())
			return false;
		boss.spawnBoss(this);
		bosses.bossSpawned(boss);
		return false;
	}

	@Override
	public Location respawn(Player player) {
		int remaining = inside.get(player.getUniqueId()) - 1;
		if(remaining >= 1) {
			player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous êtes mort. Il ne vous reste plus que " + ChatColor.DARK_RED + remaining + ChatColor.RED + " vie"+(remaining>1?"s":"")+".");
			if(remaining == 1)
				player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Si vous mourrez à nouveau, vous serez jetté en dehors du donjon.");
				
			inside.replace(player.getUniqueId(), remaining);
			return insideBoss.get(player.getUniqueId()) ? bossEntry : entry;
		}
		player.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous êtes mort 3 fois. Vous avez été téléporté en dehors du donjon.");
		inside.remove(player.getUniqueId());
		insideBoss.remove(player.getUniqueId());
		removeKeysFromPlayer(player);
		return exit;
	}
	
	public static void removeKeysFromPlayer(Player player) {
		Arrays.asList(player.getInventory().getContents()).stream().filter(i -> Trade.areItemsTheSame(i, EnemyMob.DONJON_KEY)).forEach(i -> i.setAmount(0));
	}

	@Override
	public void hideBossBar(Player player) {
		boss.hideBar(player);
	}
	
}