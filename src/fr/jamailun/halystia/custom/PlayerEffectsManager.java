package fr.jamailun.halystia.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

public class PlayerEffectsManager {
	
	private final Map<String, Map<UUID, BukkitRunnable>> data;
	
	private final HalystiaRPG main;
	public PlayerEffectsManager(HalystiaRPG main) {
		this.main = main;
		data = new HashMap<>();
	}
	
	public void registerNewEffect(String key) {
		if(data.containsKey(key)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Effect '"+key+"' is already registered.");
			return;
		}
		data.put(key, new HashMap<>());
	}
	
	public void applyEffect(final String effect, Player player, int durationSeconds) {
		if( ! data.containsKey(effect) ) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Effect '"+effect+"' does not exists.");
			return;
		}
		Map<UUID, BukkitRunnable> actives = data.get(effect);
		final UUID uuid = player.getUniqueId();
		
		clearData(actives, uuid);
		
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				removeEffect(effect, uuid);
			}
		};
		
		runnable.runTaskLater(main, durationSeconds * 20L);
		
		actives.put(uuid, runnable);
	}
	
	private void removeEffect(String effect, UUID uuid) {
		data.get(effect).remove(uuid);
	}
	
	public void removeEffect(String effect, Player player) {
		if( ! data.containsKey(effect) ) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Effect '"+effect+"' does not exists.");
			return;
		}
		clearData(data.get(effect), player.getUniqueId());
	}
	
	public void playerDied(Player player) {
		data.keySet().forEach(key -> clearData(data.get(key), player.getUniqueId()));
	}
	
	public void clearData(Map<UUID, BukkitRunnable> actives, UUID uuid) {
		if(actives.containsKey(uuid)) {
			actives.get(uuid).cancel();
			actives.remove(uuid);
		}
	}
	
	public boolean hasEffect(String effect, Player player) {
		if( ! data.containsKey(effect))
			return false;
		return data.get(effect).containsKey(player.getUniqueId());
	}
	
}