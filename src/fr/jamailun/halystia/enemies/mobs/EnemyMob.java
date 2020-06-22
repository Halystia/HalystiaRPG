package fr.jamailun.halystia.enemies.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.Enemy;
import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.enemies.tags.MetaTag.Type;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.RandomString;

public class EnemyMob implements Enemy {
	
	private final int xp;
	private final String configName;
	private EntityType type;
	private LivingEntity entity;
	private int entityId;
	private List<ItemStack> loots;
	private String customName;
	
	public static final List<MetaTag> metaDatas = Arrays.asList(
		 new MetaTag("max-health",	Type.DOUBLE	)	// Max health of entity
		,new MetaTag("invisibility",Type.BOOLEAN)	// Is invisible ?
		,new MetaTag("speed", 		Type.DOUBLE	)	// Speed value
		,new MetaTag("attack", 		Type.DOUBLE	)	// Damage per hit
		,new MetaTag("kbres",		Type.DOUBLE	)	// Knockback resistance
		,new MetaTag("baby",		Type.BOOLEAN)	// Is baby ? Zombie only
		,new MetaTag("king",		Type.BOOLEAN)	// Is worldboss ?
		,new MetaTag("poison",		Type.BOOLEAN)	// Deals poison ?
		,new MetaTag("wither",		Type.BOOLEAN)	// Deals wither ?
		,new MetaTag("firer",		Type.BOOLEAN)	// Is fire resistant ?
	);
	
	public static MetaTag getTag(String name) {
		for(MetaTag tag : metaDatas)
			if(tag.getName().equalsIgnoreCase(name))
				return tag;
		return null;
	}
	
	public static String getAllTags() {
		StringBuilder b = new StringBuilder();
		metaDatas.forEach(g -> b.append(g.getType() == Type.BOOLEAN ? ChatColor.AQUA : ChatColor.YELLOW).append(g.getName() + " "));
		return b.toString();
	}
	
	public EnemyMob(String name, FileConfiguration config, Location location, boolean isDonjon) {
		if( ! config.contains(name)) {
			throw new IllegalAccessError("[MOB] Mob '" + name + "' seems to not exist.");
		}
		this.configName = name;
		setLoots(name, config, isDonjon);
		
		xp = config.getInt(name + ".drops.xp");
		
		customName = name;
		if(config.contains(name + ".name"))
			customName = config.getString(name + ".name");
		
		String typeConfig = config.getString(name + ".type");
		if(typeConfig == null) {
			HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Impossible d'optenir le type du monstre id=[" + name + "]");
			return;
		}
		try {
			type = EntityType.valueOf(typeConfig);
		} catch(Exception e) {
			HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Impossible d'optenir le type du monstre [" + typeConfig + "]");
			return;
		}
		
		entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
		entityId = entity.getEntityId();
		
		entity.setCustomName(customName);
		entity.setCustomNameVisible(true);
		if(type == EntityType.ZOMBIE)
			((Zombie)entity).setConversionTime(-1);

		entity.getEquipment().clear();
		entity.setCanPickupItems(false);
		
		if(type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.GIANT || type == EntityType.HUSK || type == EntityType.STRAY) {
			Creature creature = ((Creature) entity);
			if(creature instanceof Zombie)
				((Zombie)creature).setBaby(false);
			
			creature.getEquipment().clear();

			for(int i = 0; i < creature.getEquipment().getArmorContents().length; i++) {
				creature.getEquipment().getArmorContents()[i] = null;
			}
			
			creature.getEquipment().setHelmet(config.getItemStack(name + ".equipment.head"));
			creature.getEquipment().setChestplate(config.getItemStack(name + ".equipment.chest"));
			creature.getEquipment().setLeggings(config.getItemStack(name + ".equipment.legs"));
			creature.getEquipment().setBoots(config.getItemStack(name + ".equipment.foot"));
			creature.getEquipment().setItemInMainHand(config.getItemStack(name + ".equipment.hand"));
			
			if(creature.getEquipment().getHelmet().getType() != Material.AIR && ! config.contains(name + ".equipment.head"))
				creature.remove();
			if(creature.getEquipment().getChestplate().getType() != Material.AIR && ! config.contains(name + ".equipment.chest"))
				creature.remove();
			if(creature.getEquipment().getLeggings().getType() != Material.AIR && ! config.contains(name + ".equipment.legs"))
				creature.remove();
			if(creature.getEquipment().getBoots().getType() != Material.AIR && ! config.contains(name + ".equipment.foot"))
				creature.remove();
			if(creature.getEquipment().getItemInMainHand().getType() != Material.AIR && ! config.contains(name + ".equipment.hand"))
				creature.remove();
		}
		
		if(config.contains(name+".king")) {
			if(config.getDouble(name+".king") > 0) {
				for(Entity ent : location.getWorld().getEntities()) {
					if(ent.getCustomName() != null) {
						if(ent.getCustomName().equals(customName) && !ent.getUniqueId().equals(this.entity.getUniqueId())) {
							entity.teleport(new Location(location.getWorld(), location.getX(), -60, location.getZ()));
							entity.remove();
							return;
						}
					}
				}
			} else {
				System.err.println("Mauvaise data de king pour le mob ["+name+"]");
			}
		}
		healthData = 20;
		if(config.contains(name+".max-health")) {
			AttributeInstance ai = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			if(ai != null) {
				double health = config.getDouble(name+".max-health");
				if(health > 0) {
					ai.setBaseValue(health);
					entity.setHealth(health);
					healthData = health;
				} else {
					System.err.println("Mauvaise data de max-health pour le mob ["+name+"]");
				}
			}
		}
		
		if(config.contains(name+".attack")) {
			damages = config.getDouble(name+".attack");
		}
		
		if(config.contains(name+".kbres")) {
			AttributeInstance ai = entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
			if(ai != null) {
				double kbres = config.getDouble(name+".kbres");
				if(kbres > 0) {
					ai.setBaseValue(kbres);
				} else {
					System.err.println("Mauvaise data de kbres pour le mob ["+name+"]");
				}
			}
		}
		
		if(config.contains(name+".baby")) {
			if(config.getDouble(name+".baby") > 0)
				if(entity instanceof Zombie)
					((Zombie)entity).setBaby(true);
		}
		
		if(config.contains(name+".poison")) {
			if(config.getDouble(name+".poison") > 0)
				poison = true;
		}
		
		if(config.contains(name+".firer")) {
			if(config.getDouble(name+".firer") > 0)
				fireResist = true;
		}
		
		if(config.contains(name+".wither")) {
			if(config.getDouble(name+".wither") > 0)
				wither = true;
		}
		
		if(config.contains(name+".invisibility")) {
			if(config.getDouble(name+".invisibility") > 0)
				((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false, false));
		}
		
		if(config.contains(name+".speed")) {
			AttributeInstance ai = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
			if(ai != null) {
				double speed = config.getDouble(name+".speed");
				if(speed > 0) {
					ai.setBaseValue(speed);
				} else {
					System.err.println("Mauvaise data de speed pour le mob ["+name+"]");
				}
			}
		}
		
	}
	
