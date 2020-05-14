package fr.jamailun.halystia.quests.steps;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.shops.Trade;

public final class QuestStepBring extends QuestStep {

	private RpgNpc target;
	private Trade trade;
	private ItemStack item;
	
	public static void serialize(RpgNpc npc, ItemStack item, ConfigurationSection section) {
		section.set("to", npc.getConfigId());
		section.set("what", item);
		section.set("messages", new ArrayList<>());
		section.set("type", QuestStepType.BRING.toString());
	}
	
	private final String configTargetName;
	public QuestStepBring(ConfigurationSection section, Quest quest, int step, NpcManager npcs, MobManager mobs) {
		super(section, quest, step, npcs, mobs);
		configTargetName = section.getString("to");
		target = npcs.getNpcWithConfigId(configTargetName);
		if(target == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[QUEST-" + quest.getID() +"/" + step + "] Impossible to get npc #" + configTargetName + ".");
			quest.invalid();
		}
		
		item = section.getItemStack("what");
		if(item == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[QUEST-" + quest.getID() +"/" + step + "] Impossible to get item !");
			return;
		}
		
		trade = new Trade(null, Arrays.asList(item));
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
	
	public void trade(Player p) {
		if( trade.trade(p, true) ) {
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {@Override
				public void run() {
					valid(p);
				}
			}, sendMessages(p) * NpcManager.TIME_BETWEEN_MESSAGES);
		} else {
			target.sendMessage(p, "Tu n'as pas ramené assez de [" 
				+ (item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase() : item.getType().toString().toLowerCase().replace("_", " ")) 
				+ ChatColor.RESET + "] !"
			);
			target.free(p);
		}
	}
	
	@Override
	public void valid(Player p) {
		giveLoot(p);
		quest.stepOver(p, getStep());
		target.free(p);
	}
	
	public ItemStack getItem() {
		return item;
	}

	private String getItemName() {
		return item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase() : item.getType().toString().toLowerCase();
	}
	
	@Override
	public QuestStepType getType() {
		return QuestStepType.BRING;
	}

	@Override
	public String getObjectiveDescription() {
		String name = configTargetName;
		if(target != null)
			name = target.getDisplayName();
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Objectif : " + ChatColor.BLUE + "Apporter " + getItemName() +ChatColor.BLUE + " x"+item.getAmount()+" à "
				+ name + ChatColor.BLUE+".";
	}
}