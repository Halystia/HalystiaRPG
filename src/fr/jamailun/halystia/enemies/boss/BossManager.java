package fr.jamailun.halystia.enemies.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;

public class BossManager {

	private List<Boss> bosses;
	
	public BossManager() {
		bosses = new ArrayList<>();
	}
	
	public void purge() {
		for(Boss boss : bosses)
			boss.purge();
	}
	
	
	/**
	 * @return true if the entity has been foundd.
	 */
	public boolean damageBoss(Entity entity, UUID damager, double damages) {
		for(Boss boss : bosses) {
			if(boss.isBoss(entity.getUniqueId())) {
				if(damager == null)
					boss.damage(damages);
				else
					boss.damage(damager, damages);
				return true;
			}
		}
		return false;
	}

	public boolean bossSpawned(Boss boss) {
		if(bosses.contains(boss))
			return false;
		bosses.add(boss);
		return true;
	}
	
	public boolean bossKilled(Boss boss) {
		if(bosses.contains(boss))
			return false;
		bosses.add(boss);
		return true;
	}
	
	public boolean isBoss(Entity en) {
		for(Boss boss : bosses)
			if(boss.isBoss(en.getUniqueId()))
				return true;
		return false;
	}

	public Boss getBoss(Entity en) {
		for(Boss boss : bosses)
			if(boss.isBoss(en.getUniqueId()))
				return boss;
		return null;
	}
	
}