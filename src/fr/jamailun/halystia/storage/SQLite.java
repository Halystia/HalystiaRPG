package fr.jamailun.halystia.storage;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.royaumes.Royaume;
import fr.jamailun.halystia.sql.temporary.Saver;
import fr.jamailun.halystia.storage.common.Database;
import fr.jamailun.halystia.titles.Title;

public class SQLite extends Database implements Saver {

	private final static String UUID = "uuid";
	private final static String CLASSE_ID = "classeId";
	private final static String CLASSE_XP = "classeXp";
	private final static String ROYAUME = "kingOf";
	private final static String SOULS_NB = "nbSouls";
	private final static String SOULS_LAST = "lastSoul";
	//private final static String QUESTS = "quests";
	private final static String TAGS = "tags";
	private final static String TITLE = "title";
	private final static String SPAWN = "spawn";
	
	public SQLite(HalystiaRPG plugin, String dbName){
		super(plugin, dbName);
	}

	public static final String CREATE_TABLE = 
			"CREATE TABLE IF NOT EXISTS playerData (" + // make sure to put your table name in here too.
			"`"+UUID+"` varchar(32) NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
			"`"+CLASSE_ID+"` INT NOT NULL," +
			"`"+CLASSE_XP+"` INT NOT NULL," +
			"`"+SOULS_NB+"` TINYINT NOT NULL," +
			"`"+SOULS_LAST+"` BIGINT NOT NULL," +
			"`"+ROYAUME+"` TEXT," +
			"`"+TAGS+"` TEXT," +
			"`"+TITLE+"` TEXT," +
			"`"+SPAWN+"` TEXT," +
			"PRIMARY KEY (`"+UUID+"`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
			");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.

	public static final String INSERT_PLAYER = "";
	
	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(CREATE_TABLE);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean createPlayerTableClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPlayerProfile(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PlayerData getPlayerData(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePlayerClasse(Player player, Classe classe) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateXp(Player player, int exp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveAll(Collection<PlayerData> players) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getSpawnLocation(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSpawnLocation(Player player, Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Royaume getKingdom(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setRoi(Royaume r, Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLastSoulRefresh(Player p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHowManySouls(Player p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean refreshSoul(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean looseSoul(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Quest> getAllQuests(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStepInQuest(Player p, Quest quest) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataInQuest(Player p, Quest quest) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateStepInQuest(Player p, Quest quest, int step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDataInQuest(Player p, Quest quest, int data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Quest> getOnGoingQuests(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<QuestStep> getOnGoingQuestSteps(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean playerHasTag(Player p, String tag) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addTagToPlayer(Player p, String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTagFromPlayer(Player p, String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getTagsOfPlayer(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentTitleOfPlayer(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentTitleOfPlayer(Player p, Title title) {
		// TODO Auto-generated method stub
		
	}


	// SQL creation stuff, You can leave the blow stuff untouched.
	
}