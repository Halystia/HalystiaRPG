package fr.jamailun.halystia.events;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.EnemyMob;
import fr.jamailun.halystia.spells.spellEntity.InvocationsManager;

public class MobAggroListener extends HalystiaListener {
	
	private InvocationsManager invocs;
	
	public MobAggroListener(HalystiaRPG main) {
		super(main);
		invocs = main.getSpellManager().getInvocationsManager();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void mobAggro(EntityTargetLivingEntityEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if(e.getTarget() == null)
			return;
		
		if(invocs.haveSameMaster(e.getEntity().getUniqueId(), e.getTarget().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		
		
		if( ! (e.getTarget() instanceof Player))
			return;
		
		Player target = (Player) e.getTarget();
		
		if(invocs.isMasterOf(target, e.getEntity())) {
			e.setCancelled(true);
			return;
		}
		
	}
	
	@EventHandler
	public void mobShoot(EntityShootBowEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if( ! (e.getEntity() instanceof Monster) )
			return;
		EnemyMob mob = main.getMobManager().getWithEntityId(e.getEntity().getEntityId());
		if(mob != null)
			if(mob.getCustomDamages() == -1)
				e.getProjectile().setMetadata("damages", new FixedMetadataValue(main, mob.getCustomDamages()));
	}
	
	@EventHandler
	public void despawnArrows(ProjectileHitEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if(e.getHitBlock() != null)
			e.getEntity().remove();
	}
}