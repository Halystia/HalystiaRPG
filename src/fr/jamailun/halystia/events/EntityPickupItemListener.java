package fr.jamailun.halystia.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import fr.jamailun.halystia.HalystiaRPG;

public class EntityPickupItemListener extends HalystiaListener {

	public EntityPickupItemListener(HalystiaRPG main) {
		super(main);
	}

	@EventHandler
	public void entityPickupItem(EntityPickupItemEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if(e.getEntity() instanceof Player)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void transformEvent(EntityTransformEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		if(e.getTransformReason() == TransformReason.DROWNED) {
			((Zombie)e.getEntity()).setConversionTime(Integer.MAX_VALUE-1);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void entityRideEvent(VehicleEnterEvent e) {
		if ( ! HalystiaRPG.isInRpgWorld(e.getEntered()) )
			return;
		if( ! (e.getEntered() instanceof Player) ) {
			e.setCancelled(true);
			return;
		}
		boolean valid = e.getVehicle().getLocation().getBlock().getType() == Material.WATER || e.getVehicle().getLocation().getBlock().getRelative(0, -1, 0).getType() == Material.WATER;
		e.setCancelled(!valid);
		if(!valid)
			((Player)e.getEntered()).sendMessage(HalystiaRPG.PREFIX+ChatColor.RED + "Impossible d'aller sur un bateau qui n'est pas dans l'eau !");
	}
}
