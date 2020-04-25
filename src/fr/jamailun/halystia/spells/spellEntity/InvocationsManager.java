package fr.jamailun.halystia.spells.spellEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.spells.Invocator;

public class InvocationsManager {
	
	private Map<UUID, UUID> map; // entity - player
	private Map<UUID, Invocator> mapSources; // entity - invocator interface
	private Map<UUID, Integer> mapDamages; // entity - damages
	
	private final HalystiaRPG main;
	public InvocationsManager(HalystiaRPG main) {
		this.main = main;
		map = new HashMap<>();
		mapSources = new HashMap<>();
		mapDamages = new HashMap<>();
	}
	
	public synchronized void add(Entity e, Player creator, boolean canAttackCreator, Invocator source, int damages) {
		map.put(e.getUniqueId(), canAttackCreator ? null : creator.getUniqueId());
		mapSources.put(e.getUniqueId(), source);
		mapDamages.put(e.getUniqueId(), damages);
	}
	
	public synchronized HashMap<UUID, UUID> getList() {
		return new HashMap<>(map);
	}
	
	public synchronized boolean haveSameMaster(UUID a, UUID b) {
		if(map.containsKey(a) && map.containsKey(b))
			return map.get(a).equals(map.get(b));
		return false;
	}
	
	public boolean isMasterOf(Player p, Entity e) {
		if( ! map.containsKey(e.getUniqueId())) //pas dans la map
			return false;
		if(map.get(e.getUniqueId()) == null) //pas de maitre
			return false;
		return map.get(e.getUniqueId()).equals(p.getUniqueId());
	}
	
	public synchronized String getCasterName(Entity e) {
		if(map.containsKey(e.getUniqueId()))
			return Bukkit.getPlayer(map.get(e.getUniqueId())).getName();
		return "un dur adversaire !";
	}
	
	public synchronized double getDamages(Entity e) {
		if( ! mapDamages.containsKey(e.getUniqueId()))
			return -1;
		return mapDamages.get(e.getUniqueId());
	}
	
	public synchronized void entityDeath(Entity e) {
		mapSources.get(e.getUniqueId()).oneIsDead(map.get(e.getUniqueId()));
		map.remove(e.getUniqueId());
		mapSources.remove(e.getUniqueId());
		mapDamages.remove(e.getUniqueId());
	}
	
	public boolean contains(UUID uuid) {
		return map.containsKey(uuid);
	}
	
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
	
}
