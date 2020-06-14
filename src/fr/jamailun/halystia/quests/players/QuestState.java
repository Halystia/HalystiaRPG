package fr.jamailun.halystia.quests.players;

public class QuestState {
	
	private final String questID;
	private int step, data;
	private QuestStatus status;
	
	public QuestState(String questID) {
		this(questID, 0);
	}
	
	public QuestState(String questID, int step) {
		this(questID, step, 0);
	}
	
	public QuestState(String questID, int step, int data) {
		this(questID, step, data, QuestStatus.STARTED);
	}
	
	public QuestState(String questID, int step, int data, QuestStatus status) {
		this.questID = questID;
		this.step = step;
		this.data = data;
		this.status = status;
	}

	public String getQuestID() {
		return questID;
	}

	public int getStep() {
		return step;
	}

	public int getData() {
		return data;
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
		if(status == QuestStatus.NOT_STARTED)
			return;
		status = QuestStatus.FINISHED;
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