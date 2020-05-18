package fr.jamailun.halystia.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.royaumes.Royaume;
import fr.jamailun.halystia.sql.temporary.DataHandler;
import fr.jamailun.halystia.storage.common.Database;
import fr.jamailun.halystia.storage.structure.TablePlayersStructure;
import fr.jamailun.halystia.storage.structure.TableQuestStructure;
import fr.jamailun.halystia.titles.Title;

@SuppressWarnings("unused")
public class SQLite extends Database implements DataHandler {
	
	private final static int FIRST_COL = 1;
	private final TablePlayersStructure playersTable;
	
	public SQLite(HalystiaRPG plugin, String dbName){
		super(plugin, dbName);
		playersTable = new TablePlayersStructure();
	}
	
	public void load() {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(playersTable.getTableCreationString());
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean createPlayerTableClass() {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			return s.executeUpdate(playersTable.getTableCreationString()) == 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean addPlayerProfile(Player player) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			return s.executeUpdate(playersTable.getStringInsertion(player)) == 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public PlayerData getPlayerData(Player player) {
		int id = getClasseID(player);
		Classe classe = Classe.getClasseWithId(id);
		int exp = getRawExp(player);
		return new PlayerData(classe, exp, player);
	}
	
	private int getRawExp(Player player) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(player, TablePlayersStructure.COL_CLASSE_XP));
			return r.getInt(FIRST_COL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int getClasseID(Player player) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(player, TablePlayersStructure.COL_CLASSE_ID));
			return r.getInt(FIRST_COL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean changePlayerClasse(Player player, Classe classe) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			int r1 = s.executeUpdate(playersTable.getStringUpdateColumn(player, TablePlayersStructure.COL_CLASSE_ID, Classe.NONE.getClasseId()));
			int r2 = s.executeUpdate(playersTable.getStringUpdateColumn(player, TablePlayersStructure.COL_CLASSE_XP, 0));
			return r1 == 0 && r2 == 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateXp(Player player, int exp) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			return 0 == s.executeUpdate(playersTable.getStringUpdateColumn(player, TablePlayersStructure.COL_CLASSE_XP, exp));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void saveAll(Collection<PlayerData> players) {
		players.forEach(playerData -> {
			updateXp(playerData.getPlayer(), playerData.getExpAmount());
		});
	}

	@Override
	public Location getSpawnLocation(Player player) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(player, TablePlayersStructure.COL_SPAWN));
			String locRaw = r.getString(1);
			return (Location) playersTable.deserialize(locRaw, TablePlayersStructure.COL_SPAWN);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateSpawnLocation(Player player, Location location) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			s.executeUpdate(playersTable.getStringUpdateColumn(player, TablePlayersStructure.COL_SPAWN, location));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	@Override
	public Royaume getKingdom(Player p) {
		// TODO getKingdom
		return null;
	}

	@Override
	public boolean setRoi(Royaume r, Player p) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			//TODO il reste des trucs à fare pour changer de roi.
			return 0 == s.executeUpdate(playersTable.getStringUpdateColumn(p, TablePlayersStructure.COL_ROYAUME, r.getName()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getLastSoulRefresh(Player p) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(p, TablePlayersStructure.COL_SOULS_LAST));
			final long current = System.currentTimeMillis();
			long last = r.getLong(FIRST_COL);
			long elapsed = current - last;
			float secs = elapsed / 1000f;
			return (int) secs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getHowManySouls(Player p) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(p, TablePlayersStructure.COL_SOULS_NB));
			return r.getInt(FIRST_COL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public boolean refreshSoul(Player p) {
		int current = getHowManySouls(p);
		current++;
		return updateSouls(p, current);
	}

	@Override
	public boolean looseSoul(Player p) {
		int current = getHowManySouls(p);
		current--;
		return updateSouls(p, current);
	}
	
	private boolean updateSouls(Player p, int current) {
		if(current > 3)
			current = 3;
		if(current < 0)
			current = 1; //pour des cas aussi étranges que particuliers
		
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			s.executeUpdate(playersTable.getStringUpdateColumn(p, TablePlayersStructure.COL_SOULS_NB, current));
			s.executeUpdate(playersTable.getStringUpdateColumn(p, TablePlayersStructure.COL_SOULS_LAST, System.currentTimeMillis()));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Set<Quest> getAllQuests(Player p) {
		
		for(Quest quest : HalystiaRPG.getInstance().getQuestManager().getAllQuests()) {
			
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private TableQuestStructure getQuestStructure(Quest quest) {
		return new TableQuestStructure(quest);
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
		return getTagsOfPlayer(p).contains(tag);
	}

	@Override
	public void addTagToPlayer(Player p, String tag) {
		List<String> tags = getTagsOfPlayer(p);
		if(tags.contains(tag))
			return;
		tags.add(tag);
		exportTags(p, tags);
	}

	@Override
	public void removeTagFromPlayer(Player p, String tag) {
		List<String> tags = getTagsOfPlayer(p);
		if( ! tags.contains(tag) )
			return;
		tags.remove(tag);
		exportTags(p, tags);
	}
	
	private void exportTags(Player p, List<String> tags) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			s.executeUpdate(playersTable.getStringUpdateColumn(p, TablePlayersStructure.COL_TAGS_LIST, tags));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTagsOfPlayer(Player p) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(p, TablePlayersStructure.COL_TAGS_LIST));
			String listRaw = r.getString(FIRST_COL);
			return (List<String>) playersTable.deserialize(listRaw, TablePlayersStructure.COL_TAGS_LIST);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public String getCurrentTitleOfPlayer(Player p) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			ResultSet r = s.executeQuery(playersTable.getStringGetColumn(p, TablePlayersStructure.COL_TAGS_SELECTED));
			return r.getString(FIRST_COL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setCurrentTitleOfPlayer(Player p, Title title) {
		Validate.isTrue(isConnected(), "No connection !");
		connection = getSQLConnection();
		try(Statement s = connection.createStatement()) {
			s.executeUpdate(playersTable.getStringUpdateColumn(p, TablePlayersStructure.COL_TAGS_SELECTED, title.getTag()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}