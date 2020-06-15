package fr.jamailun.halystia.players;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.utils.RpgEquipment;

public class PlayerArmor {

	protected int health, armor, mana;
	protected double speed;
	
	public PlayerArmor(Player player) {
		List<RpgEquipment> equipments = new ArrayList<>();
		for(ItemStack item : player.getEquipment().getArmorContents()) {
			if(item != null)
				equipments.add(new RpgEquipment(item));
		}
		if(player.getInventory().getItemInMainHand() != null)
			equipments.add(new RpgEquipment(player.getInventory().getItemInMainHand()));
		if(player.getInventory().getItemInOffHand() != null)
			equipments.add(new RpgEquipment(player.getInventory().getItemInOffHand()));
		health = armor = mana = 0;
		speed = 0;
		for(RpgEquipment equip : equipments) {
			health += equip.getHealth();
			armor += equip.getArmor();
			mana += equip.getMana();
			speed += equip.getSpeed();
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
	
}