package fr.jamailun.halystia.constants;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;

public enum GUIEntityType {
	
	ZOMBIE(EntityType.ZOMBIE, new ItemBuilder(Material.ZOMBIE_HEAD).setName(RED+"Zombie").toItemStack(), true),
	CREEPER(EntityType.CREEPER, new ItemBuilder(Material.CREEPER_HEAD).setName(RED+"Creeper").toItemStack()),
	SKELETON(EntityType.SKELETON, new ItemBuilder(Material.BONE).setName(RED+"Squelette").toItemStack(), true),
	SPIDER(EntityType.SPIDER, new ItemBuilder(Material.COBWEB).setName(RED+"Araignée").toItemStack()),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, new ItemBuilder(Material.SPIDER_EYE).setName(RED+"Araignée empoisonée").toItemStack()),
	PHANTOM(EntityType.PHANTOM, new ItemBuilder(Material.PHANTOM_MEMBRANE).setName(RED+"Phantom").toItemStack()),
	SILVERFISH(EntityType.SILVERFISH, new ItemBuilder(Material.INFESTED_MOSSY_STONE_BRICKS).setName(RED+"Silverfish").toItemStack()),
	ENDERMITE(EntityType.ENDERMITE, new ItemBuilder(Material.CHORUS_FRUIT).setName(RED+"Endermite").toItemStack()),
	HUSK(EntityType.HUSK, new ItemBuilder(Material.SAND).setName(RED+"Husk").toItemStack(), true),
	STRAY(EntityType.STRAY, new ItemBuilder(Material.SNOW).setName(RED+"Stray").toItemStack(), true),
	DROWNED(EntityType.DROWNED, new ItemBuilder(Material.WATER_BUCKET).setName(RED+"Drowned").toItemStack(), true),
	
	PIG(EntityType.PIG, new ItemBuilder(Material.PORKCHOP).setName(GREEN+"Cochon").toItemStack()),
	COW(EntityType.COW, new ItemBuilder(Material.LEATHER).setName(GREEN+"Vache").toItemStack()),
	SHEEP(EntityType.SHEEP, new ItemBuilder(Material.WHITE_WOOL).setName(GREEN+"Mouton").toItemStack()),
	HORSE(EntityType.HORSE, new ItemBuilder(Material.SADDLE).setName(GREEN+"Cheval").toItemStack()),
	CHICKEN(EntityType.CHICKEN, new ItemBuilder(Material.FEATHER).setName(GREEN+"Poulet").toItemStack()),
	FOX(EntityType.FOX, new ItemBuilder(Material.ORANGE_WOOL).setName(GREEN+"Renard").toItemStack()),
	OCELOT(EntityType.OCELOT, new ItemBuilder(Material.ORANGE_CARPET).setName(GREEN+"Ocelot").toItemStack()),
	PARROT(EntityType.PARROT, new ItemBuilder(Material.RED_CARPET).setName(GREEN+"Péroquet").toItemStack()),
	PANDA(EntityType.PANDA, new ItemBuilder(Material.BAMBOO).setName(GREEN+"Panda").toItemStack()),
	
	BLAZE(EntityType.BLAZE, new ItemBuilder(Material.BLAZE_ROD).setName(DARK_RED+"Blaze").toItemStack()),
	WITHER_SKELETON(EntityType.WITHER_SKELETON, new ItemBuilder(Material.BLAZE_ROD).setName(DARK_RED+"Blaze").toItemStack()),
	ENDERMAN(EntityType.ENDERMAN, new ItemBuilder(Material.ENDER_PEARL).setName(DARK_RED+"Enderman").toItemStack()),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, new ItemBuilder(Material.MAGMA_CREAM).setName(DARK_RED+"Magma cube").toItemStack()),
	WITCH(EntityType.WITCH, new ItemBuilder(Material.GLASS_BOTTLE).setName(DARK_RED+"Sorcière").toItemStack()),
	GHAST(EntityType.GHAST, new ItemBuilder(Material.GHAST_TEAR).setName(DARK_RED+"Ghast").toItemStack()),
	RAVAGER(EntityType.RAVAGER, new ItemBuilder(Material.IRON_BLOCK).setName(DARK_RED+"Ravager").toItemStack()),
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
	
	/**
	 * @deprecated because unused.
	 */
	@Deprecated
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
			case HUSK:
			case ILLUSIONER:
			case DRAGON_FIREBALL:
			case ELDER_GUARDIAN:
			case ENDER_CRYSTAL:
			case DROWNED:
			case ENDER_DRAGON:
			case EVOKER:
			case EVOKER_FANGS:
			case FIREBALL:
			case LLAMA_SPIT:
			case MAGMA_CUBE:
			case GUARDIAN:
			case PIG_ZOMBIE:
			case PILLAGER:
			case SHULKER:
			case RAVAGER:
			case SHULKER_BULLET:
			case SILVERFISH:
			case SLIME:
			case TRIDENT:
			case SMALL_FIREBALL:
			case PLAYER:
			case SPECTRAL_ARROW:
			case VEX:
			case VINDICATOR:
			case WITHER:
			case WITHER_SKULL:
			case STRAY:
			case ZOMBIE_VILLAGER:
			case PRIMED_TNT:
			case ARROW:
				return true;
		case AREA_EFFECT_CLOUD:
		case ARMOR_STAND:
		case BAT:
		case BEE:
		case BOAT:
		case CAT:
		case CHICKEN:
		case COD:
		case COW:
		case DOLPHIN:
		case DONKEY:
		case DROPPED_ITEM:
		case EGG:
		case ENDER_PEARL:
		case ENDER_SIGNAL:
		case EXPERIENCE_ORB:
		case FALLING_BLOCK:
		case IRON_GOLEM:
		case HORSE:
		case ITEM_FRAME:
		case LEASH_HITCH:
		case LLAMA:
		case LIGHTNING:
		case MINECART:
		case MINECART_CHEST:
		case MINECART_COMMAND:
		case MINECART_FURNACE:
		case MINECART_HOPPER:
		case MINECART_MOB_SPAWNER:
		case MINECART_TNT:
		case MULE:
		case MUSHROOM_COW:
		case OCELOT:
		case PAINTING:
		case PANDA:
		case PARROT:
		case PIG:
		case POLAR_BEAR:
		case PUFFERFISH:
		case RABBIT:
		case SHEEP:
		case SALMON:
		case SNOWMAN:
		case SNOWBALL:
		case SKELETON_HORSE:
		case ZOMBIE_HORSE:
		case WOLF:
		case WANDERING_TRADER:
		case VILLAGER:
		case SPLASH_POTION:
		case SQUID:
		case THROWN_EXP_BOTTLE:
		case TRADER_LLAMA:
		case TROPICAL_FISH:
		case TURTLE:
		case UNKNOWN:
		case FIREWORK:
		case FISHING_HOOK:
		case FOX:
			break;
		}
		return false;
	}
}