	public void checkLater(FileConfiguration config, String name) {
		boolean remove = false;
		if(entity.getEquipment().getHelmet().getType() != Material.AIR && ! config.contains(name + ".equipment.head"))
			remove = true;
		if(entity.getEquipment().getChestplate().getType() != Material.AIR && ! config.contains(name + ".equipment.chest"))
			remove = true;
		if(entity.getEquipment().getLeggings().getType() != Material.AIR && ! config.contains(name + ".equipment.legs"))
			remove = true;
		if(entity.getEquipment().getBoots().getType() != Material.AIR && ! config.contains(name + ".equipment.foot"))
			remove = true;
		if(entity.getEquipment().getItemInMainHand().getType() != Material.AIR && ! config.contains(name + ".equipment.hand"))
			remove = true;
		if(remove)
			HalystiaRPG.getInstance().getMobManager().removeMob(entityId);
	}
	
	@Override
	public List<ItemStack> getLoots() {
		return loots;
	}

	public String getConfigName() {
		return configName;
	}
	
	public EntityType getType() {
		return type;
	}
	
	@Override
	public String getCustomName() {
		return customName;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public int getXp() {
		return xp;
	}
	
	private double healthData;
	public double getRegisteredHealth() {
		return healthData;
	}
	
	private boolean poison = false;
	public boolean isPoisonous() {
		return poison;
	}
	
	private boolean wither = false;
	public boolean isWitherous() {
		return wither;
	}
	
	public static final ItemStack DONJON_KEY = new ItemBuilder(Material.IRON_NUGGET).setName(ChatColor.GOLD+"Âme de donjon").addUnsafeEnchantment(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).toItemStack();
	
	private void setLoots(String name, FileConfiguration config, boolean isDonjon) {
		loots = new ArrayList<>();
		if( ! config.contains(name + ".drops.amount"))
			return;
		
		if(isDonjon)
			loots.add(new ItemStack(DONJON_KEY));
		
		for(int i = 1; i <= config.getInt(name + ".drops.amount"); i++) {
			int luck = 10000;
			if(config.contains(name + ".drops." + i + ".chances"))
				luck = config.getInt(name + ".drops." + i + ".chances");
			if(luck < RandomString.randInt(1, 10000))
				continue;
			
			ItemStack item = config.getItemStack(name + ".drops." + i + ".item");
			if(item == null) {
				HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "[MOB] Item inconnu ! (" + name + ".drops." + i + ".item)");
				continue;
			}
			int amountMax = item.getAmount();
			int amount = RandomString.randInt(1, amountMax);
			
			ItemStack drop = new ItemStack(item);
			drop.setAmount(amount);
			
			loots.add(drop);
		}
	}

	public void purge() {
		entity.remove();
	}
	
	private double damages = -1;
	public double getCustomDamages() {
		return damages;
	}

	public boolean isValid() {
		return entity.isValid();
	}
	
	private boolean fireResist = false;
	public boolean doesResistFire() {
		return fireResist;
	}
	
}