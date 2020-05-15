package fr.jamailun.halystia.donjons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.enemies.boss.BossManager;
import fr.jamailun.halystia.utils.Reloadable;

public class DonjonManager {
	
	private final Set<DonjonI> donjons;
	private final BossManager bosses;
	
	private final String path;
	
	public DonjonManager(String path) {
		donjons = new HashSet<>();
		this.path = path;
		bosses = new BossManager();
		loadData();
	}
	
	public BossManager getBossManager() {
		return bosses;
	}
	
	public synchronized void loadData() {
		donjons.clear();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				donjons.add(new Donjon(path, name, bosses));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean createDonjon(String configName, Location entry, DonjonDifficulty difficulty) {
		for(DonjonI dj : donjons)
			if(dj.getConfigName().equalsIgnoreCase(configName))
				return false;
		Donjon donjon = new Donjon(path, configName, bosses);
		donjon.changeEntryLocation(entry);
		donjon.changeExitLocation(entry);
		donjon.changeDonjonDifficulty(difficulty);
		donjons.add(donjon);
		return true;
	}
	
	public boolean removeDonjon(String configName) {
		DonjonI removed = null;
		for(DonjonI dj : donjons) {
			if(dj.getConfigName().equalsIgnoreCase(configName)) {
				dj.destroy();
				removed = dj;
				break;
			}
		}
		if(removed == null)
			return false;
		donjons.remove(removed);
		return true;
	}
	
	public void reloadData() {
		for(DonjonI dj : donjons)
			if(dj instanceof Reloadable)
				((Reloadable)dj).reloadData();
	}
	
	/**
	 * Get the Donjon the Player is in.
	 * @return null if Player isn't in a donjon.
	 */
	public DonjonI getContainerDonjon(Player p) {
		for(DonjonI dj : donjons)
			if(dj.isPlayerInside(p))
				return dj;
		return null;
	}
	
	public void playerLeaveGame(Player p) {
		DonjonI donjon = getContainerDonjon(p);
		if(donjon != null)
			donjon.forcePlayerExit(p, false);
	}
	
	//API
	public void addDonjons(Collection<DonjonI> donjons) {
		donjons.forEach(d -> addDonjon(d));
	}
	//API
	public void addDonjon(DonjonI donjon) {
		donjons.add(donjon);
		Bukkit.getLogger().log(Level.INFO, "Donjon (" + donjon.getName() + ") loaded.");
	}
	
	public List<DonjonI> getDonjons() {
		return new ArrayList<>(donjons);
	}

	public Donjon getLegacyWithConfigName(String id) {
		for(DonjonI dj : donjons)
			if(dj instanceof Donjon) {
				Donjon donjon = (Donjon) dj;
				if(donjon.getConfigName().equalsIgnoreCase(id))
					return donjon;
			}
		return null;
	}
	
}