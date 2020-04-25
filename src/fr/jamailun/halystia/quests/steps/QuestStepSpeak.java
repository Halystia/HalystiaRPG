package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.quests.Quest;

public final class QuestStepSpeak extends QuestStep {

	private RpgNpc target;
	
	public static void serialize(RpgNpc npc, ConfigurationSection section) {
		section.set("to", npc.getConfigId());
		section.set("messages", new ArrayList<>());
		section.set("type", QuestStepType.SPEAK.toString());
	}
	
	private final String configTargetName;
	public QuestStepSpeak(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		super(section, quest, step, npcs, mobs);
		configTargetName = section.getString("to");
		target = npcs.getNpcWithConfigId(configTargetName);
		if(target == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[QUEST-" + quest.getID() +"/" + step + "] Impossible to get npc #" + configTargetName + ".");
			quest.invalid();
		}
		loot = section.getItemStack("loot");
	}
	
	public int sendMessages(Player p) {
		int delay = 0;
		for(String line : messages.getDialog()) {
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
				public void run() {
					target.sendMessage(p, ChatColor.translateAlternateColorCodes('&', line));
				}
			}, delay * NpcManager.TIME_BETWEEN_MESSAGES);
			delay++;
		}
		return delay;
	}
	
	public RpgNpc getTarget() {
		return target;
	}
	
	@Override
	public void valid(Player p) {
		Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
			public void run() {
				giveLoot(p);
				quest.stepOver(p, getStep());
				target.free(p);
			}
		}, sendMessages(p) * NpcManager.TIME_BETWEEN_MESSAGES);
	}

	@Override
	public QuestStepType getType() {
		return QuestStepType.SPEAK;
	}

	@Override
	public String getObjectiveDescription() {
		String name = configTargetName;
		if(target != null)
			name = target.getDisplayName();
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Objectif : " + ChatColor.BLUE + "Parler à " + name + ChatColor.BLUE+".";
	}
}