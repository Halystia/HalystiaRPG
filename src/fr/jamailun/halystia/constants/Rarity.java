package fr.jamailun.halystia.constants;

import org.bukkit.ChatColor;

public enum Rarity {

	COMMON("COMMUN", ChatColor.YELLOW),
	RARE("RARE", ChatColor.AQUA),
	LEGENDARY("LÉGENDAIRE", ChatColor.LIGHT_PURPLE),
	UNIQUE("UNIQUE", ChatColor.DARK_RED)
	;
	
	private final String name;
	private final ChatColor color;
	
	private Rarity(String name, ChatColor color) {
		this.name = name;
		this.color = color;
	}
	
	public final String toString() {
		return getColor() + name;
	}
	
	public String getColor() {
		return color + "" + ChatColor.BOLD;
	}
}