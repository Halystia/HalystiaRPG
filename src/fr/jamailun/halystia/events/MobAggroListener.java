package fr.jamailun.halystia.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import fr.jamailun.halystia.HalystiaRPG;
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
}