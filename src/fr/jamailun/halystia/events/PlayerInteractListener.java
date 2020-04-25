package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.codingforcookies.armorequip.ArmorEquipEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.custom.boats.CustomBoatManager;
import fr.jamailun.halystia.players.Classe;

public class PlayerInteractListener extends HalystiaListener {

	public PlayerInteractListener(HalystiaRPG main) {
		super(main);
	}
	
	private long lastBoat = System.currentTimeMillis();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if( ! HalystiaRPG.isInRpgWorld(p))
			return;
		
		if(e.getAction() == Action.PHYSICAL) {
			if(e.getClickedBlock() == null)
				return;
			if(e.getClickedBlock().getType() == Material.FARMLAND) {
				e.setUseInteractedBlock(Event.Result.DENY);
				e.setCancelled(true);
			}
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(main.getSpellManager().tryCastSpell(p)) {
				e.setCancelled(true);
				return;
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.ENDER_CHEST) {
				e.setCancelled(true);
				main.getBanque().openAccount(p);
				return;
			}
		}
		if(p.getGameMode() == GameMode.CREATIVE)
			return;
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.SMOKER || e.getClickedBlock().getType() == Material.BLAST_FURNACE
					|| e.getClickedBlock().getType() == Material.ENCHANTING_TABLE || e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.CHIPPED_ANVIL
					|| e.getClickedBlock().getType() == Material.DAMAGED_ANVIL || e.getClickedBlock().getType() == Material.BREWING_STAND || e.getClickedBlock().getType() == Material.GRINDSTONE
			) {
				e.setCancelled(true);
				return;
			}
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getInventory().getItemInMainHand() != null) {
			
			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

			if(e.getClickedBlock().getType() == Material.SAND && CustomBoatManager.boatsMaterials.contains(item.getType())) {
					
				//c'est un bateau
				if( ! e.getClickedBlock().getType().isSolid())
					return;
				//on clic sur un block, un vrai
				
				e.setCancelled(true);
			
				
				if(main.getBoatManager().isCustomBoatItem(e.getItem())) {
					if(System.currentTimeMillis() - lastBoat <= 100)
						return;
					p.getInventory().getItemInMainHand().setType(Material.AIR);
					main.getBoatManager().spawn(e.getClickedBlock().getLocation().clone().add(0,1,0));
					p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Bateau des sables spawné avec succès !");
					lastBoat = System.currentTimeMillis();
				}
			}
		}
		
		Classe classe = Classe.NONE;
		try {
			classe = main.getClasseManager().getPlayerData(p).getClasse();
		} catch (NullPointerException e1) {
			return;
		}
		
		
		if(p.getInventory().getItemInMainHand() != null) {
			Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInMainHand());
			if(classe != ob && ob != Classe.NONE) {
				e.setCancelled(true);
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
				return;
			}
		}
		
		if(p.getInventory().getItemInOffHand() != null) {
			Classe ob = main.getTradeManager().getClasseOfItem(p.getInventory().getItemInOffHand());
			if(classe != ob && ob != Classe.NONE) {
				e.setCancelled(true);
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe adaptée au maniement de cet objet !");
				return;
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerCraft(CraftItemEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getWhoClicked()))
			return;
		if(e.getRecipe().getResult().getType() == Material.FISHING_ROD) {
			e.setCancelled(true);
			return;
		}
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
			e.getPlayer().sendMessage(RED+"Une erreur est survenue, merci de réessayer dans quelques secondes !");
		}
	}

}
