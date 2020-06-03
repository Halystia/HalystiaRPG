package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class UpdateBankAccountGUI extends MenuGUI {

	private static final int SLOT_I = 12;
	private final int currentLevel;
	private final Player p;
	
	public UpdateBankAccountGUI(Player p) {
		super(DARK_RED+"Edition du compte en banque", 9*3, HalystiaRPG.getInstance());
		this.p = p;
		for(int i = 0; i < 9*3; i++)
			addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(YELLOW+"").toItemStack(), i);
		
		currentLevel = HalystiaRPG.getInstance().getBanque().getLevelOf(p);
		
		addOption(new ItemBuilder(Material.BOOK).setName(YELLOW+"Niveau du compte : "+BLUE + currentLevel).toItemStack(), 11);
		
		for(int i = 1; i <= 4; i++) {
			if(i <= currentLevel) {
				addOption(new ItemBuilder(Material.LIME_DYE).setName(GREEN+"Niveau " + GOLD+i).setLore(GRAY+"Possédé").toItemStack(), SLOT_I+i);
				continue;
			}
			ItemBuilder builder = new ItemBuilder(Material.GRAY_DYE).setName(GRAY+"Niveau " + GOLD+i).setLore(RED+"Non possédé");
			if(HalystiaRPG.getInstance().getBanque().getCurrentRules().getCost(i).isEmpty()) {
				builder.addLoreLine(GREEN+"Gratuit !");
			} else {
				builder.addLoreLine(GRAY+"Prix :");
				for(ItemStack item : HalystiaRPG.getInstance().getBanque().getCurrentRules().getCost(i)) {
					String name = "";
					if(item.getType() == Material.EMERALD) {
						name = ChatColor.GOLD + "" + item.getAmount() + " émeraude"+(item.getAmount() > 1 ? "s":"");
					} else if(item.getType() == Material.EMERALD_BLOCK) {
						name = ChatColor.GOLD + "" + item.getAmount() + " bloc"+(item.getAmount() > 1 ? "s":"")+" d'émeraude";
					} else {
						name = ""+(item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType() : item.getType())+GRAY+" x"+item.getAmount();
					}
					builder.addLoreLine(GRAY+"-"+name);
				}
			}
			addOption(builder.toItemStack(), 12+i);
		}
		show(p);
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		e.setCancelled(true);
		if(e.getCurrentItem() == null)
			return;
		if(e.getCurrentItem().getType() != Material.GRAY_DYE)
			return;
		int level = e.getSlot() - SLOT_I;
		if(level != currentLevel + 1) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Il faut d'abord acheter les améliorations précédentes.");
			return;
		}
		HalystiaRPG.getInstance().getBanque().levelupAccount(p);
		new UpdateBankAccountGUI(p);
	}
}