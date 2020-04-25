package fr.jamailun.halystia;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.codingforcookies.armorequip.ArmorListener;

import fr.jamailun.halystia.bank.Banque;
import fr.jamailun.halystia.chunks.ChunkCreator;
import fr.jamailun.halystia.chunks.ChunkManager;
import fr.jamailun.halystia.commands.CommandClasse;
import fr.jamailun.halystia.commands.CommandCreateShop;
import fr.jamailun.halystia.commands.CommandEditChunks;
import fr.jamailun.halystia.commands.CommandEditMobs;
import fr.jamailun.halystia.commands.CommandEditNPC;
import fr.jamailun.halystia.commands.CommandEditQuests;
import fr.jamailun.halystia.commands.CommandEditTitles;
import fr.jamailun.halystia.commands.CommandGiveCanne;
import fr.jamailun.halystia.commands.CommandGivePotion;
import fr.jamailun.halystia.commands.CommandGiveSpell;
import fr.jamailun.halystia.commands.CommandIS;
import fr.jamailun.halystia.commands.CommandPing;
import fr.jamailun.halystia.commands.CommandPurge;
import fr.jamailun.halystia.commands.CommandQuests;
import fr.jamailun.halystia.commands.CommandReloadShops;
import fr.jamailun.halystia.commands.CommandSetChunk;
import fr.jamailun.halystia.commands.CommandSetJob;
import fr.jamailun.halystia.commands.CommandSetRoi;
import fr.jamailun.halystia.commands.CommandSetSpawner;
import fr.jamailun.halystia.commands.CommandSetTag;
import fr.jamailun.halystia.commands.CommandSetXp;
import fr.jamailun.halystia.commands.CommandSummonMob;
import fr.jamailun.halystia.commands.CommandTitle;
import fr.jamailun.halystia.commands.ModifyOeilAntiqueCommand;
import fr.jamailun.halystia.custom.boats.CustomBoatManager;
import fr.jamailun.halystia.custom.potions.PotionManager;
import fr.jamailun.halystia.enemies.mobSpawner.MobSpawnerManager;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.enemies.mobs.NaturalSpawnWorld;
import fr.jamailun.halystia.enemies.supermobs.SuperMobManager;
import fr.jamailun.halystia.events.ConsumeItemListener;
import fr.jamailun.halystia.events.EntityDamageOtherListener;
import fr.jamailun.halystia.events.EntityPickupItemListener;
import fr.jamailun.halystia.events.GUIListener;
import fr.jamailun.halystia.events.MobAggroListener;
import fr.jamailun.halystia.events.MobDeathListener;
import fr.jamailun.halystia.events.MobSpawnListener;
import fr.jamailun.halystia.events.NpcInteractionListener;
import fr.jamailun.halystia.events.PlayerBreakListener;
import fr.jamailun.halystia.events.PlayerDeathListener;
import fr.jamailun.halystia.events.PlayerDropItemListener;
import fr.jamailun.halystia.events.PlayerFishListener;
import fr.jamailun.halystia.events.PlayerInteractListener;
import fr.jamailun.halystia.events.PlayerJoinLeaveListener;
import fr.jamailun.halystia.events.PlayerMovementsListener;
import fr.jamailun.halystia.events.TchatListener;
import fr.jamailun.halystia.guis.ChooseClasseGui;
import fr.jamailun.halystia.jobs.JobManager;
import fr.jamailun.halystia.jobs.system.CacheMemory;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.traits.HalystiaRpgTrait;
import fr.jamailun.halystia.players.ClasseManager;
import fr.jamailun.halystia.players.SoulManager;
import fr.jamailun.halystia.quests.QuestManager;
import fr.jamailun.halystia.shops.ShopManager;
import fr.jamailun.halystia.shops.TradeManager;
import fr.jamailun.halystia.spells.SpellManager;
import fr.jamailun.halystia.sql.HalystiaDataBase;
import fr.jamailun.halystia.sql.temporary.FileSaver;
import fr.jamailun.halystia.sql.temporary.Saver;
import fr.jamailun.halystia.titles.TitleHolder;
import fr.jamailun.halystia.titles.TitlesManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public final class HalystiaRPG extends JavaPlugin {
	
	public final static String WORLD = "RolePlay";
	public final static String PREFIX = GOLD + "" + BOLD + "R" + GOLD + "ole" + BOLD + "P" + GOLD + "lay" + WHITE + " | ";
	//public final static String PREFIX = GOLD + "" + BOLD + "C" + GOLD + "lasse" +  YELLOW + " | ";
	
	public final static String PATH = "plugins/HalystiaRPG";
	public static final CharSequence DONJONS_WORLD_CONTAINS = "donjon_";
	
	private static HalystiaRPG instance;
	private CommandSender console;
	
	
	private Saver bdd;
	
	private ClasseManager classesMgr;
	private ShopManager shopMgr;
	private TradeManager tradeMgr;
	private MobManager mobMgr;
	private MobSpawnerManager spawnerMgr;
	private ChunkManager mobsChunksMgr;
	private ChunkCreator chunkCreator;
	private SoulManager soulMgr;
	private SpellManager spellMgr;
	private CustomBoatManager boatMgr;
	private PotionManager potionMgr;
	private SuperMobManager superMobMgr;
	private TitlesManager titleMgr;
	private Banque banque;
	private JobManager jobs;
	
	private QuestManager questsMgr;
	private NpcManager npcMgr;
	
	private ChooseClasseGui classeGui;
	private CacheMemory cache;
	
	@Override
	public void onLoad() {
		System.out.println("HalystiaRPG loaded.");
	}
	
	@Override
	public void onEnable() {
		//BASE
		console = Bukkit.getConsoleSender();
		console.sendMessage(PREFIX+ChatColor.YELLOW+"Activation de HalystiaRPG...");
		final long debut = System.currentTimeMillis();
		instance = this;
		cache = new CacheMemory();
		
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}
		
		
		try {
			CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(HalystiaRpgTrait.class));
		} catch(IllegalArgumentException e) {
			getLogger().log(Level.SEVERE, "Citizens already registered the Rpg Trait.");
		}
		
		try {

		//BDD
		// TODO repasser en ça bdd = connectBdd();
		bdd = new FileSaver(PATH, "playerData");
		if(!bdd.createPlayerTableClass()) {
			getLogger().severe("ERREUR FATALE !");
			getLogger().severe("ERREUR FATALE -> Impossible de créer la table des classes. Extinction du plugin.");
			getLogger().severe("ERREUR FATALE !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		//MANAGERS
		classesMgr = new ClasseManager(this, bdd);
		shopMgr = new ShopManager(PATH, "shopsData");
		tradeMgr = new TradeManager(PATH, "tradeData");
		mobMgr = new MobManager(PATH, "mobsData", this);
		spawnerMgr = new MobSpawnerManager(PATH, this);
		chunkCreator = new ChunkCreator(PATH, "chuncksType");
		mobsChunksMgr = new ChunkManager(PATH, "mobsSpawn");
		soulMgr = new SoulManager(this);
		spellMgr = new SpellManager(this);
		boatMgr = new CustomBoatManager();
		potionMgr = new PotionManager();
		superMobMgr = new SuperMobManager(PATH, "superMobs");
		npcMgr = new NpcManager(PATH + "/npcs", PATH + "/npcs-textures.yml", this);
		questsMgr = new QuestManager(PATH+"/quests", this, npcMgr, mobMgr);
		titleMgr = new TitlesManager(PATH);
		banque = new Banque(PATH+"/banque");
		jobs = new JobManager(PATH+"/jobs", this);
		
		npcMgr.verifyQuests(questsMgr);
		
		
		new NaturalSpawnWorld(this, mobMgr, mobsChunksMgr, WORLD);
		
		//EVENTS
		Bukkit.getPluginManager().registerEvents(new ArmorListener(), this); //génère des ArmorEquiEvent !
		new PlayerJoinLeaveListener(this);
		new PlayerDropItemListener(this);
		new GUIListener(this);
		new NpcInteractionListener(this);
		new TchatListener(this);
		new PlayerMovementsListener(this);
		new PlayerDeathListener(this);
		new PlayerInteractListener(this);
		new PlayerBreakListener(this, jobs);
		new EntityDamageOtherListener(this);
		new ConsumeItemListener(this);
		
		new MobAggroListener(this);
		
		new MobDeathListener(this);
		new MobSpawnListener(this);
		new EntityPickupItemListener(this);
		new PlayerFishListener(this);
		
		//GUIS
		classeGui = new ChooseClasseGui(this);
		
		//COMMANDS
		getCommand("classe").setExecutor(new CommandClasse());
		getCommand("quests").setExecutor(new CommandQuests());
		getCommand("titles").setExecutor(new CommandTitle());
		getCommand("ping").setExecutor(new CommandPing());
		
		getCommand("create-shop-classe").setExecutor(new CommandCreateShop(this));
		getCommand("reload-shop-classe").setExecutor(new CommandReloadShops(this));
		getCommand("is").setExecutor(new CommandIS(this));
		getCommand("purge").setExecutor(new CommandPurge(this));
		
		getCommand("edit-mobs").setExecutor(new CommandEditMobs(this));
		getCommand("edit-chunks").setExecutor(new CommandEditChunks(this));
		
		getCommand("set-roi").setExecutor(new CommandSetRoi(this));
		getCommand("set-xp").setExecutor(new CommandSetXp(this));
		
		getCommand("give-spell").setExecutor(new CommandGiveSpell(this));
		getCommand("give-potion").setExecutor(new CommandGivePotion(this));
		
		new CommandEditNPC(this, npcMgr);
		new CommandEditQuests(this, npcMgr, questsMgr, mobMgr);
		new CommandSetTag(this, bdd);
		new CommandSetChunk(this);
		new CommandSummonMob(this, mobMgr);
		new CommandEditTitles(this, titleMgr);
		new CommandGiveCanne(this);
		new CommandSetSpawner(this, mobMgr, spawnerMgr);
		new CommandSetJob(this, jobs);
		
		if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			PlaceholderAPI.registerExpansion(new TitleHolder(titleMgr, bdd, classesMgr));
		} else {
			getLogger().log(Level.WARNING, "PlaceHolderAPI not found or not enabled");
		}
		
		
		ModifyOeilAntiqueCommand moaCmd = new ModifyOeilAntiqueCommand(this);
		getCommand("set-oeil-antique").setExecutor(moaCmd);
		getCommand("remove-oeil-antique").setExecutor(moaCmd);
		
		try {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.getWorld().getName().equals(WORLD)) {
					bdd.addPlayerProfile(player);
					classesMgr.playerConnects(player);
				}
			}
		} catch (NullPointerException ee) {
			Bukkit.getLogger().warning("Error during online players initialisation.");
		}
		
		soulMgr.startClock();
		superMobMgr.initAllSuperMobs();
		
		} catch(NullPointerException ee) {
			ee.printStackTrace();
		}
		console.sendMessage(PREFIX+ChatColor.GREEN+"Activation de HalystiaRPG terminée en " + (System.currentTimeMillis() - debut) + "ms.");
	}
	
	@Override
	public void onDisable() {
		console.sendMessage(PREFIX + ChatColor.YELLOW + "Désactivation de HalystiaRPG...");
		final long debut = System.currentTimeMillis();
		classesMgr.saveData(false);
		jobs.saveJobs();
		shopMgr.despawnAll();
		mobMgr.purge();
		spellMgr.getInvocationsManager().purge();
		superMobMgr.purge();
		cache.applyCache();
		console.sendMessage(PREFIX + ChatColor.GREEN + "Désactivation terminée en " + (System.currentTimeMillis() - debut) + "ms.");
	}
	
	@SuppressWarnings("unused")
	private HalystiaDataBase connectBdd() {
		String server = "";
		String dataBase = "";
		String user = "";
		String password = "";
		return new HalystiaDataBase(this, server, dataBase, user, password, true);
	}

	public CommandSender getConsole() {
		return console;
	}
	
	public static HalystiaRPG getInstance() {
		return instance;
	}
	
	public static boolean isInRpgWorld(Entity e) {
		return e.getWorld().getName().equals(WORLD);
	}
	
	public static boolean isRpgWorld(World w) {
		return w.getName().equals(WORLD);
	}

	public Saver getDataBase() {
		return bdd;
	}

	public ChunkCreator getChunkCreator() {
		return chunkCreator;
	}
	
	public ClasseManager getClasseManager() {
		return classesMgr;
	}
	
	public JobManager getJobManager() {
		return jobs;
	}
	
	public ShopManager getShopManager() {
		return shopMgr;
	}
	
	public SuperMobManager getSuperMobManager() {
		return superMobMgr;
	}
	
	public TradeManager getTradeManager() {
		return tradeMgr;
	}
	
	public CustomBoatManager getBoatManager() {
		return boatMgr;
	}
	
	public TitlesManager getTitlesManager() {
		return titleMgr;
	}
	
	public ChooseClasseGui getChooseClasseGui() {
		return classeGui;
	}
	
	public PotionManager getPotionManager() {
		return potionMgr;
	}
	
	public MobSpawnerManager getMobSpawnerManager() {
		return spawnerMgr;
	}
	
	public MobManager getMobManager() {
		return mobMgr;
	}

	public ChunkManager getSpawnChunkManager() {
		return mobsChunksMgr;
	}
	
	public SoulManager getSoulManager() {
		return soulMgr;
	}

	public Banque getBanque() {
		return banque;
	}
	
	public SpellManager getSpellManager() {
		return spellMgr;
	}

	public QuestManager getQuestManager() {
		return questsMgr;
	}
	
	public NpcManager getNpcManager() {
		return npcMgr;
	}
	
	public CacheMemory getCache() {
		return cache;
	}
}