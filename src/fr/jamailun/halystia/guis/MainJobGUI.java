package fr.jamailun.halystia.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobManager;
import fr.jamailun.halystia.jobs.JobName;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class MainJobGUI extends MenuGUI {

	public final static int SIZE = 9*6;
	private final Player p;
	
	public MainJobGUI(Player p, JobName job, JobManager jobs) {
		super(ChatColor.DARK_GREEN + "Informations " + job.getName(), SIZE, HalystiaRPG.getInstance());
		this.p = p;
		
		if( ! jobs.hasJob(p, job) ) {
			p.sendMessage(ChatColor.RED + "Une erreur est survenue : vous n'avez pas le métier de " + job.getName()+".");
			return;
		}
		
		for(ItemStack icon : jobs.getIcons(p, job)) {
			addOption(icon);
		}
		
		addOption(new ItemBuilder(Material.ARROW).setName(ChatColor.BLUE+"Retour").toItemStack(), SIZE-1);
		show(p);
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		removeFromList();
	}

	@Override
	public void onClick(InventoryClickEvent e) {
		e.setCancelled(true);
		if(e.getSlot() == SIZE-1)
			new MainClasseGUI(p);
	}

}