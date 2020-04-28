package fr.jamailun.halystia.spells.spellEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.spells.Invocator;

/**
 * Register for all invocations.
 * Never summo ones, just keep a trace of the data.
 */
public class InvocationsManager {
	
	private Map<UUID, UUID> map; // entity - player
	private Map<UUID, Invocator> mapSources; // entity - invocator interface
	private Map<UUID, Integer> mapDamages; // entity - damages
	
	private final HalystiaRPG main;
	/**
	 * Do not call it, HalystiaRPG does.
	 */
	public InvocationsManager(HalystiaRPG main) {
		this.main = main;
		map = new HashMap<>();
		mapSources = new HashMap<>();
		mapDamages = new HashMap<>();
	}
	
	/**
	 * Inform the system about the formation of a unit.
	 * @param e unit summoned by a Player under the control of an Invocator source
	 * @param creator Summoner of this unit.
	 * @param canAttackCreator if true, the unit will be able to target the caster.
	 * @param source Invokter who check the informations between invocations & invokaters
	 * @param damages custo amount of damage the unit will deal
	 */
	public synchronized void add(Entity e, LivingEntity creator, boolean canAttackCreator, Invocator source, int damages) {
		map.put(e.getUniqueId(), canAttackCreator ? null : creator.getUniqueId());
		mapSources.put(e.getUniqueId(), source);
		mapDamages.put(e.getUniqueId(), damages);
	}
	
	/**
	 * Get all invocations. It's a COPY only.
	 * @return a map<Entity, Summoner>'s uuids
	 */
	public synchronized HashMap<UUID, UUID> getList() {
		return new HashMap<>(map);
	}
	
	/**
	 * Check if two entities have the same master
	 * @return true if it's the case.
	 */
	public synchronized boolean haveSameMaster(UUID a, UUID b) {
		if(map.containsKey(a) && map.containsKey(b))
			return map.get(a).equals(map.get(b));
		return false;
	}
	
	/**
	 * Check if a Player controls an entity.
	 * @return true if he does.
	 */
	public boolean isMasterOf(LivingEntity p, Entity e) {
		if( ! map.containsKey(e.getUniqueId())) //pas dans la map
			return false;
		if(map.get(e.getUniqueId()) == null) //pas de maitre
			return false;
		return map.get(e.getUniqueId()).equals(p.getUniqueId());
	}
	
	/**
	 * Get the name of the summoner of a unit.
	 * @return "un dur adversaire !" if nothing has benn found.
	 */
	public synchronized String getCasterName(Entity e) {
		if(map.containsKey(e.getUniqueId()))
			return Bukkit.getPlayer(map.get(e.getUniqueId())).getName();
		return "un dur adversaire !";
	}
	
	/**
	 * Get the custom amount of damages of an entity.
	 * @return -1 if nothing is here.
	 */
	public synchronized double getDamages(Entity e) {
		if( ! mapDamages.containsKey(e.getUniqueId()))
			return -1;
		return mapDamages.get(e.getUniqueId());
	}
	
	/**
	 * Inform the system aount the death of an entity.
	 */
	public synchronized void entityDeath(Entity e) {
		mapSources.get(e.getUniqueId()).oneIsDead(map.get(e.getUniqueId()));
		map.remove(e.getUniqueId());
		mapSources.remove(e.getUniqueId());
		mapDamages.remove(e.getUniqueId());
	}
	
	/**
	 * Check if an UUID correspond to a summond unit.
	 * @return true if it's the case.
	 */
	public boolean contains(UUID uuid) {
		return map.containsKey(uuid);
	}
	
	/**
	 * Remove all summoned entities and clear the data.
	 */
	public void purge() {
		//List<Entity> toRemove = new ArrayList<>();
		World world = Bukkit.getServer().getWorld(HalystiaRPG.WORLD);
		for(Entity e : world.getEntities()) {
			for(UUID uuid : map.keySet()) {
				if(e.getUniqueId().equals(uuid)) {
					e.remove();
					continue;
				}
			}
		}
		//for(Entity e : toRemove)
		//	world.getEntities().remove(index)
		map.clear();
		mapSources.clear();
		mapDamages.clear();
		
		main.getSpellManager().resetInvocations();
	}
	
	/**
	 * 
	 */
	public UUID getMasterOf(UUID unit) {
		return map.get(unit);
	}
	
}
