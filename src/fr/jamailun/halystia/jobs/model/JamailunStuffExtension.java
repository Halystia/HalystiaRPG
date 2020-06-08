package fr.jamailun.halystia.jobs.model;

import static org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
import static org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1;
import static org.bukkit.inventory.EquipmentSlot.CHEST;
import static org.bukkit.inventory.EquipmentSlot.FEET;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.inventory.EquipmentSlot.HEAD;
import static org.bukkit.inventory.EquipmentSlot.LEGS;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;

import fr.jamailun.halystia.utils.ItemBuilder;

class JamailunStuffExtension extends JamailunItemModel {
	void generate() {
		//ARMURES FORGERON
		items.put("visiH", new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Casque du visiteur").toItemStack());
		items.put("visiC", new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Plastron du visiteur").toItemStack());
		items.put("visiL", new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Jambières du visiteur").toItemStack());
		items.put("visiB", new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Bottes du visiteur").toItemStack());
		items.put("visiE", new ItemBuilder(Material.IRON_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Épée du visiteur").toItemStack());

		items.put("pioche1", new ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 1).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Pioche moderne").toItemStack());
		items.put("hache1", new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED, 1).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Hache moderne").toItemStack());

		items.put("noviH", new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Casque du novice").toItemStack());
		items.put("noviC", new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Plastron du novice").toItemStack());
		items.put("noviL", new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Jambières du novice").toItemStack());
		items.put("noviB", new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Bottes du novice").toItemStack());
		items.put("noviE", new ItemBuilder(Material.IRON_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 2).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Épée du novice").toItemStack());

		items.put("neopH", new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Casque du néophyte").toItemStack());
		items.put("neopC", new ItemBuilder(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Plastron du néophyte").toItemStack());
		items.put("neopL", new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Jambières du néophyte").toItemStack());
		items.put("neopB", new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Bottes du néophyte").toItemStack());
		items.put("neopE", new ItemBuilder(Material.IRON_SWORD).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 8, ADD_NUMBER, HAND)
				.addEnchant(Enchantment.DAMAGE_UNDEAD, 1).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Épée du néophyte").toItemStack());

		items.put("peleH", new ItemBuilder(Material.IRON_HELMET).addAttribute(Attribute.GENERIC_ARMOR, 6, ADD_NUMBER, HEAD).addEnchant(Enchantment.DURABILITY, 4).setName(c() + "Casque du pélerin").toItemStack());
		items.put("peleC", new ItemBuilder(Material.IRON_CHESTPLATE).addAttribute(Attribute.GENERIC_ARMOR, 8, ADD_NUMBER, CHEST).addEnchant(Enchantment.DURABILITY, 4).setName(c() + "Plastron du pélerin").toItemStack());
		items.put("peleL", new ItemBuilder(Material.IRON_LEGGINGS).addAttribute(Attribute.GENERIC_ARMOR, 7, ADD_NUMBER, LEGS).addEnchant(Enchantment.DURABILITY, 4).setName(c() + "Jambières du pélerin").toItemStack());
		items.put("peleB", new ItemBuilder(Material.IRON_BOOTS).addAttribute(Attribute.GENERIC_ARMOR, 4, ADD_NUMBER, FEET).addEnchant(Enchantment.DURABILITY, 4).setName(c() + "Bottes du pélerin").toItemStack());
		items.put("peleE", new ItemBuilder(Material.IRON_SWORD).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 9, ADD_NUMBER, HAND)
				.addEnchant(Enchantment.DAMAGE_UNDEAD, 1).addEnchant(Enchantment.DURABILITY, 4).setName(c() + "Épée du pélerin").toItemStack());

		items.put("pioche2", new ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 2).addEnchant(Enchantment.DURABILITY, 3).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Pioche puissante").toItemStack());
		items.put("hache2", new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED, 2).addEnchant(Enchantment.DURABILITY, 3).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Hache puissante").toItemStack());

		items.put("voyaH", new ItemBuilder(Material.IRON_HELMET).addAttribute(Attribute.GENERIC_ARMOR, 7, ADD_NUMBER, HEAD).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Casque du voyageur").toItemStack());
		items.put("voyaC", new ItemBuilder(Material.IRON_CHESTPLATE).addAttribute(Attribute.GENERIC_ARMOR, 9, ADD_NUMBER, CHEST).addEnchant(Enchantment.DURABILITY, 5).addEnchant(Enchantment.THORNS, 1).setName(c() + "Plastron du voyageur").toItemStack());
		items.put("voyaL", new ItemBuilder(Material.IRON_LEGGINGS).addAttribute(Attribute.GENERIC_ARMOR, 8, ADD_NUMBER, LEGS).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Jambières du voyageur").toItemStack());
		items.put("voyaB", new ItemBuilder(Material.IRON_BOOTS).addAttribute(Attribute.GENERIC_ARMOR, 6, ADD_NUMBER, FEET).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Bottes du voyageur").toItemStack());
		items.put("voyaE", new ItemBuilder(Material.IRON_SWORD).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 10, ADD_NUMBER, HAND)
				.addEnchant(Enchantment.DAMAGE_UNDEAD, 2).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Épée du voyageur").toItemStack());

		items.put("explH", new ItemBuilder(Material.GOLDEN_HELMET).addAttribute(Attribute.GENERIC_ARMOR, 8, ADD_NUMBER, HEAD).addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 1).setUnbreakable().setName(c() + "Casque de l'explorateur").toItemStack());
		items.put("explC", new ItemBuilder(Material.GOLDEN_CHESTPLATE).addAttribute(Attribute.GENERIC_ARMOR, 10, ADD_NUMBER, CHEST).setUnbreakable().addEnchant(Enchantment.PROTECTION_FIRE, 1).addEnchant(Enchantment.THORNS, 1).setName(c() + "Plastron de l'explorateur").toItemStack());
		items.put("explL", new ItemBuilder(Material.GOLDEN_LEGGINGS).addAttribute(Attribute.GENERIC_ARMOR, 9, ADD_NUMBER, LEGS).setUnbreakable().addEnchant(Enchantment.PROTECTION_PROJECTILE, 1).setName(c() + "Jambières de l'explorateur").toItemStack());
		items.put("explB", new ItemBuilder(Material.GOLDEN_BOOTS).addAttribute(Attribute.GENERIC_ARMOR, 7, ADD_NUMBER, FEET).addEnchant(Enchantment.PROTECTION_FALL, 1).setUnbreakable().setName(c() + "Bottes de l'explorateur").toItemStack());
		items.put("explE", new ItemBuilder(Material.GOLDEN_SWORD).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 11, ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.02, MULTIPLY_SCALAR_1, HAND).setUnbreakable()
				.addEnchant(Enchantment.DAMAGE_UNDEAD, 3).setName(c() + "Épée de l'explorateur").toItemStack());

		items.put("conqH", new ItemBuilder(Material.GOLDEN_HELMET).addAttribute(Attribute.GENERIC_ARMOR, 9, ADD_NUMBER, HEAD).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, ADD_NUMBER, HEAD)
				.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 2).setUnbreakable().setName(r() + "Casque du conquérant").toItemStack());
		items.put("conqC", new ItemBuilder(Material.GOLDEN_CHESTPLATE).addAttribute(Attribute.GENERIC_ARMOR, 10, ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, ADD_NUMBER, CHEST)
				.setUnbreakable().addEnchant(Enchantment.PROTECTION_FIRE, 2).addEnchant(Enchantment.THORNS, 1).setName(r() + "Plastron du conquérant").toItemStack());
		items.put("conqL", new ItemBuilder(Material.GOLDEN_LEGGINGS).addAttribute(Attribute.GENERIC_ARMOR, 10, ADD_NUMBER, LEGS).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, ADD_NUMBER, LEGS)
				.setUnbreakable().addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).setName(r() + "Jambières du conquérant").toItemStack());
		items.put("conqB", new ItemBuilder(Material.GOLDEN_BOOTS).addAttribute(Attribute.GENERIC_ARMOR, 8, ADD_NUMBER, FEET).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, ADD_NUMBER, FEET)
				.addEnchant(Enchantment.PROTECTION_FALL, 2).setUnbreakable().setName(r() + "Bottes du conquérant").toItemStack());
		items.put("conqE", new ItemBuilder(Material.GOLDEN_SWORD).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 12, ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.03, MULTIPLY_SCALAR_1, HAND)
				.setUnbreakable().addEnchant(Enchantment.DAMAGE_UNDEAD, 3).setName(r() + "Épée du conquérant").toItemStack());

		items.put("pioche3", new ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 4).setUnbreakable().addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Pioche parfaite").toItemStack());
		items.put("hache3", new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED, 4).setUnbreakable().addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1, ADD_NUMBER, HAND).setName(c() + "Hache parfaite").toItemStack());

		items.put("emeraudeH", new ItemBuilder(Material.LEATHER_HELMET).setName(r()+"Casque d'émeraude")
				.addAttribute(Attribute.GENERIC_ARMOR, 11, Operation.ADD_NUMBER, HEAD).addAttribute(Attribute.GENERIC_MAX_HEALTH, 2, Operation.ADD_NUMBER, HEAD)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, HEAD).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.01, Operation.ADD_SCALAR, HEAD)
				.shine().setUnbreakable().setLeatherArmorColor(Color.LIME)
				.toItemStack());
		items.put("emeraudeC", new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(r()+"Plastron d'émeraude")
				.addAttribute(Attribute.GENERIC_ARMOR, 13, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MAX_HEALTH, 2, Operation.ADD_NUMBER, CHEST)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 2, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.02, Operation.ADD_SCALAR, CHEST)
				.shine().setUnbreakable().setLeatherArmorColor(Color.LIME)
				.toItemStack());
		items.put("emeraudeL", new ItemBuilder(Material.LEATHER_LEGGINGS).setName(r()+"Jambières d'émeraude")
				.addAttribute(Attribute.GENERIC_ARMOR, 12, Operation.ADD_NUMBER, LEGS).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, Operation.ADD_NUMBER, LEGS)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, LEGS).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.02, Operation.ADD_SCALAR, LEGS)
				.shine().setUnbreakable().setLeatherArmorColor(Color.LIME)
				.toItemStack());
		items.put("emeraudeB", new ItemBuilder(Material.LEATHER_BOOTS).setName(r()+"Bottes d'émeraude")
				.addAttribute(Attribute.GENERIC_ARMOR, 10, Operation.ADD_NUMBER, FEET).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, Operation.ADD_NUMBER, FEET)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, FEET).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.01, Operation.ADD_SCALAR, FEET)
				.shine().setUnbreakable().setLeatherArmorColor(Color.LIME)
				.toItemStack());
		
