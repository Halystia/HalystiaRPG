package fr.jamailun.halystia.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;

public class PlayerDropItemListener extends HalystiaListener {
	
	public PlayerDropItemListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDropItemEvent(PlayerDropItemEvent e) {
		if(! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		ItemStack drop = e.getItemDrop().getItemStack();
		if(main.getSoulManager().isSoulObject(drop)) {
			e.setCancelled(true);
			return;
		}
		
	}

}
