package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

public class SpellEntityManager {
	
	private List<SpellEntity> list;
	
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
					se.effect();
					if( ! se.isValid())
						list.remove(se);
				}
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 20L, 5L);
	}
	
	public synchronized void add(SpellEntity se) {
		list.add(se);
	}
	
	public synchronized void remove(SpellEntity se) {
		list.remove(se);
	}
	
}
