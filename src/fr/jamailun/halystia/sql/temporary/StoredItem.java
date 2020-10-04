package fr.jamailun.halystia.sql.temporary;

import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.RpgEquipment;

public class StoredItem {
	
	private final String id;
	private RpgEquipment item;
	
	public StoredItem(String id, RpgEquipment item) {
		this.id = id;
		this.item = item;
	}
	
	public StoredItem(String id, ItemBuilder item) {
		this(id, new RpgEquipment(item));
	}
	
	public StoredItem(String id, ItemStack item) {
		this(id, new RpgEquipment(item));
	}
	
	public ItemStack generate() {
		return item.toItemStack();
	}
	
	public RpgEquipment getRpgItem() {
		return item;
	}
	
	public String getID() {
		return id;
	}
}