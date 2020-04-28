package fr.jamailun.halystia.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class BossListeners extends HalystiaListener {

	public BossListeners(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		e.blockList().clear();
	}

}