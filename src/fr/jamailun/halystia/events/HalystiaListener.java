package fr.jamailun.halystia.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.sql.temporary.Saver;

abstract class HalystiaListener implements Listener {

	protected final HalystiaRPG main;
	protected final Saver bdd;
	
	public HalystiaListener(HalystiaRPG main) {
		this.main = main;
		bdd = main.getDataBase();
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
}
