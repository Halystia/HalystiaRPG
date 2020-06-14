package fr.jamailun.halystia.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;

public class RpgEquipment {
	
	public static final String HEALTH_BEGIN = ChatColor.RED + "Vie bonus : ";
	public static final String ARMOR_BEGIN = ChatColor.GREEN + "Armure : ";
	public static final String SPEED_BEGIN = ChatColor.WHITE + "Vitesse bonus : ";
	public static final String MANA_BEGIN = ChatColor.AQUA + "Mana bonus : ";
	
	protected int health, armor, mana;
	protected double speed;
	protected ItemStack item;
	
	public RpgEquipment(ItemBuilder item) {
		this(item.toItemStack());
	}
	
	public RpgEquipment(ItemStack item) {
		if(!validateItem(item)) {
			nullify();
			return;
		}
		this.item = item;
		readItem();
	}
	
	public ItemStack toItemStack() {
		return item;
	}
	
	private void nullify() {
		health = armor = 0;
		speed = 0;
		item = null;
	}
	
	private void readItem() {
		for(String line : item.getItemMeta().getLore()) {
			if(line.startsWith(HEALTH_BEGIN)) {
				try {
					String nb = line.split(" ")[3];
					health = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.startsWith(ARMOR_BEGIN)) {
				try {
					String nb = line.split(" ")[2];
					armor = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.startsWith(MANA_BEGIN)) {
				try {
					String nb = line.split(" ")[3];
					armor = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.startsWith(SPEED_BEGIN)) {
				try {
					String nb = line.split(" ")[3];
					speed = Double.parseDouble(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
		}
	}


	private boolean validateItem(ItemStack item) {
		if(item == null)
			return false;
		if( ! item.hasItemMeta())
			return false;
		if(!item.getItemMeta().hasLore())
			return false;
		return item.getItemMeta().hasDisplayName();
	}

	public int getHealth() {
		return health;
	}

	public int getArmor() {
		return armor;
	}

	public int getMana() {
		return mana;
	}

	public double getSpeed() {
		return speed;
	}

	public ItemStack getItem() {
		return item;
	}
	
	
	
}