package fr.jamailun.halystia.royaumes;

import org.bukkit.ChatColor;

public enum Royaume {
	
	EST("Royaume de l'Est", ChatColor.DARK_RED),
	OUEST("Royaume du Culte", ChatColor.DARK_GREEN),
	NEUTRE("Vagabond", ChatColor.GRAY);
	
	private final String nom;
	private final ChatColor color;
	
	private Royaume(String nom, ChatColor color) {
		this.nom = nom;
		this.color = color;
	}
	
	public String getName() {
		return nom;
	}
	
	public ChatColor getColor() {
		return color;
	}
}
