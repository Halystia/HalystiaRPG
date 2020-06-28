package fr.jamailun.halystia.custom.boats;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jamailun.halystia.utils.ItemBuilder;

public final class CustomBoatManager {
	
	public final static String BOAT_CERTIFICATION = "Certifié authentique";
	
	private List<CustomBoat> boats;
	public final static List<Material> boatsMaterials = Arrays.asList(Material.BIRCH_BOAT, Material.ACACIA_BOAT, Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.OAK_BOAT, Material.SPRUCE_BOAT);
	
	public CustomBoatManager() {
		boats = new ArrayList<>();
	}
	
	public void spawn(Location loc) {
		boats.add(new CustomBoat(loc));
	}
	
	private final Object key = new Object();
	public void tryRemove(UUID uuid) {
		synchronized(key) {
			for(CustomBoat b : new ArrayList<>(boats)) {
				if(b.getUUID().equals(uuid)) {
					boats.remove(b);
					return;
				}
			}
		}
	}
	
	public ItemStack generateBoat() {
		ItemBuilder builder = new ItemBuilder(Material.ACACIA_BOAT);
		builder.setName(YELLOW+"Bateau des sables");
		builder.addLoreLine("Un bateau conçu spécialement pour");
		builder.addLoreLine("pouvoir naviguer sur le sable.");
		return builder.addLoreLine(DARK_GRAY + "" + ITALIC + BOAT_CERTIFICATION).toItemStack();
	}
	
	public boolean isCustomBoatItem(ItemStack item) {
		try {
			if( ! boatsMaterials.contains(item.getType()))
				return false;
			
			ItemMeta meta = item.getItemMeta();
			for(String lore : meta.getLore())
				if(lore.contains(BOAT_CERTIFICATION))
					return true;
		} catch(Exception e) {}
		return false;
	}
	
	public void purge() {
		for(CustomBoat b : boats)
			b.remove();
		boats.clear();
	}
	
}
