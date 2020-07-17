package fr.jamailun.halystia.guis;

import java.text.SimpleDateFormat;
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
	
	private final static int SLOT_CHEST = 20, SLOT_RESUME = 4, SLOT_MEMBRES = 22, SLOT_MANAGE = 24;//, SLOT_XP = 25;
	
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
				/*if(e.getSlot() == SLOT_XP) {
					openListMembres(p, guild, rank);
					return;
				}*/
			}
		
		};
		for(int i = 0; i < gui.getSize(); i++)
			gui.addOption(MUR, i);
		
		gui.addOption(
				new ItemBuilder(Material.BOOK).setName(ChatColor.GOLD + "" + ChatColor.BOLD + guild.getGuildName())
				.addLoreLine(ChatColor.GRAY + "Tag : " + ChatColor.GREEN + guild.getPureTag().toUpperCase())
				.addLoreLine(ChatColor.GRAY + " ")
				.addLoreLine(ChatColor.GRAY + "Dirigeant : " + ChatColor.YELLOW + guild.getMasterName())
				.addLoreLine(ChatColor.GRAY + "Nombre de membres : " + ChatColor.YELLOW + guild.getOfflinePlayersNames().size())
				.addLoreLine(ChatColor.GRAY + " ")
				.addLoreLine(ChatColor.GRAY + "Niveau : " + ChatColor.YELLOW + guild.getLevel())
				.addLoreLine(ChatColor.GRAY + "Expérience : " + ChatColor.YELLOW + guild.getExpAmount())
				.addLoreLine(ChatColor.GRAY + guild.generatePercentBar())
				.toItemStack()
		, SLOT_RESUME);
		
		gui.addOption(new ItemBuilder(Material.CHEST).setName(ChatColor.BLUE + "Coffre de guilde").toItemStack(), SLOT_CHEST);
		
		gui.addOption(new ItemBuilder(Material.PLAYER_HEAD).setName(ChatColor.BLUE + "Liste des membres").setSkullOwner(p.getName()).toItemStack(), SLOT_MEMBRES);

		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName((rank == GuildRank.MASTER ? ChatColor.BLUE : ChatColor.RED) + "Gestion de la guilde").toItemStack(), SLOT_MANAGE);
		
		//gui.addOption(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(ChatColor.BLUE + "Gérer la répartition d'exp").toItemStack(), SLOT_XP);
		
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
				OfflinePlayer target = Bukkit.getOfflinePlayer(name);
				if(target == null) {
					return;
				}
				openPlayerData(p, guild, rank, target);
				/*if(p.getName().equals(name)) {
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
				*/
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
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	
	private final static int P_SLOT_XP_PLUS = 10, P_SLOT_XP_MINUS = 28, P_SLOT_XP_CENTER = 19, P_SLOT_HEAD = 22, P_SLOT_KICK = 25, P_SLOT_PROM = 13, P_SLOT_DEM = 31;
	private final static int P_SLOT_XP_PLUS_10 = 1, P_SLOT_XP_MINUS_10 = 37;
	private void openPlayerData(Player p, Guild guild, GuildRank rank, OfflinePlayer target) {
		final GuildRank targetRank = guild.getPlayerRank(target.getUniqueId());
		final boolean canChangeExp = (rank == GuildRank.RIGHT_ARM && targetRank != GuildRank.MASTER)
				|| (rank == GuildRank.MASTER)
				|| ( (rank==GuildRank.MEMBER || rank == GuildRank.CAPITAIN) && p.getUniqueId().equals(target.getUniqueId()) );
		final boolean canPromote = (rank == GuildRank.RIGHT_ARM && targetRank != GuildRank.MASTER)
				|| (rank == GuildRank.MASTER);
		
		MenuGUI gui = new MenuGUI(guild.getTag() + ChatColor.GRAY + " > " + ChatColor.DARK_BLUE + target.getName(), 9*5, HalystiaRPG.getInstance()) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getSlot() == getSize()-1) {
					openListMembres(p, guild, rank);
					return;
				}
				final int slot = e.getSlot();
				
				if((slot == P_SLOT_XP_MINUS || slot == P_SLOT_XP_PLUS || slot == P_SLOT_XP_PLUS_10 || slot == P_SLOT_XP_PLUS_10) && canChangeExp) {
					int add = 0;
					switch(slot) {
					case P_SLOT_XP_MINUS_10:
						add = -10;
						break;
					case P_SLOT_XP_MINUS:
						add = -1;
						break;
					case P_SLOT_XP_PLUS_10:
						add = 10;
						break;
					case P_SLOT_XP_PLUS:
						add = 1;
						break;
					}
					int newPercent = guild.getExpPercentOfPlayerInt(target.getUniqueId()) + add;
					guild.changeExpPercent(target.getUniqueId(), newPercent);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
					addOption(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(ChatColor.YELLOW + "Exp donnée : " + ChatColor.LIGHT_PURPLE + newPercent + ChatColor.YELLOW + " %").toItemStack(), P_SLOT_XP_CENTER);
					return;
				}
				
				if((slot == P_SLOT_PROM || slot == P_SLOT_DEM) && canPromote) {
					if(slot == P_SLOT_PROM)
						p.performCommand("guilds promote " + target.getName());
					else
						p.performCommand("guilds demote " + target.getName());
					addOption(
							new ItemBuilder(Material.PLAYER_HEAD)
							.setSkullOwner(target.getName())
							.setName(targetRank.getColor() + target.getName())
							.addLoreLine(ChatColor.GRAY + "Grade : " + targetRank.getColor() + targetRank.toString())
							.addLoreLine(ChatColor.GRAY + "Expérience donnée : "  + ChatColor.GREEN + guild.getExpGivenOfPlayer(target.getUniqueId()))
							.addLoreLine(" ")
							.addLoreLine(ChatColor.GRAY + (target.isOnline() ? ChatColor.GREEN + "Connecté" : ChatColor.RED + "Non connecté") )
							.addLoreLine(ChatColor.GRAY + "Membre depuis le " + dateFormat.format(guild.getJoinedDate(target.getUniqueId())))
							.toItemStack()
					, P_SLOT_HEAD);
					return;
				}
				
				if((slot == P_SLOT_KICK) && canPromote) {
					p.performCommand("guilds kick " + target.getName());
					return;
				}
				
			}
		};
		for(int i = 0; i < gui.getSize() - 1; i++)
			gui.addOption(MUR, i);
		gui.addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.GRAY + "Retour").toItemStack(), gui.getSize() - 1);
		
		gui.addOption(
				new ItemBuilder(Material.PLAYER_HEAD)
				.setSkullOwner(target.getName())
				.setName(targetRank.getColor() + target.getName())
				.addLoreLine(ChatColor.GRAY + "Grade : " + targetRank.getColor() + targetRank.toString())
				.addLoreLine(ChatColor.GRAY + "Expérience donnée : "  + ChatColor.GREEN + guild.getExpGivenOfPlayer(target.getUniqueId()))
				.addLoreLine(" ")
				.addLoreLine(ChatColor.GRAY + (target.isOnline() ? ChatColor.GREEN + "Connecté" : ChatColor.RED + "Non connecté") )
				.addLoreLine(ChatColor.GRAY + "Membre depuis le " + dateFormat.format(guild.getJoinedDate(target.getUniqueId())))
				.toItemStack()
		, P_SLOT_HEAD);
		
		gui.addOption(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(ChatColor.YELLOW + "Exp donnée : " + ChatColor.LIGHT_PURPLE + guild.getExpPercentOfPlayerInt(target.getUniqueId()) + ChatColor.YELLOW + " %").toItemStack(), P_SLOT_XP_CENTER);
		if ( canChangeExp ) {
			gui.addOption(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "+ 10 %").toItemStack(), P_SLOT_XP_PLUS_10);
			gui.addOption(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "+ 1 %").toItemStack(), P_SLOT_XP_PLUS);
			gui.addOption(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "- 1 %").toItemStack(), P_SLOT_XP_MINUS);
			gui.addOption(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "- 10 %").toItemStack(), P_SLOT_XP_MINUS_10);
		}
		
		if( canPromote ) {
			gui.addOption(new ItemBuilder(Material.LIME_WOOL).setName(ChatColor.GREEN + "Promouvoir le joueur").toItemStack(), P_SLOT_PROM);
			gui.addOption(new ItemBuilder(Material.RED_WOOL).setName(ChatColor.RED + "Rétrograder le joueur").toItemStack(), P_SLOT_DEM);
		}
		
		if( canPromote ) {
			gui.addOption(new ItemBuilder(Material.TNT).setName(ChatColor.DARK_RED + "Kicker le joueur").toItemStack(), P_SLOT_KICK);
		}
		
		
		
		gui.show(p);
	}
	
}