package fr.jamailun.halystia.custom.hazards.sandstrom;

import fr.jamailun.halystia.custom.hazards.Hazard;
import fr.jamailun.halystia.custom.hazards.HazardManager;
import fr.jamailun.halystia.custom.hazards.HazardType;

public class SandStorm extends Hazard {

	public SandStorm(HazardManager manager) {
		super(manager);
	}

	@Override
	public HazardType getHazardType() {
		return HazardType.SANDSTORM;
	}

}
