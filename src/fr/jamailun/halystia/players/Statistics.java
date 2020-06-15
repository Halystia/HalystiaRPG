package fr.jamailun.halystia.players;

import org.bukkit.entity.Player;

public class Statistics {

	private int maxHealth, armor;
	private double maxMana, speed;
	
	public Statistics(int level, Player player) {
		recalculateLevel(level);
		calculateArmor(player);
	}
	
	boolean levelCalculed, armorCalculed = false;
	
	public void recalculateLevel(int level) {
		maxHealth = 200 + (20 * level);
		maxMana = level * 3;
		speed = 1;
		armor = 0;
		levelCalculed = true;
		armorCalculed = false;
	}
	
	public void calculateArmor(Player player) {
		if(armorCalculed)
			return;
		if(!levelCalculed)
			return;
		PlayerArmor pArmor = new PlayerArmor(player);
		armor = pArmor.getArmor();
		maxHealth += pArmor.getHealth();
		speed += pArmor.getSpeed();
		maxMana += pArmor.getMana();
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getArmor() {
		return armor;
	}

	public double getMaxMana() {
		return maxMana;
	}

	public double getSpeed() {
		return speed;
	}
	
}