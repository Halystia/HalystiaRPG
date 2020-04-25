package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.utils.PlayerUtils;

public final class QuestStepKill extends QuestStep {
	
	private final String mobName;
	private final int killToDo;
	
	public static void serialize(String mobID, int howMany, ConfigurationSection section) {
		section.set("how", howMany);
		section.set("what", mobID);
		section.set("messages", new ArrayList<>());
		section.set("type", QuestStepType.KILL.toString());
	}
	
	public QuestStepKill(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		super(section, quest, step, npcs, mobs);
		mobName = section.getString("what");
		killToDo = section.getInt("how");
		if( ! mobs.getAllMobNames().contains(mobName)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[QUEST-" + quest.getID() +"/" + step + "] Impossible to get mob #" + mobName + ".");
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
	
	public void playerKillOneMob(Player p) {
		int killed = quest.getDataForPlayer(p) + 1;
		if(killed >= killToDo) {
			valid(p);
		} else {
			new PlayerUtils(p).sendActionBar(quest.getDisplayName() + (quest.getHowManySteps() > 1 ? ChatColor.GRAY + " - Étape " + step + " ": "")
					+ ChatColor.WHITE + " > " + ChatColor.GREEN + "Encore " + (killToDo - killed) + " !");
			quest.updateDataForPlayer(p, killed);
		}
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

	public String getMobName() {
		return mobName;
	}
	
	public int getHowManyToKill() {
		return killToDo;
	}
	
	public String getMobDisplayName() {
		ItemStack logo = HalystiaRPG.getInstance().getMobManager().getIconeOfMob(mobName);
		if(logo.getType() == Material.BARRIER) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERREUR LE MOB " + mobName + " N'EXISTE PAS ("+quest.getID()+"/"+step+").");
			return ChatColor.DARK_RED + "ERREUR";
		}
		return logo.getItemMeta().getDisplayName();
	}

	@Override
	public QuestStepType getType() {
		return QuestStepType.KILL;
	}

	@Override
	public String getObjectiveDescription() {
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Objectif : " + ChatColor.BLUE + "Tuer " + getMobDisplayName() +ChatColor.BLUE + " x"+killToDo+".";
	}
}