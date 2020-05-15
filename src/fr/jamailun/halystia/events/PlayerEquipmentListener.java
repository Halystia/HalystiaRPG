package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.codingforcookies.armorequip.ArmorEquipEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;

public class PlayerEquipmentListener extends HalystiaListener {

	public PlayerEquipmentListener(HalystiaRPG main) {
		super(main);
	}
	

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerEquipArmor(ArmorEquipEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		final Player p = e.getPlayer();
		try {
			Classe classe = main.getClasseManager().getPlayerData(p).getClasse();
			if(e.getNewArmorPiece() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getNewArmorPiece());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					return;
				}
			}
		} catch(NullPointerException ee) {
			e.setCancelled(true);
			p.updateInventory();
			p.sendMessage(RED+"Une erreur est survenue, merci de réessayer dans quelques secondes !");
		}
	}
	
	@EventHandler
	public void playerEquipSubInventory(PlayerSwapHandItemsEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		final Player p = e.getPlayer();
		try {
			Classe classe = main.getClasseManager().getPlayerData(p).getClasse();
			if(e.getMainHandItem() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getMainHandItem());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					return;
				}
			}
			if(e.getOffHandItem() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getOffHandItem());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					return;
				}
			}
		} catch(NullPointerException ee) {
			e.setCancelled(true);
			p.updateInventory();
			p.sendMessage(RED+"Une erreur est survenue, merci de réessayer dans quelques secondes !");
		}
	}

}