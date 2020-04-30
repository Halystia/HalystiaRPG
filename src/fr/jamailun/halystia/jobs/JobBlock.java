package fr.jamailun.halystia.jobs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class JobBlock {

	private final JobType job;
	private final Material type;
	private final int level;
	private final ItemStack loot;
	
	private final int xp, respawn;
	
	public JobBlock(JobType job, int level, Material type, ItemStack loot, int xp, int respawn) {
		this.job = job;
		this.type = type;
		this.level = level;
		this.loot = loot;
		this.xp = xp;
		this.respawn = respawn;
	}

	public JobType getJob() {
		return job;
	}

	public Material getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public ItemStack getLoot() {
		return loot;
	}

	/**
	 * In seconds
	 * @return time before the ore respawn.
	 */
	public int getRespawnTime() {
		return respawn;
	}

	public int getXp() {
		return xp;
	}
}