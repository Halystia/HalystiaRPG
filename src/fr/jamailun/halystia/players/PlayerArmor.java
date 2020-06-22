package fr.jamailun.halystia.players;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.jamailun.halystia.utils.RpgEquipment;

public class PlayerArmor {
	
	protected final Map<EquipmentSlot, RpgEquipment> items;
	protected int health, armor, mana, damages;
	protected double speed, damageBuff;
	
	public PlayerArmor(Player player) {
		items = new HashMap<>();
		PlayerInventory inv = player.getInventory();
		items.put(EquipmentSlot.HEAD, new RpgEquipment(inv.getHelmet()));
		items.put(EquipmentSlot.CHEST, new RpgEquipment(inv.getChestplate()));
		items.put(EquipmentSlot.LEGS, new RpgEquipment(inv.getLeggings()));
		items.put(EquipmentSlot.FEET, new RpgEquipment(inv.getBoots()));
		items.put(EquipmentSlot.HAND, new RpgEquipment(inv.getItemInMainHand()));
		items.put(EquipmentSlot.OFF_HAND, new RpgEquipment(inv.getItemInOffHand()));
		
		recalculate();
	}
	
	private void recalculate() {
		health = armor = mana = 0;
		speed = 0;
		damageBuff = 1;
		damages = 0;
		for(RpgEquipment equip : items.values()) {
			health += equip.getHealth();
			armor += equip.getArmor();
			mana += equip.getMana();
			speed += equip.getSpeed();
			damages += equip.getDamagesInt();
			damageBuff += equip.getDamageBuff();
		}
	}
	
	public void updateItem(EquipmentSlot slot, ItemStack item) {
		items.replace(slot, new RpgEquipment(item));
		
		recalculate();
	}

	public int getHealth() {
		return health;
	}

	public int getArmor() {
		return armor;
	}

	public double getMana() {
		return mana;
	}

	public double getSpeed() {
		return speed;
	}
	
	public double getDamageBuff() {
		return damageBuff;
	}
	
	public int getDamagesInteger() {
		return damages;
	}
	
}