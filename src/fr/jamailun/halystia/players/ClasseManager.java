package fr.jamailun.halystia.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.sql.temporary.DataHandler;

/**
 * Handle all {@link fr.jamailun.halystia.players.PlayerData PlayerData} in the game.
 * @author jamailun
 */
public class ClasseManager {
	
	private Map<UUID, PlayerData> data;
	
	private final DataHandler bdd;
	private final HalystiaRPG main;
	
	public ClasseManager(HalystiaRPG main, DataHandler bdd) {
		this.bdd = bdd;
		this.main = main;
		data = new TreeMap<>();
		
		startRunnable(main);
	}
	
	private final static int saveFrequence = 120;
	private final static int refreshExclamationFrequence = 30;
	private int saveCounter = 0, exclCounter = 0;
	private void startRunnable(HalystiaRPG main) {
		new BukkitRunnable() {
			@Override
			public void run() {
				data.values().forEach(PlayerData::refillMana);
				saveCounter++;
				exclCounter++;
				if(saveCounter >= saveFrequence) {
					saveCounter = 0;
					data.values().forEach(d -> d.tryImproveKarma());
					saveData(true);
				}
				if(exclCounter >= refreshExclamationFrequence) {
					exclCounter = 0;
					for(PlayerData pd : data.values()) {
						if(pd.isPlayerValid())
							main.getNpcManager().refreshExclamations(pd.getPlayer());
					}
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
		new BukkitRunnable() {
			public void run() {
				main.getNpcManager().refreshExclamations(player);
			}
		}.runTaskLater(main, 80L);
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