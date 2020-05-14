package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.quests.Messages;
import fr.jamailun.halystia.quests.Quest;

public abstract class QuestStep {

	public final Quest quest;
	public final int step;
	protected final ConfigurationSection section;
	
	protected QuestStepType type;
	protected Messages messages;
	protected final NpcManager npcs;
	protected final MobManager mobs;
	protected ItemStack loot;
	
	public static QuestStep factory(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		QuestStepType type = null;
		try {
			type = QuestStepType.valueOf(section.getString("type").toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
		switch (type) {
			case SPEAK:
				return new QuestStepSpeak(section, quest, step, npcs, mobs);
			case BRING:
				return new QuestStepBring(section, quest, step, npcs, mobs);
			case KILL:
				return new QuestStepKill(section, quest, step, npcs, mobs);
			case DONJON:
				return new QuestStepDonjon(section, quest, step, npcs, mobs);
			case INTERACT:
				return new QuestStepInteract(section, quest, step, npcs, mobs);
		}
		throw new IllegalArgumentException("Type '"+section.getString("type")+"' not compatible.");
	}
	
	public abstract int sendMessages(Player p);
	
	protected QuestStep(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		this.section = section;
		this.quest = quest;
		this.step = step;
		this.npcs = npcs;
		this.mobs = mobs;
		if(!section.contains("messages"))
			section.set("messages", new ArrayList<>());
		messages = new Messages(section.getStringList("messages"));
		if(section.contains("loot"))
			loot = section.getItemStack("loot");
	}
	
	public final Messages getMessages() {
		return messages;
	}
	
	public final Quest getQuest() {
		return quest;
	}

	public final int getStep() {
		return step;
	}
	
	public final ItemStack getLoot() {
		return loot;
	}
	
	public final void setLoot(ItemStack item) {
		this.loot = item;
	}

	public abstract QuestStepType getType();
	
	public abstract void valid(Player p);
	
	public void resetLoot() {
		loot = null;
	}
	
	public void giveLoot(Player p) {
		if(loot != null) {
			p.getInventory().addItem(loot);
			p.sendMessage(ChatColor.GREEN + "-> Tu gagnes ["+
					(loot.hasItemMeta() ? loot.getItemMeta().hasDisplayName() ? loot.getItemMeta().getDisplayName() : loot.getType().toString().toLowerCase() : loot.getType().toString().toLowerCase().replace("_", " "))
					+ ChatColor.GREEN + "]" + (loot.getAmount() > 1 ? " x"+loot.getAmount() : "")
					+ " !"
					);
		}
	}
	
	public abstract String getObjectiveDescription();

	public void setMessages(Messages msg) {
		this.messages = msg;
	}
	
}