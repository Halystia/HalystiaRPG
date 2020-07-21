package fr.jamailun.halystia.guilds.houses;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;

public class GuildHouse {

	public static final Material BLOCK_SUPPORT_IN_HOUSES = Material.BROWN_GLAZED_TERRACOTTA;
	
	private final ConfigurationSection section;
	private final String id;
	private HouseSize size;
	private final int cx, cz;
	private Location entrance;
	private String owner;
	
	public GuildHouse(ConfigurationSection section) {
		this.section = section;
		id = section.getString("id");
		size = HouseSize.fromString(section.getString("size"));
		if(section.contains("owner"))
			owner = section.getString("owner");
		this.cx = section.getInt("chunk.x");
		this.cz = section.getInt("chunk.z");
		if(section.contains("entrance"))
			this.entrance = section.getLocation("entrance");
	}
	
	public GuildHouse(String id, HouseSize size, Chunk chunk, ConfigurationSection section) {
		this.section = section;
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
	
	void changeOwnerShip(Guild guild) {
		if(guild == null) {
			owner = null;
			return;
		}
		owner = guild.getGuildName();
		section.set("owner", owner);
	}
	
	void changeSize(HouseSize size) {
		this.size = size;
		section.set("size", size.toString());
	}
	
	void changeEntrance(Location entrance) {
		this.entrance = entrance;
		section.set("entrance", entrance);
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

	public void teleport(Player p) {
		if(entrance == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Cette maison n'a pas d'entrée paramétrée...");
			return;
		}
		p.teleport(entrance);
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Vous avez été téléporté à votre maison de guilde.");
	}
	
}