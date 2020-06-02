package fr.jamailun.halystia.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;

public class ConsumeItemListener extends HalystiaListener {

	public ConsumeItemListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler
	public void playerConsumeItemEvent(PlayerItemConsumeEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		int manaGiven = main.getPotionManager().getPowerOfBottle(e.getItem());
		if(manaGiven > -1) {
			try {
				PlayerData pc = main.getClasseManager().getPlayerData(e.getPlayer());
				if(pc.addManaRegen(manaGiven)) {
					pc.getPlayer().sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Potion de mana consommée ! Tu regagneras " + ChatColor.GOLD + manaGiven + ChatColor.AQUA + " mana.");
					return;
				}
				pc.getPlayer().sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Tu as déjà tous tes points de mana !");
				e.setCancelled(true);
				return;
			} catch(NullPointerException ee) {}
		}
	}
	
	@EventHandler
	public void leaveDecayEvent(LeavesDecayEvent e) {
		if( ! HalystiaRPG.isRpgWorld(e.getBlock().getWorld()))
			return;
		e.setCancelled(true);
	}
}