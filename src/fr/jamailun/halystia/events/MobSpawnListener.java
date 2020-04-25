package fr.jamailun.halystia.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawner;

public class MobSpawnListener extends HalystiaListener {
	
	private List<SpawnReason> allowed = Arrays.asList(
			SpawnReason.SHEARED,
			SpawnReason.SPAWNER,
			SpawnReason.CUSTOM,
			SpawnReason.DEFAULT
		);
	
	public MobSpawnListener(HalystiaRPG main) {
		super(main);
	//	allowed = 
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void creatureSpawn(CreatureSpawnEvent e) {
		if( ! HalystiaRPG.isRpgWorld(e.getEntity().getWorld()))
			return;
		if( ! allowed.contains(e.getSpawnReason()) )
			e.setCancelled(true);
	}
	
	@EventHandler
	public void spawnerEvent(SpawnerSpawnEvent e) {
		World world = e.getEntity().getWorld();
		if( ! HalystiaRPG.isRpgWorld(world))
			return;
		
		if(world.getName().contains(HalystiaRPG.DONJONS_WORLD_CONTAINS)) {
			boolean insideDonjon = false;
			final double x = e.getEntity().getLocation().getX();
			final double z = e.getEntity().getLocation().getZ();
			final double y = e.getEntity().getLocation().getY();
			for(int h = 1; h <= 50; h++) {
				if(world.getBlockAt(new Location(world, x, y + h, z)).getType() != Material.AIR) {
					insideDonjon = true;
					break;
				}
			}
			if( ! insideDonjon) {
				e.setCancelled(true);
				return;
			}
		}
		
		MobSpawner spawner = main.getMobSpawnerManager().getSpawner(e.getSpawner().getLocation());
		if(spawner != null){
			main.getMobManager().spawnMob( spawner.getName(), e.getLocation(), world.getName().contains(HalystiaRPG.DONJONS_WORLD_CONTAINS) );
			e.setCancelled(true);
		}
	}

}
