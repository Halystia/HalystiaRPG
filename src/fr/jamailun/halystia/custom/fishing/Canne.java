package fr.jamailun.halystia.custom.fishing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.ItemBuilder;

public enum Canne {
	
	VIELLE("Vieille canne", Rarity.COMMON, 0, new DropList(
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Morue simplette"), 30),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Morue sombre"), 25),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Morue boueuse"), 28),
		new Drop(new ItemBuilder(Material.SALMON).setName(b() + "Mulet"), 10),
		new Drop(new ItemBuilder(Material.SALMON).setName(b() + "Poisson-chat boueux"), 10),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).shine().setName(d() + "Morue célite"), 5)
	)),
	RACCOMODEE("Canne raccommodée", Rarity.COMMON, 10, new DropList(
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Thon simple"), 20),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Bar commun"), 15),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Truite sauvage"), 15),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Morue dodue"), 15),
		new Drop(new ItemBuilder(Material.PUFFERFISH).setName(b() + "Gloups"), 8),
		new Drop(new ItemBuilder(Material.SALMON).setName(b() + "Bar doré"), 5),
		new Drop(new ItemBuilder(Material.BAMBOO).setName(b() + "Bois flotté"), 7),
		new Drop(new ItemBuilder(Material.SALMON).shine().setName(d() + "Saumon arc-en-ciel"), 3),
		new Drop(new ItemBuilder(Material.COD).shine().setName(d() + "Morue éclatante"), 2)
	)),
	CHENE("Canne en chêne", Rarity.RARE, 20, new DropList(
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Anchois"), 15),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Thon rouge"), 10),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Sardine"), 20),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Anguille"), 10),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Brème"), 10),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Congre"), 10),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(b() + "Epinoche"), 5),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(b() + "Eperlan"), 5),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(b() + "Gobbie"), 5),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(b() + "Guppy"), 5),
		new Drop(new ItemBuilder(Material.SALMON).shine().setName(d() + "Perche dorée"), 3),
		new Drop(new ItemBuilder(Material.PUFFERFISH).shine().setName(d() + "Magic-carpe"), 2)
	)),
	RENFORCEE("Canne renforcée", Rarity.RARE, 30, new DropList(
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Piranha féroce"), 10),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Rouget"), 10),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(e() + "Roussette pailletée"), 5),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Thon Albacore"), 5),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Vandoise"), 10),
		new Drop(new ItemBuilder(Material.COD).setName(e() + "Petit Esturgeon"), 10),
		new Drop(new ItemBuilder(Material.PUFFERFISH).setName(e() + "Bloups rayé"), 15),
		new Drop(new ItemBuilder(Material.SALMON).setName(e() + "Naralia argenté"), 10),
		new Drop(new ItemBuilder(Material.SALMON).setName(b() + "Naralia doré"), 5),
		new Drop(new ItemBuilder(Material.COD).setName(b() + "Barracuda"), 5),
		new Drop(new ItemBuilder(Material.COD).setName(b() + "Baudroie"), 5),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).setName(b() + "Morue du temps"), 5),
		new Drop(new ItemBuilder(Material.TROPICAL_FISH).shine().setName(d() + "Carpe sacrée"), 3),
		new Drop(new ItemBuilder(Material.PUFFERFISH).shine().setName(d() + "Cyprinodonte "), 2)
	));
	
	private final String name;
	private final Rarity rarity;
	private final int level;
	private final DropList drops;
	
	private Canne(String name, Rarity rarity, int level, DropList drops) {
		this.name = name;
		this.rarity = rarity;
		this.level = level;
		this.drops = drops;
	}
	
	public ItemStack drop() {
		return drops.drop();
	}
	
	public static Canne getCanneWithItem(ItemStack item) {
		if(item == null)
			return null;
		for(Canne canne : values())
			if(Trade.areItemsTheSame(item, canne.generate()))
				return canne;
		return null;
	}
	
	public ItemStack generate() {
		return new ItemBuilder(Material.FISHING_ROD).setName(rarity.getColor() + name).setUnbreakable().toItemStack();
	}
	
	public String getName() {
		return Rarity.COMMON.getColor() + name;
	}
	
	public int getLevel() {
		return level;
	}
	
	private static String e() {return Rarity.COMMON.getColor();}
	private static String b() {return Rarity.RARE.getColor();}
	private static String d() {return Rarity.LEGENDARY.getColor();}
}