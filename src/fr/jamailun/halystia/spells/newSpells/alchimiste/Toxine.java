package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.*;
import fr.jamailun.halystia.spells.spellEntity.EffectSpellEntity;

public class Toxine extends Spell {

	private List<PotionEffect> effects;
	@Override
	public void init() {
		effects = Arrays.asList(
			new PotionEffect(PotionEffectType.POISON, 20*6, 1)
		);
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		
		EffectSpellEntity spell = new EffectSpellEntity(p.getLocation().add(0, 1.5, 0).clone(), p, 4*2, effects, .9, false);
		
		Vector dir = p.getLocation().getDirection();
		Vector dirr = dir.multiply(.7);
		spell.setDirection(dirr);
		spell.addParticleEffect(Particle.VILLAGER_HAPPY, 50, .5, .5, 10);
		spell.addSoundEffect(Sound.ENTITY_SILVERFISH_STEP, .5f, 1.5f);
		
		return true;
	}

	@Override
	public String getName() {
		return "Toxines";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 1;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Propulsez un nuage",
			ChatColor.GRAY + "toxique devant vous.",
			ChatColor.GRAY + "Attention à vos alliés !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-toxine";
	}

	@Override
	public int getManaCost() {
		return 12;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
