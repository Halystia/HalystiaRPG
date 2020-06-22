package fr.jamailun.halystia.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;

public class RpgEquipment {

	public static final String DAMAGES_INT_BEGIN = ChatColor.DARK_RED + "Dégâts : ";
	public static final String DAMAGES_BUFF_BEGIN = ChatColor.GOLD + "Dégâts bonus : ";
	public static final String HEALTH_BEGIN = ChatColor.RED + "Vie bonus : ";
	public static final String ARMOR_BEGIN = ChatColor.GREEN + "Armure : ";
	public static final String SPEED_BEGIN = ChatColor.WHITE + "Vitesse bonus : ";
	public static final String MANA_BEGIN = ChatColor.AQUA + "Mana bonus : ";
	
	protected List<String> addionalLore;
	protected int health, armor, mana, damagesInt;
	protected double speed, damageBuff;
	protected ItemBuilder item;
	
	public RpgEquipment(ItemStack item) {
		this(new ItemBuilder(item));
	}
	
	public RpgEquipment(ItemBuilder item) {
		this.item = item;
		readItem();
	}
	
	public ItemStack toItemStack() {
		item.resetLore();
		if(health != 0)
			item.addLoreLine(HEALTH_BEGIN + (health > 0 ? "+ " : "- ")+Math.abs(health)+ " HP");
		if(armor != 0)
			item.addLoreLine(ARMOR_BEGIN + (armor > 0 ? "+ " : "- ")+Math.abs(armor)+ " AP");
		if(mana != 0)
			item.addLoreLine(MANA_BEGIN + (mana > 0 ? "+ " : "- ")+Math.abs(mana)+ " MP");
		if(speed != 0)
			item.addLoreLine(SPEED_BEGIN + (speed > 0 ? "+ " : "- ")+Math.abs(speed)+ " %");
		if(damagesInt != 0)
			item.addLoreLine(DAMAGES_INT_BEGIN + (damagesInt > 0 ? "+ " : "- ")+Math.abs(damagesInt)+ " DMG");
		if(damageBuff != 0)
			item.addLoreLine(DAMAGES_BUFF_BEGIN + (damageBuff > 0 ? "+ " : "- ")+Math.abs(damageBuff)+ " %");
		if( ! addionalLore.isEmpty()) {
			item.addLoreLine(" ");
			addionalLore.forEach(l -> item.addLoreLine(l));
		}
		item.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		return item.toItemStack();
	}
	
	private void readItem() {
		addionalLore = new ArrayList<>();
		for(String line : item.getLore()) {
			if(line.contains(HEALTH_BEGIN)) {
				try {
					String nb = line.split(" ")[4];
					health = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.contains(ARMOR_BEGIN)) {
				try {
					String nb = line.split(" ")[3];
					armor = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.contains(MANA_BEGIN)) {
				try {
					String nb = line.split(" ")[4];
					armor = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.contains(SPEED_BEGIN)) {
				try {
					String nb = line.split(" ")[4];
					speed = Double.parseDouble(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.contains(DAMAGES_INT_BEGIN)) {
				try {
					String nb = line.split(" ")[3];
					damagesInt = Integer.parseInt(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			if(line.contains(DAMAGES_BUFF_BEGIN)) {
				try {
					String nb = line.split(" ")[4];
					damageBuff = Double.parseDouble(nb);
				} catch(NumberFormatException | IndexOutOfBoundsException e) {
					HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				}
				continue;
			}
			addionalLore.add(line);
		}
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

	public int getDamagesInt() {
		return damagesInt;
	}

	public void setDamagesInt(int damagesInt) {
		this.damagesInt = damagesInt;
	}

	public double getDamageBuff() {
		return damageBuff;
	}

	public void setDamageBuff(double damageBuff) {
		this.damageBuff = damageBuff;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
}