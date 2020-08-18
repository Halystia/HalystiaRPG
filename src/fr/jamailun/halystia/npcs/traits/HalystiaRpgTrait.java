package fr.jamailun.halystia.npcs.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;

@TraitName("rpg")
public class HalystiaRpgTrait extends Trait {
	
	public HalystiaRpgTrait() {
		super("rpg");
	}
	
	
	//tick played
	public void run() {}
	
	public void load(DataKey key) {
		
	}

	// Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
	public void save(DataKey key) {
		
	}
}