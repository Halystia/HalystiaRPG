package fr.jamailun.halystia.donjons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class DonjonManager {
	
	private Set<Donjon> donjons;
	
	public DonjonManager() {
		donjons = new HashSet<>();
	}
	public void addDonjons(Collection<Donjon> donjons) {
		donjons.forEach(d -> addDonjon(d));
	}
	
	public void addDonjon(Donjon donjon) {
		donjons.add(donjon);
		Bukkit.getLogger().log(Level.INFO, "Donjon (" + donjon.getName() + ") loaded.");
	}
	
	public List<Donjon> getDonjons() {
		return new ArrayList<>(donjons);
	}
}