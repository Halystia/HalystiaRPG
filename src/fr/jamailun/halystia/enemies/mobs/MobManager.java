package fr.jamailun.halystia.enemies.mobs;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.GUIEntityType;
import fr.jamailun.halystia.enemies.tags.MetaTag;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;

public class MobManager extends FileDataRPG {
	
	private HashMap<Integer, EnemyMob> alives;
	private HashMap<Integer, Double> customsDamages;
	
	private HalystiaRPG api;
	
	public MobManager(String path, String name, HalystiaRPG main) {
		super(path, name);
		this.api = main;
		alives = new HashMap<>();
		customsDamages = new HashMap<>();
	}
	
	public void spawnMob(String name, Location location, boolean donjon) {
		if( ! config.contains(name)) {
			api.getConsole().sendMessage(ChatColor.RED + "[MOB] Mob '" + name + "' seems to not exist in configuration.");
			return;
		}
		if(name == null)
			return;
		
		EnemyMob mob = new EnemyMob(name, config, location, donjon);
		addMob(mob.getEntityId(), mob);
		if( ! mob.isValid())
			mob.purge();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(mob.isValid())
					mob.checkLater(config, name);
			}
		}.runTaskLater(api, 10L);
	}
	
	public boolean hasMob(int entityId) {
		return alives.containsKey(entityId);
	}
	
	public EnemyMob getWithEntityId(int id) {
		return alives.get(id);
	}
	
	public void addMob(int id, EnemyMob mob) {
		alives.put(id, mob);
		double damages = mob.getCustomDamages();
		if(damages > 0)
			customsDamages.put(id, damages);
	}
	
	public void removeMob(int id) {
		alives.remove(id);
		customsDamages.remove(id);
	}
	
	public Set<String> getAllMobNames() {
		return config.getKeys(false);
	}
	
	public void purge() {
		for(int id : alives.keySet())
			alives.get(id).purge();
		alives.clear();
		customsDamages.clear();
	}

	public boolean hasMobName(String name) {
		return config.contains(name);
	}

	public FileConfiguration getConfig() {
		return config;
	}
	
	public int getHowManyMobs() {
		return alives.size();
	}
	
	public void saveConfig() {
		save();
	}
	
	public ItemStack getIconeOfMob(String key) {
		if(!config.contains(key))
			return new ItemBuilder(Material.BARRIER).setName(DARK_RED+"Clef inconnue.").setLore(WHITE+"key=["+key+"]").toItemStack();
		String typeStr = config.getString(key+".type");
		try {
			EntityType type = EntityType.valueOf(typeStr);
			ItemBuilder builder = new ItemBuilder(GUIEntityType.getWithEntityType(type).getIcone().getType());
			String name = config.getString(key+".name");
			if(name == null)
				name = DARK_RED + "(bad data) " + key;
			builder.setName(GRAY + name);
			builder.addLoreLine(GRAY+"Id : ["+key+"]");
			
			String[] paths = new String[] {"head", "chest", "legs", "boot", "hand"};
			for(int n = 0; n < paths.length; n++) {
				if(config.contains(key+".equipment." + paths[n])) {
					ItemStack item = config.getItemStack(key+".equipment." + paths[n]);
					builder.addLoreLine(WHITE+paths[n]+" : "+GRAY + (item.hasItemMeta() ? YELLOW+(item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString()) : item.getType()));
				}
			}
			builder.addLoreLine(WHITE+"Expérience : "+GRAY + config.getInt(key+".drops.xp"));
			if(config.contains(key+".drops.amount")) {
				int n = config.getInt(key + ".drops.amount");
				builder.addLoreLine(WHITE+"Drop"+(n > 1 ? "s":"")+" : " +GRAY+ n);
			}
			
			for(MetaTag meta : EnemyMob.metaDatas) {
				if( config.contains(key + "." + meta.getName()))
						builder.addLoreLine(AQUA + "[" + meta.getName() + "]=[" + GOLD + meta.getValue(config.getDouble(key + "." + meta)) + AQUA + "]");
			}
			
			return builder.toItemStack();
		} catch (Exception  e) {
			e.printStackTrace();
			return new ItemBuilder(Material.BARRIER).setName(DARK_RED+"Données corrompues.").setLore(WHITE+"key=["+key+"]").toItemStack();
		}
	}
	
	public void removeTooFar(World world, double distance) {
		Map<Integer, Boolean> mobs = new HashMap<>();
		for(Integer id : alives.keySet())
			mobs.put(id, false);
		final Set<Location> plrs = world.getPlayers().stream().map(p -> p.getLocation()).collect(Collectors.toSet());
		alives.forEach((id, en) -> {
			for(Location loc : plrs) {
				if(loc.distance(en.getEntity().getLocation()) < distance) {
					mobs.replace(id, true);
					break;
				}
			}
		});
	//	System.out.println("REMOVED " + idsToRemove.size() + " MOBS.");
		for(int idMob : mobs.keySet()) {
			if( ! mobs.get(idMob))
				removeMob(idMob);
		}
	}

	public int getHowManyMobsAround(Location loc, double range) {
		return (int) alives.values().stream().filter(en -> en.getEntity().getLocation().distance(loc) < range).count();
	}
	
	public void killNonReferedsMobs(World world) {
		Set<Entity> toTest = world.getEntities().stream()
			.filter(en -> en instanceof LivingEntity)
			.filter(
					en -> en.getType() != EntityType.PLAYER 
					&& ! CitizensAPI.getNPCRegistry().isNPC(en) 
					&& en.getType() != EntityType.VILLAGER 
					&& en.getType() != EntityType.AREA_EFFECT_CLOUD
					&& en.getType() != EntityType.ARMOR_STAND
					&& en.getType() != EntityType.PAINTING
					&& en.getType() != EntityType.DROPPED_ITEM
					&& en.getType() != EntityType.ITEM_FRAME
					&&  ! api.getSuperMobManager().isOne(en)
			)
			.collect(Collectors.toSet());
		Set<UUID> invocs = new HashSet<>(api.getSpellManager().getInvocationsManager().getList().keySet());
		for(Entity en : toTest) {
			boolean delete = true;
			for(EnemyMob mob : alives.values()) {
				if(mob.getEntity().getUniqueId().equals(en.getUniqueId())) {
					delete = false;
					break;
				}
			}
			 
			if(delete)
				if( ! invocs.contains(en.getUniqueId()))
					en.remove();
		}
	}
}