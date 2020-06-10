package fr.jamailun.halystia.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.players.SkillSet;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.royaumes.Royaume;
import fr.jamailun.halystia.sql.temporary.DataHandler;
import fr.jamailun.halystia.titles.Title;

public class HalystiaDataBase extends DataBase implements DataHandler {
	
	//TODO déplacer la config
	public static final String COL_UUID = "uuid";
	public static final String COL_CLASS_ID = "classeId";
	public static final String COL_CLASS_EXP = "classeXp";
	public static final String COL_KING = "kingOf";
	public static final String COL_NB_SOULS = "nbSouls";
	public static final String COL_LAST_REFRESH = "lastRefresh";
	
	public static final String TABLE_PLAYERS_CLASSE = "rpgPlayersClass";
	// ----------------------
	
	
	public HalystiaDataBase(HalystiaRPG api, String server, String dataBase, String user, String password, boolean debug) {
		super(api, server, dataBase, user, password, debug);
	}
	
	public boolean createPlayerTableClass() {
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return false;
			}
			conn.createStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS " + TABLE_PLAYERS_CLASSE + " ( "
					+ COL_UUID + " VARCHAR(50) PRIMARY KEY, "
					+ COL_CLASS_ID + " INT NOT NULL (CHECK >= 0), "
					+ COL_CLASS_EXP + " INT NOT NULL (CHECK >= 0), "
					+ COL_KING + " TEXT, "
					+ COL_NB_SOULS + " INT NOT NULL (CHECK >= 0), "
					+ COL_LAST_REFRESH + " LONG NOT NULL"
					+ ")"
			);
			
			// 1 : vérifier que la table n'existe pas
			ResultSet rs = conn.createStatement().executeQuery("SHOW TABLES LIKE '"+TABLE_PLAYERS_CLASSE+"'");
			if(rs.next()) {
				if(debug)
					api.getConsole().sendMessage(ChatColor.GREEN + "La table de classe des joueurs existe !");
				return true;
			}
			
			api.getConsole().sendMessage(ChatColor.RED + "Impossible de créer la table des joueurs !");
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addPlayerProfile(Player player) {
		String uuid = player.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return false;
			}
			
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM "+TABLE_PLAYERS_CLASSE+" WHERE "+COL_UUID+"='"+uuid+"'");
			if(rs.next()) {
				if(debug)
					api.getConsole().sendMessage(ChatColor.GREEN + "Le joueur existe déjà !");
				return true;
			}
			
			conn.createStatement().executeUpdate(
					"INSERT INTO " + TABLE_PLAYERS_CLASSE + " ( "
					+ COL_UUID + ", "
					+ COL_CLASS_ID + ", "
					+ COL_CLASS_EXP + ", "
					+ COL_KING + ", "
					+ COL_NB_SOULS + ", "
					+ COL_LAST_REFRESH + " "
					+ ") VALUES ( '" + uuid + "', 0, 0, '', 3, "+System.currentTimeMillis()+")"
			);
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public PlayerData getPlayerData(Player player) {
		String uuid = player.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return null;
			}
			
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM "+TABLE_PLAYERS_CLASSE+" WHERE "+COL_UUID+"='"+uuid+"'");
			if( ! rs.next()) {
				if(debug)
					api.getConsole().sendMessage(ChatColor.RED + "Le joueur n'existe pas !");
				return null;
			}
			
			int classeId = rs.getInt(COL_CLASS_ID);
			int exp = rs.getInt(COL_CLASS_EXP);
			Classe classe = Classe.getClasseWithId(classeId);
			
			return new PlayerData(classe, exp, player, 0, new SkillSet());
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean changePlayerClasse(Player player, Classe classe) {
		String uuid = player.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return false;
			}
			
			conn.createStatement().executeUpdate(
				"UPDATE "+TABLE_PLAYERS_CLASSE+" SET "+COL_CLASS_ID+"=" + classe.getClasseId() + ", "+COL_CLASS_EXP+"=0 "
				+ "WHERE "+COL_UUID+"='"+uuid+"'"
			);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateXp(Player player, int exp) {
		String uuid = player.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return false;
			}
			
			conn.createStatement().executeUpdate(
				"UPDATE "+TABLE_PLAYERS_CLASSE+" SET "+COL_CLASS_EXP+"=" + exp
				+ "WHERE "+COL_UUID+"='"+uuid+"'"
			);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Royaume getKingdom(Player p) {
		String uuid = p.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return null;
			}
			
			ResultSet rs = conn.createStatement().executeQuery("SELECT "+COL_KING+" FROM "+TABLE_PLAYERS_CLASSE+" WHERE "+COL_UUID+"='"+uuid+"'");
			if( ! rs.next())
				return null;
			
			Royaume r = Royaume.valueOf( rs.getString(COL_KING));
			return r;
			
		} catch (SQLException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean setRoi(Royaume r, Player p) {
		String uuid = p.getUniqueId().toString();
		try {
			if(!isConnected()) {
				api.getConsole().sendMessage(ChatColor.RED + "BDD non connectée.");
				return false;
			}
			
			conn.createStatement().executeUpdate(
					"UPDATE "+TABLE_PLAYERS_CLASSE+" SET "+COL_KING+"=''"
					+ "WHERE "+COL_KING+"='"+r.toString()+"'"
			);
			
			conn.createStatement().executeUpdate(
				"UPDATE "+TABLE_PLAYERS_CLASSE+" SET "+COL_KING+"=" + r.toString()
				+ "WHERE "+COL_UUID+"='"+uuid+"'"
			);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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
	public Set<Quest> getOnGoingQuests(Player p) {
		// TODO Auto-generated method stub
		return null;
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

}
