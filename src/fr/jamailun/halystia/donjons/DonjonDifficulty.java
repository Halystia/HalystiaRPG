package fr.jamailun.halystia.donjons;

import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum DonjonDifficulty {
	
	TUTORIEL("Tutoriel", DARK_GREEN, Material.GREEN_WOOL),
	FACILE("Facile", GREEN, Material.LIME_WOOL),
	MOYEN("Moyen", YELLOW, Material.YELLOW_WOOL),
	DIFFICILE("Difficile", RED, Material.RED_WOOL),
	IMPOSSIBLE("Impossible", DARK_PURPLE, Material.PURPLE_WOOL),
	WTF("???", BLACK, Material.BLACK_WOOL);
	
	public final String name;
	public final ChatColor color;
	public final Material coloredItem;
	
	private DonjonDifficulty(String name, ChatColor color, Material coloredItem) {
		this.name = name;
		this.color = color;
		this.coloredItem = coloredItem;
	}
	
	public String getDisplayName() {
		return color + name;
	}
	
}