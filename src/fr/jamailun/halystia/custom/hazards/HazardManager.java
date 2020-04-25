package fr.jamailun.halystia.custom.hazards;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import fr.jamailun.halystia.utils.FileDataRPG;

public class HazardManager extends FileDataRPG {
	
	private HashMap<Zone, Hazard> hazards;
	
	public HazardManager(String path, String name) {
		super(path, name);
		
		for(Zone zone : ZoneCreator.getZones()) {
			hazards.put(zone, null);
		}
		
		Bukkit.getLogger().log(Level.INFO, "Generated " + hazards.size() + " zones.");
	}
	
	public boolean canStartsHazard(Zone zone) {
		return hazards.get(zone) == null;
	}
	
	public void stopHazard(Zone zone) {
		if(hazards.get(zone) != null) {
			hazards.get(zone).stop(false);
		}
		hazards.replace(zone, null);
	}

}