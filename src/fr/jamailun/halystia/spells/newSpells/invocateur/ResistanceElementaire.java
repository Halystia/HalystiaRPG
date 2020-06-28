package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class ResistanceElementaire extends Spell {
	
	public final static int RANGE = 5;
	
	private List<PotionEffect> effects;
	@Override
	public void init() {
		effects = Arrays.asList(
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*5, 2, true, true, true)
		);
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		for(LivingEntity en : getPlayersAroundPlayer(p, RANGE, true)) {
			for(PotionEffect effect : effects)
				en.addPotionEffect(effect);
		}
		
		for(Player pl : getPlayersAroundPlayer(p, 100, true)) {
			pl.playSound(pl.getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 2f, .4f);
			spawnParticles(pl, p.getLocation(), Particle.SMOKE_LARGE, 200, RANGE, 1, .05);
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Résistance élémentaire";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.BLUE;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 10;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Conjure les éléments de la terre",
			ChatColor.GRAY + "pour protéger tous les êtres autour de vous."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-resisElem";
	}

	@Override
	public int getManaCost() {
		return 10;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
