package fr.jamailun.halystia.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.fishing.Canne;

public class PlayerFishListener extends HalystiaListener {
	
	public PlayerFishListener(HalystiaRPG main) {
		super(main);
	}

	@EventHandler
	public void playerFishEvent(PlayerFishEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		e.setExpToDrop(0);
		if(e.getCaught() == null)
			return;
		Player p = e.getPlayer();
		
		if( ! (e.getCaught() instanceof Item)) {
			System.err.println("Truc péché pas item ? wtf ?");
			return;
		}
		Item item = (Item) e.getCaught();
		
		Canne canne = Canne.getCanneWithItem(p.getInventory().getItemInMainHand());
		if(canne == null) { // Canne non valide !
			p.sendMessage(ChatColor.RED + "Cette canne est illégale ! Utilise une canne autorisée.");
			e.setCancelled(true);
			item.setItemStack(new ItemStack(Material.DEAD_BUSH));
			return;
		}
		
		//on modifie l'item pêché.
		item.setItemStack(canne.drop());
	}
}