package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.players.QuestsAdvancement;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepInteract;
import fr.jamailun.halystia.quests.steps.QuestStepKill;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class MainQuestsGUI extends MenuGUI {

	private final Player p;
	public MainQuestsGUI(Player p) {
		super(ChatColor.DARK_AQUA + "Avancement des quêtes", 9*6, HalystiaRPG.getInstance());
		this.p = p;
		
		final QuestsAdvancement playerAdv = HalystiaRPG.getInstance().getQuestManager().getPlayerData(p);
		final Set<Quest> quests = playerAdv.getAllQuests();
		final int qtsSize = quests.size();
		final Set<QuestStep> steps = playerAdv.getOnGoingQuestSteps();
		
		ItemStack mur = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
		for(int i = 0; i <= 9*6 - 1; i++)
			addOption(mur, i);
		
		if(steps.size() == 0) {
			addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(RED + "Aucune quête en cours").toItemStack(), 9*6-2);
		}
		
		int slot = 0;
		Set<Quest> onGoing = new HashSet<>();
		for(QuestStep step : steps) {
			Quest quest = step.getQuest();
			onGoing.add(quest);
			ItemBuilder builder = new ItemBuilder(Material.BARRIER).setName(RED + "Erreur");
			Material type = Material.BARRIER;
			if(step != null) {
				List<String> additionalLore = new ArrayList<>();
				switch (step.getType()) {
					case BRING:
						type = Material.CHEST;
						break;
					case KILL:
						type = Material.IRON_SWORD;
						additionalLore.add(GRAY + "Encore " + RED + (((QuestStepKill)step).getHowManyToKill() - step.getQuest().getDataForPlayer(p)) + GRAY + ".");
						break;
					case SPEAK:
						type = Material.PAPER;
						break;
					case DONJON:
						type = Material.END_PORTAL_FRAME;
						break;
					case INTERACT:
						Block bl = ((QuestStepInteract)step).getTargettedBlock();
						if(bl.getType() == Material.AIR || bl.getType() == Material.CAVE_AIR || bl.getType() == Material.WATER || bl.getType() == Material.LAVA) {
							type = Material.BARRIER;
							additionalLore.add(RED+"ERREUR : type de bloc cible non valide:");
							additionalLore.add(GRAY+bl.toString());
						} else
							type =bl.getType();
						break;
				}
				builder = new ItemBuilder(type);
				builder.setName(YELLOW + quest.getDisplayName());
				builder.setLore(step.getObjectiveDescription());
				builder.addLoreLine(" ");
				builder.addLoreLine(GRAY + "[Étape " + GREEN + (step.getStep()+1) + GRAY + " sur " + (quest.getHowManySteps()) + "]");
				if(step.getQuest().isMainQuest()) {
					builder.addLoreLine(" ");
					builder.addLoreLine(LIGHT_PURPLE + "(Quête principale)");
				}
				builder.addLoreLine(" ");
				for(String line : additionalLore)
					builder.addLoreLine(line);
				builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
			
			}
			
			addOption(builder.toItemStack(), slot);
			slot ++;
		}
		for(Quest quest : onGoing)
			quests.remove(quest);
		
		for(Quest quest : quests) {
			ItemBuilder builder = new ItemBuilder(Material.BOOK);
			builder.setName(quest.getDisplayName());
			builder.setLore(GREEN + "Terminé");
			builder.addEnchant(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS);
			addOption(builder.toItemStack(), slot);
			slot++;
		}
		
		int completed = qtsSize - steps.size();
		
		addOption(new ItemBuilder(Material.BOOKSHELF).setName(BLUE+"Vous avez complété : " + completed + " quête" + (completed >1?"s":"")).toItemStack(), getSize()-2);
		addOption(new ItemBuilder(Material.ARROW).setName(BLUE+"Retour").toItemStack(), getSize()-1);
		
		show(p);
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null)
			return;
		if(e.getCurrentItem().getItemMeta() == null)
			return;
		if(e.getSlot() == getSize()-1) {
			p.closeInventory();
			new MainClasseGUI(p);
		}
	}

}