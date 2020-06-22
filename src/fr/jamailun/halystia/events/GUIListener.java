package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
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
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
    	if( ! HalystiaRPG.isInRpgWorld(e.getWhoClicked()))
			return;
        if( ! (e.getWhoClicked() instanceof Player))
        	return;
        final Player p = (Player) e.getWhoClicked();
        MenuGUI menu = MenuGUI.checkForMenuClick(main, e, false);
        if(menu!=null){
            e.setCancelled(true);
            	p.updateInventory();
            return;
        }
        // OFF_HAND equip !
        if(e.getSlot() == 40) {
        	PlayerData pc = main.getClasseManager().getPlayerData(p);
        	Classe classe = pc.getClasse();
			if(e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getCursor());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					pc.playerEquipItem(EquipmentSlot.OFF_HAND, e.getCurrentItem());
					return;
				}
			}
			pc.playerEquipItem(EquipmentSlot.OFF_HAND, e.getCursor());
        }
        //e.getWhoClicked().sendMessage("slotn = " + e.getSlot()+", slottype="+e.getSlotType()+", action=" + e.getAction()+", item current=§e"+e.getCurrentItem()+"§f, cursor=§a"+e.getCursor());
    }
}