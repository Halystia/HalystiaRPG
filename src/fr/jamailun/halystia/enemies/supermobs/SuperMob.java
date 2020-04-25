package fr.jamailun.halystia.enemies.supermobs;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;

public abstract class SuperMob {
	
	protected final Location loc;
	protected final double maxHealth;
	protected double health;
	protected Map<UUID, Double> damagers;
	protected BossBar bar;
	
	private final long respawnTime;
	
	public SuperMob(Location loc, double health, long respawnTime) {
		this.loc = loc;
		this.maxHealth = health;
		this.health = health;
		this.respawnTime = respawnTime;
		damagers = new HashMap<>();
		bar = Bukkit.createBossBar(getCustomName(), BarColor.RED, BarStyle.SEGMENTED_10, BarFlag.CREATE_FOG);
		bar.setVisible(true);
	}
	
	public Map<Player, Double> getDamagers() {
		HashMap<Player, Double> map = new HashMap<>();
		for(UUID uuid : damagers.keySet()) {
			if(Bukkit.getPlayer(uuid) != null)
				map.put(Bukkit.getPlayer(uuid), damagers.get(uuid));
		}
		return map;
	}
	
	public void damage(UUID p, double damages) {
		if(damagers.containsKey(p)) {
			damagers.replace(p, damagers.get(p) + damages);
		} else {
			damagers.put(p, damages);
		}
		health -= damages;
		updateBar();
		if(health <= 0 || (! isValid())) {
			if(getXp() > 0)
				displayToDamagersBests();
			killed();
			startCoolDown();
		}
	}

	protected void updateBar() {
		double per = health / maxHealth;
		if(per < 0) per = 0; if(per > 1) per = 1;
		bar.setProgress(per);
	}
	
	protected abstract void killed();
	
	public double distance(Location loc) {
		return this.loc.distance(loc);
	}
	
	public boolean isMob(Entity entity) {
		return entity.getUniqueId().equals(getEntityUUID()) && isValid();
	}
	
	protected abstract String getCustomName();
	
	public abstract int getMinutes();
	
	protected abstract UUID getEntityUUID();
	
	protected abstract boolean isValid();
	
	public abstract List<ItemStack> getLoots();

	public abstract int getXp();
	
	public abstract void purge();
	
	public abstract void spawn();
	
	protected void startCoolDown() {
		bar.setVisible(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				spawn();
				bar.setVisible(true);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), respawnTime);
	}
	
	public void displayToDamagersBests() {
		UUID[] bo = bo3();
		double otherDmgs = totalDamages();
		for(int i = 0; i < bo.length; i++)
			otherDmgs -= damagers.get(bo[i]);
		
		for(UUID id : damagers.keySet()) {
			Player pl = Bukkit.getPlayer(id);
			if(pl == null)
				continue;
			if(bo.length < 2) {
				pl.sendMessage(HalystiaRPG.PREFIX + GOLD + "Tu as tué un " + getCustomName() + GOLD + ".");
				continue;
			}
			pl.sendMessage(HalystiaRPG.PREFIX + GOLD + "Un " + getCustomName() + GOLD + " a été tué ! Top des dégats :");
			
			for(int i = 0; i < bo.length; i++) {
				pl.sendMessage(HalystiaRPG.PREFIX + YELLOW + "["+(i+1)+"] " + Bukkit.getOfflinePlayer(id).getName() + " : " + RED + damagers.get(id) + " hp.");
			}
			if(otherDmgs > 0) {
				pl.sendMessage(HalystiaRPG.PREFIX + GOLD + "Autres dégats : " + RED + otherDmgs + " hp " + GOLD + " par " + BLUE + (damagers.size() - 3) + GOLD + " autres joueurs.");
			}
		}
	}
	
	public void repartLootsAndXp() {
		final double total = totalDamages();
		for(UUID id : damagers.keySet()) {
			double percent = damagers.get(id) / total;
			Player pl = Bukkit.getPlayer(id);
			double xp = ((double)getXp()) * percent;
			if(pl != null) {
				PlayerData plc = HalystiaRPG.getInstance().getClasseManager().getPlayerData(pl);
				if(plc != null)
					plc.addXp((int)xp);
				else
					pl.sendMessage(HalystiaRPG.PREFIX + RED + "Une erreur est survenue. Tu aurais dû gagner " + xp + " xp.");
			}
		}
	}
	
	private double totalDamages() {
		double tt = 0;
		for(UUID id : damagers.keySet())
			tt += damagers.get(id);
		return tt;
	}
	
	public UUID[] bo3() {
		UUID[] bo = new UUID[3];
		Map<UUID, Double> map = sortByValue(damagers);
		int i = 0;
		for(UUID id : map.keySet()) {
			bo[i] = id;
			i++;
		}
		return bo;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

}
