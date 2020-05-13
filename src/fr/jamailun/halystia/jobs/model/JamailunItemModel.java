package fr.jamailun.halystia.jobs.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;

abstract class JamailunItemModel {
	protected final Map<String, ItemStack> items;
	JamailunItemModel() {
		items = new HashMap<>();
		generate();
	}
	abstract void generate();
	protected String c() {
		return Rarity.COMMON.getColor();
	}
	protected String r() {
		return Rarity.RARE.getColor();
	}
	protected String l() {
		return Rarity.LEGENDARY.getColor();
	}
	protected String u() {
		return Rarity.UNIQUE.getColor();
	}
	Map<String, ItemStack>getItems() {
		return items;
	}
}
