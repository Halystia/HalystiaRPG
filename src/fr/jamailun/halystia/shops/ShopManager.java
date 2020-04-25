package fr.jamailun.halystia.shops;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.RandomString;

public class ShopManager extends FileDataRPG {
	
	private List<Shop> shops;
	private List<UUID> uuids;
	
	public ShopManager(String path, String name) {
		super(path, name);
		
		shops = new ArrayList<>();
		uuids = new ArrayList<>();
		init();
	}
	
	private void init() {
		for(String key : config.getKeys(true)) {
			double locX = config.getDouble(key + ".location.x");
			double locY = config.getDouble(key + ".location.y");
			double locZ = config.getDouble(key + ".location.z");
			int classeId = config.getInt(key + ".classe");
			Classe classe = Classe.getClasseWithId(classeId);
			Location spawnLocation = new Location(Bukkit.getWorld(HalystiaRPG.WORLD), locX, locY, locZ);
			
			Shop shop = new Shop(this, classe, spawnLocation, key);
			shops.add(shop);
		}
	}

	public void createNewShop(Location spawnLocation) {
		String key;
		do {
			key = new RandomString(10).nextString();
		} while(config.contains(key));
		
		Shop shop = new Shop(this, Classe.NONE, spawnLocation, key);
		shops.add(shop);
		
		config.set(key + ".location.x", spawnLocation.getX());
		config.set(key + ".location.y", spawnLocation.getY());
		config.set(key + ".location.z", spawnLocation.getZ());
		config.set(key + ".classe", Classe.NONE.getClasseId());
		
		save();
	}
	
	public void despawnAll() {
		for(Shop shop : shops)
			shop.despawn();
	}
	
	

	public void addUUID(UUID uuid) {
		uuids.add(uuid);
	}
	
	public void removeUUID(UUID uuid) {
		uuids.remove(uuid);
	}
	
	public boolean constainsUUID(UUID uuid) {
		return uuids.contains(uuid);
	}
	
	public Shop getShop(UUID uuid) {
		for(Shop shop : shops)
			if(shop.getUUID().equals(uuid))
				return shop;
		return null;
	}
	
	public void reloadAll() {
		for(Shop shop : shops)
			shop.despawn();
		for(Shop shop : shops)
			shop.spawn();
	}

	public void removeShop(Shop shop, String key) {
		shop.despawn();
		config.set(key, null);
		shops.remove(shop);
		save();
	}

	public void updateShop(String key, Classe classe) {
		config.set(key + ".classe", classe.getClasseId());
		save();
	}
	
}
