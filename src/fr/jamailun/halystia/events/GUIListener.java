package fr.jamailun.halystia.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.MenuGUI;

public class GUIListener extends HalystiaListener {

	public GUIListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent e){
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		
		MenuGUI.checkForMenuClose(main, e);
		main.getBanque().close(e.getPlayer().getUniqueId());
	}
	/*
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryDragEvent(InventoryDragEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getWhoClicked()))
			return;
		
		final Player p = (Player) e.getWhoClicked();
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		if(e.getInventory().equals(p.getInventory())) {
			if(e.getInventorySlots().contains(8) || e.getRawSlots().contains(8)) {
				e.setCancelled(true);
				e.setResult(Result.DENY);
				cancelShit(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemHandSwap(PlayerSwapHandItemsEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if(e.getMainHandItem() != null) {
			if(main.getSoulManager().isSoulObject(e.getMainHandItem())) {
				e.setCancelled(true);
				cancelShit(e.getPlayer());
				return;
			}
		}
		if(e.getOffHandItem() != null) {
			if(main.getSoulManager().isSoulObject(e.getOffHandItem())) {
				e.setCancelled(true);
				cancelShit(e.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e){
		if( ! HalystiaRPG.isInRpgWorld(e.getWhoClicked()))
			return;
		
		final Player p = (Player) e.getWhoClicked();
		
		MenuGUI menu = MenuGUI.checkForMenuClick(main, e, false);
		if(menu != null){
			e.setCancelled(true);
			if(e.getWhoClicked() instanceof Player)
				p.updateInventory();
			return;
		}
		
		
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		if(e.getClick() == ClickType.NUMBER_KEY) {
			e.setCancelled(true);
			e.setResult(Result.DENY);
			cancelShit(p);
			return;
		}
		if(e.getClickedInventory() != null) {
			if(e.getClickedInventory().equals(p.getInventory())) {
				if(e.getSlot() == 8 && (e.getClickedInventory().getType() == InventoryType.PLAYER)) {
					e.setCancelled(true);
					e.setResult(Result.DENY);
					cancelShit(p);
					return;
				}
			}
		}
	}
	*/
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
    	if( ! HalystiaRPG.isInRpgWorld(e.getWhoClicked()))
			return;
        MenuGUI menu = MenuGUI.checkForMenuClick(main, e, false);
        if(menu!=null){
            e.setCancelled(true);
            if(e.getWhoClicked() instanceof Player)
            	((Player)e.getWhoClicked()).updateInventory();
        }
    }
	
	/*private void cancelShit(Player p) {
		p.updateInventory();
		Bukkit.getScheduler().runTaskLater(main, new Runnable() {
			@Override
			public void run() {
				p.updateInventory();
			}
		}, 2L);
	}*/

}
