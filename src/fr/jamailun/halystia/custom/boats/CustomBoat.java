package fr.jamailun.halystia.custom.boats;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;

final class CustomBoat {
	
	private final Boat boat;
	
	@SuppressWarnings("deprecation")
	CustomBoat(Location spawn) {
		boat = spawn.getWorld().spawn(spawn, Boat.class);
		
		boat.setWorkOnLand(true);
		boat.setMaxSpeed(boat.getMaxSpeed() * 5);
		boat.setOccupiedDeceleration(boat.getOccupiedDeceleration() * 0.5);
		boat.setWoodType(TreeSpecies.BIRCH);
	}
	
	UUID getUUID() {
		return boat.getUniqueId();
	}
	
	void remove() {
		if(boat.isValid())
			boat.remove();
	}
	
}
