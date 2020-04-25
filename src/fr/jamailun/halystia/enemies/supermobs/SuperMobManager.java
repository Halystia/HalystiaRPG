package fr.jamailun.halystia.enemies.supermobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.enemies.supermobs.models.OeilAntique;
import fr.jamailun.halystia.utils.FileDataRPG;

public class SuperMobManager extends FileDataRPG {
	
	public final static List<String> types = Arrays.asList("oeil");
	public List<SuperMob> mobs;
	
	public SuperMobManager(String path, String name) {
		super(path, name);
		mobs = new ArrayList<>();
		if(!config.contains("oeil.max-health"))
			config.set("oeil.max-health", 2500);
		if(!config.contains("oeil.attack"))
			config.set("oeil.attack", 20);
		if(!config.contains("oeil.range"))
			config.set("oeil.range", 30);
		if(!config.contains("oeil.respawn"))
			config.set("oeil.respawn", 10);
		if(!config.contains("oeil.cooldown"))
			config.set("oeil.cooldown", 3);
			save();
	}
	
	public void purge() {
		for(SuperMob mob : mobs)
			mob.purge();
	}
	
	public void initAllSuperMobs() {
		for(String type : types) {
			if( ! config.contains(type + ".locations"))
				continue;
			for(String locStr : config.getStringList(type + ".locations")) {
				if(type.equals("oeil")) {
					mobs.add(new OeilAntique(getStringLocation(locStr), config.getDouble("oeil.max-health"), config.getDouble("oeil.range"), config.getDouble("oeil.attack"), 20L*config.getLong("oeil.cooldown"), 20L*60*config.getInt("oeil.respawn")));
				}
			}
		}
	}
	
	/**
	 * @return true if the entity has been foundd.
	 */
	public boolean damageMob(Entity entity, UUID damager, double damages) {
		for(SuperMob mob : mobs) {
			if(mob.isMob(entity)) {
				mob.damage(damager, damages);
				return true;
			}
		}
		return false;
	}

	public boolean addMob(String name, Location location, Player p) {
		if(!types.contains(name)) {
			p.sendMessage(ChatColor.RED + "["+name+"] n'est pas un type de supermob valide.");
			return false;
		}
		List<String> list = config.getStringList(name + ".locations");
		if(list.contains(getLocationString(location))) {
			p.sendMessage(ChatColor.RED + "["+getLocationString(location)+"] est une location déjà utilisée.");
			return false;
		}
		list.add(getLocationString(location));
		config.set(name + ".locations", list);
		save();
		p.sendMessage(ChatColor.GREEN + "Succès.");
		mobs.add(new OeilAntique(location, config.getDouble("oeil.max-health"), config.getDouble("oeil.range"), config.getDouble("oeil.attack"), 20L*config.getLong("oeil.cooldown"), 20L*60*config.getInt("oeil.respawn")));
		return true;
	}
	
	public boolean removeMob(String name, Location location, Player p) {
		List<String> list = config.getStringList(name + ".locations");
		if( ! list.contains(getLocationString(location))) {
			p.sendMessage(ChatColor.RED + "["+getLocationString(location)+"] n'est pas une location déjà utilisée.");
			return false;
		}
		list.remove(getLocationString(location));
		config.set(name + ".locations", list);
		save();
		for(SuperMob mob : mobs) {
			if(mob.distance(location) < 1) {
				mob.purge();
				p.sendMessage(ChatColor.GREEN + "Succès.");
				return true;
			}
		}
		p.sendMessage(ChatColor.YELLOW + "Supermob enlevé de la config mais pas supprimé du jeu...");
		return false;
	}
	
	private String getLocationString(Location loc) {
		return loc.getWorld().getName() + "_" +loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
	
	private Location getStringLocation(String loc) {
		String[] parts = loc.split("_");
		return new Location(
				Bukkit.getWorld(parts[0]),
				Integer.parseInt(parts[1]),
				Integer.parseInt(parts[2]),
				Integer.parseInt(parts[3])
		);
	}

	public boolean isOne(Entity en) {
		for(SuperMob mob : mobs)
			if(mob.getEntityUUID().equals(en.getUniqueId()))
				return true;
		return false;
	}

}