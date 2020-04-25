package fr.jamailun.halystia.enemies.mobSpawner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.RandomString;

public class MobSpawnerManager {
	
	private HalystiaRPG api;
	
	private HashMap<World, List<MobSpawner>> ms;
	private HashMap<World, FileConfiguration> configs;
	private HashMap<World, File> files;
	
	public MobSpawnerManager(String path, HalystiaRPG api) {
		this.api = api;
		
		ms = new HashMap<>();
		configs = new HashMap<>();
		files = new HashMap<>();
		
		try {
			
			File dir = new File(path+"/spawners/");
			if( ! dir.exists())
				dir.mkdirs();
			
			for(World world : Bukkit.getServer().getWorlds()) {
				if( ! HalystiaRPG.isRpgWorld(world))
					continue;
				File file = new File(path+"/spawners/" + world.getName() + ".yml");
				if( ! file.exists())
					file.createNewFile();
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				config.save(file);
				
				configs.put(world, config);
				files.put(world, file);
				
				List<MobSpawner> list = new ArrayList<MobSpawner>();
				for(String key : config.getKeys(false)) {
					String name = config.getString(key + ".name");
					String[] loc = config.getString(key + ".location").split(";");
					MobSpawnerType spawnerType = MobSpawnerType.valueOf(config.getString(key + ".type"));
					try {
						int x = Integer.parseInt(loc[0]);
						int y = Integer.parseInt(loc[1]);
						int z = Integer.parseInt(loc[2]);
						Block block = world.getBlockAt(new Location(world, x, y, z));
						
						MobSpawner spawner = new MobSpawner(name, block, spawnerType);
						
						if(block.getType() != Material.SPAWNER)
							spawner.createOrUpdateBlock();
						
						list.add(spawner);
 					} catch(NumberFormatException e) {
 						api.getConsole().sendMessage(ChatColor.RED + "Mauvaise loc pour la clef [" + key + "] ("+file.getPath()+").");
 						continue;
 					}
				}
				
				ms.put(world, list);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean destroySpawner(Block block, CommandSender sender) {
		World world = block.getWorld();
		
		File file = files.get(world);
		FileConfiguration config = configs.get(world);
		
		MobSpawner found = null;
		
		for(MobSpawner spawner : ms.get(world)) {
			Location location = spawner.getBlock().getLocation();
			if(location.equals(block.getLocation())) {
				spawner.removeBlock();
				
				for(String key : config.getKeys(false)) {
					String[] loc = config.getString(key + ".location").split(";");
					
					try {
						int x = Integer.parseInt(loc[0]);
						int y = Integer.parseInt(loc[1]);
						int z = Integer.parseInt(loc[2]);
						if(x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ()) {
							config.set(key, null);
							try {config.save(file);} catch (IOException e) {e.printStackTrace();}
							found = spawner;
							break;
						}
					} catch(NumberFormatException e) {
 						api.getConsole().sendMessage(ChatColor.RED + "Mauvaise loc pour la clef [" + key + "] ("+file.getPath()+").");
 						continue;
 					}
					
				}
				if(found != null)
					break;
				sender.sendMessage(ChatColor.RED + "Impossible de ce spawner dans la config !");
				return false;
			}
		}
		if(found != null) {
			found.removeBlock();
			ms.get(world).remove(found);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Impossible de trouver ce spawner...");
			return false;
		}
	}
	
	public void createSpawner(Block block, CommandSender sender, String name) {
		World world = block.getWorld();
		File file = files.get(world);
		FileConfiguration config = configs.get(world);
		
		String key = new RandomString(10).nextString();
		while(config.contains(key))
			key = new RandomString(10).nextString();
		
		config.set(key + ".name", name);
		config.set(key + ".location", block.getX() + ";" + block.getY() + ";" + block.getZ());
		config.set(key + ".type", MobSpawnerType.NORMAL.toString());
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MobSpawner spawner = new MobSpawner(name, block, MobSpawnerType.NORMAL);
		spawner.createOrUpdateBlock();
		ms.get(world).add(spawner);
		
	}
	
	public MobSpawner getSpawner(Location location) {
		World world = location.getWorld();
		for(MobSpawner spawner : ms.get(world)) {
			if(spawner.getBlock().equals(world.getBlockAt(location))) {
				return spawner;
			}
		}
		return null;
	}
	
	public boolean changeSpawnerMode(MobSpawner spawner, MobSpawnerType type) {
		World world = spawner.getBlock().getWorld();
		File file = files.get(world);
		FileConfiguration config = configs.get(world);
		Location location = spawner.getBlock().getLocation();
		for(String key : config.getKeys(false)) {
			String[] loc = config.getString(key + ".location").split(";");
			try {
				int x = Integer.parseInt(loc[0]);
				int y = Integer.parseInt(loc[1]);
				int z = Integer.parseInt(loc[2]);
				if(x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ()) {
					config.set(key + ".type", type.toString());
					try {config.save(file);} catch (IOException e) {e.printStackTrace();}
					spawner.changeType(type);
					return true;
				}
			} catch(NumberFormatException e) {
				api.getConsole().sendMessage(ChatColor.RED + "Mauvaise loc pour la clef [" + key + "] ("+file.getPath()+").");
				continue;
			}
		}
		return false;
	}
	
}
