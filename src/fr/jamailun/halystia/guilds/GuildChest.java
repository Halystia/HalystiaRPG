package fr.jamailun.halystia.guilds;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.MenuGUI;

public class GuildChest {
	
	private int maxPages;
	private final Guild guild;
	private Map<UUID, Boolean> viewersPower;
	private Map<UUID, Integer> viewersPage;
	private MenuGUI[] allPages;
	
	public GuildChest(Guild guild, ConfigurationSection section) {
		this.guild = guild;
		maxPages = guild.getHowManyChestPages();
		allPages = new MenuGUI[maxPages];
		generatePages(section);
	}

	private void generatePages(ConfigurationSection section) {
		for(int i = 0; i < maxPages; i++) {
			final int page = i;
			allPages[i] = new MenuGUI(ChatColor.BLACK + "Coffre de guilde - Page " + (page+1), 9*6, HalystiaRPG.getInstance()) {
				
				@Override
				public void onClose(InventoryCloseEvent e) {
					// rien ici, on garde le truc ouvert
				}
				
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
						if(e.getSlot() == 45 && page > 0)
							playerChangePage(player, false);
						if(e.getSlot() == 53 && page < maxPages-1)
							playerChangePage(player, true);
						return;
					}
					//TODO tous les cas de transfert possibles
					if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
						if( ! viewersPower.get(player.getUniqueId()) ) {
							e.getWhoClicked().sendMessage(guild.getTag() + ChatColor.RED + "Tu n'as pas les droits nécessaires pour retirer un item du coffre de guilde !");
							e.setCancelled(true);
							player.updateInventory();
							return;
						}
					}
				}
			};
		}
	}

	public void openPlayer(Player player, boolean canGetItems) {
		viewersPower.put(player.getUniqueId(), canGetItems);
		viewersPage.put(player.getUniqueId(), 0);
		allPages[0].show(player);
	}
	
	private void playerChangePage(Player player, boolean next) {
		int currentPage = viewersPage.get(player.getUniqueId());
		int newPage = currentPage + (next ? 1 : -1);
		try {
			allPages[newPage].show(player);
		} catch (IndexOutOfBoundsException e) {
			player.sendMessage(HalystiaRPG.PREFIX +ChatColor.RED + "Une erreur est survenue : " + e.getMessage());
		}
	}
	
}