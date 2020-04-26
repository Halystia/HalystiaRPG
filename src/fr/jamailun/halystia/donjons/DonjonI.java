package fr.jamailun.halystia.donjons;

import org.bukkit.Location;
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
	
}
