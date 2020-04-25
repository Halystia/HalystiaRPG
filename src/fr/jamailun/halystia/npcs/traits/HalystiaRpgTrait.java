package fr.jamailun.halystia.npcs.traits;

import fr.jamailun.halystia.quests.Quest;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;

@TraitName("rpg")
public class HalystiaRpgTrait extends Trait {

	@Persist("questName")
	public String questName = "null";
	
	public HalystiaRpgTrait() {
		super("rpg");
	}
	
	public void editQuest(Quest quest) {
		questName = quest.getID();
	}
	
	public void resetQuest() {
		questName = "null";
	}
	
	public String getQuestName() {
		return questName;
	}
	
	public boolean hasQuest() {
		if(questName == null)
			return false;
		return ! questName.equals("null");
	}
	
	//tick played
	public void run() {}
	
	public void load(DataKey key) {
		questName = key.getString("questName");
	}

	// Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
	public void save(DataKey key) {
		key.setString("questName", questName);
	}
}