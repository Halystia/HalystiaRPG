package fr.jamailun.halystia.jobs.model.enchanteur;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;
import fr.jamailun.halystia.utils.ItemBuilder;

public final class EnchanteurSources {
	
	public static final String SOURCE = Rarity.COMMON.getColor()+"Source de";
	
	public static Source getFromItem(ItemStack item) {
		if( ! item.hasItemMeta() )
			return null;
		if(!item.getItemMeta().hasDisplayName())
			return null;
		String name = item.getItemMeta().getDisplayName();
		Source source = Source.fromName(name);
		return source;
	}
	
	public enum Source {
		VIE(SourceType.HEALTH, "vie", ChatColor.RED, Material.ORANGE_DYE),
		DMG(SourceType.DEGATS_INT, "sang", ChatColor.DARK_RED, Material.RED_DYE),
		ARM(SourceType.ARMOR, "défense", ChatColor.DARK_GREEN, Material.GREEN_DYE),
		//KBR("terre", ChatColor.GOLD, Material.BROWN_DYE),
		MAN(SourceType.MANA, "savoir", ChatColor.AQUA, Material.LIGHT_BLUE_DYE),
		ASP(SourceType.DEGATS_P, "puissance", ChatColor.WHITE, Material.WHITE_DYE),
		MVS(SourceType.SPEED, "vitesse", ChatColor.YELLOW, Material.YELLOW_DYE);
		
		private final String n;
		private final ChatColor c;
		private final Material m;
		private final SourceType s;
		private Source(SourceType s, String n, ChatColor c, Material m) {
			this.n = n;
			this.c = c;
			this.m = m;
			this.s = s;
		}
		public SourceType getSourceType() {
			return s;
		}
		public String toString() {
			return c+""+ChatColor.BOLD+n;
		}
		public ItemStack getItem() {
			return new ItemBuilder(m).setName(SOURCE+c+""+ChatColor.BOLD+" "+n).toItemStack(); //&e&lSource de&c&l 
		}
		public static Source fromName(String name) {
			try {
				String nn = name.split(" ")[2];
				for(Source s : values())
					if(s.n.equals(nn))
						return s;
			} catch (IndexOutOfBoundsException e) {}
			return null;
		}
		public String getName() {
			return n;
		}
	}
	
	public enum SourceType {
		HEALTH, ARMOR, DEGATS_INT, DEGATS_P(true), SPEED(true), MANA;
		private final boolean b;
		private SourceType() {
			b = false;
		}
		private SourceType(boolean bb) {
			b = bb;
		}
		public boolean isPercentage() {
			return b;
		}
	}
	
}