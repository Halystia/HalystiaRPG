package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.RED;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.codingforcookies.armorequip.ArmorEquipEvent.EquipMethod;
import com.codingforcookies.armorequip.ArmorType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.utils.RpgEquipment;

public class PlayerEquipmentListener extends HalystiaListener {

	public PlayerEquipmentListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerEquipArmor(ArmorEquipEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		if(e.getMethod() == EquipMethod.DEATH || e.getMethod() == EquipMethod.BROKE)
			return;
		final Player p = e.getPlayer();
		try {
			PlayerData pc = main.getClasseManager().getPlayerData(p);
			if(pc == null) {
				e.setCancelled(true);
				return;
			}
			Classe classe = pc.getClasse();
			if(e.getNewArmorPiece() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getNewArmorPiece());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					return;
				}
			}
			if(new RpgEquipment(e.getNewArmorPiece()).getLevel() > pc.getLevel()) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas le niveau adaptée au maniement de cet objet !");
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
				return;
			}
			pc.playerEquipItem(translate(e.getType()), e.getNewArmorPiece());
		} catch(NullPointerException ee) {
			e.setCancelled(true);
			p.updateInventory();
			p.sendMessage(RED+"Une erreur est survenue, merci de réessayer dans quelques secondes !");
		}
	}
	
	private EquipmentSlot translate(ArmorType type) {
		switch(type) {
		case BOOTS:
			return EquipmentSlot.FEET;
		case CHESTPLATE:
			return EquipmentSlot.CHEST;
		case HELMET:
			return EquipmentSlot.HEAD;
		case LEGGINGS:
			return EquipmentSlot.LEGS;
		}
		return EquipmentSlot.HAND;
	}
	
	@EventHandler
	public void playerHotBarChange(PlayerItemHeldEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		final Player p = e.getPlayer();
		PlayerData pc = main.getClasseManager().getPlayerData(p);
		if(pc == null) {
			e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
			return;
		}
		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		if(item != null) {
			Classe ob = main.getTradeManager().getClasseOfItem(item);
			if(pc.getClasse() != ob && ob != Classe.NONE) {
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
				return;
			}
			if(new RpgEquipment(item).getLevel() > pc.getLevel()) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas le niveau adaptée au maniement de cet objet !");
				e.setCancelled(p.getGameMode() != GameMode.CREATIVE);
				return;
			}
		}

		pc.playerEquipItem(EquipmentSlot.HAND, p.getInventory().getItem(e.getNewSlot()));
	}
	
	@EventHandler
	public void playerEquipSubInventory(PlayerSwapHandItemsEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		final Player p = e.getPlayer();
		try {
			PlayerData pc = main.getClasseManager().getPlayerData(p);
			if(pc == null) {
				e.setCancelled(true);
				return;
			}
			Classe classe = pc.getClasse();
			if(e.getMainHandItem() != null) {
				Classe ob = main.getTradeManager().getClasseOfItem(e.getMainHandItem());
				if(classe != ob && ob != Classe.NONE) {
					e.setCancelled(true);
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée pour équiper cet objet !");
					p.updateInventory();
					return;
				}
				if(new RpgEquipment(e.getMainHandItem()).getLevel() > pc.getLevel()) {
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas le niveau adaptée au maniement de cet objet !");
					e.setCancelled(true);
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
				if(new RpgEquipment(e.getOffHandItem()).getLevel() > pc.getLevel()) {
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas le niveau adaptée au maniement de cet objet !");
					e.setCancelled(true);
					return;
				}
			}
			pc.playerEquipItem(EquipmentSlot.OFF_HAND, p.getInventory().getItemInOffHand());
			pc.playerEquipItem(EquipmentSlot.HAND, p.getInventory().getItemInMainHand());
		} catch(NullPointerException ee) {
			e.setCancelled(true);
			p.updateInventory();
			p.sendMessage(RED+"Une erreur est survenue, merci de réessayer dans quelques secondes !");
		}
	}

}