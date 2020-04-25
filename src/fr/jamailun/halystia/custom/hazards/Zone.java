package fr.jamailun.halystia.custom.hazards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class Zone {

	private List<Chunk> chunks;
	
	public Zone(List<Chunk> chunks) {
		chunks = new ArrayList<>(chunks);
	}
	
	public boolean containsChunk(Chunk chunk) {
		for(Chunk c : chunks)
			if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ())
				return true;
		return false;
	}
	
	public List<Chunk> getChunks() {
		return new ArrayList<>(chunks);
	}
	
	public List<Player> getContainedPlayers() {
		List<Player> list = new ArrayList<>();
		for(Chunk c : chunks) {
			for(Entity entity : c.getEntities()) {
				if(entity instanceof Player)
					list.add((Player)entity);
			}
		}
		return list;
	}
	
}
