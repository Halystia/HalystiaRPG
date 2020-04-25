package fr.jamailun.halystia.constants;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

@Deprecated
public class CustomEffectManager {

	public final static String ACIER_BRUTAL = "brutal";
	
	private HashMap<UUID, List<String>> map;
	
	public CustomEffectManager() {
		map = new HashMap<>();
	}

	public void addEffect(Player p, String effect, int duration) {
		if(map.containsKey(p.getUniqueId())) {
			List<String> list = map.get(p.getUniqueId());
			if(list.contains(effect))
				list.remove(effect);
			list.add(effect);
			map.replace(p.getUniqueId(), list);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				removeEffect(p, effect);
			}
		}.runTaskLater(HalystiaRPG.getInstance(), duration*20L);
	}
	
	public void removeEffect(Player p, String effect) {
		List<String> list = map.get(p.getUniqueId());
		list.remove(effect);
		
		if(list.isEmpty())
			map.remove(p.getUniqueId());
		else
			map.replace(p.getUniqueId(), list);
	}
	
	public void clearAll() {
		map.clear();
	}
	
	public boolean hasEffect(Player p, String effect) {
		if(!map.containsKey(p.getUniqueId()))
			return false;
		return map.get(p.getUniqueId()).contains(effect);
	}
	
}
