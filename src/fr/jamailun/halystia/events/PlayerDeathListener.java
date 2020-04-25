package fr.jamailun.halystia.events;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.RandomString;

public class PlayerDeathListener extends HalystiaListener {

	public PlayerDeathListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDeathEvent(PlayerDeathEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		final Player p = e.getEntity();
		e.setDeathMessage("");
		if(p.getGameMode() == GameMode.CREATIVE) {
			e.setKeepInventory(true);
			return;
		}
		
		e.setKeepInventory(true);
		e.setKeepLevel(false);
		
		List<ItemStack> items = new ArrayList<>();
		
		for(ItemStack item : p.getInventory().getExtraContents())
			if(item != null)
				items.add(item);
		for(ItemStack item : p.getInventory().getContents()) {
			if(item != null) {
				items.add(item);
			}
		}	
		
		final double toLoose = main.getSoulManager().stuffLost(p);
		if(toLoose == 0)
			e.setKeepLevel(true);
		int stacksToDrop = (int) (((double)items.size()) * toLoose);
		
		//System.out.println("Il faut dropper x"+stacksToDrop+". Car taille inventaire =" +items.size());
		
		for(int i = 0; i < stacksToDrop; i++) {
			try {
				ItemStack stack = items.get(RandomString.randInt(0, items.size()-1));
				//items.remove(stack);
				p.getWorld().dropItemNaturally(p.getLocation(), stack);
				stack.setAmount(0);
				
				boolean removed = false;
				ItemStack[] armors = p.getEquipment().getArmorContents();
	            for (int j = 0; j < armors.length; j++) {
	                if (armors[j] != null) {
	                	if(Trade.areItemsTheSame(armors[j], stack)) {
		                    armors[j] = null;
		                    removed = true;
							break;
	                	}
	                }
	            }
	            if(removed)
	                p.getEquipment().setArmorContents(armors);
				
				if(!removed)
					p.getInventory().remove(stack);
				
			} catch (Exception ex) {}
		}
		
		if(toLoose > 0)
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu es mort. Tu as perdu " +DARK_RED+ ((int)(toLoose*100))+"%" +RED+ " de ton stuff");
		else
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu es mort. "+GREEN+"Mais ayant toutes tes âmes, tu n'as perdu aucun stuff.");
		main.getDataBase().looseSoul(p);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerRespawnEvent(PlayerRespawnEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
			main.getSoulManager().tryRefreshSoul(e.getPlayer());
	}
	
}
