package fr.jamailun.halystia.guilds;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guis.GuildGui;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class GuildChest {
	
	private int maxPages;
	private final Guild guild;
	private Map<UUID, Boolean> viewersPower = new HashMap<>();
	private Map<UUID, Integer> viewersPage = new HashMap<>();
	private MenuGUI[] allPages;
	
	private final ConfigurationSection section;
	public GuildChest(Guild guild, ConfigurationSection section) {
		this.guild = guild;
		this.section = section;
		maxPages = guild.getHowManyChestPages();
		allPages = new MenuGUI[maxPages];
		generatePages();
	}

	private void generatePages() {
		for(int i = 0; i < maxPages; i++) {
			final int page = i;
			allPages[i] = new MenuGUI(ChatColor.BLACK + "Coffre de guilde - Page " + ChatColor.BOLD + (page+1), 9*6, HalystiaRPG.getInstance()) {
				
				@Override
				public void onClose(InventoryCloseEvent e) {
					guild.saveChest(this, page);
				}
				
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(InventoryClickEvent e) {
					if( ! (e.getWhoClicked() instanceof Player)) {
						e.getWhoClicked().sendMessage("§4Tu n'es pas un Player ?");
						e.setCancelled(true);
						player.updateInventory();
						return;
					}
					Player player = (Player) e.getWhoClicked();
					if( ! viewersPower.containsKey(player.getUniqueId())) {
						e.getWhoClicked().sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Une erreur est survenue. Veuillez rafraichir le coffre de guilde.");
						e.setCancelled(true);
						player.updateInventory();
						return;
					}
					if(e.getSlot() >= 45) {
						e.setCancelled(true);
						player.updateInventory();
						if(e.getSlot() == 49)
							new GuildGui(player, guild, guild.getPlayerRank(player));
						if(e.getSlot() == 45 && page > 0)
							playerChangePage(player, false);
						if(e.getSlot() == 53 && page < maxPages-1)
							playerChangePage(player, true);
						return;
					}
					if(e.getAction() == InventoryAction.PLACE_ONE && (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)) {
						addOption(new ItemBuilder(e.getCursor()).setAmount(1).toItemStack(), e.getSlot());
						e.getCursor().setAmount(e.getCursor().getAmount() - 1);
						e.setCancelled(true);
						return;
					}
					if(e.getAction() == InventoryAction.PICKUP_HALF && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
						int amount = e.getCurrentItem().getAmount();
						int half = amount / 2;
						int remaining = amount - half;
						addOption(new ItemBuilder(e.getCurrentItem()).setAmount(remaining).toItemStack(), e.getSlot());
						e.setCursor(new ItemBuilder(e.getCurrentItem()).setAmount(half).toItemStack());
						e.setCancelled(true);
						return;
					}
					if(e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
						int amount = e.getCursor().getAmount();
						final int maxSize = e.getCursor().getType().getMaxStackSize();
						e.setCancelled(true);
						for(int s = 0; s < getSize() - 9; s ++) {
							if(s == e.getSlot())
								continue;
							ItemStack stack = getInventory().getItem(s);
							if( Trade.areItemsTheSame(stack, e.getCursor()) ) {
								amount += stack.getAmount();
								if(amount > maxSize) {
									addOption(new ItemBuilder(stack).setAmount(amount - maxSize).toItemStack(), s);
									amount = maxSize;
									break;
								}
								addOption(new ItemStack(Material.AIR), s);
							}
						}
						addOption(new ItemBuilder(e.getCursor()).setAmount(amount).toItemStack(), e.getSlot());
						e.setCursor(null);
						return;
					}
					Move move = getMovement(e.getCurrentItem(), e.getCursor());
					if(move == Move.NONE)
						return;
					if(move == Move.TAKE) {
						if( ! viewersPower.get(player.getUniqueId()) ) {
							e.getWhoClicked().sendMessage(guild.getTag() + ChatColor.RED + "Tu n'as pas les droits nécessaires pour retirer un item du coffre de guilde !");
							e.setCancelled(true);
							player.updateInventory();
							return;
						}
						e.setCursor(e.getCurrentItem());
						addOption(new ItemStack(Material.AIR), e.getSlot());
						player.updateInventory();
						return;
					}
					if(move == Move.PUT) {
						addOption(e.getCursor(), e.getSlot());
						e.setCursor(null);
						e.setCancelled(false);
						player.updateInventory();
						return;
					}
					if(move == Move.STACK) {
						int newAmount = e.getCurrentItem().getAmount() + e.getCursor().getAmount();
						addOption(new ItemBuilder(e.getCurrentItem()).setAmount(newAmount).toItemStack(), e.getSlot());
						e.setCursor(null);
						e.setCancelled(false);
						player.updateInventory();
						return;
					}
					if(move == Move.SWITCH) {
						final ItemStack temp = e.getCursor();
						e.setCursor(e.getCurrentItem());
						addOption(temp, e.getSlot());
						player.updateInventory();
						return;
					}
				}
			};
			for(int j = 45; j <= 53; j++)
				allPages[i].addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.WHITE+"").toItemStack(), j);
			if(i > 0)
				allPages[i].addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.BLUE+""+ChatColor.BOLD+"("+(page)+") <-").toItemStack(), 45);
			if(i< maxPages - 1)
				allPages[i].addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.BLUE+""+ChatColor.BOLD+"-> ("+(page+2)+")").toItemStack(), 53);
			allPages[i].addOption(new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Retour").toItemStack(), 49);
			ConfigurationSection pageSection = section.getConfigurationSection(""+page);
			if(pageSection == null)
				continue;
			for ( int s = 0; s < 5 * 6; s ++ ) {
				ItemStack stack = pageSection.getItemStack(""+s);
				if(stack != null)
					allPages[page].addOption(stack, s);
			}
		}
	}
	
	public void openPlayer(Player player, boolean canGetItems) {
		viewersPower.put(player.getUniqueId(), canGetItems);
		viewersPage.put(player.getUniqueId(), 0);
		allPages[0].show(player);
	}
	
	private void playerChangePage(Player player, boolean next) {
		//player.closeInventory();
		int currentPage = viewersPage.get(player.getUniqueId());
		int newPage = currentPage + (next ? 1 : -1);
		viewersPage.put(player.getUniqueId(), newPage);
		try {
			allPages[newPage].show(player);
		} catch (IndexOutOfBoundsException e) {
			player.sendMessage(HalystiaRPG.PREFIX +ChatColor.RED + "Une erreur est survenue : " + e.getMessage());
		}
	}
	
	private static Move getMovement(ItemStack clicked, ItemStack cursor) {
		if(clicked == null || clicked.getType() == Material.AIR) {
			if(cursor == null || cursor.getType() == Material.AIR) {
				return Move.NONE;
			}
			return Move.PUT;
		}
		if(cursor == null || cursor.getType() == Material.AIR) {
			return Move.TAKE;
		}
		if(Trade.areItemsTheSame(clicked, cursor)) {
			if(clicked.getType().getMaxStackSize() > cursor.getAmount() + clicked.getAmount())
				return Move.STACK;
		}
		return Move.SWITCH;
	}
	
	private static enum Move {
		NONE, TAKE, PUT, SWITCH, STACK
	}

	public void addPage() {
		for(UUID uid : viewersPage.keySet()) {
			Player pl = Bukkit.getPlayer(uid);
			if(pl != null)
				pl.closeInventory();
		}
		for(int i = 0; i < maxPages; i++)
			guild.saveChest(allPages[i], i);
		maxPages ++;
		viewersPage.clear();
		viewersPower.clear();
		generatePages();
	}

	public int getHowManyItems() {
		int total = 0;
		for(MenuGUI gui : allPages) {
			Inventory inv = gui.getInventory();
			for(int s = 0; s < gui.getSize() - 9; s++) {
				if(inv.getItem(s) != null && inv.getItem(s).getType() != Material.AIR)
					total ++;
			}
		}
		return total;
	}
	
}