package fr.jamailun.halystia.custom.hazards;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import fr.jamailun.halystia.HalystiaRPG;

public class ZoneCreator {

	static List<Zone> getZones() {
		
		List<Chunk> c1 = Arrays.asList(
			Bukkit.getWorld(HalystiaRPG.PREFIX).getChunkAt(1, 1)	//TODO coordonnées de tous les chunks
				
		);
		
		Zone desert = new Zone(c1);
		
		
		return Arrays.asList(desert);
	}
	
}
