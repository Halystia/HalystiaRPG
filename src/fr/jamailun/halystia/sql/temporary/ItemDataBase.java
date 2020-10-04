package fr.jamailun.halystia.sql.temporary;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public interface ItemDataBase {
	
	List<StoredItem> getAllItems();
	
	void initialize(); // Load all items internally.
	
	void registerNewContent(String key, ItemStack content);
	
	void registerNewAllContent(Map<String, ItemStack> items);

	void unregisterContent(String key);

	List<String> getAllKeys();
	
	public StoredItem getWithKey(String key);
	
	public StoredItem getWithKey(String key, int amount);
	
}