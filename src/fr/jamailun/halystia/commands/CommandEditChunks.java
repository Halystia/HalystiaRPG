package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.BLACK;
import static org.bukkit.ChatColor.DARK_BLUE;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.chunks.ChunkType;
import fr.jamailun.halystia.guis.EditChunkGUI;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class CommandEditChunks extends HalystiaCommand {
	
	public CommandEditChunks(HalystiaRPG main) {
		super(main, "edit-chunks");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		openGUI(p, 1, main);
		
		return true;
	}
	
	public static void openGUI(Player p, int page, HalystiaRPG mmain) {
		
		List<ChunkType> types = mmain.getChunkCreator().getChunkTypeList();
		int maxPages = 1 + (types.size() / 45);
		
		final int debut = ((page-1)*45);
		
		MenuGUI gui = new MenuGUI(DARK_BLUE + "Liste des chunks", 9*6, mmain) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				e.setCancelled(true);
				if(e.getCurrentItem() == null)
					return;
				if(e.getCurrentItem().getType() == Material.AIR)
					return;
				if(e.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE || e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE)
					return;
				
				if(e.getSlot() > 9*5) {
					Material mat = e.getCurrentItem().getType();
					if(mat == Material.BLUE_CONCRETE)
						openGUI(p, page+1,mmain);
					else if(mat == Material.YELLOW_CONCRETE)
						openGUI(p, page-1,mmain);
					else if(mat == Material.WRITABLE_BOOK)
						new EditChunkGUI(p);
					return;
				}
				ChunkType type = types.get(debut + e.getSlot());
				new EditChunkGUI(p, type);
			}
		};
		for(int i=0;i<gui.getSize()-9;i++)
			gui.addOption(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(WHITE+"").toItemStack(),i);
		for(int i=gui.getSize()-9;i<gui.getSize();i++)
			gui.addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(BLACK+"").toItemStack(),i);
		
		
		int j = debut;
		while(j < types.size() && j < debut+45) {
			ChunkType type = types.get(j);
			gui.addOption(type.getIcone(), j-debut);
			j++;
		}
		
		
		if(page < maxPages)
			gui.addOption(new ItemBuilder(Material.BLUE_CONCRETE).setName(GRAY+"Vers la page " + DARK_PURPLE + "" + (page+1)).setLore(DARK_GRAY +"Actuellement page " + page).toItemStack(),(9*6)-1);
		if(page > 1)
			gui.addOption(new ItemBuilder(Material.YELLOW_CONCRETE).setName(GRAY+"Vers la page " + DARK_PURPLE + "" + (page-1)).setLore(DARK_GRAY +"Actuellement page " + page).toItemStack(),(9*6)-9);
		
		gui.addOption(new ItemBuilder(Material.WRITABLE_BOOK).setName(GOLD+"Rajouter un chunk").toItemStack(), (9*6)-5);
		
		gui.show(p);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		return new ArrayList<>();
	}
}