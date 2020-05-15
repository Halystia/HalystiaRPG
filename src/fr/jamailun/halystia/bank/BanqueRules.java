package fr.jamailun.halystia.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public final class BanqueRules {
	
	private final int maximumLevel, minimumLevel;
	private Map<Integer, List<ItemStack>> prices;
	
	/**
	 * @deprecated use {@link #BanqueRules()}
	 */
	@Deprecated
	public BanqueRules(int minimumLevel, int maximumLevel) {
		prices = new HashMap<>();
		this.minimumLevel = minimumLevel;
		this.maximumLevel = maximumLevel;
	}
	
	/**
	 * Create new instance of rules.
	 * <br/>Edit it with {@link #changeCostForLevel(int, List)}
	 */
	public BanqueRules() {
		this(1, 4);
	}

	/**
	 * 
	 * @param level current level to buy.
	 * @param price
	 * @return
	 */
	public BanqueRules changeCostForLevel(int level, List<ItemStack> price) {
		if(level < minimumLevel)
			throw new IllegalArgumentException("Level is too low ! " + level + " < " + minimumLevel+".");
		if(level > maximumLevel)
			throw new IllegalArgumentException("Level is too high ! " + level + " > " + maximumLevel+".");
		if(price == null)
			throw new IllegalArgumentException("Price is null.");
		prices.put(level, new ArrayList<>(price));
		return this;
	}
	
	public BanqueRules changeCostForLevel(int level, ItemStack... price) {
		return this.changeCostForLevel(level, Arrays.asList(price));
	}
	
	public List<ItemStack> getCost(int level) {
		if(level < minimumLevel)
			throw new IllegalArgumentException("Level is too low ! " + level + " < " + minimumLevel+".");
		if(level > maximumLevel)
			throw new IllegalArgumentException("Level is too high ! " + level + " > " + maximumLevel+".");
		if(prices.get(level) == null)
			return new ArrayList<>();
		return prices.get(level);
	}
}