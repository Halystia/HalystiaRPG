package fr.jamailun.halystia.sql.temporary;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.royaumes.Royaume;
import fr.jamailun.halystia.titles.Title;

public interface DataHandler {
	
	public boolean createPlayerTableClass();
	
	public boolean addPlayerProfile(Player player);
	
	public PlayerData getPlayerData(Player player);
	
	public boolean changePlayerClasse(Player player, Classe classe);

	@Deprecated
	public boolean updateXp(Player player, int exp);
	
	public void saveAll(Collection<PlayerData> players);
	
	public Location getSpawnLocation(Player player);
	
	public void updateSpawnLocation(Player player, Location location);
	
	/**
	 * @return Le royaume dont le joueur est ROI.
	 */
	public Royaume getKingdom(Player p);
	
	public boolean setRoi(Royaume r, Player p);
	
	/**
	 * @return le temps écoulé depuis le dernier refresh EN SECONDES
	 */
	public int getLastSoulRefresh(Player p);
	
	public int getHowManySouls(Player p);
	
	public boolean refreshSoul(Player p);
	
	public boolean looseSoul(Player p);
	
	// QUETES
	public Set<Quest> getAllQuests(Player p);
	
	public int getStepInQuest(Player p, Quest quest);
	public int getDataInQuest(Player p, Quest quest);
	public void updateStepInQuest(Player p, Quest quest, int step);
	public void updateDataInQuest(Player p, Quest quest, int data);
	
	public Set<Quest> getOnGoingQuests(Player p);
	public Set<QuestStep> getOnGoingQuestSteps(Player p);
	
	public boolean playerHasTag(Player p, String tag);
	public void addTagToPlayer(Player p, String tag);
	public void removeTagFromPlayer(Player p, String tag);
	public List<String> getTagsOfPlayer(Player p);
	
	public String getCurrentTitleOfPlayer(Player p);
	public void setCurrentTitleOfPlayer(Player p, Title title);
	
	//KARMA
	public int getKarma(Player p);
	public void setKarma(Player p, int karma);
	
}