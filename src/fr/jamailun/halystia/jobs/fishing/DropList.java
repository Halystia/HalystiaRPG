package fr.jamailun.halystia.jobs.fishing;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.RandomPick;

public class DropList {

	private RandomPick<ItemBuilder> picks;
	
	public DropList(Drop... drops) {
		Map<ItemBuilder, Integer> elements = new HashMap<>();
		for(Drop drop : drops) {
			elements.put(drop.getItem(), drop.getChances());
		}
		picks = new RandomPick<>(elements);
	}
	
	public ItemStack drop() {
		return picks.nextPick().toItemStack();
	}
}