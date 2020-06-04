package fr.jamailun.halystia.custom.potions;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.ItemBuilder;


public class PotionManager {
	
	public final static String SIGNATURE = "mana[jam}";
	
	public boolean isManaBottle(ItemStack item) {
		return getPowerOfBottle(item) > -1;
	}
	
	public ItemStack generateManaBottle(int power) {
		ItemBuilder builder = new ItemBuilder(Material.POTION);
		builder.setName(ChatColor.AQUA + "Potion de mana (" + ChatColor.DARK_AQUA + "+" + power + ChatColor.AQUA + ")");
		builder.addLoreLine(ChatColor.LIGHT_PURPLE + "Buvez cette potion pour regagner");
		builder.addLoreLine(ChatColor.LIGHT_PURPLE + "quelques points de mana !");
		builder.addLoreLine("Mana :" + ChatColor.AQUA + " " + power + " " + ChatColor.GRAY + "points.");
		builder.addLoreLine(ChatColor.BLACK + SIGNATURE + "_"+ power);
		builder.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
		return builder.toItemStack();
	}
	
	/**
	 * @return -1 if no mana is provided.
	 */
	public int getPowerOfBottle(ItemStack item) {
		try {
			if(item.getType() != Material.POTION)
				return -1;
			List<String> lore = item.getItemMeta().getLore();
			String lastLore = lore.get(lore.size() - 1);
			if( ! lastLore.contains(SIGNATURE))
				return -1;
			String[] words = lore.get(lore.size() - 2).split(" ");
			return Integer.parseInt(words[2]);
		} catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException ignored) {}
		return -1;
	}
	
}
