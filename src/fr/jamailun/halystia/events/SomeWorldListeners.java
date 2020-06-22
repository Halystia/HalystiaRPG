package fr.jamailun.halystia.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class SomeWorldListeners extends HalystiaListener {

	public SomeWorldListeners(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler
	public void leaveDecayEvent(LeavesDecayEvent e) {
		if( ! HalystiaRPG.isRpgWorld(e.getBlock().getWorld()))
			return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void farmDepopEvent(BlockFadeEvent e) {
		if( ! HalystiaRPG.isRpgWorld(e.getBlock().getWorld()))
			return;
		
		if(e.getBlock().getType() == Material.FARMLAND)
			e.setCancelled(true);
			
	}
	
}