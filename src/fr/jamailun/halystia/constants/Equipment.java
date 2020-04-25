package fr.jamailun.halystia.constants;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public enum Equipment {
	
	SWORD(Material.WOODEN_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD),
	BOW(Material.BOW),
	OFFENSIVE(SWORD, BOW),
	
	AXE(Material.WOODEN_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE),
	PICKAXE(Material.WOODEN_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE),
	HOE(Material.WOODEN_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE),
	SHOVEL(Material.WOODEN_SHOVEL, Material.IRON_SHOVEL, Material.STONE_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL),
	FISHING_ROD(Material.FISHING_ROD),
	TOOLS(AXE, PICKAXE, SHOVEL, HOE),
	
	HELMET(Material.TURTLE_HELMET, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.CREEPER_HEAD, Material.DRAGON_HEAD),
	CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE),
	LEGGINGS(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS),
	BOOTS(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS),
	ARMOR(HELMET, CHESTPLATE, LEGGINGS, BOOTS),
	
	SHIELD(Material.SHIELD),
	DEFENSIVE(ARMOR, SHIELD),
	HAND_ITEM(OFFENSIVE, TOOLS),
	
	EQUIPMENT(OFFENSIVE, DEFENSIVE, TOOLS, FISHING_ROD);
	
	private final List<Material> materials;
	
	private Equipment(Material... materials) {
		this.materials = new ArrayList<Material>();
		for(int i = 0; i < materials.length; i++)
			this.materials.add(materials[i]);
	}
	
	private Equipment(Equipment... equipments) {
		this.materials = new ArrayList<Material>();
		for(Equipment equipment : equipments)
			for(Material material : equipment.materials)
				if( ! this.materials.contains(material))
					this.materials.add(material);
	}
	
	public boolean hasObject(Material object) {
		return (materials.contains(object));
	}
	
	public static Material getMaterialFromString(String name) {
		for(Material material : Material.values())
			if(material.toString().equalsIgnoreCase(name))
				return material;
		return null;
	}
	
}
