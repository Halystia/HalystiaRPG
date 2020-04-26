package fr.jamailun.halystia.constants;

import static org.bukkit.ChatColor.*;
import fr.jamailun.halystia.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum GUIEntityType {
	
	ZOMBIE(EntityType.ZOMBIE, new ItemBuilder(Material.ZOMBIE_HEAD).setName(RED+"Zombie").toItemStack(), true),
	CREEPER(EntityType.CREEPER, new ItemBuilder(Material.CREEPER_HEAD).setName(RED+"Creeper").toItemStack()),
	SKELETON(EntityType.SKELETON, new ItemBuilder(Material.BONE).setName(RED+"Squelette").toItemStack(), true),
	SPIDER(EntityType.SPIDER, new ItemBuilder(Material.COBWEB).setName(RED+"Araignée").toItemStack()),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, new ItemBuilder(Material.SPIDER_EYE).setName(RED+"Araignée empoisonée").toItemStack()),
	PHANTOM(EntityType.PHANTOM, new ItemBuilder(Material.PHANTOM_MEMBRANE).setName(RED+"Phantom").toItemStack()),
	SILVERFISH(EntityType.SILVERFISH, new ItemBuilder(Material.INFESTED_MOSSY_STONE_BRICKS).setName(RED+"Silverfish").toItemStack()),
	ENDERMITE(EntityType.ENDERMITE, new ItemBuilder(Material.CHORUS_FRUIT).setName(RED+"Endermite").toItemStack()),
	
	PIG(EntityType.PIG, new ItemBuilder(Material.PORKCHOP).setName(GREEN+"Cochon").toItemStack()),
	COW(EntityType.COW, new ItemBuilder(Material.LEATHER).setName(GREEN+"Vache").toItemStack()),
	SHEEP(EntityType.SHEEP, new ItemBuilder(Material.WHITE_WOOL).setName(GREEN+"Mouton").toItemStack()),
	HORSE(EntityType.HORSE, new ItemBuilder(Material.SADDLE).setName(GREEN+"Cheval").toItemStack()),
	CHICKEN(EntityType.CHICKEN, new ItemBuilder(Material.FEATHER).setName(GREEN+"Poulet").toItemStack()),
	FOX(EntityType.FOX, new ItemBuilder(Material.ORANGE_WOOL).setName(GREEN+"Renard").toItemStack()),
	PANDA(EntityType.PANDA, new ItemBuilder(Material.BAMBOO).setName(GREEN+"Panda").toItemStack()),
	
	BLAZE(EntityType.BLAZE, new ItemBuilder(Material.BLAZE_ROD).setName(DARK_RED+"Blaze").toItemStack()),
	ENDERMAN(EntityType.ENDERMAN, new ItemBuilder(Material.ENDER_PEARL).setName(DARK_RED+"Enderman").toItemStack()),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, new ItemBuilder(Material.MAGMA_CREAM).setName(DARK_RED+"Magma cube").toItemStack()),
	WITCH(EntityType.WITCH, new ItemBuilder(Material.GLASS_BOTTLE).setName(DARK_RED+"Sorcière").toItemStack()),
	GHAST(EntityType.GHAST, new ItemBuilder(Material.GHAST_TEAR).setName(DARK_RED+"Ghast").toItemStack()),
	
	;
	
	private final EntityType type;
	private final ItemStack icon;
	private final boolean canEquip;
	
	private GUIEntityType(EntityType type, ItemStack icon) {
		this(type, icon, false);
	}
	
	private GUIEntityType(EntityType type, ItemStack icon, boolean canEquip) {
		this.type = type;
		this.icon = icon;
		this.canEquip = canEquip;
	}
	
	public EntityType getEntityType() {
		return type;
	}
	
	public ItemStack getIcone() {
		return icon;
	}
	
	public boolean canEquipEquipment() {
		return canEquip;
	}
	
	public static GUIEntityType getWithEntityType(EntityType type) {
		for(GUIEntityType g : values())
			if(g.type == type)
				return g;
		throw new IllegalArgumentException("Unknow EntityType : " + type + ".");
	}
	
	public static boolean isBadEntity(Entity e) {
		switch(e.getType()) {
			case ZOMBIE:
			case SKELETON:
			case GIANT:
			case WITHER_SKELETON:
			case CREEPER:
			case CAVE_SPIDER:
			case SPIDER:
			case BLAZE:
			case ENDERMITE:
			case ENDERMAN:
			case PHANTOM:
			case WITCH:
			case GHAST:
				return true;
			default:
				return false;
		}
	}
}