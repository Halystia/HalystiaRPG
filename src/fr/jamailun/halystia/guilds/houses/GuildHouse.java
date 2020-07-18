package fr.jamailun.halystia.guilds.houses;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;

import fr.jamailun.halystia.guilds.Guild;

public class GuildHouse {

	private final String id;
	private HouseSize size;
	private final int cx, cz;
	private String owner;
	
	public GuildHouse(ConfigurationSection section) {
		id = section.getString("id");
		size = HouseSize.fromString(section.getString("size"));
		if(section.contains("owner"))
			owner = section.getString("owner");
		this.cx = section.getInt("chunk.x");
		this.cz = section.getInt("chunk.z");
	}
	
	public GuildHouse(String id, HouseSize size, Chunk chunk, ConfigurationSection section) {
		this.id = id;
		this.size = size;
		this.cx = chunk.getX();
		this.cz = chunk.getZ();
		section.set("id", id);
		section.set("size", size.toString());
		section.set("chunk.x", cx);
		section.set("chunk.z", cz);
	}
	
	public String getID() {
		return id;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}
	
	public String getGuildOwnerName() {
		return owner;
	}
	
	public void changeOwnerShip(Guild guild, ConfigurationSection section) {
		if(guild == null) {
			owner = null;
			return;
		}
		owner = guild.getGuildName();
		section.set("owner", owner);
	}
	
	public void changeSize(HouseSize size, ConfigurationSection section) {
		this.size = size;
		section.set("size", size.toString());
	}
	
	public HouseSize getSize() {
		return size;
	}
	
	public int getChunkX() {
		return cx;
	}
	
	public int getChunkZ() {
		return cz;
	}
	
}