package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class FracassTete extends Spell {

	public final static double RANGE = 3;
	public final static double POWER = 1.5;
	
	@Override
	public boolean cast(Player p) {
		for(Entity pl : getEntitiesAroundPlayer(p, RANGE, false)) {
			pl.setVelocity(new Vector(0, POWER, 0));
		}
		spawnParticles(p.getLocation(), Particle.SQUID_INK, 200, RANGE/2, .2, .1);
		playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 4f, .8f);
		return true;
	}

	@Override
	public String getName() {
		return "Fracass'tête";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 5;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "En frappant de toutes vos",
			ChatColor.GRAY + "forces sur le sol, vous",
			ChatColor.GRAY + "propulsez vos adversaires en l'air."
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-fracassTete";
	}

	@Override
	public int getManaCost() {
		return 7;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
