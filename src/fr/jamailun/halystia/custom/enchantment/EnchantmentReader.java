package fr.jamailun.halystia.custom.enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentReader {
	private EnchantmentReader() {}
	
	public static final int MAX_LEVEL = 20;
	public static final String color = ChatColor.BLUE + "";
	
	public static List<CustomEnchantment> getEnchantmentsOf(ItemStack item) {
		List<CustomEnchantment> list = new ArrayList<>();
		if(item == null)
			return list;
		if( ! item.hasItemMeta())
			return list;
		ItemMeta meta =  item.getItemMeta();
		if( ! meta.hasLore())
			return list;
		for(String line : meta.getLore()) {
			if( ! line.startsWith(color))
				continue;
			line = line.replace(color, "");
			String[] words = line.split(" ");
			if(words.length < 2)
				continue;
			EnchantementType type = EnchantementType.getFromString(words[0]);
			if(type == null)
				continue;
			int level = getLevelFromString(words[1]);
			if(level > 0)
				list.add(new CustomEnchantment(type, level));
		}
		return list;
	}
	
	public static boolean enchantItem(ItemStack item, EnchantementType type, int level) {
		if(item == null)
			return false;
		removeEnchantOfItem(item, type);
		if(level <= 0)
			return true;

		List<String> lore = item.getItemMeta().getLore();
		lore.add(color+type + " " + getStringFromLevel(level) );
		item.getItemMeta().setLore(lore);
		
		return true;
	}
	
	public static boolean removeEnchantOfItem(ItemStack item, EnchantementType type) {
		Optional<CustomEnchantment> enchant = getEnchantmentsOf(item).stream().filter(ce -> ce.getEnchantementType() == type).findAny();
		if( ! enchant.isPresent() )
			return false;
		List<String> lore = item.getItemMeta().getLore();
		lore.removeIf(line -> line.startsWith(color + type.toString()));
		item.getItemMeta().setLore(lore);
		return true;
	}
	
	public static int getLevelFromString(String level) {
		int tt = 0;
		if(level.contains("IX"))
			tt--;
		while(level.contains("X")) {
			tt += 10;
			level = level.replace("X", "");
			continue;
		}
		
		if(level.contains("IV"))
			tt--;
		if(level.contains("V")) {
			tt += 5;
			level = level.replace("V", "");
		}
		while(level.contains("I")) {
			tt++;
			level = level.replace("I", "");
		}
		return tt;
	}
	
	public static String getStringFromLevel(int level) {
		if(level > MAX_LEVEL)
			level = MAX_LEVEL;
		StringBuilder builder = new StringBuilder();
		while(level >= 10) {
			builder.append("X");
			level -= 10;
		}
		if(level >= 9) {
			builder.append("IX");
			level -= 9;
		}
		if(level >= 5) {
			builder.append("V");
			level -= 5;
		}
		if(level >= 4) {
			builder.append("IV");
			level -= 4;
		}
		while(level >= 1) {
			builder.append("I");
			level--;
		}
		return builder.toString();
	}
}