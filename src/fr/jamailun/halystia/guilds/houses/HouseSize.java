package fr.jamailun.halystia.guilds.houses;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum HouseSize {
	UNDEFINED(ChatColor.RED + "Erreur", Material.BARRIER, Integer.MAX_VALUE),
	SMALL(ChatColor.BLUE + "Demeure", Material.BRICKS, 50),
	MEDIUM(ChatColor.GREEN + "Donjon", Material.GOLD_BLOCK, 150),
	LARGE(ChatColor.LIGHT_PURPLE + "Château", Material.DIAMOND_BLOCK, 400),
	PALACE(ChatColor.GOLD + "" + ChatColor.BOLD + "Palais", Material.EMERALD_BLOCK, 1000);
	
	private final Material iconType;
	private final String name;
	private final int cost;
	
	private HouseSize(String name, Material iconType, int cost) {
		this.name = name;
		this.iconType = iconType;
		this.cost = cost;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public Material getIconMaterial() {
		return iconType;
	}
	
	public static HouseSize fromString(String string) {
		for(HouseSize size : values()) {
			if(size.toString().equalsIgnoreCase(string))
				return size;
		}
		return UNDEFINED;
	}
}