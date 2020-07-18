package fr.jamailun.halystia.guilds.houses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.utils.FileDataRPG;

public class GuildHousesRegistry extends FileDataRPG {

	private final Map<Chunk, GuildHouse> houses = new HashMap<>();
	
	public GuildHousesRegistry(String path) {
		super(path, "house-registry");
		synchronized (config) {
			config.getKeys(false).forEach(key -> {
				GuildHouse house = new GuildHouse(config.getConfigurationSection(key));
				houses.put(Bukkit.getWorld(HalystiaRPG.PREFIX).getChunkAt(house.getChunkX(), house.getChunkZ()), house);
			});
		}
	}

	public boolean houseIdExists(String id) {
		return houses.entrySet().stream().anyMatch(entry -> entry.getValue().getID().equals(id));
	}
	
	public List<GuildHouse> getAllHouses() {
		return new ArrayList<>(houses.values());
	}
	
	public GuildHouse getHouse(String id) {
		return houses.entrySet().stream().filter(entry -> entry.getValue().getID().equals(id)).map(e -> e.getValue()).findAny().orElse(null);
	}
	
	public GuildHouse getHouseAt(Chunk chunk) {
		return houses.entrySet().stream().filter(entry -> entry.getKey().getX() == chunk.getX() && entry.getKey().getZ() == chunk.getZ()).map(e -> e.getValue()).findFirst().orElse(null);
	}
	
	public boolean generateHouse(String id, HouseSize size, Chunk chunk) {
		if(houseIdExists(id))
			return false;
		if(houses.containsKey(chunk))
			return false;
		synchronized (config) {
			GuildHouse house = new GuildHouse(id, size, chunk, config.getConfigurationSection("id"));
			houses.put(chunk, house);
			save();
		}
		return true;
	}
	
	public boolean guildBuyHouse(Guild guild, String id) {
		GuildHouse house = getHouse(id);
		if(house == null)
			return false;
		if(house.hasOwner())
			return false;
		synchronized (config) {
			house.changeOwnerShip(guild, config.getConfigurationSection(id));
			save();
		}
		return true;
	}
	
}