package fr.jamailun.halystia.jobs.model;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.RpgEquipment;

class JamailunStuffExtension extends JamailunItemModel {
	void generate() {
// STUFF FORGERON
	
	// visiteur
		items.put("visiH",
				new RpgEquipment(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Casque du visiteur")
				.setArmor(2)
		.toItemStack());
		items.put("visiC",
				new RpgEquipment(Material.IRON_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Plastron du visiteur")
				.setArmor(3)
		.toItemStack());
		items.put("visiL",
				new RpgEquipment(Material.IRON_LEGGINGS).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Jambières du visiteur")
				.setArmor(2)
		.toItemStack());
		items.put("visiB",
				new RpgEquipment(Material.IRON_BOOTS).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Bottes du visiteur")
				.setArmor(2)
		.toItemStack());
		items.put("visiE",
				new RpgEquipment(Material.IRON_SWORD).addEnchant(Enchantment.DURABILITY, 1).setName(c() + "Épée du visiteur")
				.setDamagesInt(20)
		.toItemStack());
	// novice
		items.put("noviH",
				new RpgEquipment(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Casque du novice")
				.setArmor(3).setLevel(5)
		.toItemStack());
		items.put("noviC",
				new RpgEquipment(Material.IRON_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Plastron du novice")
				.setArmor(4).setLevel(5)
		.toItemStack());
		items.put("noviL",
				new RpgEquipment(Material.IRON_LEGGINGS).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Jambières du novice")
				.setArmor(3).setLevel(5)
		.toItemStack());
		items.put("noviB",
				new RpgEquipment(Material.IRON_BOOTS).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Bottes du novice")
				.setArmor(3).setLevel(5)
		.toItemStack());
		items.put("noviE",
				new RpgEquipment(Material.IRON_SWORD).addEnchant(Enchantment.DURABILITY, 2).setName(c() + "Épée du novice")
				.setDamagesInt(25).setLevel(5)
		.toItemStack());
	// néophyte
		items.put("néopH",
				new RpgEquipment(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Casque du néophyte")
				.setArmor(4).setLevel(10)
		.toItemStack());
		items.put("néopC",
				new RpgEquipment(Material.IRON_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Plastron du néophyte")
				.setArmor(5).setLevel(10)
		.toItemStack());
		items.put("néopL",
				new RpgEquipment(Material.IRON_LEGGINGS).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Jambières du néophyte")
				.setArmor(4).setLevel(10)
		.toItemStack());
		items.put("néopB",
				new RpgEquipment(Material.IRON_BOOTS).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Bottes du néophyte")
				.setArmor(4).setLevel(10)
		.toItemStack());
		items.put("néopE",
				new RpgEquipment(Material.IRON_SWORD).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.DAMAGE_UNDEAD, 1).setName(c() + "Épée du néophyte")
				.setDamagesInt(30).setLevel(10)
		.toItemStack());
	// pélerin
		items.put("peleH",
				new RpgEquipment(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Casque du pélerin")
				.setArmor(5).setLevel(15)
		.toItemStack());
		items.put("peleC",
				new RpgEquipment(Material.IRON_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Plastron du pélerin")
				.setArmor(6).setLevel(15)
		.toItemStack());
		items.put("peleL",
				new RpgEquipment(Material.IRON_LEGGINGS).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Jambières du pélerin")
				.setArmor(5).setLevel(15)
		.toItemStack());
		items.put("peleB",
				new RpgEquipment(Material.IRON_BOOTS).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Bottes du pélerin")
				.setArmor(5).setLevel(15)
		.toItemStack());
		items.put("peleE",
				new RpgEquipment(Material.IRON_SWORD).addEnchant(Enchantment.DURABILITY, 5).addEnchant(Enchantment.DAMAGE_UNDEAD, 2).setName(c() + "Épée du pélerin")
				.setDamagesInt(40).setHealth(10).setLevel(15)
		.toItemStack());
	// Voyageur
		items.put("voyaH",
				new RpgEquipment(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 7).setName(c() + "Casque du voyageur")
				.setArmor(6).setLevel(20)
		.toItemStack());
		items.put("voyaC",
				new RpgEquipment(Material.IRON_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 7).setName(c() + "Plastron du voyageur")
				.setArmor(7).setLevel(20)
		.toItemStack());
		items.put("voyaL",
				new RpgEquipment(Material.IRON_LEGGINGS).addEnchant(Enchantment.DURABILITY, 7).setName(c() + "Jambières du voyageur")
				.setArmor(6).setLevel(20)
		.toItemStack());
		items.put("voyaB",
				new RpgEquipment(Material.IRON_BOOTS).addEnchant(Enchantment.DURABILITY, 7).setName(c() + "Bottes du voyageur")
				.setArmor(6).setLevel(20)
		.toItemStack());
		items.put("voyaE",
				new RpgEquipment(Material.IRON_SWORD).addEnchant(Enchantment.DURABILITY, 7).addEnchant(Enchantment.DAMAGE_UNDEAD, 2).setName(c() + "Épée du voyageur")
				.setDamagesInt(55).setHealth(20).setLevel(20)
		.toItemStack());
		
	// explorateur
		items.put("explH",
				new RpgEquipment(Material.GOLDEN_HELMET).shineAndUnbreak().setName(c() + "Casque de l'explorateur")
				.setArmor(8).setLevel(30)
		.toItemStack());
		items.put("explC",
				new RpgEquipment(Material.GOLDEN_CHESTPLATE).shineAndUnbreak().setName(c() + "Plastron de l'explorateur")
				.setArmor(9).setLevel(30)
		.toItemStack());
		items.put("explL",
				new RpgEquipment(Material.GOLDEN_LEGGINGS).shineAndUnbreak().setName(c() + "Jambières de l'explorateur")
				.setArmor(8).setLevel(30)
		.toItemStack());
		items.put("explB",
				new RpgEquipment(Material.GOLDEN_BOOTS).shineAndUnbreak().setName(c() + "Bottes de l'explorateur")
				.setArmor(8).setLevel(30)
		.toItemStack());
		items.put("explE",
				new RpgEquipment(Material.GOLDEN_SWORD).shineAndUnbreak().addEnchant(Enchantment.DAMAGE_UNDEAD, 4).setName(c() + "Épée de l'explorateur")
				.setDamagesInt(80).setHealth(40).setLevel(30)
		.toItemStack());
	// conquérant
		items.put("conqH",
				new RpgEquipment(Material.GOLDEN_HELMET).shineAndUnbreak().setName(c() + "Casque du conquérant")
				.setArmor(9).setLevel(40)
		.toItemStack());
		items.put("conqC",
				new RpgEquipment(Material.GOLDEN_CHESTPLATE).shineAndUnbreak().setName(c() + "Plastron du conquérant")
				.setArmor(10).setLevel(40)
		.toItemStack());
		items.put("conqL",
				new RpgEquipment(Material.GOLDEN_LEGGINGS).shineAndUnbreak().setName(c() + "Jambières du conquérant")
				.setArmor(9).setLevel(40)
		.toItemStack());
		items.put("conqB",
				new RpgEquipment(Material.GOLDEN_BOOTS).shineAndUnbreak().setName(c() + "Bottes du conquérant")
				.setArmor(9).setLevel(40)
		.toItemStack());
		items.put("conqE",
				new RpgEquipment(Material.GOLDEN_SWORD).shineAndUnbreak().addEnchant(Enchantment.DAMAGE_UNDEAD, 5).addEnchant(Enchantment.FIRE_ASPECT, 1).setName(c() + "Épée du conquérant")
				.setDamagesInt(80).setHealth(80).setLevel(40)
		.toItemStack());
		
		
		items.put("pioche1",new RpgEquipment(Material.IRON_PICKAXE).setDamagesInt(4).addEnchant(Enchantment.DIG_SPEED, 1).setName(c() + "Pioche ancienne").toItemStack());
		items.put("hache1", new RpgEquipment(Material.IRON_AXE).setDamagesInt(4).addEnchant(Enchantment.DIG_SPEED, 1).setName(c() + "Hache ancienne").toItemStack());
		items.put("pioche2",new RpgEquipment(Material.IRON_PICKAXE).setDamagesInt(8).addEnchant(Enchantment.DIG_SPEED, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Pioche puissante").toItemStack());
		items.put("hache2", new RpgEquipment(Material.IRON_AXE).setDamagesInt(8).addEnchant(Enchantment.DIG_SPEED, 2).addEnchant(Enchantment.DURABILITY, 3).setName(c() + "Hache puissante").toItemStack());
		items.put("pioche3",new RpgEquipment(Material.GOLDEN_PICKAXE).setLevel(30).setDamagesInt(15).addEnchant(Enchantment.DIG_SPEED, 4).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Pioche parfaite").toItemStack());
		items.put("hache3", new RpgEquipment(Material.GOLDEN_AXE).setLevel(30).setDamagesInt(15).addEnchant(Enchantment.DIG_SPEED, 4).addEnchant(Enchantment.DURABILITY, 5).setName(c() + "Hache parfaite").toItemStack());

// STUFF AUTRE
		
	// émeraude
		items.put("emeraudeH",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.fromRGB(0, 255, 0))).setUnbreakable().setName(c() + "Casque d'émeraude")
				.setArmor(12).setHealth(50).setLevel(50)
		.toItemStack());
		items.put("emeraudeC",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.fromRGB(0, 255, 0))).setUnbreakable().setName(c() + "Plastron d'émeraude")
				.setArmor(12).setDamageBuff(5).setHealth(60).setLevel(50)
		.toItemStack());
		items.put("emeraudeL",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.fromRGB(0, 255, 0))).setUnbreakable().setName(c() + "Jambières d'émeraude")
				.setArmor(12).setHealth(50).setLevel(50)
		.toItemStack());
		items.put("emeraudeB",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.fromRGB(0, 255, 0))).setUnbreakable().setName(c() + "Bottes d'émeraude")
				.setArmor(12).setHealth(50).setLevel(50)
		.toItemStack());
	// rubis
		items.put("rubisH",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(Color.fromRGB(255, 0, 0))).setUnbreakable().setName(c() + "Casque de rubis")
				.setArmor(14).setHealth(80).setLevel(70)
		.toItemStack());
		items.put("rubisC",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(Color.fromRGB(255, 0, 0))).setUnbreakable().setName(c() + "Plastron de rubis")
				.setArmor(14).setDamageBuff(7.5).setHealth(100).setLevel(70)
		.toItemStack());
		items.put("rubisL",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(Color.fromRGB(255, 0, 0))).setUnbreakable().setName(c() + "Jambières de rubis")
				.setArmor(14).setHealth(80).setLevel(70)
		.toItemStack());
		items.put("rubisB",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.fromRGB(255, 0, 0))).setUnbreakable().setName(c() + "Bottes de rubis")
				.setArmor(14).setHealth(80).setLevel(70)
		.toItemStack());
		
	// ancestral
		items.put("plastronAncestral",
				new RpgEquipment(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(Color.PURPLE)).setUnbreakable().setName(c() + "Plastron ancestral")
				.setArmor(15).setHealth(200).setLevel(40).setDamageBuff(-10)
		.toItemStack());
		
	
		
	}

}