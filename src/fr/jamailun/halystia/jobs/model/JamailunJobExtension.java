package fr.jamailun.halystia.jobs.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import fr.jamailun.halystia.jobs.model.enchanteur.EnchanteurGUI;
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
			registerEnchanteur();
			registerMineur();
			registerBucheron();
			registerFogeron();
			registerPhytomancien();
			registerPaysan();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		jobs.getItemManager().addAllContent(items);
	}
	
	private Map<String, ItemStack> registerItems() {
		Map<String, ItemStack> items = new HashMap<>();
		items.putAll(new JamailunItemExtension().getItems());
		items.putAll(new JamailunStuffExtension().getItems());
		
		return items;
	}
	
	private ItemStack item(String index) {
		return item(index, 1);
	}
	
	private ItemStack item(String index, int amount) {
		if(items.get(index) == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERREUR : index ("+index+") not found.");
			return new ItemBuilder(Material.BARRIER).setName("§c§lERREUR").toItemStack();
		}
		return new ItemBuilder(items.get(index)).setAmount(amount).toItemStack();
	}

	private void registerEnchanteur() {
		JobType enchanteur = new JobType(path, "enchanteur", JobCategory.BOOST, jobs);
		enchanteur.setIcon(1, Material.BOOK);
		enchanteur.setIcon(2, Material.BOOKSHELF);
		enchanteur.setIcon(3, Material.ENCHANTING_TABLE);
		enchanteur.setIcon(4, Material.HEART_OF_THE_SEA);
		enchanteur.setIcon(5, Material.BEACON);
		jobs.registerJob(enchanteur);
		EnchanteurGUI gui = new EnchanteurGUI(enchanteur);
		items.putAll(gui.getSources());
		enchanteur.changeCraftGUI(Material.ENCHANTING_TABLE, gui);
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
		blocs.registerContent(new JobBlock(mineur, 1, Material.COBBLESTONE, item("cobble"), 1, 70));
		blocs.registerContent(new JobBlock(mineur, 1, Material.GRAVEL, item("gravel"), 1, 90));
		blocs.registerContent(new JobBlock(mineur, 1, Material.COAL_ORE, item("coal"), 2, 120));
		blocs.registerContent(new JobBlock(mineur, 2, Material.IRON_ORE, item("iron"), 3, 180));
		blocs.registerContent(new JobBlock(mineur, 3, Material.GOLD_ORE, item("gold"), 5, 230));
		blocs.registerContent(new JobBlock(mineur, 4, Material.NETHER_QUARTZ_ORE, item("quartz"), 7, 3000));
		blocs.registerContent(new JobBlock(mineur, 5, Material.DIAMOND_ORE, item("diams"), 8, 380));
		
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
		crafts.registerContent(new JobCraft(mineur, 3, item("gold1", 2), 4, 	item("gold", 3), item("coal1", 3)));
		crafts.registerContent(new JobCraft(mineur, 3, item("gold2", 1), 12, 	item("gold1", 12), item("coal2", 3)));
		
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
		blocs.registerContent(new JobBlock(bucheron, 1, Material.OAK_LOG, item("oak"), 1, 60));
		blocs.registerContent(new JobBlock(bucheron, 2, Material.BIRCH_LOG, item("birch"), 2, 90));
		blocs.registerContent(new JobBlock(bucheron, 3, Material.SPRUCE_LOG, item("spruce"), 5, 180));
		blocs.registerContent(new JobBlock(bucheron, 4, Material.DARK_OAK_LOG, item("dark"), 7, 230));
		blocs.registerContent(new JobBlock(bucheron, 5, Material.ACACIA_LOG, item("acacia"), 9, 300));
		
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
	
	private void registerPaysan() {
		JobType agriculteur = new JobType(path, "agriculteur", JobCategory.RECOLTE, jobs);
		agriculteur.setIcon(1, Material.WOODEN_HOE);
		agriculteur.setIcon(2, Material.STONE_HOE);
		agriculteur.setIcon(3, Material.IRON_HOE);
		agriculteur.setIcon(4, Material.GOLDEN_HOE);
		agriculteur.setIcon(5, Material.DIAMOND_HOE);
		jobs.registerJob(agriculteur);
		
		JobBlockManager blocs = jobs.getBlocsManager();
		blocs.registerContent(new JobBlock(agriculteur, 1, Material.WHEAT, item("blé"), 1, 110));
		blocs.registerContent(new JobBlock(agriculteur, 2, Material.BEETROOTS, item("bettrave"), 2, 190));
		blocs.registerContent(new JobBlock(agriculteur, 2, Material.POTATOES, item("patate"), 2, 150));
		
		blocs.registerContent(new JobBlock(agriculteur, 3, Material.SUGAR_CANE, item("canna"), 3, 210));
		blocs.registerContent(new JobBlock(agriculteur, 3, Material.CARROT, item("carotte"), 3, 212));
		
		blocs.registerContent(new JobBlock(agriculteur, 4, Material.PUMPKIN, item("citrouille"), 5, 300));
		blocs.registerContent(new JobBlock(agriculteur, 5, Material.MELON, item("melon"), 8, 360));
		
		JobCraftsManager crafts = jobs.getCraftsManager();
		crafts.registerContent(new JobCraft(agriculteur, 1, item("farine1", 1), 4, 	item("blé", 6), item("cobble")));
		crafts.registerContent(new JobCraft(agriculteur, 1, item("pain1", 3), 6, 	item("farine1", 2), item("water")));
		crafts.registerContent(new JobCraft(agriculteur, 1, item("perlimpimpim", 1), 30, item("farine1", 20), item("water", 3), item("miel", 5), item("chair", 64), item("vésiculePokoi", 10)));
		
		crafts.registerContent(new JobCraft(agriculteur, 2, item("farine2", 1), 12, item("blé", 30), item("bettrave",5), item("patate", 5), item("silex")));
		crafts.registerContent(new JobCraft(agriculteur, 2, item("pain2", 3), 12, item("farine2", 2), item("water",2)));
		crafts.registerContent(new JobCraft(agriculteur, 2, item("bakedpotato", 32), 8, item("patate", 8), item("coal",1)));
		crafts.registerContent(new JobCraft(agriculteur, 2, item("soupe1", 4), 8, item("bettrave", 8), item("coal",1), item("stick", 10)));
		
		crafts.registerContent(new JobCraft(agriculteur, 3, item("sucre1", 1), 6, item("canna", 3), item("water"), item("silex")));
		crafts.registerContent(new JobCraft(agriculteur, 3, item("sucre2", 1), 18, item("canna", 32), item("water", 4), item("silex1")));
		crafts.registerContent(new JobCraft(agriculteur, 3, item("carottedorée", 5), 5, item("carotte", 2), item("birch2"), item("gold1")));
		crafts.registerContent(new JobCraft(agriculteur, 3, item("farine3", 1), 24, item("farine2", 2), item("carotte", 15), item("sucre2", 2)));
		crafts.registerContent(new JobCraft(agriculteur, 3, item("soupe2", 1), 5, item("farine2", 2), item("carotte", 15), item("sucre2", 2)));
		
		agriculteur.changeCraftGUI(Material.COMPOSTER, new JobCraftGUI(agriculteur));
	}
	
	private void registerPhytomancien() {
		JobType phyto = new JobType(path, "phytomancien", JobCategory.CRAFT, jobs);
		phyto.setIcon(1, Material.DEAD_BUSH);
		phyto.setIcon(2, Material.GLASS_BOTTLE);
		phyto.setIcon(3, Material.HONEY_BOTTLE);
		phyto.setIcon(4, Material.CAULDRON);
		phyto.setIcon(5, Material.BREWING_STAND);
		
		jobs.registerJob(phyto);

		JobCraftsManager crafts = jobs.getCraftsManager();

		crafts.registerContent(new JobCraft(phyto, 1, item("réceptacle", 1), 15, 		item("stone", 3), item("oak1", 4)));
		crafts.registerContent(new JobCraft(phyto, 1, item("bouteille", 1), 15, 		item("stone", 4), item("oak1", 3)));
		
		crafts.registerContent(new JobCraft(phyto, 1, item("potion_abso1", 1), 40, 		item("bouteille"), item("oak2", 16), item("iron1")));
		crafts.registerContent(new JobCraft(phyto, 1, item("potion_resis1", 1), 40, 	item("bouteille"), item("os1", 10)));
		crafts.registerContent(new JobCraft(phyto, 1, item("potion_jump1", 1), 40,	 	item("bouteille"), item("eye", 3), item("string", 5)));
		
		crafts.registerContent(new JobCraft(phyto, 2, item("source_vie", 1), 55, 		item("réceptacle"), item("vésiculePokoi", 5), item("chair", 3), item("perlimpimpim")));
		crafts.registerContent(new JobCraft(phyto, 2, item("source_sang", 1), 50, 		item("réceptacle"), item("iron2", 2), item("perlimpimpim")));
		crafts.registerContent(new JobCraft(phyto, 2, item("source_défense", 1), 50, 	item("réceptacle"), item("stone2"), item("perlimpimpim")));
		crafts.registerContent(new JobCraft(phyto, 2, item("source_terre", 1), 50, 		item("réceptacle"), item("birch2"), item("perlimpimpim")));
		crafts.registerContent(new JobCraft(phyto, 2, item("source_puissance", 1), 50, 	item("réceptacle"), item("farine3"), item("perlimpimpim")));
		crafts.registerContent(new JobCraft(phyto, 2, item("source_vitesse", 1), 50, 	item("réceptacle"), item("silex1"), item("perlimpimpim")));
		
		phyto.changeCraftGUI(Material.BREWING_STAND, new JobCraftGUI(phyto));
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
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiH"), 20, 	item("iron1", 5), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiC"), 35, 	item("iron1", 8), item("coal1", 2)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiL"), 30, 	item("iron1", 7), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiB"), 15, 	item("iron1", 4), item("coal1")));
		crafts.registerContent(new JobCraft(forgeron, 1, item("visiE"), 30, 	item("iron1", 3), item("coal1"), item("stick", 10)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("noviH"), 30, 	item("iron1", 12), item("coal1", 2)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("noviC"), 45, 	item("iron1", 16), item("coal1", 4)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("noviL"), 40, 	item("iron1", 13), item("coal1", 2)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("noviB"), 30, 	item("iron1", 10), item("coal1", 2)));
		crafts.registerContent(new JobCraft(forgeron, 1, item("noviE"), 45, 	item("iron1", 8), item("coal1", 2), item("stick", 20), item("silex1")));
		
		crafts.registerContent(new JobCraft(forgeron, 2, item("neopH"), 50, 	item("iron2", 18), item("coal2", 2), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 2, item("neopC"), 80, 	item("iron2", 22), item("coal2", 4), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 2, item("neopL"), 70, 	item("iron2", 20), item("coal2", 2), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 2, item("neopB"), 40, 	item("iron2", 16), item("coal2", 2), item("string")));
		crafts.registerContent(new JobCraft(forgeron, 2, item("neopE"), 60, 	item("iron2", 3), item("coal2", 2), item("string", 4), item("stick", 40)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("hache1"), 60, 	item("iron2", 15), item("coal2", 2), item("string"), item("stick", 20)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("pioche1"), 60, 	item("iron2", 15), item("coal2", 2), item("string"), item("stick", 20)));
		
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleH"), 90, 	item("iron2", 10), item("coal2", 4), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleC"), 110, 	item("iron2", 16), item("coal2", 8), item("spruce2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleL"), 100, 	item("iron2", 14), item("coal2", 7), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleB"), 80, 	item("iron2", 8), item("coal2", 4), item("spruce2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 2, item("peleE"), 90, 	item("iron2", 10), item("coal2", 5), item("oak2", 15), item("spruce2", 3)));
		
		crafts.registerContent(new JobCraft(forgeron, 3, item("voyaH"), 110, 	item("acier", 5), item("coal2", 10), item("spruce2", 5)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("voyaC"), 140, 	item("acier", 8), item("coal2", 16), item("spruce2", 8)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("voyaL"), 120, 	item("acier", 7), item("coal2", 14), item("spruce2", 7)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("voyaB"), 100, 	item("acier", 4), item("coal2", 12), item("spruce2", 6)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("voyaE"), 130, 	item("acier", 6), item("coal2", 18), item("oak2", 15), item("spruce2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("hache2"), 180, 	item("acier", 15), item("coal2", 4), item("string", 10), item("stick", 30)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("pioche2"), 180, 	item("acier", 15), item("coal2", 4), item("string", 10), item("stick", 30)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explH"), 130, 	item("gold1", 15), item("coal2", 15), item("dark2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explC"), 160, 	item("gold1", 24), item("coal2", 24), item("dark2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explL"), 150, 	item("gold1", 21), item("coal2", 21), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explB"), 120, 	item("gold1", 12), item("coal2", 12), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 3, item("explE"), 140, 	item("gold1", 18), item("coal2", 18), item("oak2", 20), item("birch2", 10), item("spruce2", 5), item("dark2", 1)));
		
		crafts.registerContent(new JobCraft(forgeron, 4, item("conqH"), 170, 	item("gold2", 12), item("coal2", 20), item("dark2", 2)));
		crafts.registerContent(new JobCraft(forgeron, 4, item("conqC"), 200, 	item("gold2", 20), item("coal2", 29), item("dark2", 4)));
		crafts.registerContent(new JobCraft(forgeron, 4, item("conqL"), 180, 	item("gold2", 17), item("coal2", 25), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 4, item("conqB"), 150, 	item("gold2", 9), item("coal2", 17), item("dark2", 3)));
		crafts.registerContent(new JobCraft(forgeron, 4, item("conqE"), 160, 	item("gold2", 14), item("coal2", 22), item("oak2", 40), item("birch2", 20), item("spruce2", 10), item("dark2", 5)));
		
		
		forgeron.changeCraftGUI(Material.ANVIL, new JobCraftGUI(forgeron));
	}
	
	protected String c() {
		return Rarity.COMMON.getColor();
	}
	protected String r() {
		return Rarity.RARE.getColor();
	}
}