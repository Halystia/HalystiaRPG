package fr.jamailun.halystia.jobs.model.enchanteur;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;
import fr.jamailun.halystia.utils.ItemBuilder;

public final class EnchanteurSources {
	
	public static final String SOURCE = Rarity.COMMON.getColor()+"Source de";
	
	public static Attribute getFromItem(ItemStack item) {
		if( ! item.hasItemMeta() )
			return null;
		if(!item.getItemMeta().hasDisplayName())
			return null;
		String name = item.getItemMeta().getDisplayName();
		Source source = Source.fromName(name);
		if(source == null)
			return null;
		return source.getAttribute();
	}
	
	public enum Source {
		VIE(Attribute.GENERIC_MAX_HEALTH, "vie", ChatColor.RED, Material.ORANGE_DYE),
		DMG(Attribute.GENERIC_ATTACK_DAMAGE, "sang", ChatColor.DARK_RED, Material.RED_DYE),
		ARM(Attribute.GENERIC_ARMOR, "défense", ChatColor.DARK_GREEN, Material.GREEN_DYE),
		KBR(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "terre", ChatColor.GOLD, Material.BROWN_DYE),
		ASP(Attribute.GENERIC_ATTACK_SPEED, "puissance", ChatColor.WHITE, Material.WHITE_DYE),
		MVS(Attribute.GENERIC_MOVEMENT_SPEED, "vitesse", ChatColor.GOLD, Material.YELLOW_DYE);
		
		private final Attribute a;
		private final String n;
		private final ChatColor c;
		private final Material m;
		private Source(Attribute a, String n, ChatColor c, Material m) {
			this.a = a;
			this.n = n;
			this.c = c;
			this.m = m;
		}
		public String toString() {
			return c+""+ChatColor.BOLD+n;
		}
		public Attribute getAttribute() {
			return a;
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
	
}