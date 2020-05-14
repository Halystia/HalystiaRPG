package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.quests.Quest;

public final class QuestStepInteract extends QuestStep {
	
	private final Location location;
	private final String blockName;
	
	public static void serialize(Location location, String blockName, ConfigurationSection section) {
		section.set("where", location);
		section.set("what", blockName);
		section.set("messages", new ArrayList<>());
		section.set("type", QuestStepType.INTERACT.toString());
	}
	
	public QuestStepInteract(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		super(section, quest, step, npcs, mobs);
		location = section.getLocation("where");
		blockName = ChatColor.translateAlternateColorCodes('&', section.getString("what"));
		loot = section.getItemStack("loot");
	}
	
	public int sendMessages(Player p) {
		int delay = 0;
		for(String line : messages.getDialog()) {
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
				public void run() {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
			}, delay * NpcManager.TIME_BETWEEN_MESSAGES);
			delay++;
		}
		return delay;
	}
	
	@Override
	public void valid(Player p) {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				giveLoot(p);
				quest.stepOver(p, getStep());
			}
		}, sendMessages(p) * NpcManager.TIME_BETWEEN_MESSAGES);
	}

	public Block getTargettedBlock() {
		return location.getBlock();
	}
	
	@Override
	public QuestStepType getType() {
		return QuestStepType.INTERACT;
	}

	@Override
	public String getObjectiveDescription() {
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Objectif : " + ChatColor.BLUE + "Interargir avec " + blockName +ChatColor.BLUE + ".";
	}

	public String getBlockName() {
		return blockName;
	}
}