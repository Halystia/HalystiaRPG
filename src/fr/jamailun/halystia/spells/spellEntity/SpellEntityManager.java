package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

/**
 * Manage all {@linkplain SpellEntity} of the server.
 */
public class SpellEntityManager {
	
	private List<SpellEntity> list;
	
	/**
	 * Do not call it. Handled by HalystaiRPG's instance.
	 */
	public SpellEntityManager() {
		list = new ArrayList<>();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(list.isEmpty())
					return;
				List<SpellEntity> copy = new ArrayList<>(list);
				for(SpellEntity se : copy) {
					se.liveTick();
					if( ! se.isValid())
						list.remove(se);
				}
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 20L, 5L);
	}
	
	/**
	 * Add a SpellEntity to the list.
	 */
	public synchronized void add(SpellEntity se) {
		list.add(se);
	}
	
	/**
	 * Remove a SpellEntity from the list.
	 */
	public synchronized void remove(SpellEntity se) {
		list.remove(se);
	}
	
}
