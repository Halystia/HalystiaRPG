package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.*;
import fr.jamailun.halystia.spells.spellEntity.EffectSpellEntity;

public class Sommeil extends Spell {

	private List<PotionEffect> effects;
	@Override
	public void init() {
		effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SLOW, 20*3, 50, false, false, true),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 9*20, 2, false, false, true),
			new PotionEffect(PotionEffectType.JUMP, 9*20, 199, false, false, false),
			new PotionEffect(PotionEffectType.BLINDNESS, 9*20, 199, false, false, true)
		);
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		
		Block bl = getLookedBlock(p, 15);
		if(bl == null) {
			return false;
		}
		EffectSpellEntity spell = new EffectSpellEntity(bl.getLocation().add(0, .5, 0).clone(), p, 4*7, effects, 4, false);
		
		for(Player pl : getPlayersAround(bl.getLocation(), 100))
			pl.playSound(bl.getLocation(), Sound.ENTITY_ENDERMAN_STARE, .2f, 1.7f);
		
		spell.addParticleEffect(Particle.SPELL_INSTANT, 200, 3, 3, .1);
		
		return true;
	}

	@Override
	public String getName() {
		return "Sommeil";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 30;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Créer un nuage mystérieux",
			ChatColor.GRAY + "dans votre direction.",
			ChatColor.GRAY + "Il endormira vos adversaires !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-sommeil";
	}

	@Override
	public int getManaCost() {
		return 15;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
