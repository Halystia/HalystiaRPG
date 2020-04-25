package fr.jamailun.halystia.guis;

import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.shops.Shop;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.shops.TradeManager;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class TradeEditorGUI extends MenuGUI {
	
	private static final ItemStack TO_COMPLETE = new ItemBuilder(Material.STONE_BUTTON).setName(YELLOW+"À compléter !").setLore(GRAY+"Faites glisser un item !", DARK_GRAY + "(facultatif)").toItemStack();
	private static final ItemStack TO_COMPLETE_SELL = new ItemBuilder(Material.JUNGLE_BUTTON).setName(GOLD+"À compléter !").setLore(GRAY+"Faites glisser un item !").toItemStack();
	private static final ItemStack IMPOSSIBLE = new ItemBuilder(Material.BARRIER).setName(RED+"<-").toItemStack();
	private static final ItemStack IMPOSSIBLE_VALIDATE = new ItemBuilder(Material.BARRIER).setName(RED+"Il faut un item à vendre et un item requis !").toItemStack();
	private static final ItemStack VALIDATE = new ItemBuilder(Material.EMERALD_BLOCK).setName(GREEN + "Valider").toItemStack();
	
	private final boolean editFinal;
	private final Trade trade;
	private final TradeManager manager;
	private final Classe classe;
	private final Player p;
	private final Object from;
	
	public TradeEditorGUI(String title, int size, TradeManager manager, Player p, Classe classe, boolean edit, Trade trade, Object from) {
		super(title, size, HalystiaRPG.getInstance());
		editFinal = edit;
		this.trade = trade;
		this.manager = manager;
		this.classe = classe;
		this.p = p;
		this.from = from;
	}
	
	private int currentLevel = 1;
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
		ItemStack item = e.getCurrentItem();
		if(item.getType() == Material.BARRIER)
			return;
		Player p = (Player) e.getWhoClicked();
		switch(e.getSlot()) {
			case 27:
				if(!editFinal)
					return;
				HalystiaRPG.getInstance().getTradeManager().removeTrade(trade, trade.getKey());
				p.sendMessage(HalystiaRPG.PREFIX + DARK_RED + "Trade détruit avec succès.");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, .7f, 1f);
				close();
				return;
			case 28:
				close();
				return;
			case 34:
				if(Trade.areItemsTheSame(e.getCurrentItem(), IMPOSSIBLE_VALIDATE)) {
					p.sendMessage(RED+"Une erreur étrange est survenue... Trademanager#createNewTrade(...) Merci de rapporter ça au développeur.");
					return;
				}
				
				if(editFinal)
					manager.removeTrade(trade, trade.getKey());
				
				List<ItemStack> toTrade = new ArrayList<>();
				for(int i = 10; i <= 16; i++) {
					ItemStack element = getInventory().getItem(i);
					if(Trade.areItemsTheSame(element, TO_COMPLETE) || element.getType() == Material.BARRIER)
						break;
					toTrade.add(element);
				}
				
				manager.addTrade(classe, getInventory().getItem(31), toTrade, currentLevel);
				p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Edition de trade terminée avec succès !");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
				close();
				return;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
				//On sait que c'est PAS une barrier
				if(Trade.areItemsTheSame(item, TO_COMPLETE)) {
					if(e.getCursor() != null) {
						if(e.getCursor().getType() != Material.AIR) {
							putItem(e.getCursor(), e.getSlot());
						}
					}
					computeValidate();
					return;
				}
				//tout décaler car on supprime notre clic
				for(int s = e.getSlot(); s <= 16; s++) {
					if(Trade.areItemsTheSame(getInventory().getItem(s), IMPOSSIBLE)) {
						//rien après
						addOption(IMPOSSIBLE, s);
					} else {
						//truc après
						addOption(getInventory().getItem(s+1), s);
					}
				}
				computeValidate();
				return;
			case 31:
				if(Trade.areItemsTheSame(item, TO_COMPLETE_SELL)) {
					if(e.getCursor() != null) {
						if(e.getCursor().getType() != Material.AIR) {
							addOption(e.getCursor(), 31);
						}
					}
					computeValidate();
					return;
				}
				addOption(TO_COMPLETE_SELL, 31);
				computeValidate();
				return;
			case 46:
			case 47:
			case 48:
				deltaLevel( - e.getCurrentItem().getAmount());
				return;
			case 50:
			case 51:
			case 52:
				deltaLevel(e.getCurrentItem().getAmount());
				return;
		}
	}
	
	public void computeValidate() {
		boolean appear = true;
		if(Trade.areItemsTheSame(getInventory().getItem(10), TO_COMPLETE))
			appear = false;
		if(Trade.areItemsTheSame(getInventory().getItem(31), TO_COMPLETE_SELL))
			appear = false;
		if(appear)
			addOption(VALIDATE, 34);
		else
			addOption(IMPOSSIBLE_VALIDATE, 34);
	}
	
	private void close() {
		if(from != null) {
			if(from instanceof Shop)
				HalystiaRPG.getInstance().getTradeManager().openTradeClasseEditor(p, classe, from);
			return;
		}
		p.closeInventory();
	}
	
	public void putItem(ItemStack item, int slot) {
		addOption(item, slot);
		if(slot < 16)
			addOption(TO_COMPLETE, slot + 1);
	}
	
	public void forceLevel(int lvl) {
		currentLevel = lvl;
		if(currentLevel < 1)
			currentLevel = 1;
		if(currentLevel > 100)
			currentLevel = 100;
		updateLevel();
	}
	
	public void deltaLevel(int delta) {
		currentLevel += delta;
		if(currentLevel < 1)
			currentLevel = 1;
		if(currentLevel > 100)
			currentLevel = 100;
		updateLevel();
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .4f, .4f);
	}
	
	private void updateLevel() {
		int amountLevel = currentLevel;
		Material mat = Material.BRICK;
		if(currentLevel >= 10 && currentLevel < 30) {
			amountLevel -= 9;
			mat = Material.IRON_INGOT;
		} else if(currentLevel >= 30 && currentLevel < 60) {
			amountLevel -= 29;
			mat = Material.GOLD_INGOT;
		} else if(currentLevel >= 60 && currentLevel < 90) {
			amountLevel -= 59;
			mat = Material.DIAMOND;
		}else if(currentLevel >= 90) {
			amountLevel -= 89;
			mat = Material.EMERALD;
		}
		if(currentLevel == 100) {
			amountLevel = 1;
			mat = Material.OBSIDIAN;
		}
		addOption(new ItemBuilder(mat, amountLevel).setName(YELLOW + "Niveau " + LIGHT_PURPLE + currentLevel).toItemStack(), 49);
	}
	
}
