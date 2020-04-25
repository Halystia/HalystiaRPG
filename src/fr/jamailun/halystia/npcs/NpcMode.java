package fr.jamailun.halystia.npcs;

import org.mcmonkey.sentinel.SentinelTrait;
import net.citizensnpcs.api.trait.Trait;

public enum NpcMode {

	STANDING(null),
	SENTINEL(SentinelTrait.class);
	
	private final Class<? extends Trait> classe;
	private NpcMode(Class<? extends Trait> classe) {
		this.classe = classe; 
	}
	
	public Class<? extends Trait> getTrait() {
		return classe;
	}
}