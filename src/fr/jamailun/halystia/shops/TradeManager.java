package fr.jamailun.halystia.shops;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guis.TradeEditorGUI;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.utils.FileDataRPG;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;
import fr.jamailun.halystia.utils.RandomString;

public class TradeManager extends FileDataRPG {

	public final static String COLOR_LORE_CLASSE = DARK_PURPLE + "";
	public final static String LORE_CLASSE = "Objet de classe ";
	
	private List<Trade> trades;
	
	public TradeManager(String path, String name) {
		super(path, name);
		
		trades = new ArrayList<>();
		init();
	}
	
	private void init() {
		for(String key : config.getKeys(false)) {
			int classeId = config.getInt(key+".classe");
			Classe classe = Classe.getClasseWithId(classeId);
			int requiredLevel = config.getInt(key + ".level");
			ItemStack toSell = config.getItemStack(key + ".sell");
			List<ItemStack> toBuy = new ArrayList<>();
			int howMany = config.getInt(key + ".ingredients.how-many");
			for(int j = 1; j <= howMany; j++) {
				ItemStack item = config.getItemStack(key + ".ingredients." + j);
				toBuy.add(item);
			}
			trades.add(new Trade(key, classe, toSell, toBuy, requiredLevel));
		}
	}
	
	public void addTrade(Classe classe, ItemStack toSell, List<ItemStack> toTrade, int levelRequired) {
		
		String key = generateKey();
		config.set(key+".classe", classe.getClasseId());
		config.set(key+".level", levelRequired);
		config.set(key+".sell", toSell);
		config.set(key+".ingredients.how-many", toTrade.size());
		for(int i = 1; i <= toTrade.size(); i++) {
			config.set(key + ".ingredients." + i, toTrade.get(i-1));
		}
		
		trades.add(new Trade(key, classe, toSell, toTrade, levelRequired));
		
		save();
	}
	
	private String generateKey() {
		String key;
		do {
			key = new RandomString(20).nextString();
		} while(config.contains(key));
		return key;
	}
	
	public void removeTrade(Trade trade, String key) {
		config.set(key, null);
		trades.remove(trade);
		save();
	}
	
	public List<Trade> getTradesOfClasse(Classe classe) {
		List<Trade> list = new ArrayList<>();
		for(Trade trade : trades)
			if(trade.getClasse() == classe)
				list.add(trade);
		Collections.sort(list);
		return list;
	}
	
