package fr.jamailun.halystia.events;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class PlayerJoinLeaveListener extends HalystiaListener {
	
	public PlayerJoinLeaveListener(HalystiaRPG main) {
		super(main);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if( ! HalystiaRPG.isInRpgWorld(player))
			return;
		joinGame(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerLeaveEvent(PlayerQuitEvent e) {
		if(HalystiaRPG.isInRpgWorld(e.getPlayer())) {
			main.getBanque().close(e.getPlayer().getUniqueId());
			leaveGame(e.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerChangeWorldEvent(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		World to = p.getWorld();
		if(HalystiaRPG.isRpgWorld(e.getFrom())) {
			if(HalystiaRPG.isRpgWorld(to))
				return;
			leaveGame(p);
			return;
		}
		
		if(HalystiaRPG.isRpgWorld(to)) {
			if(HalystiaRPG.isRpgWorld(e.getFrom()))
				return;
			joinGame(p);
			return;
		}
	}
	
	private void joinGame(Player p) {
		if(!HalystiaRPG.isInRpgWorld(p))
			return;
		main.getSoulManager().tryRefreshSoul(p);
		bdd.addPlayerProfile(p);
		main.getClasseManager().playerConnects(p);
	///	main.getNpcManager().refreshPlayer(p);
	}
	
	private void leaveGame(Player p) {
		main.getClasseManager().playerDisconnects(p);;
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
	//	main.getNpcManager().removeFromPlayer(p);
	}
	
}