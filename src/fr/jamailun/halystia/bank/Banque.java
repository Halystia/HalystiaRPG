package fr.jamailun.halystia.bank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

public class Banque {

	private Map<UUID, PlayerBanque> viewers;
	private Map<UUID, PlayerBanque> data;
	private final String path;
	
	public Banque(String path) {
		this.path = path;
		viewers = new HashMap<>();
		data = new HashMap<>();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				UUID uuid = UUID.fromString(name);
				data.put(uuid, new PlayerBanque(path, uuid));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openAccount(Player p) {
		final UUID uuid = p.getUniqueId();
		if(isLooking(uuid))
			close(uuid);
		if( ! data.containsKey(uuid)) {
			PlayerBanque account = new PlayerBanque(path, uuid);
			data.put(uuid, account);
			account.openInventoryToOwner(p);
			viewers.put(uuid, account);
			return;
		}
		viewers.put(uuid, data.get(uuid));
		data.get(uuid).openInventoryToOwner(p);
		return;
	}
	
	public boolean isLooking(UUID uuid) {
		return viewers.containsKey(uuid);
	}
	
	public void close(UUID uuid) {
		if( ! isLooking(uuid))
			return;
		viewers.get(uuid).saveInventory();
		viewers.remove(uuid);
	}
}