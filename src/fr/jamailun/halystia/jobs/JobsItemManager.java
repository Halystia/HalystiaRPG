package fr.jamailun.halystia.jobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

public class JobsItemManager {

	private final Map<String, ItemStack> items;
	
	public JobsItemManager() {
		items = new HashMap<>();
	}
	
	public void registerContent(String key, ItemStack content) {
		items.put(key, content);
	}

	public void unregisterCremoveContent(String key) {
		items.remove(key);
	}
	
	public void addAllContent(Map<String, ItemStack> items) {
		this.items.putAll(items);
	}

	public Set<String> getAllKeys() {
		return items.keySet();
	}

	public ItemStack getWithKey(String key) {
		return items.get(key);
	}
	
}