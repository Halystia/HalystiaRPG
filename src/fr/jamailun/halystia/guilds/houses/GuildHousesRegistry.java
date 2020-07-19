package fr.jamailun.halystia.guilds.houses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildManager;
import fr.jamailun.halystia.utils.FileDataRPG;

public class GuildHousesRegistry extends FileDataRPG {

	private final Map<Chunk, GuildHouse> houses = new HashMap<>();
	private final GuildManager guilds;
	
	public GuildHousesRegistry(String path, GuildManager guilds) {
		super(path, "house-registry");
		this.guilds = guilds;
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
	
	public void unregisterHouse(String id) {
		GuildHouse house = getHouse(id);
		if(house.hasOwner()) {
			guilds.getGuild(house.getGuildOwnerName()).sendMessageToMembers(ChatColor.RED + "Votre maison a été détruite par un administrateur. COntactez-les pour un remboursement éventuel.");
		}
		house.changeOwnerShip(null);
		Chunk c = null;
		for(Chunk ch : houses.keySet()) {
			if(ch.getX() == house.getChunkX() && ch.getZ() == house.getChunkZ()) {
				c = ch;
				break;
			}
		}
		houses.remove(c);
		synchronized (config) {
			config.set(id, null);
			save();
		}
	}
	
	public List<GuildHouse> getAllBuyableHouses() {
		return houses.values().stream().filter(h -> ! h.hasOwner()).collect(Collectors.toList());
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
		if(guild.getHowManyUnits() < house.getSize().getCost())
			return false;
		guild.deltaUnits(house.getSize().getCost());
		synchronized (config) {
			house.changeOwnerShip(guild);
			save();
		}
		guild.sendMessageToMembers(ChatColor.GREEN + "" + ChatColor.BOLD + "Votre guilde a fait l'acquisition d'une maison !");
		return true;
	}

	public void changeSize(GuildHouse house, HouseSize size) {
		if(size == HouseSize.UNDEFINED)
			return;
		synchronized (config) {
			house.changeSize(size);
			save();
		}
	}
	
	public void changeEntrance(GuildHouse house, Location entrance) {
		synchronized (config) {
			house.changeEntrance(entrance);
			save();
		}
	}
	
}