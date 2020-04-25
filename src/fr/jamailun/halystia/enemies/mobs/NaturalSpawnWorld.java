package fr.jamailun.halystia.enemies.mobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.chunks.ChunkManager;
import fr.jamailun.halystia.chunks.ChunkType;
import fr.jamailun.halystia.utils.RandomString;

public class NaturalSpawnWorld {

	public final static int REMOVE_ALL_EVERY_X_TICKS = 5;
	public final static int TRIES_PER_PLAYER = 10;
	
	public final static int Y_SPAWN_DISTANCE = 10;
	public final static double ADD_DISTANCE = 40;
	public final static double REMOVAL_DISTANCE = 60;
	public final static double MINIMAL_DISTANCE = 10;
	public final static double MAX_MOBS_AROUND = 20;
	
	private final World world;
	private final MobManager mobs;
	private final ChunkManager chunks;
	
	public NaturalSpawnWorld(HalystiaRPG main, MobManager mobs, ChunkManager chunks, String worldName) {
		this.mobs = mobs;
		this.chunks = chunks;
		world = Bukkit.getWorld(worldName);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				tick();
			}
		}.runTaskTimer(main, 4*20L, 4*20L);
	}
	
	private int counterRemove = 0;
	
	public void tick() {
		counterRemove++;
		if(counterRemove >= REMOVE_ALL_EVERY_X_TICKS) {
			mobs.killNonReferedsMobs(world);
			mobs.removeTooFar(world, REMOVAL_DISTANCE);
			counterRemove = 0;
		}
		
		for(Player p : world.getPlayers()) {
			Location loc = p.getLocation();

			if( ! canSpawn(loc) )
				return;
			int minY = (int) (loc.getBlockY() - (Y_SPAWN_DISTANCE / 2) );
			if(minY < 0)
				minY = 0;
			int maxY = (int) (loc.getBlockY() + Y_SPAWN_DISTANCE);
			if(maxY > 200)
				maxY = 200;
			
			int tent = TRIES_PER_PLAYER;
			while ( tent >= 0) {
				tent--;
				int x = loc.getBlockX() + (int) RandomString.randDouble(- ADD_DISTANCE, ADD_DISTANCE);
				int z = loc.getBlockZ() + (int) RandomString.randDouble(- ADD_DISTANCE, ADD_DISTANCE);
				int y = minY;
				if(world.getBlockAt(x, y, z).getLocation().distance(loc) < MINIMAL_DISTANCE)
					continue;
				ChunkType chunk = chunks.getValueOfChunk(world.getBlockAt(x, y, z).getChunk());
				if(chunk == null) //chunk non valide
					continue;
				boolean validZone = false;
				while( y < maxY && !validZone) {
					y++;
					validZone = isValidZone( world.getBlockAt(x, y, z) );
				}
				if( ! validZone) //on a pas trouvé d'endroit bien.
					continue;
				
				String mobName = chunk.createMobIdToSpawn();
				if(mobName == null)
					continue; // pas de mob ici :D
				
				mobs.spawnMob(mobName, world.getBlockAt(x, y, z).getLocation().add(0.5, 0, 0.5), false);
		//		System.out.println("SPAWN DE "+mobName+" en x="+x+", y="+y+", z="+z);
			}
		}
	}
	
	private boolean isValidZone(Block b) {
		if( ! isSolidBlock(b.getRelative(0, -1, 0)))
			return false; //pas du sol
		return isValidBlock(b) && isValidBlock(b.getRelative(0, 1, 0));
	}
	
	private boolean isSolidBlock(Block b) {
		return b.getType().isSolid() && b.getType().isOccluding();
	}
	
	private boolean isValidBlock(Block b) {
		return b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR;
	}
	
	private boolean canSpawn(Location loc) {
		return mobs.getHowManyMobsAround(loc, ADD_DISTANCE) < MAX_MOBS_AROUND;
	}
	
	
}