package fr.jamailun.halystia.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.sql.temporary.ItemDataBase;
import fr.jamailun.halystia.sql.temporary.StoredItem;

public class JobsItemManager implements ItemDataBase {

	private final Map<String, StoredItem> items;
	
	public JobsItemManager() {
		items = new HashMap<>();
	}
	
	@Override
	public void registerNewContent(String key, ItemStack item) {
		items.put(key, new StoredItem(key, item));
	}

	@Override
	public void unregisterContent(String key) {
		items.remove(key);
		//TODO remove definitively
	}
	
	
	@Override
	public void registerNewAllContent(Map<String, ItemStack> items) {
		items.forEach((s,i)-> {
			this.items.put(s, new StoredItem(s, i));
		});
	}

	public List<String> getAllKeys() {
		return new ArrayList<>(items.keySet());
	}

	public StoredItem getWithKey(String key) {
		return items.get(key);
	}
	
	public StoredItem getWithKey(String key, int amount) {
		if(!items.containsKey(key))
			return null;
		return items.get(key);
	}

	@Override
	public List<StoredItem> getAllItems() {
		return new ArrayList<>(items.values());
	}

	@Override
	public void initialize() {}

}