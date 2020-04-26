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
import fr.jamailun.halystia.commands.CommandEditDonjons;
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
import fr.jamailun.halystia.donjons.DonjonManager;
import fr.jamailun.halystia.donjons.util.CommandDonjonPorte;
import fr.jamailun.halystia.donjons.util.CommandJoinDonjon;
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

/**
 * The core of HalystiaRPG.
 * <br />Get the instance with the static {@link #getInstance()} method.
 * <br />Never construct an other.
 * @author jamailun
 * @see #getSpellManager() #getSpellManager() to modify the spells
 * @see #getClasseManager() #getClasseManager() to get the players data
 */
public final class HalystiaRPG extends JavaPlugin {
	
	//TODO make it modifiable.
	/**
	 * The world where everything is considered to be part of the RPG.
	 */
	public final static String WORLD = "RolePlay";
	
	/**
	 * The prefix of almost all plugin messages.
	 */
	public final static String PREFIX = GOLD + "" + BOLD + "R" + GOLD + "ole" + BOLD + "P" + GOLD + "lay" + WHITE + " | ";
	
	// Data managig things
	public final static String PATH = "plugins/HalystiaRPG";
	public static final String DONJONS_WORLD_CONTAINS = "donjon_";
	
	// static access.
	private static HalystiaRPG instance;
	
	// bdd
	private Saver bdd;
	
	//Managers
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
	private DonjonManager donjonsMgr;
	private QuestManager questsMgr;
	private NpcManager npcMgr;
	
	//Usefull things
	private ChooseClasseGui classeGui;
	private CacheMemory cache;
	private CommandSender console;
	
	@Override
	public void onLoad() {/* Yeah, nothing to do here */}
	
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
		donjonsMgr = new DonjonManager(PATH+"/donjons");
		
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
		new CommandEditNPC(this, npcMgr);
		new CommandEditQuests(this, npcMgr, questsMgr, mobMgr);
		new CommandEditTitles(this, titleMgr);
		new CommandEditDonjons(this, donjonsMgr);
		
		getCommand("set-roi").setExecutor(new CommandSetRoi(this));
		new CommandSetXp(this);
		new CommandSetTag(this, bdd);
		new CommandSetChunk(this);
		new CommandSetSpawner(this, mobMgr, spawnerMgr);
		new CommandSetJob(this, jobs);
		
		getCommand("give-spell").setExecutor(new CommandGiveSpell(this));
		getCommand("give-potion").setExecutor(new CommandGivePotion(this));
		new CommandSummonMob(this, mobMgr);
		new CommandGiveCanne(this);
		
		getCommand("joindonjon").setExecutor(new CommandJoinDonjon(this));
		getCommand("donjonPorte").setExecutor(new CommandDonjonPorte(this));
		
		
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

	/**
	 * Get the console to send coloured messages.
	 * @return a CommandSender used by Bukkit.
	 */
	public CommandSender getConsole() {
		return console;
	}
	
	/**
	 * Get the instance of HalystiaRPG's API.
	 * @return the instance of HalystiaRPG used by Bukkit.
	 */
	public static HalystiaRPG getInstance() {
		return instance;
	}
	
	/**
	 * Check if a specific entity is in the RPG world.
	 * @param e Entity to check.
	 * @return true if it's the case.
	 * @see #isRpgWorld(World)
	 */
	public static boolean isInRpgWorld(Entity e) {
		return isRpgWorld(e.getWorld());
	}
	
	/**
	 * Check if a specific world is the RPG world.
	 * @param w World to check.
	 * @return true if it's the case.
	 * @see #isInRpgWorld(Entity)
	 */
	public static boolean isRpgWorld(World w) {
		return w.getName().equals(WORLD) || w.getName().startsWith(DONJONS_WORLD_CONTAINS);
	}
	
	/**
	 * Get the donjon manager of the plugin
	 * @return the {@link fr.jamailun.halystia.donjons.DonjonManager DonjonManager} of the plugin.
	 */
	public DonjonManager getDonjonManager() {
		return donjonsMgr;
	}

	/**
	 * Get the plugin's database.
	 * @return the current {@link fr.jamailun.halystia.sql.temporary.Saver bdd} of the plugin.
	 * @see #setDataBase(Saver)
	 */
	public Saver getDataBase() {
		return bdd;
	}
	
	/**
	 * Change the current database system. Use SQL if you need to.
	 * @param saver new Saver manager.
	 */
	public void setDataBase(Saver saver) {
		this.bdd = saver;
		console.sendMessage(PREFIX + ChatColor.YELLOW + "BDD has been updated."); 
	}

