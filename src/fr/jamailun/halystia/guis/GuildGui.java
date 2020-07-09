package fr.jamailun.halystia.guis;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildRank;
import fr.jamailun.halystia.shops.Trade;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class GuildGui {
	
	private final static int SLOT_CHEST = 20, SLOT_RESUME = 4, SLOT_MEMBRES = 22, SLOT_MANAGE = 24;
	
	private final static ItemStack MUR = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(ChatColor.GRAY+"").toItemStack();
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
				if(e.getSlot() == SLOT_MEMBRES) {
					openListMembres(p, guild, rank);
					return;
				}
				if(e.getSlot() == SLOT_MANAGE) {
					if(rank != GuildRank.MASTER) {
						p.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 3f, 1.5f);
						return;
					}
					openManage(p, guild, rank);
					return;
				}
			}
		
		};
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(MUR, i);
		
		gui.addOption(
				new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + guild.getGuildName())
				.addLoreLine(ChatColor.GRAY + "Tag : " + ChatColor.GREEN + guild.getTag())
				.addLoreLine(ChatColor.GRAY + " ")
				.addLoreLine(ChatColor.GRAY + "Dirigeant : " + ChatColor.YELLOW + guild.getMasterName())
				.addLoreLine(ChatColor.GRAY + "Nombre de membres : " + ChatColor.YELLOW + guild.getOfflinePlayersNames().size())
				.addLoreLine(ChatColor.GRAY + " ")
				.addLoreLine(ChatColor.GRAY + "Niveau : " + ChatColor.YELLOW + guild.getLevel())
				.toItemStack()
		, SLOT_RESUME);
		
		gui.addOption(new ItemBuilder(Material.CHEST).setName(ChatColor.BLUE + "Coffre de guilde").toItemStack(), SLOT_CHEST);
		
		gui.addOption(new ItemBuilder(Material.PLAYER_HEAD).setName(ChatColor.BLUE + "Liste des membres").setSkullOwner(p.getName()).toItemStack(), SLOT_MEMBRES);

		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName((rank == GuildRank.MASTER ? ChatColor.GREEN : ChatColor.RED) + "Gestion de la guilde").toItemStack(), SLOT_MANAGE);
		
		gui.show(p);
	}
	
	private static final int M_SLOT_PVP = 11, M_SLOT_TAG = 13, M_SLOT_CHEST = 15;
	private void openManage(Player p, Guild guild, GuildRank rank) {
		
		int chestLevel = guild.getHowManyChestPages();
		int guildLevelRequired = chestLevel + 1;
		int BERequired = chestLevel * 64;
		
		MenuGUI gui = new MenuGUI(ChatColor.DARK_BLUE + "Guilde " + guild.getGuildName(), 9*3, HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getSlot() == getSize() - 1) {
					new GuildGui(p, guild, rank);
					return;
				}
				if(e.getSlot() == M_SLOT_PVP) {
					guild.setPvp( ! guild.allowsPVP());
					addOption(
							new ItemBuilder(guild.allowsPVP() ? Material.LIME_DYE : Material.GRAY_DYE)
							.setName(guild.allowsPVP() ? ChatColor.GREEN + "PvP actif" : ChatColor.RED + "PvP inactif")
							.setLore(ChatColor.GRAY + "Cliquez pour changer")
							.toItemStack(),
					M_SLOT_PVP);
					return;
				}
				if(e.getSlot() == M_SLOT_CHEST) {
					if(guild.getLevel() < guildLevelRequired) {
						p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut que la guilde soit niveau " + ChatColor.DARK_RED + guildLevelRequired + ChatColor.RED + ".");
						return;
					}
					Trade trade = new Trade(null, new ItemStack(Material.EMERALD_BLOCK, BERequired));
					if( ! trade.trade(p, true)) {
						p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut les " + ChatColor.DARK_RED + BERequired + ChatColor.RED + " blocs d'émeraudes requis !");
						return;
					}
					p.sendMessage(ChatColor.GREEN + "Succès : coffre amélioré !");
					guild.addNewPage();
					openManage(p, guild, rank);
					return;
				}
			}
		
		};
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(MUR, i);
		gui.addOption(
				new ItemBuilder(guild.allowsPVP() ? Material.LIME_DYE : Material.GRAY_DYE)
				.setName(guild.allowsPVP() ? ChatColor.GREEN + "PvP actif" : ChatColor.RED + "PvP inactif")
				.setLore(ChatColor.GRAY + "Cliquez pour changer")
				.toItemStack(),
		M_SLOT_PVP);
		gui.addOption(
				new ItemBuilder(Material.PAPER).setName(ChatColor.GRAY + "Tag : " + ChatColor.YELLOW + guild.getTag())
				.setLore(ChatColor.WHITE + "Pour le changer : "+ChatColor.YELLOW + "/guild edit-tag <tag>")
				.addLoreLine(ChatColor.WHITE + " ")
				.addLoreLine(ChatColor.DARK_GRAY + "Obtenir de l'aide : /guild help")
				.toItemStack(),
		M_SLOT_TAG);
		gui.addOption(
				new ItemBuilder(chestLevel < 10 ? Material.GOLD_BLOCK : Material.EMERALD_BLOCK).setName(ChatColor.YELLOW + "Améliorer le coffre de guilde")
				.setLore(ChatColor.GRAY + "Niveau actuel : " + ChatColor.YELLOW + chestLevel)
				.addLoreLine(ChatColor.DARK_GRAY + "Items : " + guild.getHowManyItemsInChest() + "/" + (9*5*chestLevel))
				.addLoreLine(ChatColor.WHITE + " ")
				.addLoreLine(chestLevel < 10 ? ChatColor.GRAY + "Niveau requis : " + ChatColor.BLUE + guildLevelRequired : ChatColor.GOLD + "Niveau max !")
				.addLoreLine(chestLevel < 10 ? ChatColor.GRAY + "Blocs d'émeraudes requis : " + ChatColor.BLUE + BERequired : "")
				.toItemStack(),
		M_SLOT_CHEST);
		gui.addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Retour").toItemStack(), gui.getSize() - 1);
		gui.show(p);
	}
	
	@SuppressWarnings("deprecation")
	private void openListMembres(Player p, Guild guild, GuildRank rank) {
		MenuGUI gui = new MenuGUI(guild.getTag() + ChatColor.BLUE + "Liste des membres", 9*4, HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getSlot() == getSize()-1) {
					new GuildGui(p, guild, rank);
					return;
				}
				if(e.getCurrentItem().getType() != Material.PLAYER_HEAD)
					return;
				String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				if(p.getName().equals(name)) {
					if(rank == GuildRank.MASTER) {
						p.sendMessage(guild.getTag() + ChatColor.RED + "Vous êtes le maître de la guilde : impossible de la quitter !");
						return;
					}
					p.sendMessage(guild.getTag() + ChatColor.RED + "Pour quitter la guilde, faites '/guild leave'.");
					return;
				}
				if(rank.getPower() < GuildRank.RIGHT_ARM.getPower())
					return;
				p.sendMessage(guild.getTag() + ChatColor.RED + "Pour renvoyer un membre, faites '/guild kick'. Pour le promouvoir ou le rétrograder, c'est '/guild promote' ou 'demote'.");
			}
		};
		for(int i = 0; i < gui.getSize() - 1; i++)
			gui.addOption(MUR, i);
		gui.addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Retour").toItemStack(), gui.getSize() - 1);
		int slot = 0;
		for(String name : guild.getOfflinePlayersNames()) {
			ItemBuilder head = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(name);
			OfflinePlayer off = Bukkit.getOfflinePlayer(name);
			if(off == null) {
				continue;
			}
			GuildRank rk = guild.getPlayerRank(off.getUniqueId());
			head.setName(rk.getColor() + name);
			head.addLoreLine(ChatColor.GRAY + "Rang : " + rk.getColor() + rk.toString().toLowerCase(Locale.FRENCH));
			if(name.equals(p.getName()))
				head.addLoreLine(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "C'est vous !");
			gui.addOption(head.toItemStack(), slot++);
		}
		gui.show(p);
	}
	
}