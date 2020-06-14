package fr.jamailun.halystia.quests.players;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.QuestManager;
import fr.jamailun.halystia.quests.players.QuestState.QuestStatus;
import fr.jamailun.halystia.quests.steps.QuestStep;

/*
 * INstance who handle & store all quests progression of a Player.
 */
public class QuestsAdvancement {
	
	private Set<QuestState> quests;
	private final UUID uuid;
	
	private final QuestManager qm;
	
	public QuestsAdvancement(UUID uuid, Set<QuestState> quests) {
		this.uuid = uuid;
		this.qm = HalystiaRPG.getInstance().getQuestManager();
		quests = new HashSet<>(quests);
	}
	
	public Set<Quest> getAllQuests() {
		Set<Quest> set = new HashSet<>();
		for(Quest quest : qm.getAllQuests()) {
			for(QuestState state : quests) {
				if(state.getQuestID().equals(quest.getID())) {
					set.add(quest);
				}
			}
		}
		return set;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public boolean owns(Player player) {
		return player.getUniqueId().equals(uuid);
	}
	
	public int getStepInQuest(Quest quest) {
		try {
			return quests.stream()
					.filter(state -> state.getQuestID().equals(quest.getID()))
					.findFirst().get()
					.getStep();
		} catch(NoSuchElementException e) {
			return -1;
		}
	}
	
	public int getDataInQuest(Quest quest) {
		try {
			return quests.stream()
					.filter(state -> state.getQuestID().equals(quest.getID()))
					.findFirst().get()
					.getData();
		} catch(NoSuchElementException e) {
			return -1;
		}
	}
	
	public void updateStepInQuest(Quest quest, int step) {
		try {
			quests.stream()
				.filter(state -> state.getQuestID().equals(quest.getID()))
				.findFirst().get()
				.updateStep(step);;
		} catch(NoSuchElementException ignored) {
			quests.add(new QuestState(quest.getID(), step));
		}
	}
	
	public void updateDataInQuest(Quest quest, int data) {
		try {
			quests.stream()
				.filter(state -> state.getQuestID().equals(quest.getID()))
				.findFirst().get()
				.updateData(data);
		} catch(NoSuchElementException ignored) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : player don't have quest " + quest.getID() + ". Step back to 0.");
			quests.add(new QuestState(quest.getID(), 0, data));
		}
	}
	
	public Set<Quest> getOnGoingQuests() {
		Set<Quest> set = new HashSet<>();
		for(QuestState state : quests) {
			if(state.isFinished())
				continue;
			for(Quest quest : qm.getAllQuests()) {
				if(state.getQuestID().equals(quest.getID())) {
					set.add(quest);
				}
			}
		}
		return set;
	}
	
	public Set<QuestStep> getOnGoingQuestSteps() {
		Set<QuestStep> steps = new HashSet<>();
		for(Quest quest : getAllQuests()) {
			int step = getStepInQuest(quest);
			if(step < quest.getHowManySteps() && step != -1)
				steps.add(quest.getStep(step));
		}
		return steps;
	}

	public void questRemoved(String id) {
		quests.removeIf(q -> q.getQuestID().equals(id));
	}

	public void questAdded(QuestState questState) {
		quests.add(questState);
	}
	
	public void resetQuest(Quest quest) {
		quests.removeIf(data -> data.getQuestID().equals(quest.getID()));
		quests.add(new QuestState(quest.getID(), -1, 0, QuestStatus.NOT_STARTED));
	}
	
}