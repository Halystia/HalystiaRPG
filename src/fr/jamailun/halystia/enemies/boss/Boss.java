package fr.jamailun.halystia.enemies.boss;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonI;
import fr.jamailun.halystia.enemies.Enemy;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.spells.Invocator;

public abstract class Boss implements Enemy, Invocator {

	private BukkitRunnable runnable = new BukkitRunnable() {public void run() {doAction();}};
	
	protected List<LivingEntity> invocations = new ArrayList<>();
	protected DonjonI donjon = null;
	
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
		runnable = new BukkitRunnable() {public void run() {doAction();}};
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
	
	protected void damage(double damages) {
		if(!exists)
			return;
		health -= damages;
		updateBar();
		damageAnimation();
		//Bukkit.broadcastMessage("§cOOF§e -"+damages+"PV. §d-> " + health+"PV.");
		if(health <= 0 || (! exists())) {
			exists = false;
			stopLoop();
			killed();
			repartLootsAndXp();
			if(getXp() > 0)
				displayToDamagersBests();
		}
	}
	
	protected abstract void damageAnimation();
	
	public void damage(UUID p, double damages) {
		if(!exists)
			return;
		//if(p == null)
			//Bukkit.broadcastMessage("§cptn c'est null");
		if(Bukkit.getPlayer(p) != null) {
			//Bukkit.broadcastMessage("§aTout ok");
			if(damagers.containsKey(p)) {
				damagers.replace(p, damagers.get(p) + damages);
			} else {
				damagers.put(p, damages);
			}
		} //else
			//Bukkit.broadcastMessage("hit by non player");
		damage(damages);
	}
	
	protected Player getClosestPlayer(Location loc, double maxDistance, boolean wallSentitive) {
		Player player = null;
		double distance = maxDistance+0.1;
		for(Player pl : loc.getWorld().getPlayers()) {
			double dist = pl.getLocation().distance(loc);
			if(dist < distance) {
				if( ! wallSentitive) {
					player = pl;
					distance = dist;
					continue;
				}
				if( ! isThereWallBetweenLocations(loc, pl.getLocation(), dist)) {
					player = pl;
					distance = dist;
				}
			}
		}
		return player;
	}
	
	protected List<Player> getClosePlayers(Location loc, double maxDistance) {
		return loc.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(loc) <= maxDistance).collect(Collectors.toList());
	}
	
	protected void makeSound(Location loc, Sound sound, float pitch) {
		getClosePlayers(loc, 300).forEach(pl -> pl.playSound(loc, sound, 5f, pitch));
	}

	protected void updateBar() {
		double per = health / maxHealth;
		if(per < 0) per = 0; else if(per > 1) per = 1;
		bar.setTitle(getCustomName()+ChatColor.RED+"  "+(int)health+"/"+(int)maxHealth);
		bar.setProgress(per);
	}

	protected boolean isInvocation(LivingEntity en) {
		return invocations.contains(en);
	}
	
	protected abstract void killed();
	
	public abstract double distance(Location loc);
	
	public abstract String getCustomName();
	
	protected abstract boolean isBoss(UUID uuid);
	
	public abstract List<ItemStack> getLoots();

	public abstract int getXp();
	
	public abstract void purge();
	
	protected abstract boolean spawn(DonjonI donjon);
	
	public abstract UUID getMainUUID();
	
	public boolean spawnBoss(DonjonI donjon) {
		if(spawn(donjon)) {
			exists = true;
			startActionLoop();
			return true;
		}
		return false;
	}
	
	public boolean isThereWallBetweenLocations(final Location from, final Location to, double maxRange) {
		Location cl = from.clone();
		double x = to.getX()-from.getX(), y = to.getY()-from.getY(), z = to.getZ()-from.getZ();
		Vector direction = new Vector(x, y, z).normalize();
		//cl = from.clone();
		while(cl.distance(to) > 1 && cl.distance(from) < maxRange) {
			cl = cl.add(direction);
			Block bl = cl.getBlock();
			if(bl.getType() == Material.AIR || bl.getType() == Material.CAVE_AIR)
				continue;

			if (cl.distance(from) > 2.1) {
				return true;
			}
		}
		return false;
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
				pl.sendMessage(HalystiaRPG.PREFIX + YELLOW + "["+(i+1)+"] " + Bukkit.getOfflinePlayer(bo[i]).getName() + " : " + RED + Math.floor( damagers.get(bo[i]) )+ " dmgs.");
			}
			if(otherDmgs > 0) {
				pl.sendMessage(HalystiaRPG.PREFIX + GOLD + "Autres dégâts : " + RED + Math.floor(otherDmgs) + " dmgs " + GOLD + " par " + BLUE + (damagers.size() - 3) + GOLD + " autres joueurs.");
			} 	
		}
	}
	
	protected int MAX_INVOCATIONS = 0;
	@Override
	public boolean canInvoke(UUID uuid, int howMany) {
		if(invocations.size() + howMany >= MAX_INVOCATIONS)
			return false;
		return true;
	}
	
	public void repartLootsAndXp() {
		final double total = totalDamages();
		damagers.forEach((id, damages) -> {
			double percent = damages / total;
			Player pl = Bukkit.getPlayer(id);
			double xp = ((double)getXp()) * percent;
			if(pl != null) {
				for(ItemStack loot : getLoots()) {
					pl.getInventory().addItem(loot);
					pl.sendMessage(HalystiaRPG.PREFIX + GREEN + "" + BOLD + "Vous recevez " + loot.getAmount() + "x " + 
							(loot.hasItemMeta() ? loot.getItemMeta().hasDisplayName() ? loot.getItemMeta().getDisplayName() : loot.getType().toString() : loot.getType().toString().toLowerCase().replaceAll("_", " ")));
				}
				pl.sendMessage(HalystiaRPG.PREFIX + GREEN + "" + BOLD + "Vous gagnez " + ChatColor.GOLD + (int)xp + "xp" + GREEN + "" + BOLD + ".");
				PlayerData plc = HalystiaRPG.getInstance().getClasseManager().getPlayerData(pl);
				if(plc != null)
					plc.addXp((int)xp);
				else
					pl.sendMessage(HalystiaRPG.PREFIX + RED + "Une erreur est survenue. Tu aurais dû gagner " + (int)xp + " xp. Signalez vite ce message.");
			}
		});
	}
	
	private double totalDamages() {
		double tt = 0;
		for(UUID id : damagers.keySet())
			tt += damagers.get(id);
		return tt;
	}
	
	public UUID[] bo3() {
		UUID[] bo = new UUID[damagers.size() > 3 ? 3 : damagers.size()];
		Map<UUID, Double> map = sortByValue(damagers);
		int i = 0;
		for(UUID id : map.keySet()) {
			if(i >= bo.length)
				break;
			bo[i] = id;
			i++;
		}
		return bo;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);
		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Boss)
			((Boss)o).getMainUUID().equals(this.getMainUUID());
		return false;
	}
}