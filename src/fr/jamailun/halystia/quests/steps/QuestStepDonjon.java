package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.donjons.DonjonManager;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.quests.Quest;

public final class QuestStepDonjon extends QuestStep {
	
	private final String donjonID;
	
	public static void serialize(String donjonID, ConfigurationSection section) {
		section.set("what", donjonID);
		section.set("messages", new ArrayList<>());
		section.set("type", QuestStepType.DONJON.toString());
	}
	
	public QuestStepDonjon(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs, DonjonManager donjons) {
		super(section, quest, step, npcs, mobs);
		
		donjonID = section.getString("what");
		if( donjons.getLegacyWithConfigName(donjonID) == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[QUEST-" + quest.getID() +"/" + step + "] Impossible to get donjon #" + donjonID + ".");
			quest.invalid();
		}
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

	public String getDonjonID() {
		return donjonID;
	}

	@Override
	public QuestStepType getType() {
		return QuestStepType.DONJON;
	}
	
	private String getDisplayDonjon() {
		return HalystiaRPG.getInstance().getDonjonManager().getLegacyWithConfigName(donjonID).getName();
	}

	@Override
	public String getObjectiveDescription() {
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Objectif : " + ChatColor.BLUE + "Finir le " + getDisplayDonjon() +ChatColor.BLUE + ".";
	}
}