	public void openTradeClasseEditor(Player p, Classe classe, Object from) {
		List<Trade> trades = getTradesOfClasse(classe);
		MenuGUI gui = new MenuGUI(DARK_BLUE + "Edition trades de [" + DARK_GREEN + classe.getName().toLowerCase() + DARK_BLUE + "]", 9*6, HalystiaRPG.getInstance()) {
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getSlot() == trades.size()) {
					createNewTrade(p, classe, from, null);
					return;
				}
				if(e.getSlot() == 53) {
					if(from != null) {
						if(from instanceof Shop) {
							((Shop)from).openParametersGUI(p);
						}
					}
					return;
				}
				createNewTrade(p, classe, from, trades.get(e.getSlot()));
			}
		};
		for(int i = 0; i < 9*6; i++)
			gui.addOption(WALL, i);
		
		for(int i = 0; i < trades.size(); i++) {
			Trade trade = trades.get(i);
			gui.addOption(trade.getIcone(), i);
		}
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(YELLOW+"Rajouter un trade").setLore(GRAY+"Pour la classe [" + WHITE + classe.getName().toLowerCase() + GRAY + "]").toItemStack(), trades.size());
		
		gui.addOption(new ItemBuilder(Material.ARROW).setName(RED+(from == null ? "Quitter" : "Retour")).toItemStack(), 53);
		
		gui.show(p);
	}
	
	private static final ItemStack WALL = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
	private static final ItemStack TO_COMPLETE = new ItemBuilder(Material.STONE_BUTTON).setName(YELLOW+"À compléter !").setLore(GRAY+"Faites glisser un item !", DARK_GRAY + "(facultatif)").toItemStack();
	private static final ItemStack TO_COMPLETE_SELL = new ItemBuilder(Material.JUNGLE_BUTTON).setName(GOLD+"À compléter !").setLore(GRAY+"Faites glisser un item !").toItemStack();
	private static final ItemStack IMPOSSIBLE = new ItemBuilder(Material.BARRIER).setName(RED+"<-").toItemStack();
	private static final ItemStack IMPOSSIBLE_VALIDATE = new ItemBuilder(Material.BARRIER).setName(RED+"Il faut un item à vendre et un item requis !").toItemStack();
	
	public void createNewTrade(Player p, Classe classe, Object from, Object tradeToEdit) {
		boolean edit = false;
		Trade toEdit = null;
		String title = "Nouveau trade";
		if(tradeToEdit != null) {
			if(tradeToEdit instanceof Trade) {
				edit = true;
				toEdit = (Trade)tradeToEdit;
				title = "Edition de trade";
			}
		}
		final boolean editFinal = edit;
		final Trade trade = toEdit;
		title = DARK_BLUE + title + " (" +DARK_RED+classe.getName() + DARK_BLUE + ")";
		
		TradeEditorGUI gui = new TradeEditorGUI(title, 9*6, this, p, classe, editFinal, trade, from);
		
		for(int i = 0; i < 9*6; i++)
			gui.addOption(WALL, i);
		for(int i = 11; i <= 16; i++)
			gui.addOption(IMPOSSIBLE, i);
		gui.addOption(TO_COMPLETE, 10);
		gui.addOption(TO_COMPLETE_SELL, 31);
		
		gui.addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(RED + "Annuler").toItemStack(), 28);
		gui.addOption(IMPOSSIBLE_VALIDATE, 34);
		
		if(edit)
			gui.addOption(new ItemBuilder(Material.TNT).setName(DARK_RED+"Détruire le trade").setLore(RED+"(Définitif !)").toItemStack(),27);
		
		gui.addOption(new ItemBuilder(Material.RED_CARPET).setName(DARK_RED+"-1 niveau").toItemStack(), 48);
		gui.addOption(new ItemBuilder(Material.RED_CARPET, 5).setName(DARK_RED+"-5 niveaux").toItemStack(), 47);
		gui.addOption(new ItemBuilder(Material.RED_CARPET, 10).setName(DARK_RED+"-10 niveaux").toItemStack(), 46);
		gui.addOption(new ItemBuilder(Material.BRICK).setName(YELLOW + "Niveau " + LIGHT_PURPLE + "1").toItemStack(), 49);
		gui.addOption(new ItemBuilder(Material.GREEN_CARPET).setName(DARK_GREEN+"+1 niveau").toItemStack(), 50);
		gui.addOption(new ItemBuilder(Material.GREEN_CARPET, 5).setName(DARK_GREEN+"+5 niveaux").toItemStack(), 51);
		gui.addOption(new ItemBuilder(Material.GREEN_CARPET, 10).setName(DARK_GREEN+"+10 niveaux").toItemStack(), 52);
		
		if(edit) {
			gui.forceLevel(trade.getLevelRequired());
			int i = 10;
			for(ItemStack item : trade.getItemNeeded()) {
				gui.putItem(item, i);
				i++;
			}
			gui.addOption(trade.getItemToSell(), 31);
			gui.computeValidate();
		}
		
		gui.show(p);
	}
	
	public Classe getClasseOfItem(ItemStack item) {
		try {
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			String line = lore.get(lore.size() - 1);
			if(line.contains(LORE_CLASSE)) {
				String end = line.split(LORE_CLASSE)[1];
				for(Classe classe : Classe.values()) {
					if(end.contains(classe.getName().toLowerCase())) {
						return classe;
					}
				}
			}
		} catch (Exception e) {}
		return Classe.NONE;
	}
	
}
