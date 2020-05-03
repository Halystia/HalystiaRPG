package fr.jamailun.halystia.jobs.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
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
		items.put("diams", new ItemBuilder(Material.DIAMOND_ORE).setName(c() + "Roche diamantée").toItemStack());
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
		items.put("oak2", new ItemBuilder(Material.STRIPPED_OAK_WOOD).setName(c() + "Essence de chêne").toItemStack());
		items.put("birch1", new ItemBuilder(Material.BIRCH_PLANKS).setName(c() + "Planches de boulot").toItemStack());
		items.put("birch2", new ItemBuilder(Material.STRIPPED_BIRCH_WOOD).setName(c() + "Essence de boulot").toItemStack());
		items.put("spruce1", new ItemBuilder(Material.SPRUCE_PLANKS).setName(c() + "Planches de sapin").toItemStack());
		items.put("spruce2", new ItemBuilder(Material.STRIPPED_SPRUCE_WOOD).setName(c() + "Essence de sapin").toItemStack());
		items.put("dark1", new ItemBuilder(Material.DARK_OAK_PLANKS).setName(c() + "Planches de bois maudit").toItemStack());
		items.put("dark2", new ItemBuilder(Material.STRIPPED_DARK_OAK_WOOD).setName(c() + "Essence de bois maudit").toItemStack());
		items.put("acacia1", new ItemBuilder(Material.ACACIA_PLANKS).setName(c() + "Planches de bois sacré").toItemStack());
		items.put("acacia2", new ItemBuilder(Material.STRIPPED_ACACIA_WOOD).setName(c() + "Essence de bois sacré").toItemStack());
		
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
		blocs.registerContent(new JobBlock(mineur, 2, Material.COAL_ORE, item("coal"), 10, 90));
		blocs.registerContent(new JobBlock(mineur, 3, Material.IRON_ORE, item("iron"), 30, 180));
		blocs.registerContent(new JobBlock(mineur, 4, Material.GOLD_ORE, item("gold"), 50, 230));
		blocs.registerContent(new JobBlock(mineur, 5, Material.DIAMOND_ORE, item("diams"), 100, 300));
		
		JobCraftsManager crafts = jobs.getCraftsManager();
		crafts.registerContent(new JobCraft(mineur, 1, item("stone", 4), 1, 	item("cobble", 5), item("water")));
		crafts.registerContent(new JobCraft(mineur, 1, item("silex", 1), 4, 	item("gravel", 3), item("water")));
		crafts.registerContent(new JobCraft(mineur, 1, item("stone1", 4), 5, 	item("stone", 5), item("water"), item("silex")));
		crafts.registerContent(new JobCraft(mineur, 2, item("coal1", 4), 2, 	item("coal", 3), item("water")));
		crafts.registerContent(new JobCraft(mineur, 2, item("coal2", 1), 10, 	item("coal1", 10), item("silex")));
		crafts.registerContent(new JobCraft(mineur, 2, item("silex1", 1), 13, 	item("coal1", 2), item("silex", 3), item("stone1", 2)));
		crafts.registerContent(new JobCraft(mineur, 3, item("iron1", 2), 3, 	item("iron", 2), item("coal1")));
		crafts.registerContent(new JobCraft(mineur, 3, item("iron2", 1), 10, 	item("iron1", 12), item("coal1", 5)));
		crafts.registerContent(new JobCraft(mineur, 3, item("stone2", 1), 13, 	item("stone1", 64), item("silex1", 3), item("iron2"), item("coal2", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("gold1", 2), 4, 	item("gold", 3), item("coal1", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("gold2", 1), 12, 	item("gold1", 12), item("coal2", 3)));
		crafts.registerContent(new JobCraft(mineur, 4, item("acier", 2), 10, 	item("iron2", 1), item("gold1", 3), item("lava", 1)));
		crafts.registerContent(new JobCraft(mineur, 5, item("diams1", 2), 4, 	item("diams", 3), item("coal2", 2)));
		crafts.registerContent(new JobCraft(mineur, 5, item("diams2", 1), 12, 	item("diams1", 12), item("coal2", 6), item("lava", 2)));
		crafts.registerContent(new JobCraft(mineur, 5, item("tasei", 1), 100, 	item("diams2", 2), item("acier", 2), item("stone2", 5), item("lava", 4)));
		
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
		crafts.registerContent(new JobCraft(bucheron, 1, item("stick", 5), 1, 		item("oak1", 2)));
		crafts.registerContent(new JobCraft(bucheron, 1, item("oak2", 1), 1, 		item("oak1", 10), item("oak",10), item("water", 2)));
		crafts.registerContent(new JobCraft(bucheron, 2, item("birch1", 4), 1, 		item("birch", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 2, item("string", 2), 1, 		item("birch1", 3)));
		crafts.registerContent(new JobCraft(bucheron, 2, item("birch2", 1), 1, 		item("birch1", 10), item("birch",10), item("water", 2)));
		crafts.registerContent(new JobCraft(bucheron, 3, item("spruce1", 3), 1, 	item("spruce", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 3, item("spruce2", 1), 1, 	item("spruce1", 10), item("spruce", 10), item("water", 2), item("silex")));
		crafts.registerContent(new JobCraft(bucheron, 4, item("dark1", 2), 1, 		item("dark", 5), item("water")));
		crafts.registerContent(new JobCraft(bucheron, 4, item("dark2", 1), 1, 		item("dark1", 10), item("dark", 10), item("water", 2), item("silex1")));
		crafts.registerContent(new JobCraft(bucheron, 5, item("acacia1", 1), 1, 	item("acacia", 5), item("water"), item("silex1")));
		crafts.registerContent(new JobCraft(bucheron, 5, item("acacia2", 1), 1, 	item("acacia1", 10), item("acacia", 10), item("water", 3), item("silex1", 2), item("stone2")));
		
		
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
		
		
		

		forgeron.changeCraftGUI(Material.ANVIL, new JobCraftGUI(forgeron));
	}
	
	private static String c() {
		return Rarity.COMMON.getColor();
	}
	private static String r() {
		return Rarity.RARE.getColor();
	}
	
}