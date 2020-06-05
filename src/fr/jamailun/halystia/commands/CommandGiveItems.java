package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobsItemManager;
import fr.jamailun.halystia.jobs.JobsManager;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class CommandGiveItems extends HalystiaCommand {

	private final JobsManager jobs;
	public CommandGiveItems(HalystiaRPG main, JobsManager jobs) {
		super(main, "give-item");
		this.jobs = jobs;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}

		Player p = (Player) sender;
		
		if(args.length < 1) {
			openGUI(p, 1);
			return true;
		}
		
		givePlayerItem(p, args[0]);
		
		return true;
	}
	
	private void givePlayerItem(Player p, String key) {
		ItemStack item = jobs.getItemManager().getWithKey(key);
		if(item == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Item '"+key+"' non reconnu.");
			return;
		}
		p.getInventory().addItem(new ItemBuilder(item).setAmount(64).toItemStack());
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Succès de la requête.");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length <= 1)
			return jobs.getItemManager().getAllKeys().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
	
	private void openGUI(final Player player, final int page) {
		
		JobsItemManager items = main.getJobManager().getItemManager();
		final List<String> keys = items.getAllKeys();
		
		final int nbPerPage = 9*5;
		final int maxPages = keys.size() / nbPerPage;
		
		MenuGUI gui = new MenuGUI(ChatColor.DARK_BLUE + "Give item", 9*6, main) {
			
			@Override
			public void onClose(InventoryCloseEvent e) {
				removeFromList();
			}
			
			@Override
			public void onClick(InventoryClickEvent e) {
				if(e.getCurrentItem() == null)
					return;
				int pageSlot = (nbPerPage * page) + e.getSlot();
				if(e.getSlot() < nbPerPage) {
					givePlayerItem(player, keys.get(pageSlot));
					return;
				}
				if(e.getSlot() == nbPerPage+1 && e.getCurrentItem().getType() == Material.ARROW) {
					openGUI(player, page - 1);
					return;
				}
				if(e.getSlot() == getSize() && e.getCurrentItem().getType() == Material.ARROW) {
					openGUI(player, page + 1);
					return;
				}
			}
		};
		
		
		for(int i = 0; i < keys.size(); i++) {
			if(i >= gui.getSize() - 9)
				break;
			int index = ( (page-1) * nbPerPage ) + i;
			gui.addOption(items.getWithKey(keys.get(index)), i);
		}
		
		for(int i = nbPerPage+1; i < gui.getSize(); i++)
			gui.addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.BLUE + "").toItemStack(), i);
		
		if(page > 1)
			gui.addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.BLUE + "Page précédente").toItemStack(), nbPerPage + 1);
		if(page < maxPages)
			gui.addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.BLUE + "Page suivante").toItemStack(), gui.getSize());
		
		gui.show(player);
	}

}
