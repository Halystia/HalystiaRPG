package fr.jamailun.halystia.enemies.boss;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.Enemy;
import fr.jamailun.halystia.players.PlayerData;

public abstract class Boss implements Enemy {

	private BukkitRunnable runnable = new BukkitRunnable() {public void run() {doAction();}};
	
	protected boolean exists = false;
	protected double maxHealth = 100;
	protected double health = 100;
	protected Map<UUID, Double> damagers = new HashMap<>();
	protected BossBar bar = Bukkit.createBossBar("unset", BarColor.WHITE, BarStyle.SOLID, BarFlag.CREATE_FOG);
	
	public abstract boolean canMove();
	
	protected abstract void doAction();
	
	public boolean exists() {
		return exists;
	}
	
	protected void startActionLoop() {
		runnable.runTaskTimer(HalystiaRPG.getInstance(), 20L, 20L);
	}
	
	protected void stopLoop() {
		runnable.cancel();
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
		if(health <= 0 || (! exists())) {
			if(getXp() > 0)
				displayToDamagersBests();
			repartLootsAndXp();
			killed();
		}
	}

	protected void updateBar() {
		double per = health / maxHealth;
		if(per < 0) per = 0; if(per > 1) per = 1;
		bar.setProgress(per);
	}
	
	protected abstract void killed();
	
	public abstract double distance(Location loc);
	
	public abstract String getCustomName();
	
	protected abstract boolean isBoss(UUID uuid);
	
	public abstract List<ItemStack> getLoots();

	public abstract int getXp();
	
	public abstract void purge();
	
	public abstract void spawn(DonjonI donjon);
	
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
				pl.sendMessage(HalystiaRPG.PREFIX + GOLD + "Autres dégâts : " + RED + otherDmgs + " hp " + GOLD + " par " + BLUE + (damagers.size() - 3) + GOLD + " autres joueurs.");
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