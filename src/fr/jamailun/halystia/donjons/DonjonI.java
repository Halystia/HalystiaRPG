package fr.jamailun.halystia.donjons;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DonjonI {
	
	/**
	 * @return name of a donjon, without color
	 */
	public String getName();
	
	/**
	 * @return location to be teleport when enter the donjon
	 */
	public Location getEntryInDonjon();
	
	/**
	 * @return location to be teleport when exit the donjon
	 */
	public Location getExitOfDonjon();
	
	/**
	 * @return shortcgut to get world name.
	 */
	public String getWorldName();
	
	/**
	 * @return ItemStack to possess to be able to enter the donjon.
	 */
	public ItemStack getKeyNeed();
	
	public int getExpReward();
	
	/**
	 * @return the epg level required to enter the donjon.
	 */
	public int getLevelNeed();
	
	/**
	 * @return the DonjonI difficulty.
	 */
	public DonjonDifficulty getDonjonDifficulty();
	
	/**
	 * @return the configuration identitfication of the donjon.
	 */
	public String getConfigName();
	
	/**
	 *  Destroy all data about it.
	 */
	public void destroy();
	
	/**
	 * @return a Set of UUIDof players who are inside the Donjon.
	 */
	public Set<UUID> getJoinedPlayers();
	
	/**
	 * Check if a specific player is inside the donjon.
	 * @return true is it's the case.
	 */
	public boolean isPlayerInside(Player p);
	
	/**
	 * Alert the donjon that someone want to enter.
	 * @return false if player is already in.
	 */
	public boolean playerEnterDonjon(Player p);
	
	/**
	 * Force a Player to go out of the donjon.
	 * @return false if the Player is not in the donjon.
	 */
	public boolean forcePlayerExit(Player p);
	
	/**
	 * @return the location the boss have to spawn
	 */
	public Location getBossLocation();
	
	/**
	 * Try to spawn the {@link fr.jamailun.halystia.enemies.boss.Boss Boss} of the donjon
	 * @param player the Player who try to summon the Boss
	 * @return false if the boss is already spawned.
	 */
	public boolean trySpawnBoss(Player player);

	public Location respawn(Player player);

	public void hideBossBar(Player player);
	
}
