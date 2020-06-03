package fr.jamailun.halystia.jobs.model;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.utils.ItemBuilder;

class JamailunItemExtension extends JamailunItemModel {

	void generate() {
		items.put("cobble", new ItemBuilder(Material.COBBLESTONE).setName(c() + "Pierre taillée").toItemStack());
		items.put("gravel", new ItemBuilder(Material.GRAVEL).setName(c() + "Gravier").toItemStack());
		items.put("coal", new ItemBuilder(Material.CHARCOAL).setName(c() + "Vieux charbon").toItemStack());
		items.put("iron", new ItemBuilder(Material.IRON_ORE).setName(c() + "Fer primitif").toItemStack());
		items.put("gold", new ItemBuilder(Material.GOLD_ORE).setName(c() + "Or primitif").toItemStack());
		items.put("quartz", new ItemBuilder(Material.QUARTZ).setName(c() + "Quartz").toItemStack());
		items.put("diams", new ItemBuilder(Material.DIAMOND_ORE).setName(c() + "Roche diamantée").toItemStack());
		items.put("obsi", new ItemBuilder(Material.OBSIDIAN).setName(c() + "Obsidienne").toItemStack());
		//RESSOUCRES BUCHERON
		items.put("oak", new ItemBuilder(Material.OAK_LOG).setName(c() + "Bûche de chêne").toItemStack());
		items.put("birch", new ItemBuilder(Material.BIRCH_LOG).setName(c() + "Bois de boulot").toItemStack());
		items.put("spruce", new ItemBuilder(Material.SPRUCE_LOG).setName(c() + "Bois de sapin").toItemStack());
		items.put("dark", new ItemBuilder(Material.DARK_OAK_LOG).setName(c() + "Bois maudit").toItemStack());
		items.put("acacia", new ItemBuilder(Material.ACACIA_LOG).setName(c() + "Bois sacré").toItemStack());
		//RESSOURCES DIVERSES
		items.put("stick", new ItemBuilder(Material.STICK).setName(c()+"Baton").toItemStack());
		items.put("string", new ItemBuilder(Material.STRING).setName(c()+"Corde").toItemStack());
		items.put("water", new ItemBuilder(Material.POTION).addItemFlag(ItemFlag.HIDE_POTION_EFFECTS).setPotionColor(Color.BLUE).setName(c()+"Eau pure").toItemStack());
		items.put("lava", new ItemBuilder(Material.LAVA_BUCKET).setName(c()+"Lave").toItemStack());
		
		items.put("miel", new ItemBuilder(Material.HONEY_BOTTLE).setName(c()+"Fiole de miel").toItemStack());
		items.put("chair", new ItemBuilder(Material.ROTTEN_FLESH).setName(c()+"Chair décomposée").toItemStack());
		items.put("chairSub", new ItemBuilder(Material.ROTTEN_FLESH).shine().setName(r()+"Chair sublimée").toItemStack());
		items.put("vésiculePokoi", new ItemBuilder(Material.PURPLE_DYE).setName(c()+"Vésicule de pokoï").toItemStack());
		items.put("os1", new ItemBuilder(Material.BONE).setName(c()+"Vieil os").toItemStack());
		items.put("os2", new ItemBuilder(Material.BONE).setName(r()+"Os ancestral").toItemStack());
		items.put("eye", new ItemBuilder(Material.SPIDER_EYE).setName(c()+"Oeil de tarentule").toItemStack());
		
		
		
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
		//PAYSAN
		items.put("blé", new ItemBuilder(Material.WHEAT).setName(c()+"Blé doré").toItemStack());
		items.put("bettrave", new ItemBuilder(Material.BEETROOT).setName(c()+"Betterave nouvelle").toItemStack());
		items.put("canna", new ItemBuilder(Material.SUGAR_CANE).setName(c()+"Canne à sucre").toItemStack());
		items.put("patate", new ItemBuilder(Material.POTATO).setName(c()+"Petite patate").toItemStack());
		items.put("carotte", new ItemBuilder(Material.CARROT).setName(c()+"Carotte").toItemStack());
		items.put("citrouille", new ItemBuilder(Material.PUMPKIN).setName(c()+"Belle citwouille").toItemStack());
		items.put("melon", new ItemBuilder(Material.MELON).setName(c()+"Gros melon").toItemStack());

		items.put("farine1", new ItemBuilder(Material.LIGHT_GRAY_DYE).setName(c()+"Farine grossière").toItemStack());
		items.put("farine2", new ItemBuilder(Material.LIGHT_GRAY_DYE).setName(c()+"Farine rafinée").toItemStack());
		items.put("farine3", new ItemBuilder(Material.LIGHT_GRAY_DYE).setName(c()+"Farine enchantée").shine().toItemStack());
		items.put("pain1", new ItemBuilder(Material.BREAD).setName(c()+"Petit pain").toItemStack());
		items.put("pain2", new ItemBuilder(Material.BREAD).setName(r()+"Pain excellent").addLoreLine(ChatColor.RED+"+1 ❤").shine().toItemStack());
		items.put("bakedpotato", new ItemBuilder(Material.BAKED_POTATO).setName(c()+"Patate cuite").toItemStack());
		items.put("soupe1", new ItemBuilder(Material.BEETROOT_SOUP).setName(c()+"Soupe de betteraves").toItemStack());
		items.put("soupe2", new ItemBuilder(Material.MUSHROOM_STEW).setName(c()+"Soupe lourde").addLoreLine(ChatColor.RED+"+3 ❤").toItemStack());

		items.put("perlimpimpim", new ItemBuilder(Material.LIGHT_BLUE_DYE).setName(c()+"Poudre de perlimpimpim").toItemStack());

		items.put("sucre1", new ItemBuilder(Material.SUGAR).setName(c()+"Sucre grossier").toItemStack());
		items.put("sucre2", new ItemBuilder(Material.SUGAR).shine().setName(c()+"Sucre rafiné").toItemStack());
		items.put("carottedorée", new ItemBuilder(Material.GOLDEN_CARROT).setName(c()+"Carotte dorée").toItemStack());
		
		
		//CRAFTS phyto
		items.put("réceptacle", new ItemBuilder(Material.WHITE_STAINED_GLASS).setName(c()+"Réceptable").toItemStack());
		items.put("bouteille", new ItemBuilder(Material.GLASS_BOTTLE).setName(c() + "Bouteille à potion").toItemStack());
		
		items.put("potion_abso1", new ItemBuilder(PotionEffectType.ABSORPTION, 60, 0, Color.YELLOW, false, 1).setName(c() + "Potion d'absorption I").toItemStack());
		items.put("potion_resis1", new ItemBuilder(PotionEffectType.DAMAGE_RESISTANCE, 10, 0, Color.SILVER, false, 1).setName(c() + "Potion de résistance I").toItemStack());
		items.put("potion_atterissage", new ItemBuilder(PotionEffectType.JUMP, 10, 255, Color.OLIVE, false, 1).setName(c() + "Potion de pieds-durs").addItemFlag(ItemFlag.HIDE_POTION_EFFECTS).toItemStack());
		
		
	}
}