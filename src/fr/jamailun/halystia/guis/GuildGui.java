package fr.jamailun.halystia.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildRank;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class GuildGui {
	
	private final static int SLOT_CHEST = 11, SLOT_RESUME = 4;
	
	public GuildGui(Player p, Guild guild, GuildRank rank) {
		MenuGUI gui = new MenuGUI(ChatColor.DARK_BLUE + "Guilde " + guild.getGuildName(), 9*4, HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getSlot() == SLOT_CHEST) {
					guild.playerRequestOpenChest(p);
					return;
				}
			}
		};
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(ChatColor.GRAY+"").toItemStack(), i);
		
		gui.addOption(
				new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + guild.getGuildName())
				.addLoreLine(ChatColor.GRAY + "Tag : " + ChatColor.GREEN + guild.getTag())
				.addLoreLine("")
				.addLoreLine(ChatColor.GRAY + "Dirigeant : " + ChatColor.YELLOW + guild.getMasterName())
				.addLoreLine(ChatColor.GRAY + "Nombre de membres : " + ChatColor.YELLOW + guild.getOfflinePlayersNames().size())
				.addLoreLine("")
				.addLoreLine(ChatColor.GRAY + "Niveau : " + ChatColor.YELLOW + guild.getLevel())
				.toItemStack()
		, SLOT_RESUME);
		gui.addOption(new ItemBuilder(Material.CHEST).setName(ChatColor.DARK_BLUE + "Coffre de guilde").toItemStack(), 11);
		gui.show(p);
	}
	
	
	
}