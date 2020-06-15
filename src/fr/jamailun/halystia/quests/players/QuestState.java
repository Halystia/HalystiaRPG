package fr.jamailun.halystia.quests.players;

import fr.jamailun.halystia.quests.Quest;

public class QuestState {
	
	private final Quest quest;
	private int step, data;
	private QuestStatus status;
	
	public QuestState(Quest quest, int step, int data, QuestStatus status) {
		this.quest = quest;
		this.step = step;
		this.data = data;
		this.status = status;
	}

	public Quest getQuest() {
		return quest;
	}

	public int getStep() {
		return step;
	}

	public int getData() {
		return data;
	}
	
	public QuestStatus getState() {
		return status;
	}

	public boolean isFinished() {
		return status == QuestStatus.FINISHED;
	}
	
	public boolean isStarted() {
		return status == QuestStatus.STARTED;
	}
	
	public boolean isKnown() {
		return status != QuestStatus.NOT_STARTED;
	}
	
	public void finish() {
		if(status != QuestStatus.STARTED)
			return;
		status = QuestStatus.FINISHED;
	}
	
	public void start() {
		if(status != QuestStatus.NOT_STARTED)
			return;
		status = QuestStatus.STARTED;
	}
	
	public void updateStep(int step) {
		this.step = step;
	}
	
	public void updateData(int data) {
		this.data = data;
	}
	
	public static enum QuestStatus {
		NOT_STARTED, STARTED, FINISHED;
	}
	
}