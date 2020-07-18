package fr.jamailun.halystia.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.chunks.ChunkManager;
import fr.jamailun.halystia.commands.CommandSetChunk;

public class PlayerMovementsListener extends HalystiaListener {
	
	private HashMap<Player, Chunk> players;
	private final ChunkManager chunks;
	
	public PlayerMovementsListener(HalystiaRPG main, ChunkManager chunks) {
		super(main);
		players = new HashMap<>();
		for(Player p : Bukkit.getOnlinePlayers())
			players.put(p, p.getLocation().getChunk());
		this.chunks = chunks;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerChangeChunkEvent(PlayerMoveEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		Player p = e.getPlayer();
		
		Chunk current = p.getLocation().getChunk();
		
		if( ! players.containsKey(p)) {
			players.put(p, p.getLocation().getChunk());
			chunks.title(p, null, current);
			return;
		}
		
		Chunk old = players.get(p);
		if(old.equals(current))
			return;
		
		//TODO fixer les enfers
		
		chunks.title(p, old, current);

		players.replace(p, current);
		if(CommandSetChunk.isObservingChunkValues(p))
			CommandSetChunk.sendChunkReport(p);
	}

}