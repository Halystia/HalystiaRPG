package fr.jamailun.halystia.spells;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;

/**
 * Spell specialized in the management of units.
 * @see Invocator
 */
public abstract class InvocationSpell extends Spell implements Invocator {
	
	private HashMap<UUID, Integer> map = new HashMap<>();
	/**
	 * how many units can be invoced at the same time. Set it in the {@link #init()} method.
	 */
	protected int LIMIT = 2;
	
	@Override
	public synchronized boolean canInvoke(UUID uuid, int howMany) {
		if( ! map.containsKey(uuid))
			return true;
		return map.get(uuid) + howMany <= LIMIT;
	}
	
	@Override
	public synchronized void oneIsDead(UUID uuid) {
		if( ! map.containsKey(uuid))
			return;
		int current = map.get(uuid);
		map.replace(uuid, current - 1);
	}
	
	/**
	 * Alert the system about the creation of a summoned unit.
	 * @param entity the summoned unit
	 * @param p the caster
	 * @param masterCanBeAttacked if true the master will also be targeted by is own units.
	 * @param damages Custom amount of damages the entity with deal. -1 for natural damages.
	 */
	protected synchronized void addInvocation(Entity entity, Player p, boolean masterCanBeAttacked, int damages) {
		if( ! map.containsKey(p.getUniqueId()))
			map.put(p.getUniqueId(), 1);
		else
			map.replace(p.getUniqueId(), 1 + map.get(p.getUniqueId()));
		HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add(entity, p, masterCanBeAttacked, this, damages);
	}
	
	/**
	 * Reset all the data.
	 */
	public void reset() {
		map.clear();
	}
	
}