	/**
	 * Get the chunk managerr of the server.
	 * @return the {@link fr.jamailun.halystia.chunks.ChunkCreator ChunkCreator} of the plugin.
	 */
	public ChunkCreator getChunkCreator() {
		return chunkCreator;
	}
	
	/**
	 * Get the player data handler of the server.
	 * @return the {@link fr.jamailun.halystia.players.ClasseManager ClasseManager} of the plugin.
	 */
	public ClasseManager getClasseManager() {
		return classesMgr;
	}
	
	/**
	 * Get the job manager of the server.
	 * @return the {@link fr.jamailun.halystia.jobs.JobManager JobManager} of the plugin.
	 */
	public JobManager getJobManager() {
		return jobs;
	}
	
	/**
	 * Get the shop manager of the server.
	 * @return the {@link fr.jamailun.halystia.shops.ShopManager ShopManager} of the plugin.
	 */
	public ShopManager getShopManager() {
		return shopMgr;
	}
	
	/**
	 * Get the superobs manager of the server.
	 * @return the {@link fr.jamailun.halystia.enemies.supermobs.SuperMobManager SuperMobManager} of the plugin.
	 */
	public SuperMobManager getSuperMobManager() {
		return superMobMgr;
	}
	
	/**
	 * Get the trade manager & registry of the server.
	 * @return the {@link fr.jamailun.halystia.shops.TradeManager TradeManager} of the plugin.
	 */
	public TradeManager getTradeManager() {
		return tradeMgr;
	}
	
	/**
	 * Get the potions manager of the server.
	 * @return the {@link fr.jamailun.halystia.custom.boats.CustomBoatManager CustomBoatManager} of the plugin.
	 */
	public CustomBoatManager getBoatManager() {
		return boatMgr;
	}
	
	/**
	 * Get the potions manager of the server.
	 * @return the {@link fr.jamailun.halystia.titles.TitlesManager TitleManager} of the plugin.
	 */
	public TitlesManager getTitlesManager() {
		return titleMgr;
	}
	
	/**
	 * Get the superobs manager of the server.
	 * @return the {@link fr.jamailun.halystia.guis.ChooseClasseGui ChooseClasseGui} of the plugin.
	 */
	public ChooseClasseGui getChooseClasseGui() {
		return classeGui;
	}
	
	/**
	 * Get the potions manager of the server.
	 * @return the {@link fr.jamailun.halystia.custom.potions.PotionManager PotionManager} of the plugin.
	 */
	public PotionManager getPotionManager() {
		return potionMgr;
	}
	
	/**
	 * Get the chunks manager of the server.
	 * @return the {@link fr.jamailun.halystia.enemies.mobSpawner.MobSpawnerManager MobSpawnerManager} of the plugin.
	 */
	public MobSpawnerManager getMobSpawnerManager() {
		return spawnerMgr;
	}
	
	/**
	 * Get the chunks manager of the server.
	 * @return the {@link fr.jamailun.halystia.enemies.mobs.MobManager MobManager} of the plugin.
	 */
	public MobManager getMobManager() {
		return mobMgr;
	}

	/**
	 * Get the chunks manager of the server.
	 * @return the {@link fr.jamailun.halystia.chunks.ChunkManager ChunkManager} of the plugin.
	 */
	public ChunkManager getSpawnChunkManager() {
		return mobsChunksMgr;
	}
	
	/**
	 * Get the souls manager of the server.
	 * @return the {@link fr.jamailun.halystia.players.SoulManager SoulManager} of the plugin.
	 */
	public SoulManager getSoulManager() {
		return soulMgr;
	}

	/**
	 * Get the private bank accounts manager of the server.
	 * @return the {@link fr.jamailun.halystia.bank.Banque Banque} of the plugin.
	 */
	public Banque getBanque() {
		return banque;
	}
	
	/**
	 * Get the spell registry and manager of the server.
	 * @return the {@link fr.jamailun.halystia.spells.SpellManager SpellManager} of the plugin.
	 */
	public SpellManager getSpellManager() {
		return spellMgr;
	}

	/**
	 * Get the quests manager of the server.
	 * @return the {@link fr.jamailun.halystia.quests.QuestManager QuestManager} of the plugin.
	 */
	public QuestManager getQuestManager() {
		return questsMgr;
	}
	
	/**
	 * Get the NPC manager of the server. Uses Citizens.
	 * @return the {@link fr.jamailun.halystia.npcs.NpcManager NpcManager} of the plugin.
	 */
	public NpcManager getNpcManager() {
		return npcMgr;
	}
	
	/**
	 * Get the Blocks cache of the server.
	 * @return the {@link fr.jamailun.halystia.jobs.system.CacheMemory CacheMemory} of the plugin.
	 */
	public CacheMemory getCache() {
		return cache;
	}
}