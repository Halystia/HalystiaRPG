package fr.jamailun.halystia.custom.hazards;

public abstract class Hazard {
	
	protected final HazardManager manager;
	
	public Hazard(HazardManager manager) {
		this.manager = manager;
	}
	
	public abstract HazardType getHazardType();
	
	public void stop(boolean alertManager) {
		
	}
	
}
