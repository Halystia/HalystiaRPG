package fr.jamailun.halystia.players;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Statistics {

	// Health
	private double baseMaxHealth, armorMaxHealth, bonusMaxHealth;
	// Armor
	private int baseArmor, armorArmor;
	// Mana
	private double baseMaxMana, armorMaxMana;
	// Speed
	private double baseSpeed, armorSpeed;
	// Damages (bruts + buffs)
	private double baseDamages, baseDamagesBuff, armorDamages, armorDamagesBuff;
	
	
	private PlayerArmor playerArmor;
	
	public Statistics(int level, Player player) {
		bonusMaxHealth = 0; // TODO implémenter ça.
		
		recalculateLevel(level);
		
		playerArmor = new PlayerArmor(player);
		
		calculateArmor();
	}
	
	public void resetArmor(Player player) {
		playerArmor = new PlayerArmor(player);
		
		calculateArmor();
	}
	
	public void recalculateLevel(int level) {
		baseMaxHealth = 200 + (20 * level);
		baseMaxMana = level * 3;
		baseSpeed = 1;
		baseArmor = level / 10;
		baseDamages = level / 5 + 1;
		baseDamagesBuff = 1;
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "JOUEUR TOTAL : §aarmor="+getArmor()+", §dhealth="+getMaxHealth()+", §fspeed="+getSpeed()+", §cdmgs="+getDamages()+", §bmana="+getMaxMana()+".");
	}
	
	private void calculateArmor() {
		armorArmor = playerArmor.getArmor();
		armorMaxHealth = playerArmor.getHealth();
		armorSpeed = playerArmor.getSpeed();
		armorMaxMana = playerArmor.getMana();
		armorDamages = playerArmor.getDamagesInteger();
		armorDamagesBuff = playerArmor.getDamageBuff();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "ARMURE UNQUEMENT : §aarmor="+playerArmor.getArmor()+", §dhealth="+playerArmor.getHealth()+", §fspeed="+playerArmor.getSpeed()+", §cdmgs="+playerArmor.getDamagesInteger()
			+", dmgsBuff=" + playerArmor.getDamageBuff() +"%, §bmana="+playerArmor.getMana()+".");
	}

	public double getMaxHealth() {
		return baseMaxHealth + armorMaxHealth + bonusMaxHealth;
	}

	public int getArmor() {
		return baseArmor + armorArmor;
	}

	public double getMaxMana() {
		return baseMaxMana + armorMaxMana;
	}

	public double getSpeed() {
		return baseSpeed + armorSpeed;
	}
	
	public double getDamages() {
		return (baseDamages + armorDamages) * (baseDamagesBuff + (armorDamagesBuff/100.0));
	}
	
	public void levelChanged(int level) {
		recalculateLevel(level);
	}
	
	public void changeEquipment(EquipmentSlot slot, ItemStack item) {
		playerArmor.updateItem(slot, item);
		calculateArmor();
	}
}