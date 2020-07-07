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
import fr.jamailun.halystia.quests.players.QuestState.QuestStatus;
import fr.jamailun.halystia.quests.steps.QuestStep;

/*
 * INstance who handle & store all quests progression of a Player.
 */
public class QuestsAdvancement {
	
	private final Set<QuestState> quests;
	private final UUID uuid;
	
	public QuestsAdvancement(UUID uuid, Set<QuestState> quests) {
		this.uuid = uuid;
		this.quests = new HashSet<>(quests);
	//	System.out.println("résumé de playerADV : ("+quests+ " entrées) :");
	//	for(QuestState st : quests)
	//		System.out.println("- " + st.getQuest().getID() + " > ("+st.getStep()+","+st.getData()+") : known/started/finished ? " + st.isKnown()+"/"+st.isStarted()+"/"+st.isFinished()+".");
	}
	
	public Set<Quest> getAllQuests() {
		Set<Quest> set = new HashSet<>();
		for(QuestState state : quests) {
			if(state.getState() == QuestStatus.NOT_STARTED)
				continue;
			set.add(state.getQuest());
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
					.filter(state -> state.getQuest().equals(quest))
					.findFirst().get()
					.getStep();
		} catch(NoSuchElementException e) {
			return -1;
		}
	}
	
	public int getDataInQuest(Quest quest) {
		try {
			return quests.stream()
					.filter(state -> state.getQuest().equals(quest))
					.findFirst().get()
					.getData();
		} catch(NoSuchElementException e) {
			return -1;
		}
	}
	
	public void updateStepInQuest(Quest quest, int step) {
		try {
			QuestState stateQ = quests.stream()
				.filter(state -> state.getQuest().equals(quest))
				.findFirst().get();
			stateQ.start();
			stateQ.updateStep(step);
		} catch(NoSuchElementException ignored) {
			quests.add(new QuestState(quest, step, 0, QuestStatus.STARTED));
		}
		HalystiaRPG.getInstance().getDataBase().updateStepInQuest(Bukkit.getPlayer(uuid), quest, step);
	}
	
	public void updateDataInQuest(Quest quest, int data) {
		try {
			quests.stream()
				.filter(state -> state.getQuest().equals(quest))
				.findFirst().get()
				.updateData(data);
			HalystiaRPG.getInstance().getDataBase().updateDataInQuest(Bukkit.getPlayer(uuid), quest, data);
		} catch(NoSuchElementException ignored) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erreur : player don't have quest " + quest.getID() + ". Step back to 0.");
			quests.add(new QuestState(quest, 0, data, QuestStatus.STARTED));
		}
	}
	
	public Set<Quest> getOnGoingQuests() {
		Set<Quest> set = new HashSet<>();
		for(QuestState state : quests) {
			if(state.isFinished() || !state.isKnown())
				continue;
			set.add(state.getQuest());
		}
		return set;
	}
	
	public Set<QuestStep> getOnGoingQuestSteps() {
		Set<QuestStep> steps = new HashSet<>();
		for(QuestState state : quests) {
			if(state.isFinished() || !state.isKnown())
				continue;
			int step = state.getStep();
			if(step < state.getQuest().getHowManySteps() && step != -1)
				steps.add(state.getQuest().getStep(step));
		}
		return steps;
	}

	public void questRemoved(String id) {
		quests.removeIf(q -> q.getQuest().getID().equals(id));
	}

	public void questAdded(QuestState questState) {
		quests.add(questState);
	}
	
	public void resetQuest(Quest quest) {
		quests.removeIf(data -> data.getQuest().equals(quest));
		quests.add(new QuestState(quest, -1, 0, QuestStatus.NOT_STARTED));
		HalystiaRPG.getInstance().getDataBase().updateStepInQuest(Bukkit.getPlayer(uuid), quest, -1);
	}

	public boolean knows(Quest quest) {
		for(QuestState state : quests) {
			if(state.isKnown())
				if(state.getQuest().equals(quest))
					return true;
		}
		return false;
	}

	public QuestStatus getState(Quest quest) {
		for(QuestState state : quests) {
			if(quest.equals(state.getQuest()))
				return state.getState();
		}
		return QuestStatus.NOT_STARTED;
	}
	
}