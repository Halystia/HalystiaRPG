package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class JobCraft {
	
	private final JobType job;
	private final ItemStack obtained;
	private final List<ItemStack> ressources;
	private final int level, xp;
	
	public JobCraft(JobType job, int level, ItemStack obtained, int xp, Collection<ItemStack> ressources) {
		this.job = job;
		this.level = level;
		this.obtained = obtained;
		this.ressources = new ArrayList<>(ressources);
		this.xp = xp;
	}

	public List<ItemStack> getRessources() {
		return new ArrayList<>(ressources);
	}

	public int getLevel() {
		return level;
	}

	public ItemStack getObtained() {
		return obtained;
	}

	public JobType getJob() {
		return job;
	}

	public int getXp() {
		return xp;
	}
}