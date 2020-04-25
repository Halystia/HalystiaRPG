package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class ChooseClasseGui {
	
	public final static Material ALCHIMISTE = Material.BREWING_STAND;
	public final static Material EPEISTE = Material.IRON_SWORD;
	public final static Material ARCHER = Material.BOW;
	public final static Material INVOCATEUR = Material.PANDA_SPAWN_EGG;
	
	private final HalystiaRPG api;
	private MenuGUI gui;
	
	public ChooseClasseGui(HalystiaRPG main) {
		this.api = main;
		init();
	}
	
	private void init() {
		gui = new MenuGUI(DARK_GREEN + "Choix de votre classe", 3*9, api) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getCurrentItem() == null)
					return;
				if(e.getCurrentItem().getItemMeta() == null)
					return;
				if( ! e.getCurrentItem().getItemMeta().hasDisplayName())
					return;
				Player p = (Player) e.getWhoClicked();
				Material mat = e.getCurrentItem().getType();
				if(mat == ALCHIMISTE)
					confirmToPlayer(p, Classe.ALCHIMISTE);
				else if(mat == EPEISTE)
					confirmToPlayer(p, Classe.EPEISTE);
				else if(mat == ARCHER)
					confirmToPlayer(p, Classe.ARCHER);
				else if(mat == INVOCATEUR)
					confirmToPlayer(p, Classe.INVOCATEUR);
				else if(mat == Material.ARROW)
					new MainClasseGUI(p);
			}
		};
		
		
		for(int i = 0; i < 3*9; i++)
			gui.addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(), i);
		
		gui.addOption(new ItemBuilder(ALCHIMISTE).setName(GREEN + "Alchimiste").addLoreLine(GRAY+"Manipule les potions, buffe tes alliés").addLoreLine(GRAY+"et maudit tes ennemis !").toItemStack(), 10);
		gui.addOption(new ItemBuilder(EPEISTE).setName(GREEN + "Epéiste").addLoreLine(GRAY+"Une lame toujours affutée pour").addLoreLine(GRAY+"découper ton némésis !").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack(), 12);
		gui.addOption(new ItemBuilder(ARCHER).setName(GREEN + "Archer").addLoreLine(GRAY+"Prend du recul sur la vie, et").addLoreLine(GRAY+"n'ait aucune miséricorde !").toItemStack(), 14);
		gui.addOption(new ItemBuilder(INVOCATEUR).setName(GREEN + "Invocateur").addLoreLine(GRAY+"Invoque de puissants compagnons").addLoreLine(GRAY+"pour terrasser tes adversaires !").toItemStack(), 16);
		
		gui.addOption(new ItemBuilder(Material.ARROW).setName(RED+"Retour").toItemStack(), 26);
	}
	
	public void openGui(Player p) {
		gui.show(p);
	}
	
	private void confirmToPlayer(Player p, Classe classe) {
		
		MenuGUI gui = new MenuGUI(DARK_RED + "Choisir la classe [" + DARK_BLUE + classe.getName() + DARK_RED + "]", 9*2, api) {
			
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
				if( ! e.getCurrentItem().getItemMeta().hasDisplayName())
					return;
				Player p = (Player) e.getWhoClicked();
				Material mat = e.getCurrentItem().getType();
				if(mat == Material.LIME_CONCRETE) {
					api.getDataBase().changePlayerClasse(p, classe);
					api.getClasseManager().changePlayerClasse(p, classe);
					p.sendMessage(HalystiaRPG.PREFIX+LIGHT_PURPLE+""+BOLD + "FELICITATION ! " + GOLD + " Tu es désormais un "+classe.getDisplayName(1).toLowerCase()+GOLD+" !");
					p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
					p.closeInventory();
				} else if(mat == Material.RED_CONCRETE) {
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, .5f);
					openGui(p);
				} else if(mat == Material.ARROW) {
					openGui(p);
				}
			}
		};
		for(int i = 0; i < 18; i++)
			gui.addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(), i);
		gui.addOption(new ItemBuilder(Material.PAPER).setName(RED+"Attention !").addLoreLine(GRAY+"Il sera très difficile de changer après...").addLoreLine(GRAY+"Soit sûr et certain de ton choix !").toItemStack(), 4);
		
		gui.addOption(new ItemBuilder(Material.LIME_CONCRETE).setName(DARK_GREEN+""+BOLD+"OUI").toItemStack(), 2);
		gui.addOption(new ItemBuilder(Material.RED_CONCRETE).setName(DARK_RED+""+BOLD+"NON").toItemStack(), 6);
		
		gui.addOption(new ItemBuilder(Material.ARROW).setName(RED+"Retour").toItemStack(), 17);
		
		gui.show(p);
	}
	
}
