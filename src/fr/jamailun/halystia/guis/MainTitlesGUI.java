package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.titles.Title;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class MainTitlesGUI extends MenuGUI {
	
	private final List<Title> titles;
	private final Player p;
	public MainTitlesGUI(Player p) {
		super(ChatColor.DARK_BLUE + "Titres", 9*6, HalystiaRPG.getInstance());
		this.p = p;
		
		HalystiaRPG main = HalystiaRPG.getInstance();
		final List<String> tags = main.getDataBase().getTagsOfPlayer(p);
		titles = new ArrayList<>(main.getTitlesManager().getTitlesWithTags(tags));
		final String currentTagTitle = main.getDataBase().getCurrentTitleOfPlayer(p); 
		
		int curr = titles.size();
		
		ItemStack mur = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
		for(int i = 1; i <= (9 * 6) - 1; i++)
			addOption(mur, i);
		
		if(curr == 0)
			addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(RED + "Aucune titre de débloqué.").toItemStack(), 0);
		
		int slot = 0;
		for(Title title : titles) {
			ItemBuilder builder = new ItemBuilder(Material.BOOK);
			builder.setName(title.getDisplayName());
			
			if(currentTagTitle != null) {
				if(title.getTag().equals(currentTagTitle)) {
					builder.addLoreLine(GREEN + "Titre sélectionné !");
					builder.addEnchant(Enchantment.DURABILITY, 1);
					builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
				}
			}
			addOption(builder.toItemStack(), slot);
			slot++;
		}
		
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
		if(e.getSlot() >= titles.size())
			return;
		selectTitle(e.getSlot());
	}
	
	private void selectTitle(int slot) {
		Title title = titles.get(slot);
		p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Titre sélectionné : " + title.getDisplayName());
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, .7f);
		HalystiaRPG.getInstance().getDataBase().setCurrentTitleOfPlayer(p, title);
		new MainTitlesGUI(p);
	}

}