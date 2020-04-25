package fr.jamailun.halystia.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.sql.temporary.Saver;

/**
 * Handle all {@link fr.jamailun.halystia.players.PlayerData PlayerData} in the game.
 * @author jamailun
 */
public class ClasseManager {
	
	private Map<UUID, PlayerData> data;
	
	private final Saver bdd;
	
	public ClasseManager(HalystiaRPG main, Saver bdd) {
		this.bdd = bdd;
		data = new TreeMap<>();
		
		startRunnable(main);
	}
	
	private final static int saveFrequence = 120;
	private int saveCounter = 0;
	private void startRunnable(HalystiaRPG main) {
		new BukkitRunnable() {
			@Override
			public void run() {
				data.forEach((uuid, data) -> {data.refillMana(0.2);});
				saveCounter++;
				if(saveCounter >= saveFrequence) {
					saveCounter = 0;
					saveData(true);
				}
			}
		}.runTaskTimer(main, 20L, 20L);
	}
	
	public void saveData(final boolean shouldClearOldData) {
		bdd.saveAll(data.values());
		if ( shouldClearOldData )
			data.values().removeIf(pData -> ! pData.isPlayerValid());
	}
	
	/**
	 * Inform the manager that a player has connected to the server. It will refresh the informations.
	 * @param player : {@link org.bukkit.entity.Player Player} who connected.
	 */
	public void playerConnects(Player player) {
		UUID uuid = player.getUniqueId();
		if(data.containsKey(uuid)) {
			data.get(uuid).reconnect(player);
			return;
		}
		PlayerData playerData = bdd.getPlayerData(player);
		data.put(uuid, playerData);
	}
	
	/**
	 * Inform the manager that a player has diconnected from the server. It will refresh the informations and clear it if no needed.
	 * @param player : {@link org.bukkit.entity.Player Player} who disconnected.
	 */
	public void playerDisconnects(Player player) {
		data.get(player.getUniqueId()).disconnect();
	}
	
	public synchronized PlayerData getPlayerData(Player player) {
		return data.get(player.getUniqueId());
	}
	
	public void changePlayerClasse(Player player, Classe classe) {
		data.get(player.getUniqueId()).changeClasse(classe);
		bdd.changePlayerClasse(player, classe);
	}

	/**
	 * Get all informations.
	 * @return a copy of all {@link fr.jamailun.halystia.players.PlayerData PlayerData}
	 */
	public List<PlayerData> getAll() {
		return new ArrayList<>(data.values());
	}	
}