		items.put("epeeDure", new ItemBuilder(Material.STONE_SWORD).setName(r()+"Épée colossale")
				.addAttribute(Attribute.GENERIC_ARMOR, -4, Operation.ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_ATTACK_SPEED, -.9, Operation.ADD_SCALAR, HAND)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, -2, Operation.ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, -1, Operation.ADD_SCALAR, HAND)
				.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 0.5, Operation.ADD_SCALAR, HAND).shine().setUnbreakable()
				.toItemStack());
		
		items.put("epeeRapide", new ItemBuilder(Material.STONE_SWORD).setName(r()+"Épée rapide")
				.addAttribute(Attribute.GENERIC_ARMOR, -4, Operation.ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_ATTACK_SPEED, 1, Operation.ADD_SCALAR, HAND)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, -2, Operation.ADD_NUMBER, HAND).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, .1, Operation.ADD_SCALAR, HAND)
				.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, -.6, Operation.ADD_SCALAR, HAND).shine().setUnbreakable()
				.toItemStack());
		
		items.put("rubisH", new ItemBuilder(Material.LEATHER_HELMET).setName(l()+"Casque de rubis")
				.addAttribute(Attribute.GENERIC_ARMOR, 13, Operation.ADD_NUMBER, HEAD).addAttribute(Attribute.GENERIC_MAX_HEALTH, 2, Operation.ADD_NUMBER, HEAD)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, HEAD).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.02, Operation.ADD_SCALAR, HEAD)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 0.02, Operation.ADD_SCALAR, HEAD).shine().setUnbreakable().setLeatherArmorColor(Color.RED)
				.toItemStack());
		items.put("rubisC", new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(l()+"Plastron de rubis")
				.addAttribute(Attribute.GENERIC_ARMOR, 15, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MAX_HEALTH, 2, Operation.ADD_NUMBER, CHEST)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 2, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.04, Operation.ADD_SCALAR, CHEST)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 0.04, Operation.ADD_SCALAR, CHEST).shine().setUnbreakable().setLeatherArmorColor(Color.RED)
				.toItemStack());
		items.put("rubisL", new ItemBuilder(Material.LEATHER_LEGGINGS).setName(l()+"Jambières de rubis")
				.addAttribute(Attribute.GENERIC_ARMOR, 14, Operation.ADD_NUMBER, LEGS).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, Operation.ADD_NUMBER, LEGS)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, LEGS).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.03, Operation.ADD_SCALAR, LEGS)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 0.03, Operation.ADD_SCALAR, LEGS).shine().setUnbreakable().setLeatherArmorColor(Color.RED)
				.toItemStack());
		items.put("rubisB", new ItemBuilder(Material.LEATHER_BOOTS).setName(l()+"Bottes de rubis")
				.addAttribute(Attribute.GENERIC_ARMOR, 12, Operation.ADD_NUMBER, FEET).addAttribute(Attribute.GENERIC_MAX_HEALTH, 1, Operation.ADD_NUMBER, FEET)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 1, Operation.ADD_NUMBER, FEET).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.02, Operation.ADD_SCALAR, FEET)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 0.01, Operation.ADD_SCALAR, FEET ).shine().setUnbreakable().setLeatherArmorColor(Color.RED)
				.toItemStack());
		
		
		items.put("ancestralC", new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(u()+"Plastron ancestral")
				.addAttribute(Attribute.GENERIC_ARMOR, 16, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MAX_HEALTH, 6, Operation.ADD_NUMBER, CHEST)
				.addAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS, 3, Operation.ADD_NUMBER, CHEST).addAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.05, Operation.ADD_SCALAR, CHEST)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 0.05, Operation.ADD_SCALAR, CHEST).addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 0.05, Operation.ADD_SCALAR, CHEST)
				.addEnchant(Enchantment.ARROW_DAMAGE, 2).addEnchant(Enchantment.PROTECTION_FIRE, 2).shine().setUnbreakable().setLeatherArmorColor(Color.PURPLE)
				.toItemStack());
	}

}