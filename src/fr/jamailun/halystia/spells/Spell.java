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

/**
 * Represent a spell a {@link org.bukkit.entity.Player Player} can use.
 * <br/> Contains a lot of cools method to use in childern classes.
 * @see SpellManager
 */
public abstract class Spell {
	
	protected List<Player> casters = new ArrayList<>();
	
	/**
	 * Cast the spell as Player p.
	 * @return true if the cast is sucessfull. false if not.
	 */
	public abstract boolean cast(Player p);

	/**
	 * Get the name (without colors) of the spell.
	 * @return name of the spell without colors.
	 */
	public abstract String getName();
	
	/**
	 * Get color of the spell to display it later.
	 * @return the color under the {@link org.bukkit.ChatColor ChatColor} form.
	 */
	public abstract ChatColor getColor();
	
	/**
	 * Get the required class to cast the spell.
	 * @return the {@link fr.jamailun.halystia.players.Classe Classe} the player must have to cast this spell.
	 */
	public abstract Classe getClasseRequired();
	
	/**
	 * Get the required class level to cast the spell.
	 * @return the level the player must have to cast this spell.
	 */
	public abstract int getLevelRequired();

	/**
	 * Get the spell's description.
	 * @return List<String> who details the spell.
	 */
	public abstract List<String> getLore();
	/**
	 * Get the ID of the spell
	 * @return the ID of a spell.
	 */
	public abstract String getStringIdentification();

	/**
	 * Get the cost in mana to cast this spell.
	 * @return the price in mana points.
	 */
	public abstract int getManaCost();
	
	/**
	 * Get the spell's cooldown.
	 * @return the cooldown in seconds.
	 */
	public abstract int getCooldown();
	
	/**
	 * Initiates lists or things of your spells.
	 * <br /> Called by the {@link SpellManager SpellManager} after being added.
	 */
	public void init() {}
	
	/**
	 * Main. usefull to avoid the repetition of <i>Halystia.getInstance()</i>
	 */
	protected final HalystiaRPG main = HalystiaRPG.getInstance();
	
	/**
	 * Check if a player is casting a spell.
	 * @return true if the player is casting this spell.
	 */
	protected boolean isCasting(Player p) {
		return casters.contains(p);
	}
	
	/**
	 * Spawn particles around location.
	 */
	protected synchronized void spawnParticles(Location loc, Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		for(Player pl : getPlayersAround(loc, 100)) {
			spawnParticles(pl, loc, type, count, offsetHorizontal, offsetVertical, speed);
		}
	}
	
	/**
	 * Play sound around location.
	 */
	protected synchronized void playSound(Location loc, Sound sound, float volume, float pitch) {
		for(Player pl : getPlayersAround(loc, 100)) {
			pl.playSound(loc, sound, volume, pitch);
		}
	}
	
	/**
	 * Spawn particles around location.
	 */
	protected void spawnParticles(Player pl, Location loc, Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		pl.spawnParticle(type, 
			loc.getX(), loc.getY(), loc.getZ(), 
			count,
			offsetHorizontal, offsetVertical, offsetHorizontal,
			speed
		);
	}
	
	/**
	 * @return a List of {@link org.bukkit.entity.Player players} who are around a location.
	 * @see #getPlayersAroundPlayer(Player, double, boolean)
	 */
	protected synchronized List<Player> getPlayersAround(Location loc, double distance) {
		List<Player> list = new ArrayList<>();
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getLocation().distance(loc) <= distance)
				list.add(pl);
		}
		return list;
	}
	
	/**
	 * @return a List of {@link org.bukkit.entity.Player players} who are around a location.
	 * @param p source Player.
	 * @param distance maximal distance of the query.
	 * @param himself if true, the source player will be in the list.
	 * @see #getEntitiesAroundPlayer(Player, double, boolean)
	 */
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
	
	/**
	 * @return a List of {@link org.bukkit.entity.Entity entities} who are around a location.
	 * @param p source Player.
	 * @param himself if true, the source player will be in the list.
	 * @param distance maximal distance of the query.
	 * @see #getPlayersAroundPlayer(Player, double, boolean)
	 */
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
	
	/**
	 * Apply particles to an entity during a time.
	 * If the entity is not valid, cancel the method.
	 * @param entity : Entity to follow with particles.
	 * @param duration : total duration of the particles (in ticks).
	 * @param frequency : frequency of the particles to be refreshed (in ticks).
	 * @param type type of the Particles. Caution : do not use particles who require more data.
	 * @param count amount of particles to be generated
	 * @param offset radius of the appartiion of particles.
	 */
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
	
	/**
	 * Remove an entity after a certain time, only if this entity is still valid.
	 * @param e entity to remove.
	 * @param seconds amount of seconds before removal.
	 */
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
	
	/**
	 * Use rayTraces to get the looked block of a player, to a certain distance.
	 * @param from Player who look.
	 * @param range maximum distance the Player can look at.
	 * @return null if no blokc was found.
	 */
	protected synchronized Block getLookedBlock(Player from, double range) {
		RayTraceResult targetBlockInfo = from.rayTraceBlocks(range, FluidCollisionMode.NEVER);
		if (targetBlockInfo == null) {
			from.sendMessage(RED + "Ce sort n'a qu'une portée de " + ((int)range) + " blocs !");
			return null;
		}
		
		return targetBlockInfo.getHitBlock();
	}
	
	/**
	 * Change blocks temporarely.
	 * <br /> This is <b>clien sided</b> so it DO NOT modify your World.
	 * <br/>All players around 500 blocks of the first block of the map will be concerned. Stay in a same region.
	 * @param map Map of blocks to be changed.
	 * @param newType the Materialto be display to all blocks.
	 * @param secondsDuration time during the one block will be in the 'newtype' param state.
	 * @param tickModifier delta of seconds for the duration. Randomly set 
	 */
	protected synchronized void changeBlocksTemporarely(Map<Location, Block> map, Material newType, int secondsDuration, int tickModifier) {
		final HalystiaRPG main = HalystiaRPG.getInstance();
		if(map.isEmpty())
			return;
		final Map<Location, Block> blocks = new HashMap<>(map);
		final List<Player> players = getPlayersAround(blocks.keySet().iterator().next(), 500);

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
	
	/**
	 * Get all blocks between two points.
	 */
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
	
	/**
	 * Get the closest Player of a point.
	 * @param loc Source location.
	 * @param range maxum range to query players.
	 * @return null if not Player has been found.
	 * @see #getPlayersAround(Location, double)
	 */
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
