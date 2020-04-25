package fr.jamailun.halystia.spells;

import java.util.UUID;

/**
 * Thing which can invok things.
 * @see InvocationSpell
 */
public interface Invocator {

	/**
	 * Check if a summoner can summon a certain amount of entities.
	 * @param uuid UUID of the summoner.
	 * @param homMany how many units the summor wants to invoke.
	 * @return true if the summoer can invoke that amount of units.
	 */
	public boolean canInvoke(UUID uuid, int homMany);
	
	/**
	 * Alert the Incator about the death of a unit
	 * @param uuid UUID of the dead unit.
	 */
	public void oneIsDead(UUID uuid);
	
}
