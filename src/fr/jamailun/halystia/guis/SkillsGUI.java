package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.players.SkillSet;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class SkillsGUI {
	
	private final static ItemStack mur = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(BLACK + "").toItemStack();
	
	private MenuGUI gui;
	private SkillSet skills;
	private int level;
	private int remaining;
	
	public SkillsGUI(Player p) {
		PlayerData pc = HalystiaRPG.getInstance().getClasseManager().getPlayerData(p);
		if(pc == null) {
			p.sendMessage(RED + "Une erreur est survnue. Réessayez.");
			return;
		}
		level = pc.getLevel();
		skills = pc.getSkillSetInstance();
		remaining = (level / 2) - skills.getTotalPoints();
		// 1 pt tous les 2 niveaux
		gui = new MenuGUI(DARK_GREEN + "Statut des skills", 9*3, HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getCurrentItem() == null)
					return;
				if(remaining <= 0 && (e.getSlot() == 10 || e.getSlot() == 12 || e.getSlot() == 14 || e.getSlot() == 16)) {
					p.sendMessage(RED+"Tu n'as plus de points de skills disponible.");
					return;
				}
				switch (e.getSlot()) {
				case 10:
					skills.updateSkill(SkillSet.SKILL_FORCE);
					break;
				case 12:
					skills.updateSkill(SkillSet.SKILL_INTELLIGENCE);
					break;
				case 14:
					skills.updateSkill(SkillSet.SKILL_CONSTITUTION);
					break;
				case 16:
					skills.updateSkill(SkillSet.SKILL_AGILITE);
					break;
				default:
					return;
				}
				updateGUI();
			}
		};
		
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(mur, i);
		
		updateGUI();
		
		gui.show(p);
	}
	
	private void updateGUI() {
		remaining = (level / 2) - skills.getTotalPoints();
		gui.addOption(new ItemBuilder(Material.PAPER).setName(LIGHT_PURPLE+"Points restants : "+ (remaining == 0 ? RED : GREEN) +remaining ).toItemStack(), 4);
		int levelForce = skills.getLevel(SkillSet.SKILL_FORCE);
		int levelIntel = skills.getLevel(SkillSet.SKILL_INTELLIGENCE);
		int levelConsti = skills.getLevel(SkillSet.SKILL_CONSTITUTION);
		int levelAgi = skills.getLevel(SkillSet.SKILL_AGILITE);
		gui.addOption(new ItemBuilder(Material.SKELETON_SKULL).setName(BLUE + "Force").addLoreLine("Augmente de 1% les chances de donner un coup critique.")
				.addLoreLine(WHITE + "Actuellement niveau " + GOLD + levelForce + ITALIC + " (+"+(levelForce*1)+"%)").toItemStack(), 10);
		gui.addOption(new ItemBuilder(Material.BOOK).setName(BLUE + "Intelligence").addLoreLine("Augmente de 0,1 la régénération de mana.")
				.addLoreLine(WHITE + "Actuellement niveau " + GOLD + levelIntel + ITALIC + " (+"+(levelIntel*0.2)+" mana/sec)").toItemStack(), 12);
		gui.addOption(new ItemBuilder(Material.COOKED_BEEF).setName(BLUE + "Constitution").addLoreLine("Augmente de 2% les chances de ne pas perdre de faim.")
				.addLoreLine(WHITE + "Actuellement niveau " + GOLD + levelConsti + ITALIC + " (+"+(levelConsti*2)+"%)").toItemStack(), 14);
		gui.addOption(new ItemBuilder(Material.FEATHER).setName(BLUE + "Agilité").addLoreLine("Augmente de 1% les chances d'esquiver un coup.")
				.addLoreLine(WHITE + "Actuellement niveau " + GOLD + levelAgi + ITALIC + " (+"+(levelAgi*1)+"%)").toItemStack(), 16);
		
	}
	
	
}