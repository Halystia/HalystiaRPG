package fr.jamailun.halystia;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.jamailun.halystia.chunks.ChunkManager;
import fr.jamailun.halystia.events.BossListeners;
import fr.jamailun.halystia.events.ConsumeItemListener;
import fr.jamailun.halystia.events.EntityDamageOtherListener;
import fr.jamailun.halystia.events.EntityPickupItemListener;
import fr.jamailun.halystia.events.GUIListener;
import fr.jamailun.halystia.events.MobAggroListener;
import fr.jamailun.halystia.events.MobDeathListener;
import fr.jamailun.halystia.events.MobSpawnListener;
import fr.jamailun.halystia.events.NpcInteractionListener;
import fr.jamailun.halystia.events.PlayerBreakListener;
import fr.jamailun.halystia.events.PlayerDeathListener;
import fr.jamailun.halystia.events.PlayerDropItemListener;
import fr.jamailun.halystia.events.PlayerEquipmentListener;
import fr.jamailun.halystia.events.PlayerFishListener;
import fr.jamailun.halystia.events.PlayerInteractListener;
import fr.jamailun.halystia.events.PlayerJoinLeaveListener;
import fr.jamailun.halystia.events.PlayerMovementsListener;
import fr.jamailun.halystia.events.SomeWorldListeners;
import fr.jamailun.halystia.events.TchatListener;
import fr.jamailun.halystia.jobs.JobsManager;

public class EventsManager {
	
	EventsManager(HalystiaRPG main, JobsManager jobs, ChunkManager chunks) {
		//Bukkit.getPluginManager().registerEvents(new ArmorListener(), main); //génère des ArmorEquiEvent !
		new PlayerJoinLeaveListener(main);
		new PlayerDropItemListener(main);
		new GUIListener(main);
		new NpcInteractionListener(main);
		new TchatListener(main);
		new PlayerMovementsListener(main, chunks);
		new PlayerDeathListener(main);
		new PlayerInteractListener(main, jobs);
		new PlayerBreakListener(main, jobs);
		new EntityDamageOtherListener(main);
		new ConsumeItemListener(main);
		new SomeWorldListeners(main);
		
		new MobAggroListener(main);
		new BossListeners(main);
		
		new MobDeathListener(main);
		new MobSpawnListener(main);
		new EntityPickupItemListener(main);
		new PlayerFishListener(main);
		
		if(Bukkit.getPluginManager().getPlugin("ArmorEquipEvent") != null && Bukkit.getPluginManager().getPlugin("ArmorEquipEvent").isEnabled()) {
			new PlayerEquipmentListener(main);
		} else {
			main.getConsole().sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "ArmorEquipEvent is not enabled !");
		}
		
	}
}