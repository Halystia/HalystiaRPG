package fr.jamailun.halystia.enemies.mobSpawner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;

public class MobSpawner {

	private final Block block;
	private final String name;
	private MobSpawnerType spawnerType;
	
	private FileConfiguration mobConfig;
	
	public MobSpawner(String name, Block block, MobSpawnerType spawnerType) {
		
		this.block = block;
		this.name = name;
		this.mobConfig = HalystiaRPG.getInstance().getMobManager().getConfig();
		this.spawnerType = spawnerType;
		
	}

	public Block getBlock() {
		return block;
	}
	
	public void removeBlock() {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(block.getType() == Material.SPAWNER)
					block.breakNaturally(new ItemStack(Material.AIR));
			}
		}, 10L);
	}
	
	public void createOrUpdateBlock() {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			@Override
			public void run() {

				String mobType = mobConfig.getString(name + ".type");
				EntityType type = EntityType.valueOf(mobType);
				if(type == null) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Impossible to get type of monster [" + name + "]");
					return;
				}
				
				block.setType(Material.SPAWNER);
				
				CreatureSpawner spawner = (CreatureSpawner) block.getState();
				
				spawner.setSpawnedType(type);
				spawner.setMaxSpawnDelay(100000);
				spawner.setMinSpawnDelay(1);
				spawner.setMaxSpawnDelay((spawnerType.delay + 1) * 20);
				spawner.setMinSpawnDelay(spawnerType.delay * 20);
				spawner.setRequiredPlayerRange(spawnerType.playerRange);
				spawner.setSpawnCount(spawnerType.spawnCount);
				spawner.setSpawnRange(spawnerType.range);
				
				spawner.setDelay(-1);
				spawner.update();
			}
		}, 10L);
	}
	
	public String getName() {
		return name;
	}
	
	public MobSpawnerType getType() {
		return spawnerType;
	}

	public void changeType(MobSpawnerType type) {
		spawnerType = type;
		createOrUpdateBlock();
	}
	
}
