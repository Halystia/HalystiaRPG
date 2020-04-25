package fr.jamailun.halystia.jobs.recolte;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;

public final class JobRecolteBloc {

	private final Material type;
	private final int level;
	private final Material tool;
	private final ItemBuilder obtain;
	
	public JobRecolteBloc(Material type, Material tool, int level, ItemBuilder obtain) {
		this.type = type;
		this.tool = tool;
		this.level = level;
		this.obtain = obtain;
	}
	
	public final Material getType() {
		return type;
	}
	
	public final int getLevelRequired() {
		return level;
	}
	
	public final int getExpGiven() {
		return level * 1;
	}
	
	public final Material getToolType() {
		return tool;
	}
	
	public final int getSecondsRepop() {
		return level * 30;
	}
	
	public final ItemStack getResult() {
		return obtain.toItemStack();
	}

	public boolean matchesData(Block block, Material tool) {
		return block.getType() == getType() && tool == this.tool;
	}
}