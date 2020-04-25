package fr.jamailun.halystia.shops;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_BLUE;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guis.ChooseClasseGui;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class Shop {
	
	private Villager npc;
	
	private final String key;
	private final ShopManager manager;
	private Classe classe;
	private final Location location;
	
	public Shop(ShopManager manager, Classe classe, Location location, String key) {
		this.manager = manager;
		this.classe = classe;
		this.location = location;
		this.key = key;
		
		spawn();
	}
	
	void spawn() {
		npc = (Villager) Bukkit.getWorld(HalystiaRPG.WORLD).spawnEntity(location, EntityType.VILLAGER);
		npc.setAdult();
		npc.setCanPickupItems(false);
		npc.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
		npc.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		npc.setCollidable(false);
		npc.setGravity(false);
		npc.setRemoveWhenFarAway(false);
		npc.setCustomName((classe == Classe.NONE) ? ChatColor.RED+"Non paramétré !" : classe.getDisplayName(1));
		npc.setInvulnerable(true);
		npc.setSilent(true);
		npc.setAI(false);
		
		manager.addUUID(npc.getUniqueId());
	}
	
	public void despawn() {
		npc.remove();
		manager.removeUUID(npc.getUniqueId());
	}
	
	
	public Classe getClasse() {
		return classe;
	}

	public UUID getUUID() {
		return npc.getUniqueId();
	}
	
	public void openGUI(PlayerData pc) {
		final Shop thiisFrom = this;
		Player p = pc.getPlayer();
		final List<Trade> trades = HalystiaRPG.getInstance().getTradeManager().getTradesOfClasse(pc.getClasse());
		MenuGUI gui = new MenuGUI(DARK_BLUE + "Acheter au maître " + pc.getClasse().getName(), 9*(trades.size() / 9 + 1), HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getSlot() < trades.size()) {
					if(pc.getLevel() >= trades.get(e.getSlot()).getLevelRequired())
						openBuyTradeGUI(pc, trades.get(e.getSlot()), thiisFrom);
					else
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 2f);
				}
			}
		};
		
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(), i);
		for(int i = 0; i < trades.size(); i++) {
			Trade trade = trades.get(i);
			ItemBuilder builder = new ItemBuilder(trade.getIcone());
			if(pc.getLevel() < trade.getLevelRequired()) {
				ItemStack toSell = trade.getItemToSell();
				builder.setName(DARK_RED + "" + (toSell.hasItemMeta() ? toSell.getItemMeta().getDisplayName() :  toSell.getType()) + RED + (toSell.getAmount()>1 ? "x"+toSell.getAmount() : ""));
				builder.addLoreLine(DARK_RED + "Niveau insuffisant ! ("+pc.getLevel()+")");
			}
			gui.addOption(builder.toItemStack(), i);
		}
		
		gui.show(p);
	}
	
	private final static ItemStack NOP = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack();
	
	public void openBuyTradeGUI(final PlayerData pc, final Trade trade, Object from) {
		Player p = pc.getPlayer();
		final List<ItemStack> needed = trade.getItemNeeded();
		final ItemStack toSell = trade.getItemToSell();
		MenuGUI gui = new MenuGUI(DARK_BLUE + "Acheter au maître " + pc.getClasse().getName(), 9*5, HalystiaRPG.getInstance()) {
			
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
				if(item.getType() == Material.REDSTONE_BLOCK) {
					close();
					return;
				} else if (item.getType() == Material.EMERALD_BLOCK) {
					if( ! trade.canAfford(p)) {
						p.sendMessage(HalystiaRPG.PREFIX + RED + "Une erreur est survenue... Tu n'as pas tous les items requis !");
						return;
					}
					if(trade.trade(p)) {
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1.1f, .7f);
						close();
						return;
					}
					p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.1f, 1f);
					p.closeInventory();
					return;
				}
			}
			
			private void close() {
				if(from != null) {
					if(from instanceof Shop)
						((Shop)from).openGUI(pc);
					return;
				}
				p.closeInventory();
			}
		};
		
		for(int i = 0; i < 9*3; i++)
			gui.addOption(new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).setName(" ").toItemStack(), i);
		for(int i = 9*3; i < 9*5; i++)
			gui.addOption(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(), i);
		for(int i = 10; i <= 16; i++) {
			if(i >= needed.size() + 10)
				gui.addOption(NOP, i);
			else
				gui.addOption(new ItemBuilder(needed.get(i-10)).addLoreLine(DARK_PURPLE + "" + BOLD + "Item à échanger !").toItemStack(), i);
		}
		gui.addOption(new ItemBuilder(toSell).addLoreLine(LIGHT_PURPLE + "" + BOLD + "Item que vous obtenez !").toItemStack(), 31);
		
		if(trade.canAfford(p)) 
			gui.addOption(new ItemBuilder(Material.EMERALD_BLOCK).setName(GREEN+"Acheter").toItemStack(), 34);
		else
			gui.addOption(new ItemBuilder(Material.BARRIER).setName(DARK_RED+"Vous n'avez pas tous les items requis !").addLoreLine(RED+"Vérifier votre inventaire !").toItemStack(), 34);
		gui.addOption(new ItemBuilder(Material.REDSTONE_BLOCK).setName(RED+"Annuler").toItemStack(), 28);
		
		gui.show(p);
	}
	
	
	
	private static final ItemStack ACTIVE = new ItemBuilder(Material.LIME_CONCRETE).setName(GREEN + "Actif").toItemStack();
	private static final ItemStack INACTIVE = new ItemBuilder(Material.GRAY_CONCRETE).setName(GRAY + "Inactif").toItemStack();
	
	public void openParametersGUI(Player p) {
		final Shop thiis = this;
		MenuGUI gui = new MenuGUI("Paramétrer le villageois", 9*6, HalystiaRPG.getInstance()) {
			
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
				int slot = e.getSlot();
				
				switch(slot) {
					case 28:
					case 10:
						resetButtons(p);
						addOption(ACTIVE, 10);
						changeType(Classe.ALCHIMISTE);
						return;
					case 30:
					case 12:
						resetButtons(p);
						addOption(ACTIVE, 12);
						changeType(Classe.EPEISTE);
						return;
					case 32:
					case 14:
						resetButtons(p);
						addOption(ACTIVE, 14);
						changeType(Classe.ARCHER);
						return;
					case 34:
					case 16:
						resetButtons(p);
						addOption(ACTIVE, 16);
						changeType(Classe.INVOCATEUR);
						return;
					case 37:
						HalystiaRPG.getInstance().getTradeManager().openTradeClasseEditor(p, Classe.ALCHIMISTE, thiis);
						return;
					case 39:
						HalystiaRPG.getInstance().getTradeManager().openTradeClasseEditor(p, Classe.EPEISTE, thiis);
						return;
					case 41:
						HalystiaRPG.getInstance().getTradeManager().openTradeClasseEditor(p, Classe.ARCHER, thiis);
						return;
					case 43:
						HalystiaRPG.getInstance().getTradeManager().openTradeClasseEditor(p, Classe.INVOCATEUR, thiis);
						return;
					case 53:
						deleteShop();
						p.sendMessage(HalystiaRPG.PREFIX + RED + "Shop détruit avec succès.");
						p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, .5f, .7f);
						p.closeInventory();
						return;
				}
			}
			
			private void resetButtons(Player p) {
				addOption(INACTIVE, 10);
				addOption(INACTIVE, 12);
				addOption(INACTIVE, 14);
				addOption(INACTIVE, 16);
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, .7f);
			}
		};
		
		for(int i = 0; i < 9*6; i++)
			gui.addOption(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack(), i);
		
		gui.addOption(new ItemBuilder(ChooseClasseGui.ALCHIMISTE).setName(GRAY + "Choix : " + Classe.ALCHIMISTE.getDisplayName(1)).toItemStack(), 28);
		gui.addOption(new ItemBuilder(ChooseClasseGui.EPEISTE).setName(GRAY + "Choix : " + Classe.EPEISTE.getDisplayName(1)).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack(), 30);
		gui.addOption(new ItemBuilder(ChooseClasseGui.ARCHER).setName(GRAY + "Choix : " + Classe.ARCHER.getDisplayName(1)).toItemStack(), 32);
		gui.addOption(new ItemBuilder(ChooseClasseGui.INVOCATEUR).setName(GRAY + "Choix : " + Classe.INVOCATEUR.getDisplayName(1)).toItemStack(), 34);
		
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(GRAY+"Modifier les trades d'" + GOLD + Classe.ALCHIMISTE.getName().toLowerCase()).toItemStack(), 37);
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(GRAY+"Modifier les trades d'" + GOLD + Classe.EPEISTE.getName().toLowerCase()).toItemStack(), 39);
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(GRAY+"Modifier les trades d'" + GOLD + Classe.ARCHER.getName().toLowerCase()).toItemStack(), 41);
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(GRAY+"Modifier les trades d'" + GOLD + Classe.INVOCATEUR.getName().toLowerCase()).toItemStack(), 43);
		
		gui.addOption(classe == Classe.ALCHIMISTE ? ACTIVE : INACTIVE, 10);
		gui.addOption(classe == Classe.EPEISTE ? ACTIVE : INACTIVE, 12);
		gui.addOption(classe == Classe.ARCHER ? ACTIVE : INACTIVE, 14);
		gui.addOption(classe == Classe.INVOCATEUR ? ACTIVE : INACTIVE, 16);
		
		gui.addOption(new ItemBuilder(Material.BARRIER).setName(DARK_RED + "Retirer le villageois").toItemStack(), 53);
		
		gui.show(p);
	}
	
	public void changeType(Classe classe) {
		manager.updateShop(key, classe);
		this.classe = classe;
		npc.setCustomName((classe == Classe.NONE) ? ChatColor.DARK_RED+"[Non paramétré]" : classe.getDisplayName(1));
	}
	
	public void deleteShop() {
		manager.removeShop(this, key);
	}
	
}
