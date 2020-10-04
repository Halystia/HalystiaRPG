package fr.jamailun.halystia.players;

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
	
	//Autre
	private double lifeSteal;
	
	
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
		baseMaxHealth = 100 + (10 * (level-1));
		baseMaxMana = level * 3;
		baseSpeed = 1;
		baseArmor = level / 10;
		baseDamages = level / 5 + .9;
		baseDamagesBuff = 1.0;
		lifeSteal = 0;
		
	}
	
	private void calculateArmor() {
		armorArmor = playerArmor.getArmor();
		armorMaxHealth = playerArmor.getHealth();
		armorSpeed = playerArmor.getSpeed();
		armorMaxMana = playerArmor.getMana();
		armorDamages = playerArmor.getDamagesInteger();
		armorDamagesBuff = playerArmor.getDamageBuff();
		lifeSteal += playerArmor.getLifeSteal();
		
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
		return Math.max(0, (baseDamages + Math.max(0, armorDamages) ) * (baseDamagesBuff + (armorDamagesBuff/100.0)) );
	}
	
	public double getLifeStealPercent() {
		return lifeSteal;
	}
	
	public void levelChanged(int level) {
		recalculateLevel(level);
	}
	
	public void changeEquipment(EquipmentSlot slot, ItemStack item, int playerCurrentLevel) {
		playerArmor.updateItem(slot, item, playerCurrentLevel);
		calculateArmor();
	}
}