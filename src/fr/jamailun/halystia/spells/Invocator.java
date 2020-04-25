package fr.jamailun.halystia.spells;

import java.util.UUID;

public interface Invocator {

	public boolean canInvoke(UUID uuid, int homMany);
	
	public void oneIsDead(UUID uuid);
	
}
