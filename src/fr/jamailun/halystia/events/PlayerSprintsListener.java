package fr.jamailun.halystia.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

public class PlayerSprintsListener extends HalystiaListener {
	
	public PlayerSprintsListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler
	public void playerRuns(PlayerToggleSprintEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		final Player p = e.getPlayer();
		if(p.getFoodLevel() < 6) {
			e.setCancelled(true);
			return;
		}
		
		new BukkitRunnable() {
			int timer = 0;
			@Override
			public void run() {
				if( ! p.isValid() || p.isDead() || ! p.isSprinting() || p.getFoodLevel() < 2) {
					cancel();
					return;
				}
				timer++;
				if(timer > 8) {
					timer = 0;
					p.setFoodLevel(p.getFoodLevel() - 1);
				}
			}
		}.runTaskTimer(main, 4L, 4L);
	}
}