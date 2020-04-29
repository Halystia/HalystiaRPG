package fr.jamailun.halystia.custom.fishing;

import fr.jamailun.halystia.utils.ItemBuilder;

public class Drop {
	
	private final int chances;
	private final ItemBuilder item;
	
	public Drop(ItemBuilder item, int chances) {
		this.item = item.clone();
		this.chances = chances;
	}

	public int getChances() {
		return chances;
	}

	public ItemBuilder getItem() {
		return item;
	}
	
}