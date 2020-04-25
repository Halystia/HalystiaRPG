package fr.jamailun.halystia.spells;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.utils.RandomString;

public abstract class Spell {
	
	protected List<Player> casters = new ArrayList<>();
	
	/**
	 * Cast the spell as Player p.
	 * @return true if the cast is sucessfull.
	 */
	public abstract boolean cast(Player p);

	public abstract String getName();
	public abstract ChatColor getColor();
	
	public abstract Classe getClasseRequired();
	public abstract int getLevelRequired();

	public abstract List<String> getLore();
	public abstract String getStringIdentification();

	public abstract int getManaCost();
	public abstract int getCooldown();
	
	public void init() {}
	
	protected final HalystiaRPG main = HalystiaRPG.getInstance();
	
	protected boolean isCasting(Player p) {
		return casters.contains(p);
	}
	
	protected synchronized void spawnParticles(Location loc, Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		for(Player pl : getPlayersAround(loc, 70)) {
			spawnParticles(pl, loc, type, count, offsetHorizontal, offsetVertical, speed);
		}
	}
	
	protected synchronized void playSound(Location loc, Sound sound, float volume, float pitch) {
		for(Player pl : getPlayersAround(loc, 70)) {
			pl.playSound(loc, sound, volume, pitch);
		}
	}
	
	protected void spawnParticles(Player pl, Location loc, Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		pl.spawnParticle(type, 
			loc.getX(), loc.getY(), loc.getZ(), 
			count,
			offsetHorizontal, offsetVertical, offsetHorizontal,
			speed
		);
	}
	
	protected synchronized List<Player> getPlayersAround(Location loc, double distance) {
		List<Player> list = new ArrayList<>();
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) <= distance)
				list.add(pl);
		}
		return list;
	}
	
	protected synchronized List<Player> getPlayersAroundPlayer(Player p, double distance, boolean himself) {
		List<Player> list = new ArrayList<>();
		for(Player pl : p.getWorld().getPlayers()) {
			if(pl.getUniqueId().equals(p.getUniqueId()) && (!himself))
				continue;
			if(pl.getLocation().distance(p.getLocation()) <= distance)
				list.add(pl);
		}
		return list;
	}
	
	protected synchronized List<Entity> getEntitiesAroundPlayer(Player p, double distance, boolean himself) {
		List<Entity> list = new ArrayList<>();
		for(Entity e : p.getWorld().getEntities()) {
			if(e.getUniqueId().equals(p.getUniqueId()) && (!himself))
				continue;
			if(e.getLocation().distance(p.getLocation()) <= distance)
				list.add(e);
		}
		return list;
	}
	
	protected void applyParticlesToEntity(Entity entity, int duration, int frequency, Particle type, int count, double offset) {
		new BukkitRunnable() {
			private int duree = 0;
			@Override
			public void run() {
				if( ! entity.isValid()) {
					cancel();
					return;
				}
				duree+=frequency;
				spawnParticles(entity.getLocation(), type, count, offset, offset, .8);
				if(duree >= duration)
					cancel();
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 0, frequency);
	}
	
	protected void scheduleRemoveEntity(Entity e, int seconds) {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				try {
					if(e.isValid())
						e.remove();
				} catch(Exception e) {}
			}
		}, seconds*20L);
	}
	
	protected synchronized Block getLookedBlock(Player from, double range) {
		RayTraceResult targetBlockInfo = from.rayTraceBlocks(range, FluidCollisionMode.NEVER);
		if (targetBlockInfo == null) {
			from.sendMessage(RED + "Ce sort n'a qu'une portée de " + ((int)range) + " blocs !");
			return null;
		}
		
		return targetBlockInfo.getHitBlock();
	}
	
	protected synchronized void changeBlocksTemporarely(Map<Location, Block> map, Material newType, int secondsDuration, int tickModifier) {
		final HalystiaRPG main = HalystiaRPG.getInstance();
		final Map<Location, Block> blocks = new HashMap<>(map);
		final List<Player> players = getPlayersAround(blocks.keySet().iterator().next(), 200);

		for(final Location loc : blocks.keySet()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player p : players) {
						p.sendBlockChange(loc, main.getServer().createBlockData(newType));
					}
				}
			}.runTaskLater(main, RandomString.randInt(0, tickModifier) * 1L);
		}
		
		for(final Location loc : blocks.keySet()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player p : players) {
						p.sendBlockChange(loc, blocks.get(loc).getBlockData());
					}
				}
			}.runTaskLater(main, RandomString.randInt((secondsDuration*20)-tickModifier, (secondsDuration*20)+tickModifier) * 1L);
		}
	}
	
	protected List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<Block>();
 
		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				for(int y = bottomBlockY; y <= topBlockY; y++) {
						Block block = loc1.getWorld().getBlockAt(x, y, z);
						blocks.add(block);
					}
				}
			}
		return blocks;
	}
	
	protected Player getClosestPlayerAtRange(Location loc, double range) {
		Player p = null;
		double dist = 10000;
		for(Player pl : loc.getWorld().getPlayers()) {
			double distance = pl.getLocation().distance(loc);
			if(distance < range && distance < dist) {
				p = pl;
				dist = distance;
			}
		}
		return p;
	}
	
}
