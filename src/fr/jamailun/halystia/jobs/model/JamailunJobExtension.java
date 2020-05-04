package fr.jamailun.halystia.jobs.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import static org.bukkit.attribute.AttributeModifier.Operation.*;
import org.bukkit.enchantments.Enchantment;
import static org.bukkit.inventory.EquipmentSlot.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.constants.Rarity;
import fr.jamailun.halystia.jobs.JobBlock;
import fr.jamailun.halystia.jobs.JobBlockManager;
import fr.jamailun.halystia.jobs.JobCategory;
import fr.jamailun.halystia.jobs.JobCraft;
import fr.jamailun.halystia.jobs.JobCraftGUI;
import fr.jamailun.halystia.jobs.JobCraftsManager;
import fr.jamailun.halystia.jobs.JobType;
import fr.jamailun.halystia.jobs.JobsManager;
import fr.jamailun.halystia.utils.ItemBuilder;

public class JamailunJobExtension {
	
	private final JobsManager jobs;
	private final String path;
	
	private final Map<String, ItemStack> items;
	
	public JamailunJobExtension(String path, JobsManager jobs) {
		this.jobs = jobs;
		this.path = path;
		
		items = registerItems();
		try {
			registerMineur();
			registerBucheron();
			registerFogeron();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		jobs.getItemManager().addAllContent(items);
	}
	
	private Map<String, ItemStack> registerItems() {
		Map<String, ItemStack> items = new HashMap<>();
		// RESSOURCES MINEUR
		items.put("cobble", new ItemBuilder(Material.COBBLESTONE).setName(c() + "Pierre taillée").toItemStack());
		items.put("gravel", new ItemBuilder(Material.GRAVEL).setName(c() + "Gravier").toItemStack());
		items.put("coal", new ItemBuilder(Material.CHARCOAL).setName(c() + "Vieux charbon").toItemStack());
		items.put("iron", new ItemBuilder(Material.IRON_ORE).setName(c() + "Fer primitif").toItemStack());
		items.put("gold", new ItemBuilder(Material.GOLD_ORE).setName(c() + "Or primitif").toItemStack());
		items.put("quartz", new ItemBuilder(Material.QUARTZ).setName(c() + "Quartz").toItemStack());
		items.put("diams", new ItemBuilder(Material.DIAMOND_ORE).setName(c() + "Roche diamantée").toItemStack());
		items.put("obsi", new ItemBuilder(Material.OBSIDIAN).setName(c() + "Obsidienne").toItemStack());
		//RESSOUCRES BUCHERON
		items.put("oak", new ItemBuilder(Material.OAK_LOG).setName(c() + "Bois de chêne").toItemStack());
		items.put("birch", new ItemBuilder(Material.BIRCH_LOG).setName(c() + "Bois de boulot").toItemStack());
		items.put("spruce", new ItemBuilder(Material.SPRUCE_LOG).setName(c() + "Bois de sapin").toItemStack());
		items.put("dark", new ItemBuilder(Material.DARK_OAK_LOG).setName(c() + "Bois maudit").toItemStack());
		items.put("acacia", new ItemBuilder(Material.ACACIA_LOG).setName(c() + "Bois sacré").toItemStack());
		//RESSOURCES DIVERSES
		items.put("stick", new ItemBuilder(Material.STICK).setName(c()+"Baton").toItemStack());
		items.put("string", new ItemBuilder(Material.STRING).setName(c()+"Corde").toItemStack());
		items.put("water", new ItemBuilder(Material.POTION).addItemFlag(ItemFlag.HIDE_POTION_EFFECTS).setPotionColor(Color.BLUE).setName(c()+"Eau pure").toItemStack());
		items.put("lava", new ItemBuilder(Material.LAVA_BUCKET).setName(c()+"Lave").toItemStack());
		
		//CRAFTS MINEUR
		items.put("stone", new ItemBuilder(Material.STONE).setName(c() + "Pierre lisse").toItemStack());
		items.put("silex", new ItemBuilder(Material.FLINT).setName(c() + "Silex").toItemStack());
		items.put("stone1", new ItemBuilder(Material.SMOOTH_STONE).setName(c() + "Pierre condensée").toItemStack());
		items.put("coal1", new ItemBuilder(Material.COAL).setName(c() + "Charbon épuré").toItemStack());
		items.put("coal2", new ItemBuilder(Material.COAL).shine().setName(c() + "Charbon raffiné").toItemStack());
		items.put("coal3", new ItemBuilder(Material.COAL_BLOCK).setName(c() + "Charbon pur").toItemStack());
		items.put("silex1", new ItemBuilder(Material.FLINT).shine().setName(c() + "Silex enchanté").toItemStack());
		items.put("iron1", new ItemBuilder(Material.IRON_INGOT).setName(c() + "Lingot de fer").toItemStack());
		items.put("iron2", new ItemBuilder(Material.IRON_BLOCK).setName(c() + "Fer condensé").toItemStack());
		items.put("stone2", new ItemBuilder(Material.DEAD_BRAIN_CORAL_BLOCK).shine().setName(c() + "Roche enchantée").toItemStack());
		items.put("gold1", new ItemBuilder(Material.GOLD_INGOT).setName(c() + "Lingot d'or").toItemStack());
		items.put("gold2", new ItemBuilder(Material.GOLD_BLOCK).setName(c() + "Or condensé").toItemStack());
		items.put("acier", new ItemBuilder(Material.IRON_INGOT).shine().setName(r() + "Barre d'acier").toItemStack());
		items.put("diams1", new ItemBuilder(Material.DIAMOND).setName(c() + "Diamant brut").toItemStack());
		items.put("diams2", new ItemBuilder(Material.DIAMOND_BLOCK).setName(c() + "Diamant taillée").toItemStack());
		items.put("tasei", new ItemBuilder(Material.BLUE_GLAZED_TERRACOTTA).shine().setName(r() + "Taseigaru").toItemStack());
		
		//CRAFTS BUCHERON
		items.put("oak1", new ItemBuilder(Material.OAK_PLANKS).setName(c() + "Planches de chêne").toItemStack());
		items.put("oak2", new ItemBuilder(Material.STRIPPED_OAK_WOOD).shine().setName(c() + "Essence de chêne").toItemStack());
		items.put("birch1", new ItemBuilder(Material.BIRCH_PLANKS).setName(c() + "Planches de boulot").toItemStack());
		items.put("birch2", new ItemBuilder(Material.STRIPPED_BIRCH_WOOD).shine().setName(c() + "Essence de boulot").toItemStack());
		items.put("spruce1", new ItemBuilder(Material.SPRUCE_PLANKS).setName(c() + "Planches de sapin").toItemStack());
		items.put("spruce2", new ItemBuilder(Material.STRIPPED_SPRUCE_WOOD).shine().setName(c() + "Essence de sapin").toItemStack());
		items.put("dark1", new ItemBuilder(Material.DARK_OAK_PLANKS).setName(c() + "Planches de bois maudit").toItemStack());
		items.put("dark2", new ItemBuilder(Material.STRIPPED_DARK_OAK_WOOD).shine().setName(c() + "Essence de bois maudit").toItemStack());
		items.put("acacia1", new ItemBuilder(Material.ACACIA_PLANKS).setName(c() + "Planches de bois sacré").toItemStack());
		items.put("acacia2", new ItemBuilder(Material.STRIPPED_ACACIA_WOOD).shine().setName(c() + "Essence de bois sacré").toItemStack());
		
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
		
		
		return items;
	}
	
	private ItemStack item(String index) {
		return item(index, 1);
	}
	
	private ItemStack item(String index, int amount) {
		return new ItemBuilder(items.get(index)).setAmount(amount).toItemStack();
	}

	private void registerMineur() {
		JobType mineur = new JobType(path, "mineur", JobCategory.RECOLTE, jobs);
		mineur.setIcon(1, Material.WOODEN_PICKAXE);
		mineur.setIcon(2, Material.STONE_PICKAXE);
		mineur.setIcon(3, Material.IRON_PICKAXE);
		mineur.setIcon(4, Material.GOLDEN_PICKAXE);
		mineur.setIcon(5, Material.DIAMOND_PICKAXE);
		jobs.registerJob(mineur);
		
		JobBlockManager blocs = jobs.getBlocsManager();
		blocs.registerContent(new JobBlock(mineur, 1, Material.COBBLESTONE, item("cobble"), 5, 30));
		blocs.registerContent(new JobBlock(mineur, 1, Material.GRAVEL, item("gravel"), 2, 45));
		blocs.registerContent(new JobBlock(mineur, 1, Material.COAL_ORE, item("coal"), 8, 90));
		blocs.registerContent(new JobBlock(mineur, 2, Material.IRON_ORE, item("iron"), 14, 180));
		blocs.registerContent(new JobBlock(mineur, 3, Material.GOLD_ORE, item("gold"), 21, 230));
		blocs.registerContent(new JobBlock(mineur, 4, Material.NETHER_QUARTZ_ORE, item("quartz"), 27, 180));
		blocs.registerContent(new JobBlock(mineur, 5, Material.DIAMOND_ORE, item("diams"), 35, 300));
		
		JobCraftsManager crafts = jobs.getCraftsManager();
		crafts.registerContent(new JobCraft(mineur, 1, item("stone", 4), 1, 	item("cobble", 5), item("water")));
		crafts.registerContent(new JobCraft(mineur, 1, item("stone1", 4), 5, 	item("stone", 5), item("water"), item("silex")));
		crafts.registerContent(new JobCraft(mineur, 1, item("coal1", 4), 2, 	item("coal", 3), item("water")));
		crafts.registerContent(new JobCraft(mineur, 1, item("coal2", 1), 10, 	item("coal1", 10), item("silex")));
		crafts.registerContent(new JobCraft(mineur, 1, item("silex", 1), 4, 	item("gravel", 3), item("water")));
		
		crafts.registerContent(new JobCraft(mineur, 2, item("silex1", 2), 13, 	item("coal1", 2), item("silex", 3), item("stone1", 2)));
		crafts.registerContent(new JobCraft(mineur, 2, item("iron1", 2), 3, 	item("iron", 2), item("coal1")));
		crafts.registerContent(new JobCraft(mineur, 2, item("iron2", 1), 10, 	item("iron1", 12), item("coal1", 5)));
		
		crafts.registerContent(new JobCraft(mineur, 3, item("stone2", 1), 13, 	item("stone1", 64), item("silex1", 3), item("iron2"), item("coal2", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("gold1", 2), 4, 	item("gold", 3), item("coal1", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("gold2", 1), 12, 	item("gold1", 12), item("coal2", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("acier", 2), 10, 	item("iron2", 1), item("gold1", 3), item("lava", 1)));
		crafts.registerContent(new JobCraft(mineur, 5, item("diams1", 2), 4, 	item("diams", 3), item("coal2", 2)));
		crafts.registerContent(new JobCraft(mineur, 5, item("diams2", 1), 12, 	item("diams1", 12), item("coal2", 6), item("lava", 2)));
		crafts.registerContent(new JobCraft(mineur, 5, item("tasei", 1), 40, 	item("diams2", 2), item("acier", 2), item("stone2", 5), item("lava", 4)));
		
		mineur.changeCraftGUI(Material.BLAST_FURNACE, new JobCraftGUI(mineur));
	}

	private void registerBucheron() {
		JobType bucheron = new JobType(path, "bûcheron", JobCategory.RECOLTE, jobs);
		bucheron.setIcon(1, Material.WOODEN_AXE);
		bucheron.setIcon(2, Material.STONE_AXE);
		bucheron.setIcon(3, Material.IRON_AXE);
		bucheron.setIcon(4, Material.GOLDEN_AXE);
		bucheron.setIcon(5, Material.DIAMOND_AXE);
		jobs.registerJob(bucheron);
		
		JobBlockManager blocs = jobs.getBlocsManager();
		blocs.registerContent(new JobBlock(bucheron, 1, Material.OAK_LOG, item("oak"), 5, 30));
		blocs.registerContent(new JobBlock(bucheron, 2, Material.BIRCH_LOG, item("birch"), 10, 90));
		blocs.registerContent(new JobBlock(bucheron, 3, Material.SPRUCE_LOG, item("spruce"), 30, 180));
		blocs.registerContent(new JobBlock(bucheron, 4, Material.DARK_OAK_LOG, item("dark"), 50, 230));
		blocs.registerContent(new JobBlock(bucheron, 5, Material.ACACIA_LOG, item("acacia"), 100, 300));
		
		JobCraftsManager crafts = jobs.getCraftsManager();
		crafts.registerContent(new JobCraft(bucheron, 1, item("oak1", 5), 1, 		item("oak", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 1, item("stick", 5), 0, 		item("oak1", 2)));
		crafts.registerContent(new JobCraft(bucheron, 1, item("oak2", 1), 5, 		item("oak1", 10), item("oak",10), item("water", 2)));
		crafts.registerContent(new JobCraft(bucheron, 2, item("birch1", 4), 2, 		item("birch", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 2, item("string", 2), 1, 		item("birch1", 3)));
		crafts.registerContent(new JobCraft(bucheron, 2, item("birch2", 1), 8, 		item("birch1", 10), item("birch",10), item("water", 2)));
		crafts.registerContent(new JobCraft(bucheron, 3, item("spruce1", 3), 3, 	item("spruce", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 3, item("spruce2", 1), 12, 	item("spruce1", 10), item("spruce", 10), item("water", 2), item("silex")));
		crafts.registerContent(new JobCraft(bucheron, 4, item("dark1", 2), 4, 		item("dark", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 4, item("dark2", 1), 16, 		item("dark1", 10), item("dark", 10), item("water", 2), item("silex1")));
		crafts.registerContent(new JobCraft(bucheron, 5, item("acacia1", 1), 5, 	item("acacia", 5), item("water"), item("silex1")));
		crafts.registerContent(new JobCraft(bucheron, 5, item("acacia2", 1), 20, 	item("acacia1", 10), item("acacia", 10), item("water", 3), item("silex1", 2), item("stone2")));
		
		
		bucheron.changeCraftGUI(Material.STONECUTTER, new JobCraftGUI(bucheron));
	}
	
	private void registerFogeron() {
		JobType forgeron = new JobType(path, "forgeron", JobCategory.CRAFT, jobs);
		forgeron.setIcon(1, Material.BRICK);
		forgeron.setIcon(2, Material.IRON_INGOT);
		forgeron.setIcon(3, Material.GOLD_INGOT);
		forgeron.setIcon(4, Material.DIAMOND);
		forgeron.setIcon(5, Material.EMERALD);
		jobs.registerJob(forgeron);
		
		JobCraftsManager crafts = jobs.getCraftsManager();
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiH"), 25, 	item("iron1", 5), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiC"), 40, 	item("iron1", 8), item("coal1", 2)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiL"), 35, 	item("iron1", 7), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiB"), 20, 	item("iron1", 4), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiE"), 30, 	item("iron1", 3), item("coal1"), item("stick", 10)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("neopH"), 50, 	item("iron2", 18), item("coal2"), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("neopC"), 80, 	item("iron2", 22), item("coal2", 2), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("neopL"), 70, 	item("iron2", 20), item("coal2"), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("neopB"), 40, 	item("iron2", 16), item("coal2"), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("neopE"), 60, 	item("iron2", 3), item("coal2", 2), item("string", 4), item("stick", 40)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("hache1"), 40, 	item("iron2", 15), item("coal2"), item("string"), item("stick", 20)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("pioche1"), 40, 	item("iron2", 15), item("coal2"), item("string"), item("stick", 20)));
		
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleH"), 90, 	item("iron2", 10), item("coal2", 4), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleC"), 110, 	item("iron2", 16), item("coal2", 8), item("spruce2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleL"), 100, 	item("iron2", 14), item("coal2", 7), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleB"), 80, 	item("iron2", 8), item("coal2", 4), item("spruce2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleE"), 90, 	item("iron2", 10), item("coal2", 5), item("oak2", 15), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("voyaH"), 110, 	item("acier", 5), item("coal2", 10), item("spruce2", 5)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("voyaC"), 140, 	item("acier", 8), item("coal2", 16), item("spruce2", 8)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("voyaL"), 120, 	item("acier", 7), item("coal2", 14), item("spruce2", 7)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("voyaB"), 100, 	item("acier", 4), item("coal2", 12), item("spruce2", 6)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("voyaE"), 130, 	item("acier", 6), item("coal2", 18), item("oak2", 15), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("hache2"), 80, 	item("acier", 15), item("coal2", 4), item("string", 10), item("stick", 30)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("pioche2"), 80, 	item("acier", 15), item("coal2", 4), item("string", 10), item("stick", 30)));
		
		crafts.registerContent(new JobCraft(forgeron, 3, item("explH"), 130, 	item("gold1", 15), item("coal2", 15), item("dark2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explC"), 160, 	item("gold1", 24), item("coal2", 24), item("dark2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explL"), 150, 	item("gold1", 21), item("coal2", 21), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explB"), 120, 	item("gold1", 12), item("coal2", 12), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explE"), 140, 	item("gold1", 18), item("coal2", 18), item("oak2", 20), item("birch2", 10), item("spruce2", 5), item("dark2", 1)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("conqH"), 170, 	item("gold2", 12), item("coal2", 20), item("dark2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("conqC"), 200, 	item("gold2", 20), item("coal2", 29), item("dark2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("conqL"), 180, 	item("gold2", 17), item("coal2", 25), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("conqB"), 150, 	item("gold2", 9), item("coal2", 17), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("conqE"), 160, 	item("gold2", 14), item("coal2", 22), item("oak2", 40), item("birch2", 20), item("spruce2", 10), item("dark2", 5)));
		
		
		forgeron.changeCraftGUI(Material.ANVIL, new JobCraftGUI(forgeron));
	}
	
	private static String c() {
		return Rarity.COMMON.getColor();
	}
	private static String r() {
		return Rarity.RARE.getColor();
	}
	
}