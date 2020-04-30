package fr.jamailun.halystia.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobCraft;
import fr.jamailun.halystia.jobs.JobType;
import fr.jamailun.halystia.jobs.JobsManager;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class MainJobGUI extends MenuGUI {

	public final static int SIZE = 9*6;
	private final Player p;
	
	public MainJobGUI(Player p, JobType job, JobsManager jobs) {
		super(ChatColor.DARK_GREEN + "Informations " + job.getJobName(), SIZE, HalystiaRPG.getInstance());
		this.p = p;
		
		if( ! job.hasJob(p) ) {
			p.sendMessage(ChatColor.RED + "Une erreur est survenue : vous n'avez pas le métier de " + job.getJobName()+".");
			return;
		}
		
		for(JobCraft craft : jobs.getCraftsManager().getCrafts(job)) {
			addOption(craft.getObtained());
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