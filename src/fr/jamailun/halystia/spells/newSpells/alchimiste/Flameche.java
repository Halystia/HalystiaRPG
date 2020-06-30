package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;
import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;

public class Flameche extends Spell {

	@Override
	public synchronized boolean cast(Player p) {
		EffectAndDamageSpellEntity spell = new EffectAndDamageSpellEntity(p.getLocation().add(0, 1.5, 0).clone(), p, 4*3, 5*20, false, false);
		spell.setDamages(20);
		spell.setFireTick(8);
		Vector dir = p.getLocation().getDirection();
		Vector dirr = dir.multiply(.5);
		spell.setDirection(dirr);
		spell.addParticleEffect(Particle.FLAME, 50, 0, 0, .05);
		spell.addSoundEffect(Sound.BLOCK_FIRE_EXTINGUISH, .5f, 1.5f);
		
		return true;
	}

	@Override
	public String getName() {
		return "Flammèche";
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
		return 5;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Lance une petite flamme devant vous.",
			ChatColor.GRAY + "Efficace contre les monstres... et",
			ChatColor.GRAY + "contre les beaux parents collants."
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-flammeche";
	}

	@Override
	public int getManaCost() {
		return 9;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
