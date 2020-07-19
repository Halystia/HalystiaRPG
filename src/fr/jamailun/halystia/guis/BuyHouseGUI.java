package fr.jamailun.halystia.guis;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.guilds.Guild;
import fr.jamailun.halystia.guilds.GuildRank;
import fr.jamailun.halystia.guilds.houses.GuildHouse;
import fr.jamailun.halystia.guilds.houses.HouseSize;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;
import fr.jamailun.halystia.utils.YesNoGUI;

public class BuyHouseGUI extends MenuGUI {

	private final static ItemStack MUR = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(ChatColor.GRAY+"").toItemStack();
	private final static ItemStack MUR2 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.GRAY+"").toItemStack();
	
	private final Guild guild;
	private final Player p;
	private final GuildRank rank;
	private final List<GuildHouse> houses;
	public BuyHouseGUI(Player p) {
		super(ChatColor.BLUE + "Maisons de guilde à vendre", 9*4, HalystiaRPG.getInstance());
		this.p = p;
		this.guild = HalystiaRPG.getInstance().getGuildManager().getGuild(p);
		this.rank = guild.getPlayerRank(p);
		for(int i = 0; i < getSize() - 9; i++)
			addOption(MUR, i);
		for(int i = getSize() - 9; i < getSize()-1; i++)
			addOption(MUR2, i);
		addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.RED + "Quitter").toItemStack(), getSize() - 1);
		
		houses = HalystiaRPG.getInstance().getGuildManager().getHousesRegistry().getAllBuyableHouses();
		if ( houses.isEmpty() ) {
			addOption(new ItemBuilder(Material.BARRIER).setName(ChatColor.DARK_RED + "Aucune maison n'est disponible...").toItemStack(), 13);
			return;
		} else {
			for(int slot = 0; slot < houses.size(); slot ++) {
				GuildHouse house = houses.get(slot);
				HouseSize size = house.getSize();
				addOption(
						new ItemBuilder(size.getIconMaterial())
						.setName(size.getName())
						.addLoreLine(ChatColor.GRAY + "Prix : " + ChatColor.YELLOW + size.getCost() + ChatColor.GRAY + " unités")
						.addLoreLine(ChatColor.GRAY + "Localisation : " + ChatColor.YELLOW + " (" + house.getChunkX() + ":" + house.getChunkZ() + ")")
						.toItemStack()
				, slot);
			}
		}
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		if(e.getSlot() == getSize() - 1) {
			if(guild == null)
				p.closeInventory();
			else
				new GuildGui(p, guild, rank);
			return;
		}
		if ( houses.isEmpty() || e.getSlot() >= houses.size() )
			return;
		if(rank != GuildRank.MASTER) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Seul le maitre de guilde peut acheter une maison de guilde !");
			return;
		}
		if(guild.hasHouse()) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Votre guilde possède déjà une maison de guilde !");
			return;
		}
		
		GuildHouse house = houses.get(e.getSlot());
		
		if ( guild.getHowManyUnits() < house.getSize().getCost() ) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Votre guilde n'est pas assez riche ! Il vous manque " + ChatColor.DARK_RED + (house.getSize().getCost() - guild.getHowManyUnits()) + ChatColor.RED + " unités.");
			return;
		}
		
		if(house.hasOwner()) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Cette maison est déjà possédée par quelqu'un. Il fallait être plus rapide !");
			return;
		}
		
		buyHouse(house);
	}
	
	private void buyHouse(GuildHouse house) {
		new YesNoGUI(ChatColor.GOLD + "Acheter cette maison ?", HalystiaRPG.getInstance()) {
			@Override
			public void onFinish(Response response) {
				if(response == Response.NO) {
					new BuyHouseGUI(p).show(p);
					return;
				}
				buyingAccepted(house);
			}
		}.show(p);
	}
	
	private void buyingAccepted(GuildHouse house) {
		if ( guild.getHowManyUnits() < house.getSize().getCost() ) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Votre guilde n'est pas assez riche ! Il vous manque " + ChatColor.DARK_RED + (house.getSize().getCost() - guild.getHowManyUnits()) + ChatColor.RED + " unités.");
			return;
		}
		
		if(house.hasOwner()) {
			p.sendMessage(guild.getTag() + ChatColor.RED + "Cette maison est déjà possédée par quelqu'un. Il fallait être plus rapide !");
			return;
		}
		
		if ( ! HalystiaRPG.getInstance().getGuildManager().getHousesRegistry().guildBuyHouse(guild, house.getID()) ) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.DARK_RED + "Une erreur est survenue.");
		}
	}
	
}