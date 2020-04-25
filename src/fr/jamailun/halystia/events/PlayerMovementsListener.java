package fr.jamailun.halystia.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.commands.CommandSetChunk;

public class PlayerMovementsListener extends HalystiaListener {
	
	private HashMap<Player, Chunk> players;
	
	public PlayerMovementsListener(HalystiaRPG main) {
		super(main);
		players = new HashMap<>();
		for(Player p : Bukkit.getOnlinePlayers())
			players.put(p, p.getLocation().getChunk());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerChangeChunkEvent(PlayerMoveEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		Player p = e.getPlayer();
		
		if( ! CommandSetChunk.isObservingChunkValues(p))
			return;
		
		if( ! players.containsKey(p)) {
			players.put(p, p.getLocation().getChunk());
			return;
		}
		
		Chunk current = p.getLocation().getChunk();
		
		if(players.get(p).equals(current))
			return;
		
		players.replace(p, current);
		CommandSetChunk.sendChunkReport(p);
	}

}
