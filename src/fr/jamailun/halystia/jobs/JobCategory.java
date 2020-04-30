package fr.jamailun.halystia.jobs;

import org.bukkit.ChatColor;

public enum JobCategory {
	
	RECOLTE("Métier de récolte", ChatColor.BLUE),
	CRAFT("Métier de conception", ChatColor.GREEN),
	BOOST("Métier d'amélioration", ChatColor.GOLD),
	NONE("Aucun métier", ChatColor.GRAY);
	
	private final String name;
	private final ChatColor color;
	
	private JobCategory(String name, ChatColor color) {
		this.name = name;
		this.color = color;
	}
	
	public String getDisplayName() {
		return color + name;
	}
	
	public ChatColor getColor() {
		return color;
	}
}